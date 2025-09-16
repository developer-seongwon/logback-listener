package org.sw.logback;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;

import java.io.File;

public class RepairRollingFileAppender<E> extends RollingFileAppender<E> {

    protected boolean repair;

    @Override
    public void start() {
        super.start();
    }

    public void setRepair(boolean repair) {
        this.repair = repair;
    }

    public boolean isRepair() {
        return this.repair;
    }

    @Override
    protected void subAppend(E event) {
        if (isRepair()) {
            repairFile();
        }
        super.subAppend(event);
    }

    public void repairFile() {
        try {
            File activeFile = new File(getFile());
            if (activeFile.exists()) {
                return;
            }
            File parentFile = activeFile.getParentFile();
            if (parentFile != null && parentFile.mkdirs()) {
                addStatus(new InfoStatus("repair parent directory: " + parentFile.getAbsolutePath(), this));
            }
            if (activeFile.createNewFile()) {
                addStatus(new InfoStatus("repair active file: " + activeFile.getAbsolutePath(), this));
            }
        } catch (Exception cause) {
            this.started = false;
            addStatus(new ErrorStatus("IO failure in repair rolling file appender", this, cause));
        }
    }
}
