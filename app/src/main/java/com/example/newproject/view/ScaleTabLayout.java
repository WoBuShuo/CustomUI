package com.example.newproject.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.newproject.R;

import java.util.ArrayList;
import java.util.List;
public class ScaleTabLayout extends ConstraintLayout {

    private ImageView mIndicatorView;

    public ScaleTabLayout(Context context) {
        this(context, null);
    }

    public ScaleTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mDefaultColor=Color.RED;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleTabLayout);
            mSelectColor = ta.getColor(R.styleable.ScaleTabLayout_tab_select_text_color, Color.BLUE);
            mDefaultColor = ta.getColor(R.styleable.ScaleTabLayout_tab_unselect_text_color, Color.RED);
            ta.recycle();
        }
    }

    private Context context;
    private ArrayList<TextView> mViewList = new ArrayList<>();
    private List<String> mTitleList;
    private int mLastPosition = 0;
    private int mCurrentPosition = 0;
    private int mLeftMargin = dpToPx(15);
    private int mIndicatorWidth = dpToPx(30);
    private int mTopMargin = dpToPx(10);
    private int mDefaultColor;
    private int mSelectColor;

    private void addIndicator() {
        if (mViewList.size() == 0) {
            return;
        }
        mIndicatorView = new ImageView(context);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                mIndicatorWidth, dpToPx(4));
        layoutParams.topToBottom = mViewList.get(0).getId();
        layoutParams.leftToLeft = mViewList.get(0).getId();
        layoutParams.rightToRight = mViewList.get(0).getId();
        layoutParams.topMargin = mTopMargin;
        mIndicatorView.setLayoutParams(layoutParams);
        mIndicatorView.setBackgroundResource(R.drawable.other_gradient_bg);
        addView(mIndicatorView);

        if (mCurrentPosition == 0) {
            return;
        }
        mIndicatorView.postDelayed(new Runnable() {
            @Override
            public void run() {
                selectIndicator();
            }
        }, 200);


    }

    private void addText(int index,String title) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(context);
        textView.setId(View.generateViewId());
        if (index == 0) {
            layoutParams.leftToLeft = LayoutParams.PARENT_ID;
        } else {
            layoutParams.leftToRight = mViewList.get(mViewList.size() - 1).getId();
        }
        layoutParams.leftMargin = mLeftMargin;
        layoutParams.topToTop = LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        layoutParams.bottomMargin = mTopMargin;
        textView.setLayoutParams(layoutParams);
        textView.setText(title);
        if (index==0){
            textView.setTextColor(mSelectColor);
        }else{
            textView.setTextColor(mDefaultColor);
        }
        textView.setBackgroundResource(0);
        textView.setPadding(mTopMargin, mTopMargin, mTopMargin, -(dpToPx(2)));
        textView.setLineSpacing(-1.0f, 1.0f);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果点击同一个tab
                for (int i = 0; i < mViewList.size(); i++) {
                    if (v.getId() == mViewList.get(i).getId()) {
                        mCurrentPosition = i;
                        if (mLastPosition == i) {
                            return;
                        }
                    }
                }
                selectAnim(mCurrentPosition);
                //记录上一个tab的位置
                for (int i = 0; i < mViewList.size(); i++) {
                    mViewList.get(i).setTextColor(mDefaultColor);
                    if (v.getId() == mViewList.get(i).getId()) {
                        mLastPosition = i;
                        mViewList.get(i).setTextColor(mSelectColor);
                    }
                }
                if (mListener != null) {
                    mListener.onScaleTabSelected(mLastPosition, mCurrentPosition);
                }
            }
        });
        addView(textView);
        mViewList.add(textView);

        if (mViewList.size() - 1 == mCurrentPosition) {
            textView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectBigAnimText(mCurrentPosition);
                }
            }, 200);
        }
    }


    public void setDefaultSelectPosition(int position) {
        if (position >= mTitleList.size()) {
            return;
        }
//        mDefaultPosition = position;
        mCurrentPosition = position;
        mLastPosition = position;
        for (int i = 0; i < mTitleList.size(); i++) {
            addText(i,mTitleList.get(i));
        }
        addIndicator();
    }

    //如果不是通过点击选中tab，而是viewpager那些选的，坐这里
    public void selectPosition(int position) {
        if (position >= mTitleList.size()||position==mCurrentPosition) {
            return;
        }
        mLastPosition = mCurrentPosition;
        mCurrentPosition = position;
        selectAnim(position);
        mLastPosition = position;
        for (int i = 0; i < mViewList.size(); i++) {
            mViewList.get(i).setTextColor(mDefaultColor);
            if (i==mLastPosition){
                mViewList.get(i).setTextColor(mSelectColor);
            }
        }
    }


    private void selectAnim(int position) {
        //这次选中的tab变大的动画
        selectBigAnimText(position);

        //指示器滑动的距离和动画
        selectIndicator();


        //上一次选中的tab变小的动画
        TextView lastText = mViewList.get(mLastPosition);
        AnimatorSet lastAnimatorSet = new AnimatorSet();  //组合动画
        ObjectAnimator lastScaleX = ObjectAnimator.ofFloat(lastText, "scaleX", 1.6f, 1.0f);
        ObjectAnimator lastScaleY = ObjectAnimator.ofFloat(lastText, "scaleY", 1.6f, 1.0f);
        lastAnimatorSet.setDuration(200);  //动画时间
        lastAnimatorSet.setInterpolator(new DecelerateInterpolator());  //设置插值器
        lastAnimatorSet.play(lastScaleX).with(lastScaleY);  //同时执行
        lastAnimatorSet.start();  //启动动画
    }

    private void selectIndicator() {
        int offset = 0;
        for (int i = 1; i < mCurrentPosition; i++) {
            offset += mViewList.get(i).getWidth();
        }
        if (mCurrentPosition==0){
            offset += 0;
        }else{
            offset += Math.abs(mViewList.get(mCurrentPosition).getWidth() - mIndicatorWidth) / 2 +
                    Math.abs(mViewList.get(0).getWidth() - mIndicatorWidth) / 2 +
                    mLeftMargin * mCurrentPosition
                    + mIndicatorWidth;
        }
        ObjectAnimator translation = ObjectAnimator.ofFloat(mIndicatorView, "translationX", offset);
        translation.start();
    }


    private void selectBigAnimText(int position) {
        mViewList.get(position).setPivotX(mViewList.get(position).getWidth() / 2);
        mViewList.get(position).setPivotY(mViewList.get(position).getHeight());
        AnimatorSet animatorSet = new AnimatorSet();  //组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mViewList.get(position), "scaleX", 1.0f, 1.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mViewList.get(position), "scaleY", 1.0f, 1.6f);
        animatorSet.setDuration(200);  //动画时间
        animatorSet.setInterpolator(new DecelerateInterpolator());  //设置插值器
        animatorSet.play(scaleX).with(scaleY);  //同时执行
        animatorSet.start();  //启动动画
    }

    int dpToPx(int dps) {
        return Math.round(getResources().getDisplayMetrics().density * dps);
    }


    public interface OnScaleTabSelectedListener {
        public void onScaleTabSelected(int lastPosition, int currentPosition);
    }

    private OnScaleTabSelectedListener mListener;

    public void addOnScaleTabSelectedListener(OnScaleTabSelectedListener listener) {
        this.mListener = listener;
    }

    public void setTitleList(List<String> titleList) {
        mTitleList = titleList;
    }

}