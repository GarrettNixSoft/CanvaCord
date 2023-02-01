package org.canvacord.gui;

import org.canvacord.setup.TokenSetupWizard;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class VerifyBackgroundTask extends SwingWorker<Boolean, Object> {

    private final TokenSetupWizard parentWizard;
    private final BooleanTask backgroundTask;
    private final int typeCode;

    public VerifyBackgroundTask(TokenSetupWizard parentWizard, BooleanTask backgroundTask, int typeCode) {
        this.parentWizard = parentWizard;
        this.backgroundTask = backgroundTask;
        this.typeCode = typeCode;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return backgroundTask.execute();
    }

    @Override
    protected void done() {
        try {
            parentWizard.updateVerifyTask(typeCode, get());
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
