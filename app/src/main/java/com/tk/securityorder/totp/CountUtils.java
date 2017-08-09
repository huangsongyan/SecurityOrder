package com.tk.securityorder.totp;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CountUtils {

    protected final static long mStartTime = 0;
    // 30s变换一次密码
    public final static long mTimeStep = 30;

    public final static String KEY = "LFLFMU2SGVCUIUCZKBMEKRKLIQ";


    public static final long millisToSeconds(long timeMillis) {
        return timeMillis / 1000;
    }

    /**
     * Gets the number of milliseconds since epoch.
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long getValueAtTime(long time) {
        long timeSinceStartTime = time - mStartTime;
        if (timeSinceStartTime >= 0) {
            return timeSinceStartTime / mTimeStep;
        } else {
            return (timeSinceStartTime - (mTimeStep - 1)) / mTimeStep;
        }
    }

    public static long getValueStartTime(long value) {
        return mStartTime + (value * mTimeStep);
    }

    public static String getTotpNum() {
        try {
            byte[] KEYBYTES = Base32String.decode(KEY);
            Mac mac = Mac.getInstance("HMACSHA1");
            mac.init(new SecretKeySpec(KEYBYTES, ""));
            PasscodeGenerator passcodeGenerator = new PasscodeGenerator(mac);
            String toptnum = passcodeGenerator.generateResponseCode(getValueAtTime(CountUtils
                    .millisToSeconds(currentTimeMillis())));
            return toptnum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
