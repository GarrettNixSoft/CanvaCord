package org.canvacord.gui.task;

import org.canvacord.gui.wizard.cards.BackgroundTaskCard;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * The VerifyBackgroundTask is used to verify the ID of some entity,
 * whether on Discord or Canvas. It returns a Boolean value indicating
 * whether the ID in question was verified.
 */
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
