package com.example.newproject.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.newproject.R;

public class PlayVoiceView extends ConstraintLayout {

    private Context mContext;
    private ImageView mImageView;
    private VoiceLineView lineView;
    private SeekBar seekBar;

    private static int STATE_PLAYING = 0;
    private static int STATE_PAUSE = 1;
    private static int STATE_STOP = 2;


    private static int state = STATE_STOP;
    private boolean mTouchSeekBar = false;


    //未播放的颜色
    private int notPlayColor;
    //已播放的颜色
    private int alreadyPlayColor;


    public PlayVoiceView(Context context) {
        this(context, null);
    }

    public PlayVoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayVoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PlayVoiceView);
            notPlayColor = ta.getColor(R.styleable.PlayVoiceView_not_play_color, Color.BLUE);
            alreadyPlayColor = ta.getColor(R.styleable.PlayVoiceView_already_played_color, Color.RED);
            ta.recycle();
        }
    }


    private void initView(Context context) {
        mImageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(VoiceLineView.dp2px(20), VoiceLineView.dp2px(20));
        layoutParams.topToTop = LayoutParams.PARENT_ID;
        layoutParams.leftToLeft = LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        mImageView.setLayoutParams(layoutParams);
        mImageView.setId(View.generateViewId());
        mImageView.setImageResource(R.mipmap.icon_chat_voice_play);
        addView(mImageView);
    }


    /**
     * @param size     语音时长
     * @param progress 语音播放的进度，默认为0
     */
    public void setSize(int size, int progress) {

        addInitView(size);

        seekBar.setProgress(progress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setProgress(progress);
                if (progress == 100) {
                    //如果是点击拖动seekBar,到了100%，也不走这些回调
                    if (mTouchSeekBar) {
                        return;
                    }
                    completeView();
                    if (listener != null) {
                        listener.onPlayComplete();
                        return;
                    }
                }
                if (listener != null) {
                    listener.onChange(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mTouchSeekBar = true;
                if (listener != null) {
                    listener.onTouchProgress(true);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTouchSeekBar = false;
                if (listener != null) {
                    listener.onTouchProgress(false);
                }
            }
        });


        if (progress > 0) {
            mImageView.setImageResource(R.mipmap.icon_chat_voice_pause);
            seekBar.setVisibility(VISIBLE);
            seekBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setProgress(progress);
                }
            }, 500);
        } else {
            completeView();
        }

    }


    private void addInitView(int size) {
        lineView = new VoiceLineView(mContext, size, notPlayColor, alreadyPlayColor);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftToRight = mImageView.getId();
        layoutParams.topToTop = mImageView.getId();
        layoutParams.leftMargin = 20;
        layoutParams.bottomToBottom = mImageView.getId();
        lineView.setLayoutParams(layoutParams);
        lineView.setId(View.generateViewId());

        addView(lineView);


        TextView textView = new TextView(mContext);

        LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.rightToRight = LayoutParams.PARENT_ID;
        textParams.leftToRight = lineView.getId();
        textParams.topToTop = LayoutParams.PARENT_ID;
        textParams.bottomToBottom = LayoutParams.PARENT_ID;
        textParams.leftMargin = 20;
        textView.setLayoutParams(textParams);
        textView.setText(size + "'");
        addView(textView);


        seekBar = new SeekBar(mContext);
        LayoutParams seekParams = new LayoutParams(0, 0);
        seekParams.topToTop = LayoutParams.PARENT_ID;
        seekParams.bottomToBottom = LayoutParams.PARENT_ID;
        seekParams.leftToLeft = lineView.getId();
        seekParams.rightToRight = lineView.getId();

        seekBar.setLayoutParams(seekParams);
        seekBar.setBackground(null);
        seekBar.setMax(100);
        seekBar.setPadding(0, 0, 0, 0);
        seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_layer));
        seekBar.setThumb(getResources().getDrawable(R.mipmap.icon_line));
        addView(seekBar);
        seekBar.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("---------", "seekBar:onLongClick: ");
                performLongClick();
                return false;
            }
        });


    }


    public void setProgress(int progress) {
        mImageView.setImageResource(R.mipmap.icon_chat_voice_pause);
        lineView.setProgress(progress);
    }

    public void playVoice(int progress) {
        state = STATE_PLAYING;
        seekBar.setVisibility(VISIBLE);
        seekBar.setProgress(progress);
    }

    public void pauseVoice() {
        state = STATE_PAUSE;
    }


    /**
     * 播放完成，回到初始状态
     */
    public void completeView() {
        state = STATE_STOP;
        mImageView.setImageResource(R.mipmap.icon_chat_voice_play);
        seekBar.setVisibility(GONE);
    }


    private static final int SEEK_OFFSET_TTHRESHOLD = 20;
    private static final long LONGCLICK_THRESHOLD = 500;
    private boolean executedLongClick = false;
    private long pointerDownTime = 0;
    int offX = 0;
    int x = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //如果是点击，则拦截事件响应自己的click事件，如果滑动则会向下传递
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointerDownTime = SystemClock.elapsedRealtime();
                x = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                offX = (int) Math.abs(x - ev.getX());
                if (offX <= SEEK_OFFSET_TTHRESHOLD && !executedLongClick &&
                        SystemClock.elapsedRealtime() - pointerDownTime > LONGCLICK_THRESHOLD) {
                    //响应长按事件
                    performLongClick();
                    executedLongClick = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (executedLongClick) {
                    executedLongClick = false;
                    return true;
                }

                if (offX > SEEK_OFFSET_TTHRESHOLD) {
                    return super.onInterceptTouchEvent(ev);
                } else {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }


    private VoiceListener listener;

    private void setOnCompleteListener(VoiceListener listener) {
        this.listener = listener;
    }

    public interface VoiceListener {
        /**
         * 播放完成回调
         */
        void onPlayComplete();

        /**
         * 拖动进度条回调
         */
        void onChange(int progress);


        /**
         * 是否正在拖动进度条
         *
         * @param touch true为是
         */
        void onTouchProgress(boolean touch);
    }


}
