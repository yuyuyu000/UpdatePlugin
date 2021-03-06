package org.lzh.framework.updatepluginlib.creator;

import android.app.Activity;
import android.app.Dialog;

import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.callback.UpdateCheckCB;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.util.Recyclable;
import org.lzh.framework.updatepluginlib.util.UpdatePreference;
import org.lzh.framework.updatepluginlib.util.Utils;

public abstract class InstallCreator implements Recyclable {

    private UpdateCheckCB checkCB;
    protected InstallChecker installChecker;
    protected Update update;

    public void setCheckCB(UpdateCheckCB checkCB) {
        this.checkCB = checkCB;
    }

    public void setInstallChecker (InstallChecker checker) {
        this.installChecker = checker;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public abstract Dialog create(Update update, String path, Activity activity);

    /**
     * request to install this apk file
     * @param filename the absolutely file name that downloaded
     */
    public void sendToInstall(String filename) {
        if (installChecker == null || installChecker.check(update,filename)) {
            Utils.installApk(UpdateConfig.getConfig().getContext(),filename);
        } else {
            checkCB.onCheckError(-1,String.format("apk %s checked failed",filename));
        }
        release();
    }

    /**
     * request cancel install action
     */
    public void sendUserCancel() {
        if (this.checkCB != null) {
            this.checkCB.onUserCancel();
        }

        release();
    }

    public void sendCheckIgnore(Update update) {
        if (this.checkCB != null) {
            this.checkCB.onCheckIgnore(update);
        }
        UpdatePreference.saveIgnoreVersion(update.getVersionCode());
        release();
    }

    @Override
    public void release() {
        this.checkCB = null;
        this.installChecker = null;
        this.update = null;
    }
}
