package org.canvacord.gui;

import org.canvacord.gui.wizard.BackgroundTaskWizard;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class VerifyBackgroundTask extends SwingWorker<Boolean, Object> {

    private final BackgroundTaskWizard<Boolean> parentWizard;
    private final BackgroundTask<Boolean> backgroundTask;
    private final int typeCode;

    public VerifyBackgroundTask(BackgroundTaskWizard<Boolean> parentWizard, BackgroundTask<Boolean> backgroundTask, int typeCode) {
        this.parentWizard = parentWizard;
        this.backgroundTask = backgroundTask;
        this.typeCode = typeCode;
    }

    @Override
    protected Boolean doInBackground() {
        return backgroundTask.execute();
    }

    @Override
    protected void done() {
        try {
            parentWizard.updateTask(typeCode, get());
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
