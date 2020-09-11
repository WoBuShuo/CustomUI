package com.example.newproject.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class VoiceLineView extends View {

    private Paint mPaint;
    private Random mRandom;
    float lineWidth = dp2px(3);
    float offsetWidth = dp2px(3);
    private int size = 17;
    private int progress = 0;

    private int mNotPlayColor;
    private int mAlreadyPlayColor;


    private ArrayList<Float> mPositionArr = new ArrayList<>();
    private boolean isFirst = true;


    public VoiceLineView(Context context, int size, int notPlayColor, int alreadyPlayColor) {
        this(context, null);
        this.size = size;
        this.mAlreadyPlayColor = alreadyPlayColor;
        this.mNotPlayColor = notPlayColor;
    }

    public VoiceLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public VoiceLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRandom = new Random();
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        float totalWidth = 0;
        if (size <= 10) {
            totalWidth = lineWidth * (size + 3) + offsetWidth * (size + 3 - 1);
        } else if (size > 10 && size <= 16) {
            totalWidth = lineWidth * (size + 2) + offsetWidth * (size + 2 - 1);
        } else if (size > 16 && size <= 19) {
            totalWidth = lineWidth * (size + 1) + offsetWidth * (size + 1 - 1);
        } else if (size > 19 && size <= 23) {
            totalWidth = lineWidth * (size) + offsetWidth * (size - 1);
        } else if (size == 24) {
            totalWidth = lineWidth * (size - 1) + offsetWidth * (size - 1 - 1);
        } else if (size > 24 && size <= 31) {
            totalWidth = lineWidth * (24) + offsetWidth * (24 - 1);
        } else if (size > 31 && size <= 41) {
            totalWidth = lineWidth * (25) + offsetWidth * (25 - 1);
        } else {
            totalWidth = lineWidth * (26) + offsetWidth * (26 - 1);
        }
        setMeasuredDimension((int) totalWidth, dp2px(23));
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mAlreadyPlayColor);
        int width = getWidth();
        float off = lineWidth / 2;
        while (off + lineWidth/2 <= width) {
            //防止每次调用draw方法都添加坐标，只添加一次
            if (!isFirst) {
                break;
            }
            float height = mRandom.nextInt(dp2px(8)) + lineWidth;
            mPositionArr.add(off);
            mPositionArr.add((float) getHeight() - height);
            mPositionArr.add(off);
            mPositionArr.add((float) (height));
            off = off + lineWidth + offsetWidth;
        }
        isFirst = false;

        float[] positions = new float[mPositionArr.size()];
        for (int i = 0; i < mPositionArr.size(); i++) {
            positions[i] = mPositionArr.get(i);
        }
        canvas.drawLines(positions, mPaint);
        //换个颜色，裁剪画布区域，画未播放的语音的颜色
        mPaint.setColor(mNotPlayColor);
        canvas.clipRect(width * progress / 100, 0, width, getHeight());
        canvas.drawLines(positions, mPaint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }


    /**
     * dp转为px
     *
     * @param dp dp值
     * @return px值
     */
    public static int dp2px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


}
