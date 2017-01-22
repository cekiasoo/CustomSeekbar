package com.ce.customseekbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class CustomSeekBar extends SeekBar {

    private final static int DEFAULT_RECT_LINE_WIDTH=0;
    private final static int DEFAULT_RECT_COLOR=ColorStateList.valueOf(0x66000000).getDefaultColor();
    private final static int DEFAULT_PADDING_RECT=2;
    private final static int DEFAULT_PROGRESS_HEIGHT=30;
    private final static int DEFAULT_PROGRESS_COLOR=ColorStateList.valueOf(0xFF58ccff).getDefaultColor();
    private final static int DEFAULT_SECONDARY_PROGRESS_COLOR=ColorStateList.valueOf(0x22000000).getDefaultColor();

    /**
     * Seek bar的方向
     */
    public enum Orientation {
        HORIZONTAL, VARTICAL
    }
    private Context mContext;
    /**
     * 外边框线的大小
     */
    private int mRectLineWidth=DEFAULT_RECT_LINE_WIDTH;
    /**
     * 外边框线的颜色
     */
    private int mRectColor=DEFAULT_RECT_COLOR;
    /**
     * 里面的进度条与外边框的距离
     */
    private int mPaddingRect=dp2px(DEFAULT_PADDING_RECT);
    /**
     * 进度条的粗细
     */
    private int mProgressStrokeWidth=dp2px(DEFAULT_PROGRESS_HEIGHT);
    /**
     * 进度条的颜色
     */
    private int mProgressColor=DEFAULT_PROGRESS_COLOR;
    /**
     * 缓冲进度条的颜色
     */
    private int mSecondaryProgressColor=DEFAULT_SECONDARY_PROGRESS_COLOR;
    /**
     * Seek bar的方向 默认为水平方向
     */
    private Orientation mOrientation=Orientation.HORIZONTAL;
    /**
     * Seek bar真实的宽度
     */
    private int mRealWidth;
    /**
     * Seek bar真实的高度
     */
    private int mRealHeight;
    /**
     * 画笔
     */
    private Paint mPaint;


    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        parseAttr(attrs, defStyleAttr);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
    }

    /**
     * 解析属性
     *
     * @param attrs        AttributeSet
     * @param defStyleAttr defStyleAttr
     */
    private void parseAttr(AttributeSet attrs, int defStyleAttr) {
        TypedArray _TypedArray=mContext.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar, defStyleAttr, 0);
        int _Count=_TypedArray.getIndexCount();
        for (int i=0; i < _Count; i++) {
            int _Index=_TypedArray.getIndex(i);
            switch (_Index) {
                case R.styleable.CustomSeekBar_rect_line_width:
                    mRectLineWidth=_TypedArray.getDimensionPixelSize(_Index, DEFAULT_RECT_LINE_WIDTH);
                    break;
                case R.styleable.CustomSeekBar_rect_color:
                    mRectColor=_TypedArray.getColor(_Index, DEFAULT_RECT_COLOR);
                    break;
                case R.styleable.CustomSeekBar_padding_rect:
                    mPaddingRect=_TypedArray.getDimensionPixelSize(_Index, dp2px(DEFAULT_PADDING_RECT));
                    break;
                case R.styleable.CustomSeekBar_progress_stroke_width:
                    mProgressStrokeWidth=_TypedArray.getDimensionPixelSize(_Index, dp2px(DEFAULT_PROGRESS_HEIGHT));
                    break;
                case R.styleable.CustomSeekBar_progress_color:
                    mProgressColor=_TypedArray.getColor(_Index, DEFAULT_PROGRESS_COLOR);
                    break;
                case R.styleable.CustomSeekBar_secondary_progress_color:
                    mSecondaryProgressColor=_TypedArray.getColor(_Index, DEFAULT_SECONDARY_PROGRESS_COLOR);
                    break;
                case R.styleable.CustomSeekBar_orientation:
                    int _Orientation=_TypedArray.getInt(_Index, 0);
                    if (_Orientation == 0) {
                        mOrientation=Orientation.HORIZONTAL;
                    } else {
                        mOrientation=Orientation.VARTICAL;
                    }
                    break;
            }
        }
        _TypedArray.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == Orientation.HORIZONTAL) {
            int _HeightMode=MeasureSpec.getMode(heightMeasureSpec);
            if (_HeightMode != MeasureSpec.EXACTLY) {
                //进度条的高度 + 上下两边与外边框的距离 + 上下外边框的线的大小 + 上下padding
                int _Height=mProgressStrokeWidth + (mPaddingRect * 2) + (mRectLineWidth * 2)
                        + getPaddingTop() + getPaddingBottom();
                heightMeasureSpec=MeasureSpec.makeMeasureSpec(_Height, MeasureSpec.EXACTLY);
            }
        } else {
            int _WidthMode=MeasureSpec.getMode(widthMeasureSpec);
            if (_WidthMode != MeasureSpec.EXACTLY) {
                //进度条的宽度 + 左右两边与外边框的距离 + 左右外边框的线的大小 + 左右padding
                int _Width=mProgressStrokeWidth + (mPaddingRect * 2) + (mRectLineWidth * 2)
                        + getPaddingLeft() + getPaddingRight();
                widthMeasureSpec=MeasureSpec.makeMeasureSpec(_Width, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (mOrientation == Orientation.HORIZONTAL) {
            drawHorizontalRect(canvas);
            drawHorizontalProgress(canvas);
        } else {
            drawVertacalRect(canvas);
            drawVertacalProgress(canvas);
        }
    }

    /**
     * 画纵向的进度条
     * @param canvas Canvas
     */
    private void drawVertacalProgress(Canvas canvas) {
        if (mProgressStrokeWidth <= 0) return;
        canvas.save();
        //把坐标原点移到进度条底部的中间
        canvas.translate(getWidth() / 2.0f, getHeight() - getPaddingBottom() - mRectLineWidth - mPaddingRect);

        //缓冲进度条所占总进度的百分比
        float _SecondaryPercentage=(float) getSecondaryProgress() / getMax();
        //进度条的总高度
        float _TotalProgressWidth=mRealHeight - (2 * mRectLineWidth + 2 * mPaddingRect);
        //缓冲进度条的高度
        float _SecondaryReachWidth=_TotalProgressWidth * _SecondaryPercentage;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSecondaryProgressColor);
        mPaint.setStrokeWidth(mProgressStrokeWidth);
        canvas.drawLine(0, 0, 0, -_SecondaryReachWidth, mPaint);

        //进度条所占总进度的百分比
        float _Percentage=(float) getProgress() / getMax();
        //进度条的高度
        float _ReachWidth=_TotalProgressWidth * _Percentage;
        mPaint.setColor(mProgressColor);
        canvas.drawLine(0, 0, 0, -_ReachWidth, mPaint);

        canvas.restore();
    }

    /**
     * 画纵向的外边框
     * @param canvas Canvas
     */
    private void drawVertacalRect(Canvas canvas) {
        if (mRectLineWidth <= 0) return;
        canvas.save();
        //将坐标原点移到SeekBar的底部中间
        canvas.translate(getWidth() / 2.0f, getHeight() - getPaddingBottom() - (mRectLineWidth / 2.0f));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRectLineWidth);
        mPaint.setColor(mRectColor);
        //根据坐标原点计算左上右下的位置
        float _Left=-(mRealWidth / 2.0f - (mRectLineWidth / 2.0f));
        float _Top=-(mRealHeight - (mRectLineWidth));
        float _Right=mRealWidth / 2.0f - (mRectLineWidth / 2.0f);
        float _Bottom=0;
        canvas.drawRect(_Left, _Top, _Right, _Bottom, mPaint);
        canvas.restore();
    }

    /**
     * 画横向进度条
     *
     * @param canvas Canvas
     */
    private void drawHorizontalProgress(Canvas canvas) {
        if (mProgressStrokeWidth <= 0) return;
        canvas.save();
        //将坐标原点移到进度条左边中间的位置
        canvas.translate(getPaddingLeft() + mRectLineWidth + mPaddingRect, getHeight() / 2);

        //缓冲进度条所占总进度的百分比
        float _SecondaryPercentage=(float) getSecondaryProgress() / getMax();
        //进度条的总宽度
        float _TotalProgressWidth=mRealWidth - (2 * mRectLineWidth + 2 * mPaddingRect);
        //缓冲进度条的宽度
        float _SecondaryReachWidth=_TotalProgressWidth * _SecondaryPercentage;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSecondaryProgressColor);
        mPaint.setStrokeWidth(mProgressStrokeWidth);
        canvas.drawLine(0, 0, _SecondaryReachWidth, 0, mPaint);

        //进度条所占总进度的百分比
        float _Percentage=(float) getProgress() / getMax();
        //进度条的宽度
        float _ReachWidth=_TotalProgressWidth * _Percentage;
        mPaint.setColor(mProgressColor);
        canvas.drawLine(0, 0, _ReachWidth, 0, mPaint);

        canvas.restore();
    }

    /**
     * 画横向的外面的边框
     * @param canvas Canvas
     */
    private void drawHorizontalRect(Canvas canvas) {
        if (mRectLineWidth <= 0) return;
        canvas.save();
        //把坐标原点移到进度条左边的垂直中间位置
        canvas.translate(getPaddingLeft(), getHeight() / 2.0f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRectLineWidth);
        mPaint.setColor(mRectColor);
        //根据坐标原点计算左上右下的位置
        float _Left=mRectLineWidth / 2.0f;
        float _Top=-(mRealHeight / 2.0f - (mRectLineWidth / 2.0f));
        float _Right=mRealWidth - (mRectLineWidth / 2.0f);
        float _Bottom=mRealHeight / 2.0f - (mRectLineWidth / 2.0f);
        canvas.drawRect(_Left, _Top, _Right, _Bottom, mPaint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //宽度 - 左右两边padding
        mRealWidth=w - getPaddingLeft() - getPaddingRight();
        //高度 - 上下两边padding
        mRealHeight=h - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果是纵向的就把触摸事件拦截下来，如果是横向的就用系统的
        if (mOrientation == Orientation.VARTICAL) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    int _Progress=0;
                    //总大小 - 触摸时的高度与总高度比例换算成所占进度条的进度
                    _Progress=getMax() - (int) (getMax() * (event.getY() / getHeight()));
                    setProgress(_Progress);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private int dp2px(int pDpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pDpVal,
                getResources().getDisplayMetrics());
    }

}
