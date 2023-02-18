package org.canvacord.gui;

import org.canvacord.gui.wizard.cards.BackgroundTaskCard;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class VerifyBackgroundTask extends SwingWorker<Boolean, Object> {

    private final BackgroundTaskCard<Boolean> parentCard;
    private final BackgroundTask<Boolean> backgroundTask;
    private final int typeCode;

    public VerifyBackgroundTask(BackgroundTaskCard<Boolean> parentCard, BackgroundTask<Boolean> backgroundTask, int typeCode) {
        this.parentCard = parentCard;
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
            parentCard.updateTask(typeCode, get());
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            parentCard.updateTask(typeCode, false);
        }
    }
}
