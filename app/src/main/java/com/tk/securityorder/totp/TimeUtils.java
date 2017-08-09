package com.tk.securityorder.totp;


public class TimeUtils {

    protected final static long mStartTime = 0;
    // 30s变换一次密码
    public final static long mTimeStep = 30;


    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long currentTimeSeconds() {
        return currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间所处的值
     *
     * @param time
     * @return
     */
    public static long getValueAtTime(long time) {
        long timeSinceStartTime = time - mStartTime;
        if (timeSinceStartTime >= 0) {
            return timeSinceStartTime / mTimeStep;
        } else {
            return (timeSinceStartTime - (mTimeStep - 1)) / mTimeStep;
        }
    }

    public static long getValueAtTime() {
        return getValueAtTime(currentTimeSeconds());
    }


    public static long getValueStartTime(long value) {
        return mStartTime + (value * mTimeStep);
    }

}
