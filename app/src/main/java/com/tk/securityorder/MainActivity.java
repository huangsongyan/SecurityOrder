package com.tk.securityorder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.jaeger.library.StatusBarUtil;
import com.tk.securityorder.totp.Base32String;
import com.tk.securityorder.totp.CountUtils;
import com.tk.securityorder.totp.PasscodeGenerator;
import com.tk.securityorder.widget.RoundProgressBar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends Activity {

    private float num = 0;

    private int i = 0;

    private RoundProgressBar roundProgressBar;

    private RelativeLayout mainLayout;

    private long repeatTime = 30 * 1000;

    private ValueAnimator valueAnimator;

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTransparent(this);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        roundProgressBar = (RoundProgressBar) findViewById(R.id.round_pb);

        long currentTime = System.currentTimeMillis() / 1000;
        float progress = currentTime % 30 / 30.0f * 100;
        anim(progress);
        roundProgressBar.setTotpNum(getTotpNum());
    }

    private String getTotpNum() {
        try {
            byte[] KEYBYTES = Base32String.decode("LFLFMU2SGVCUIUCZKBMEKRKLIQ");
            Mac mac = Mac.getInstance("HMACSHA1");
            mac.init(new SecretKeySpec(KEYBYTES, ""));
            PasscodeGenerator passcodeGenerator = new PasscodeGenerator(mac);
            String toptnum = passcodeGenerator.generateResponseCode(CountUtils.getValueAtTime(CountUtils
                    .millisToSeconds(CountUtils.currentTimeMillis())));
            return toptnum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void anim(float progress) {
        anim(progress, false);
    }

    protected void anim(float progress, boolean isRepeat) {
        valueAnimator = ValueAnimator.ofFloat(progress, 100f);
        valueAnimator.setDuration((long) ((1 - progress / 100) * repeatTime));
        if (isRepeat) {
            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        } else {
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isFirst) {
                        isFirst = false;
                        anim(0.1f, true);
                        roundProgressBar.setTotpNum(getTotpNum());
                    }

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float money = (float) animation.getAnimatedValue();
                roundProgressBar.setProgress(money);
                if (Math.abs(num - money) > 10) {
                    i++;
                    Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
                    alphaAnimation.setDuration(500);
                    // mainLayout.startAnimation(alphaAnimation);
                    if (i % 2 == 0) {
                        mainLayout.setBackgroundResource(R.mipmap.bg_img_1);
                    } else {
                        mainLayout.setBackgroundResource(R.mipmap.bg_img_2);
                    }
                    roundProgressBar.setTotpNum(getTotpNum());
                }
                num = money;
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (valueAnimator != null) {
//            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            valueAnimator.end();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }
}
