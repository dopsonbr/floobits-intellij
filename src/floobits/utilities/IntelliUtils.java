package floobits.utilities;

import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import floobits.common.interfaces.IContext;
import floobits.common.interfaces.IFile;
import floobits.impl.FileImpl;

import java.util.ArrayList;


public class IntelliUtils {
    public static ArrayList<String> getAllNestedFilePaths(VirtualFile vFile) {
        final ArrayList<String> filePaths = new ArrayList<String>();
        if (!vFile.isDirectory()) {
            filePaths.add(vFile.getPath());
            return filePaths;
        }
        VfsUtil.iterateChildrenRecursively(vFile, null, new ContentIterator() {
            @Override
            public boolean processFile(VirtualFile file) {
                if (!file.isDirectory()) {
                    filePaths.add(file.getPath());
                }
                return true;
            }
        });
        return filePaths;
    }

    public static ArrayList<IFile> getAllValidNestedFiles(final IContext context, VirtualFile vFile) {
        final ArrayList<IFile> virtualFiles = new ArrayList<IFile>();
        FileImpl fileImpl = new FileImpl(vFile);

        if (!fileImpl.isDirectory()) {
            if (fileImpl.isValid() && !context.isIgnored(fileImpl)) virtualFiles.add(fileImpl);
            return virtualFiles;
        }

        VfsUtil.iterateChildrenRecursively(vFile, null, new ContentIterator() {
            @Override
            public boolean processFile(VirtualFile file) {
                FileImpl fileImpl = new FileImpl(file);
                if (!context.isIgnored(fileImpl) && !fileImpl.isDirectory() && fileImpl.isValid()) {
                    virtualFiles.add(fileImpl);
                }
                return true;
            }
        });
        return virtualFiles;
    }

    public static Boolean isSharable(VirtualFile virtualFile) {
        return (virtualFile != null  && virtualFile.isValid() && virtualFile.isInLocalFileSystem() && !virtualFile.is(VFileProperty.SPECIAL) && !virtualFile.is(VFileProperty.SYMLINK));
    }
}
