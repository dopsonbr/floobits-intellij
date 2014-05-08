package floobits.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import floobits.FlooContext;
import floobits.FloobitsPlugin;
import floobits.common.API;
import floobits.common.EditorEventHandler;
import floobits.common.handlers.FlooHandler;
import floobits.utilities.Flog;

public abstract class IsJoinedAction extends AnAction {

    public abstract void actionPerformed(AnActionEvent e, EditorEventHandler editorEventHandler);

    @Override
    public void actionPerformed(AnActionEvent e) {
        FloobitsPlugin floobitsPlugin;
        FlooHandler flooHandler = null;
        FlooContext context = null;
        try {
            floobitsPlugin = FloobitsPlugin.getInstance(e.getProject());
            if (floobitsPlugin == null) {
                Flog.log("no floobits plugin, aborting.");
                return;
            }
            context = floobitsPlugin.context;
            flooHandler = context.getFlooHandler();
            if (flooHandler == null) {
                return;
            }
            actionPerformed(e, flooHandler.editorEventHandler);
        } catch (Throwable throwable) {
            API.uploadCrash(context, throwable);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        FloobitsPlugin floobitsPlugin = FloobitsPlugin.getInstance(e.getProject());
        if (floobitsPlugin == null) {
            return;
        }
        e.getPresentation().setEnabled(isEnabled(floobitsPlugin));
    }

    protected boolean isEnabled(FloobitsPlugin floobitsPlugin) {
        return floobitsPlugin.context.isJoined();
    }
}