package org.sw.logback;

import ch.qos.logback.core.util.FileSize;

public class SizeAndCronBasedRollingPolicy<E> extends CronBasedRollingPolicy<E> {

    FileSize maxFileSize;

    @Override
    public void start() {
        SizeAndCronBasedFileNamingAndTriggeringPolicyBase<E> sizeAndCronBasedFNATP = new SizeAndCronBasedFileNamingAndTriggeringPolicyBase<E>();
        if (maxFileSize == null) {
            addError("maxFileSize property is mandatory.");
            return;
        } else {
            addInfo("Archive files will be limited to [" + maxFileSize + "] each.");
        }

        sizeAndCronBasedFNATP.setMaxFileSize(maxFileSize);
        setTimeBasedFileNamingAndTriggeringPolicy(sizeAndCronBasedFNATP);

        if (!isUnboundedTotalSizeCap() && totalSizeCap.getSize() < maxFileSize.getSize()) {
            addError("totalSizeCap of [" + totalSizeCap + "] is smaller than maxFileSize [" + maxFileSize
                    + "] which is non-sensical");
            return;
        }

        // most work is done by the parent
        super.start();
    }

    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }


    @Override
    public String toString() {
        return "c.q.l.core.rolling.SizeAndCronBasedRollingPolicy@" + this.hashCode();
    }
}