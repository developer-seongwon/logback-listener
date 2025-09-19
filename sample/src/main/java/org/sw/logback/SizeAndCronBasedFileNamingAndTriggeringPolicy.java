package org.sw.logback;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.LengthCounter;
import ch.qos.logback.core.rolling.LengthCounterBase;
import ch.qos.logback.core.util.FileSize;

import java.time.Duration;

import static ch.qos.logback.core.CoreConstants.MANUAL_URL_PREFIX;

@NoAutoStart
public class SizeAndCronBasedFileNamingAndTriggeringPolicy<E> extends DefaultCronBasedFileNamingAndTriggeringPolicy<E> {

    enum Usage {
        EMBEDDED, DIRECT
    }


    volatile int currentPeriodsCounter = 0;
    FileSize maxFileSize;

    Duration checkIncrement = null;

    static String MISSING_INT_TOKEN = "Missing integer token, that is %i, in FileNamePattern [";
    static String MISSING_DATE_TOKEN = "Missing date token, that is %d, in FileNamePattern [";

    private final Usage usage;
    public LengthCounter lengthCounter = new LengthCounterBase();

    public SizeAndCronBasedFileNamingAndTriggeringPolicy() {
        this(Usage.DIRECT);
    }

    public SizeAndCronBasedFileNamingAndTriggeringPolicy(Usage usage) {
        this.usage = usage;
    }

    @Override
    public void start() {
        // we depend on certain fields having been initialized in super class
        super.start();

        if (usage == Usage.DIRECT) {
            addWarn(CoreConstants.SIZE_AND_TIME_BASED_FNATP_IS_DEPRECATED);
            addWarn(CoreConstants.SIZE_AND_TIME_BASED_FNATP_IS_DEPRECATED_BIS);
            addWarn("For more information see " + MANUAL_URL_PREFIX + "appenders.html#SizeAndTimeBasedRollingPolicy");
        }

        if (!super.isErrorFree())
            return;

        if (maxFileSize == null) {
            addError("maxFileSize property is mandatory.");
            withErrors();
        }

        if (!validateDateAndIntegerTokens()) {
            withErrors();
            return;
        }

//        archiveRemover = createArchiveRemover();
//        archiveRemover.setContext(context);

        // we need to get the correct value of currentPeriodsCounter.
        // usually the value is 0, unless the appender or the application
        // is stopped and restarted within the same period
        String regex = cbrp.fileNamePattern.toRegexForFixedDate(dateInCurrentPeriod);
//        String stemRegex = FileFilterUtil.afterLastSlash(regex);
//
//        computeCurrentPeriodsHighestCounterValue(stemRegex);

        if (isErrorFree()) {
            started = true;
        }
    }

    private boolean validateDateAndIntegerTokens() {
        boolean inError = false;
        if (cbrp.fileNamePattern.getIntegerTokenConverter() == null) {
            inError = true;
            addError(MISSING_INT_TOKEN + cbrp.getFileNamePattern() + "]");
            addError(CoreConstants.SEE_MISSING_INTEGER_TOKEN);
        }
        if (cbrp.fileNamePattern.getPrimaryDateTokenConverter() == null) {
            inError = true;
            addError(MISSING_DATE_TOKEN + tbrp.getFileNamePattern() + "]");
        }

        return !inError;
    }

//    protected ArchiveRemover createArchiveRemover() {
//        return new SizeAndTimeBasedArchiveRemover(tbrp.fileNamePattern, rc);
//    }

    //
//    void computeCurrentPeriodsHighestCounterValue(final String stemRegex) {
//        File file = new File(getCurrentPeriodsFileNameWithoutCompressionSuffix());
//        File parentDir = file.getParentFile();
//
//        File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
//
//        if (matchingFileArray == null || matchingFileArray.length == 0) {
//            currentPeriodsCounter = 0;
//            return;
//        }
//        currentPeriodsCounter = FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
//
//        // if parent raw file property is not null, then the next
//        // counter is max found counter+1
//        if (tbrp.getParentsRawFileProperty() != null || (tbrp.compressionMode != CompressionMode.NONE)) {
//            // TODO test me
//            currentPeriodsCounter++;
//        }
//    }
//
//    @Override
//    public boolean isTriggeringEvent(File activeFile, final E event) {
//
//        long currentTime = getCurrentTime();
//        long localNextCheck = atomicNextCheck.get();
//
//        // first check for roll-over based on time
//        if (currentTime >= localNextCheck) {
//            long nextCheckCandidate = computeNextCheck(currentTime);
//            atomicNextCheck.set(nextCheckCandidate);
//            Instant instantInElapsedPeriod = dateInCurrentPeriod;
//            elapsedPeriodsFileName = tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(
//                    instantInElapsedPeriod, currentPeriodsCounter);
//            currentPeriodsCounter = 0;
//            setDateInCurrentPeriod(currentTime);
//            lengthCounter.reset();
//            return true;
//        }
//
//        boolean result = checkSizeBasedTrigger(activeFile, currentTime);
//        if(result)
//            lengthCounter.reset();
//        return result;
//    }
//
//    private boolean checkSizeBasedTrigger(File activeFile, long currentTime) {
//        // next check for roll-over based on size
//        //if (invocationGate.isTooSoon(currentTime)) {
//        //    return false;
//        //}
//
//        if (activeFile == null) {
//            addWarn("activeFile == null");
//            return false;
//        }
//        if (maxFileSize == null) {
//            addWarn("maxFileSize = null");
//            return false;
//        }
//
//
//
//        if (lengthCounter.getLength() >= maxFileSize.getSize()) {
//
//            elapsedPeriodsFileName = tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(dateInCurrentPeriod,
//                    currentPeriodsCounter);
//            currentPeriodsCounter++;
//
//            return true;
//        }
//
//        return false;
//    }
//
//    public Duration getCheckIncrement() {
//        return null;
//    }
//
//    public void setCheckIncrement(Duration checkIncrement) {
//        addWarn("Since version 1.5.8, 'checkIncrement' property has no effect");
//    }
//
//    @Override
//    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
//        return tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(dateInCurrentPeriod,
//                currentPeriodsCounter);
//    }
//
    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }

//    @Override
//    public LengthCounter getLengthCounter() {
//        return lengthCounter;
//    }
}
