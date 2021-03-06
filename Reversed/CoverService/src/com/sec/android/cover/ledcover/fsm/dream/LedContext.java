package com.sec.android.cover.ledcover.fsm.dream;

import android.os.SystemClock;
import android.text.TextUtils;
import com.sec.android.cover.ledcover.GraceLEDCoverCMD;
import com.sec.android.cover.ledcover.fsm.dream.LedStatePriorityQueue.QueueType;
import com.sec.android.cover.ledcover.fsm.dream.missedevent.LedCoverMissedEventManager;
import com.sec.android.cover.ledcover.fsm.dream.missedevent.MissedEvent;
import com.sec.android.cover.monitor.CoverUpdateMonitor.BatteryStatus;
import java.util.Iterator;

public class LedContext {
    public static final long CALL_START_TIME_UNKNOWN = -1;
    private BatteryStatus mBatteryStatus = new BatteryStatus(null);
    private int mBixbyState = -1;
    private String mCallDuration = null;
    private long mCallStartTime = -1;
    private int mCallState = 0;
    private CallerData mCallerData = new CallerData(0, null, null, false, false);
    private LedStatePriorityQueue mCoverCloseLedStatePriorityQueue = new LedStatePriorityQueue(QueueType.COVER_CLOSE);
    private LedStatePriorityQueue mDelayedLedStatePriorityQueue = new LedStatePriorityQueue(QueueType.DELAYED);
    private boolean mDoNotDisturbNotiOffPolicy;
    private GraceLEDCoverCMD mGraceLEDCoverCMD = new GraceLEDCoverCMD();
    private boolean mHeadsetPlugged;
    private boolean mIs24HourFormat;
    private boolean mIsAlarm;
    private boolean mIsCalendar;
    private boolean mIsCoverClosedWakeUp;
    private boolean mIsInCallTouchReject;
    private boolean mIsPowerButtonWakeUp;
    private boolean mIsTimer;
    private LedStatePriorityQueue mLedStatePriorityQueue = new LedStatePriorityQueue(QueueType.MAIN);
    private MissedEvent mMissedCallEvent;
    private LedCoverMissedEventManager mMissedEvents = new LedCoverMissedEventManager();
    private MusicState mMusicState;
    private LedStatePriorityQueue mPowerButtonLedStatePriorityQueue = new LedStatePriorityQueue(QueueType.POWER_BUTTON);
    private LedState mPrevLedState;
    private boolean mUPSMEnabled;
    private boolean mVideoCall;
    private long mVoiceRecorderRecordingStartTime;
    private VoiceRecorderState mVoiceRecorderState;
    private int mVolumeLevel;

    public static class BixbyState {
        public static final int IDLE = 0;
        public static final int LISTENING = 1;
        public static final int PROCESSING = 3;
        public static final int RESPONSE = 4;
        public static final int STOPPED = -1;
        public static final int USER_SPEAKING = 2;
    }

    public static final class CallerData {
        public static final int CALLER_ID_UNKNOWN = 0;
        private final int callerId;
        private final boolean emergencyModeIcon;
        private final byte[] iconData;
        private final boolean knownNumber;
        private final String number;

        public CallerData(int callerId, String number, byte[] iconData, boolean knownNumber, boolean emergencyModeIcon) {
            this.callerId = callerId;
            this.number = number;
            this.iconData = iconData;
            this.knownNumber = knownNumber;
            this.emergencyModeIcon = emergencyModeIcon;
        }

        public int getCallerID() {
            return this.callerId;
        }

        public String getNumber() {
            return this.number;
        }

        public byte[] getIconData() {
            return this.iconData;
        }

        public boolean isKnownNumber() {
            return this.knownNumber;
        }

        public boolean isEmergencyModeIcon() {
            return this.emergencyModeIcon;
        }

        public boolean isDefault() {
            return this.callerId == 0 && this.iconData == null && this.number == null;
        }

        public boolean isRestricted() {
            return TextUtils.isEmpty(this.number);
        }

        public String toString() {
            return "callerId=" + String.valueOf(this.callerId) + " number=" + (this.number != null ? this.number.replaceAll("[0-9]", "*") : this.number) + " " + "iconData=" + this.iconData + " knownNumber=" + this.knownNumber + " " + "emergencyModeIcon=" + this.emergencyModeIcon;
        }
    }

    public enum MusicState {
        STOP,
        PLAY,
        PAUSE
    }

    public enum VoiceRecorderState {
        STOP,
        PLAY,
        RECORD
    }

    public LedContext() {
        this.mLedStatePriorityQueue.add(LedState.CLOCK);
    }

    public GraceLEDCoverCMD getGraceLEDCoverCMD() {
        return this.mGraceLEDCoverCMD;
    }

    public void addState(LedState state) {
        this.mLedStatePriorityQueue.add(state);
    }

    public void removeState(LedState state) {
        this.mLedStatePriorityQueue.remove(state);
        this.mDelayedLedStatePriorityQueue.remove(state);
        this.mPowerButtonLedStatePriorityQueue.remove(state);
        this.mCoverCloseLedStatePriorityQueue.remove(state);
    }

    public LedState pollState() {
        return (LedState) this.mLedStatePriorityQueue.poll();
    }

    public boolean isEmpty() {
        return this.mLedStatePriorityQueue.isEmpty();
    }

    public boolean hasState(LedState state) {
        return this.mLedStatePriorityQueue.contains(state);
    }

    public void addState(QueueType type, LedState state) {
        switch (type) {
            case DELAYED:
                this.mDelayedLedStatePriorityQueue.add(state);
                return;
            case POWER_BUTTON:
                this.mPowerButtonLedStatePriorityQueue.add(state);
                return;
            case COVER_CLOSE:
                this.mCoverCloseLedStatePriorityQueue.add(state);
                return;
            default:
                this.mLedStatePriorityQueue.add(state);
                return;
        }
    }

    public void removeState(QueueType type, LedState state) {
        switch (type) {
            case DELAYED:
                this.mDelayedLedStatePriorityQueue.remove(state);
                return;
            case POWER_BUTTON:
                this.mPowerButtonLedStatePriorityQueue.remove(state);
                return;
            case COVER_CLOSE:
                this.mCoverCloseLedStatePriorityQueue.remove(state);
                return;
            default:
                this.mLedStatePriorityQueue.remove(state);
                return;
        }
    }

    public LedState pollState(QueueType type) {
        switch (type) {
            case DELAYED:
                return (LedState) this.mDelayedLedStatePriorityQueue.poll();
            case POWER_BUTTON:
                return (LedState) this.mPowerButtonLedStatePriorityQueue.poll();
            case COVER_CLOSE:
                return (LedState) this.mCoverCloseLedStatePriorityQueue.poll();
            default:
                return (LedState) this.mLedStatePriorityQueue.poll();
        }
    }

    public LedState peekState(QueueType type) {
        switch (type) {
            case DELAYED:
                return (LedState) this.mDelayedLedStatePriorityQueue.peek();
            case POWER_BUTTON:
                return (LedState) this.mPowerButtonLedStatePriorityQueue.peek();
            case COVER_CLOSE:
                return (LedState) this.mCoverCloseLedStatePriorityQueue.peek();
            default:
                return (LedState) this.mLedStatePriorityQueue.peek();
        }
    }

    public boolean isEmpty(QueueType type) {
        switch (type) {
            case DELAYED:
                return this.mDelayedLedStatePriorityQueue.isEmpty();
            case POWER_BUTTON:
                return this.mPowerButtonLedStatePriorityQueue.isEmpty();
            case COVER_CLOSE:
                return this.mCoverCloseLedStatePriorityQueue.isEmpty();
            default:
                return this.mLedStatePriorityQueue.isEmpty();
        }
    }

    public boolean hasState(QueueType type, LedState state) {
        switch (type) {
            case DELAYED:
                return this.mDelayedLedStatePriorityQueue.contains(state);
            case POWER_BUTTON:
                return this.mPowerButtonLedStatePriorityQueue.contains(state);
            case COVER_CLOSE:
                return this.mCoverCloseLedStatePriorityQueue.contains(state);
            default:
                return this.mLedStatePriorityQueue.contains(state);
        }
    }

    public void clearQueue(QueueType type) {
        switch (type) {
            case DELAYED:
                this.mDelayedLedStatePriorityQueue.clear();
                return;
            case POWER_BUTTON:
                this.mPowerButtonLedStatePriorityQueue.clear();
                return;
            case COVER_CLOSE:
                this.mCoverCloseLedStatePriorityQueue.clear();
                return;
            default:
                return;
        }
    }

    public void resetAndFillQueue(QueueType type) {
        Iterator it;
        LedState state;
        switch (type) {
            case POWER_BUTTON:
                this.mPowerButtonLedStatePriorityQueue.clear();
                it = this.mLedStatePriorityQueue.iterator();
                while (it.hasNext()) {
                    state = (LedState) it.next();
                    if (state.includeInQueue(QueueType.POWER_BUTTON, this)) {
                        this.mPowerButtonLedStatePriorityQueue.add(state);
                    }
                }
                return;
            case COVER_CLOSE:
                this.mCoverCloseLedStatePriorityQueue.clear();
                it = this.mLedStatePriorityQueue.iterator();
                while (it.hasNext()) {
                    state = (LedState) it.next();
                    if (state.includeInQueue(QueueType.COVER_CLOSE, this)) {
                        this.mCoverCloseLedStatePriorityQueue.add(state);
                    }
                }
                return;
            default:
                return;
        }
    }

    public int getVolumeLevel() {
        return this.mVolumeLevel;
    }

    public void setVolumeLevel(int volumeLevel) {
        this.mVolumeLevel = volumeLevel;
    }

    public boolean isHeadsetPlugged() {
        return this.mHeadsetPlugged;
    }

    public void setHeadsetPlugged(boolean headsetPlugged) {
        this.mHeadsetPlugged = headsetPlugged;
    }

    public BatteryStatus getBatteryStatus() {
        return this.mBatteryStatus;
    }

    public void setBatteryStatus(BatteryStatus batteryStatus) {
        if (batteryStatus == null) {
            throw new IllegalArgumentException();
        }
        this.mBatteryStatus = batteryStatus;
    }

    public MusicState getMusicState() {
        return this.mMusicState;
    }

    public void setMusicState(MusicState musicState) {
        if (musicState == null) {
            throw new IllegalArgumentException();
        }
        this.mMusicState = musicState;
    }

    public VoiceRecorderState getVoiceRecorderState() {
        return this.mVoiceRecorderState;
    }

    public void setVoiceRecorderState(VoiceRecorderState voiceRecorderState) {
        this.mVoiceRecorderState = voiceRecorderState;
    }

    public long getCallStartTime() {
        return this.mCallStartTime;
    }

    public void setCallStartTime(long callStartTime) {
        this.mCallStartTime = callStartTime;
    }

    public String getCallDuration() {
        return this.mCallDuration;
    }

    public void setCallDuration(String callDuration) {
        this.mCallDuration = callDuration;
    }

    public boolean isNoCallStateDisplayed() {
        return (this.mLedStatePriorityQueue.contains(LedState.INCOMING_CALL) || this.mLedStatePriorityQueue.contains(LedState.INCOMING_VIDEO_CALL) || this.mLedStatePriorityQueue.contains(LedState.DURING_CALL) || this.mLedStatePriorityQueue.contains(LedState.END_CALL)) ? false : true;
    }

    public int getCallState() {
        return this.mCallState;
    }

    public void setCallState(int callState) {
        this.mCallState = callState;
    }

    public CallerData getCallerData() {
        return this.mCallerData;
    }

    public void setCallerData(CallerData callerData) {
        this.mCallerData = callerData;
    }

    public boolean isVideoCall() {
        return this.mVideoCall;
    }

    public void setVideoCall(boolean videoCall) {
        this.mVideoCall = videoCall;
    }

    public boolean is24HourFormat() {
        return this.mIs24HourFormat;
    }

    public void set24HourFormat(boolean is24HourFormat) {
        this.mIs24HourFormat = is24HourFormat;
    }

    public boolean isAlarm() {
        return this.mIsAlarm;
    }

    public void setAlarm(boolean isAlarm) {
        this.mIsAlarm = isAlarm;
    }

    public boolean isTimer() {
        return this.mIsTimer;
    }

    public void setTimer(boolean isTimer) {
        this.mIsTimer = isTimer;
    }

    public boolean isCalendar() {
        return this.mIsCalendar;
    }

    public void setCalendar(boolean isCalendar) {
        this.mIsCalendar = isCalendar;
    }

    public void setMissedCallEvent(MissedEvent missedEvent) {
        this.mMissedCallEvent = missedEvent;
    }

    public MissedEvent getMissedCallEvent() {
        return this.mMissedCallEvent;
    }

    public LedCoverMissedEventManager getMissedEvents() {
        return this.mMissedEvents;
    }

    public boolean isPowerButtonWakeUp() {
        return this.mIsPowerButtonWakeUp;
    }

    public void setIsPowerButtonWakeUp(boolean isPowerButtonWakeUp) {
        this.mIsPowerButtonWakeUp = isPowerButtonWakeUp;
    }

    public boolean isCoverClosedWakeUp() {
        return this.mIsCoverClosedWakeUp;
    }

    public void setIsCoverClosedWakeUp(boolean isCoverCloseWakeUp) {
        this.mIsCoverClosedWakeUp = isCoverCloseWakeUp;
    }

    public boolean isInCallTouchReject() {
        return this.mIsInCallTouchReject;
    }

    public void setIsInCallTouchReject(boolean inInCallTouchReject) {
        this.mIsInCallTouchReject = inInCallTouchReject;
    }

    public boolean isUPSMEnabled() {
        return this.mUPSMEnabled;
    }

    public void setUPSMEnabled(boolean mUPSMEnabled) {
        this.mUPSMEnabled = mUPSMEnabled;
    }

    public long getVoiceRecorderRecordingStartTime() {
        return this.mVoiceRecorderRecordingStartTime;
    }

    public void setVoiceRecorderRecordingStartTime(long voiceRecorderRecordingStartTime) {
        this.mVoiceRecorderRecordingStartTime = voiceRecorderRecordingStartTime;
    }

    public LedState getPrevLedState() {
        return this.mPrevLedState;
    }

    public void setPrevLedState(LedState mPrevLedState) {
        this.mPrevLedState = mPrevLedState;
    }

    public long getVoiceRecorderRecordedTime() {
        return SystemClock.elapsedRealtime() - this.mVoiceRecorderRecordingStartTime;
    }

    public boolean shouldWakeupForLedLamp() {
        Iterator it = this.mLedStatePriorityQueue.iterator();
        while (it.hasNext()) {
            if (!((LedState) it.next()).shouldWakeupForLedLamp()) {
                return false;
            }
        }
        return true;
    }

    public int getBixbyState() {
        return this.mBixbyState;
    }

    public void setBixbyState(int bixbyState) {
        this.mBixbyState = bixbyState;
    }

    public boolean isDoNotDisturbNotiOffPolicy() {
        return this.mDoNotDisturbNotiOffPolicy;
    }

    public void setDoNotDisturbNotiOffPolicy(boolean doNotDisturbNotiOffPolicy) {
        this.mDoNotDisturbNotiOffPolicy = doNotDisturbNotiOffPolicy;
    }
}
