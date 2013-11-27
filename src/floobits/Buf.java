package floobits;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import com.intellij.openapi.diagnostic.Logger;

import com.google.gson.Gson;
import com.google.gson.*;
import com.google.gson.JsonParser;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import dmp.diff_match_patch;
import dmp.diff_match_patch.Patch;

import floobits.FlooUrl;
import floobits.Settings;
import floobits.PersistentJson;

enum Encoding {
    BASE64("base64"), UTF8("utf-8");
    private final String enc;

    Encoding (String enc) {
        this.enc = enc;
    }

    @Override
    public String toString () {
        return this.enc;
    }

    public static Encoding from (String str) {
        for(Encoding v : Encoding.values()) {
            if(v.toString().equals(str)) {
                return v;
            }
        }
        return null;
    }
}

abstract class Buf <T>{
    public String path;
    public File f;
    public Integer id;
    public String md5;
    public T buf;

    public Buf (String path, Integer id, T buf, String md5) {
        this.id = id;
        this.path = path;
        this.buf = buf;
        this.md5 = md5;
        this.f = new File(path);

        if (buf == null) {
            try {
                this.readFromDisk();
                if (!this.md5.equals(md5)) {
                    this.buf = null;
                }
            } catch (IOException e) {
                this.buf = null;
            }
        }
    }
    abstract public void readFromDisk () throws IOException;
    
    public static BinaryBuf createBuf (String path, Integer id, byte[] buf, String md5) {
        return new BinaryBuf(path, id, buf, md5);
    }
    public static TextBuf createBuf (String path, Integer id, String buf, String md5) {
        return new TextBuf(path, id, buf, md5);
    }
    public static Buf createBuf (String path, Integer id, Encoding enc, String md5) {
        if (enc == Encoding.BASE64) {
            return new BinaryBuf(path, id, null, md5);
        }
        return new TextBuf(path, id, null, md5);
    }
}

class BinaryBuf extends Buf <byte[]> {
    public Encoding encoding = Encoding.BASE64;

    public BinaryBuf (String path, Integer id, byte[] buf, String md5) {
        super(path, id, buf, md5);
    }

    public void readFromDisk () throws IOException {
        this.buf = FileUtils.readFileToByteArray(this.f);
        this.md5 = DigestUtils.md5Hex(this.buf);
    }
}

class TextBuf extends Buf <String> {
    public Encoding encoding = Encoding.UTF8;

    public TextBuf (String path, Integer id, String buf, String md5) {
        super(path, id, buf, md5);
    }

    public void readFromDisk () throws IOException {
        this.buf = FileUtils.readFileToString(this.f, "UTF-8");
        this.md5 = DigestUtils.md5Hex(this.buf);
    }
}