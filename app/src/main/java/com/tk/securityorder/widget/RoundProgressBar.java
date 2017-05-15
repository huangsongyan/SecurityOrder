package com.tk.securityorder.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hsy on 2017/5/12.
 */

public class RoundProgressBar extends View {

    /**
     * 画笔对象的引用
     */
    private Paint paint;


    /**
     * 画笔对象的引用
     */
    private Paint rb_paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环的宽度
     */
    private float roundWidth;


    /**
     * 最大进度
     */
    private int max = 100;


    /**
     * 当前进度
     */
    private float progress;

    private String totpNum;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();

        rb_paint = new Paint();

        roundColor = Color.parseColor("#000000");

        roundWidth = 20;
//        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
//                R.styleable.RoundProgressBar);
//
//        //获取自定义属性和默认值
//        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.rgb(155, 193, 238));
//        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.rgb(49, 220, 178));
//        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.rgb(255, 255, 255));
//        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
//        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
//        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
//        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
//        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
//
//        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centre = getWidth() / 2; //获取圆心的x坐标

        /**
         * 画底图
         */
        paint.setColor(Color.parseColor("#80FFFFFF"));
        paint.setStyle(Paint.Style.FILL); //设置实心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿

        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawCircle(centre, centre, centre - 60, paint); //画出圆环


        /**
         * 画最外层的大圆环
         */

        int radius = (int) (centre - roundWidth / 2 - 75); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿

        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环


        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        rb_paint.setStrokeWidth(roundWidth + 5); //设置圆环的宽度
        rb_paint.setColor(Color.GREEN);  //设置进度的颜色
        rb_paint.setAntiAlias(true);
        rb_paint.setStrokeJoin(Paint.Join.ROUND);
        rb_paint.setStrokeCap(Paint.Cap.ROUND);
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        rb_paint.setStyle(Paint.Style.STROKE);

        canvas.drawArc(oval, -90, 360 * progress / max, false, rb_paint);  //根据进度画圆弧     从顶部开始画


        /**
         * 画进度百分比
         */
        paint.setStrokeWidth(0);
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        float size = (radius - roundWidth/2)*2/5;
        paint.setTextSize(size);
//        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        float textWidth = paint.measureText(totpNum+"");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

//        if (textIsDisplayable && percent != 0 && style == STROKE) {
            canvas.drawText(totpNum, centre - textWidth / 2, centre + size / 2, paint); //画出进度百分比
//        }
        //画字体

//        if(progress == 100){
//            Paint paint = new Paint();
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            canvas.drawPaint(paint);
//        }
    }


    public void setTotpNum(String totpNum){
        this.totpNum = totpNum;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(float progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    public void clear() {
        rb_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //计算最小宽度
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int circleWidth;
        if (width >= height) {
            circleWidth = height;
        } else {

            circleWidth = width;
        }

        setMeasuredDimension(circleWidth, circleWidth);
    }
}
