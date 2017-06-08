package com.ezuikit.open.timeshaftbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 项目名称：EZUIKitDemo
 * 类描述：回放时间轴
 * 创建人：dingwei3
 * 创建时间：2017/4/12 14:03
 * 修改人：dingwei3
 * 修改时间：2017/4/12 14:03
 * 修改备注：
 */
public class TimerShaftBar extends View implements TimerShaftInterface{
    private static final String TAG = "TimerShaftBar";

    private Context mContext;

    /**
     * 手势事件处理
     */
    private GestureDetector mGestureDetector;
    /**
     * 缩放手势事件处理
     */
    private ScaleGestureDetector mScaleGestureDetector;

    /**
     * Touch默认模式
     */
    private final static int TOUCH_MODE_NONE = 0;

    /**
     * 滑动事件
     */
    private final static int TOUCH_MODE_SCROLLER = 1;

    /**
     * 缩放事件
     */
    private final static int TOUCH_MODE_SCALE = 2;
    /**
     * 滑动FlING事件
     */
    private final static int TOUCH_MODE_SCROLLER_FlING = 3;
    /**
     * 手势事件模式
     */
    private int mTouchMode = TOUCH_MODE_NONE;


    /**
     * 滑动处理
     */
    private LayoutController mLayoutController;
    /**
     *  是否触摸屏幕
     */
    private boolean isTouchScreent = false;
    /**
     * 组件屏幕上现实的宽度
     */
    private int mWidth;
    /**
     * 组件屏幕上现实的高度
     */
    private int mHeight;
    /**
     *
     */
    private TimebarTick mTimebarTick;

    /**
     * 绘制时间轴起始时间
     */
    private Calendar mStartCalendar;
    /**
     * 绘制时间轴结束时间
     */
    private Calendar mEndCalendar;

    /**
     * 绘制时间轴总时长（秒）
     */
    private long TOTOAL_TIME = 24 * 60 * 60L;
    /**
     * 时间刻度画笔
     */
    private Paint mTextPaint = new Paint();

    /**
     * 当前时间刻度画笔
     */
    private Paint mCurrentTimeLinePaint = new Paint();
    /**
     * 时间区域画笔
     */
    private Paint mTimeRegionPaint = new Paint();

    /**
     * 时间轴区域背景画笔
     */
    private Paint mBackParint = new Paint();
    /**
     * 当前时间画笔
     */
    private Paint mCurrentTimePaint = new Paint();
    /**
     * 时间轴与上边距间距
     */
    private int mTop;

    /**
     * 播放时间显示区域高度
     */
    private int mCurrentTimeTextHeight;

    private Paint.FontMetrics mFontMetrics;

    private ArrayList<TimebarTick> mTimebarTicks = new ArrayList<TimebarTick>();
    private ArrayList<TimerShaftRegionItem> mTimeShaftItems;

    /**
     * 对比起始位置的时间（秒）
     */
    private int mCurrentSecond;

    /**
     * 组件当前位置
     */
    private int mCurrentPosition;

    /**
     * 单位距离（每秒）
     */
    private float mPixelsPerSecond = 0;

    /**
     * 当前屏幕位置的起始秒数
     */
    private long mStartSecondOnScreen;
    /**
     * 当前屏幕位置的结束秒数
     */
    private long mEndSecondOnScreen;

    private TimerShaftBarListener mTimerShaftBarListener;

    /**
     * 大刻度绘线绘制高度
     */
    private int mMaxScaleHeight;

    /**
     * 中刻度绘线绘制高度
     */
    private int mMidScaleHeight;

    /**
     * 小刻度绘线绘制高度
     */
    private int mMinScaleHeight;

    /**
     * 刻度显示字体大小
     */
    private int mScaleTextSize = 10;

    /**
     * 当前时间显示字体大小
     */
    private int mCurrentScaleTextSize = 10;

    /**
     * 刻度显示字体高度
     */
    private int mScaleTextHeight;

    /**
     * 绘制包含时间区域的颜色
     */
    private int mRegionCloudBackColor = 0x8000FF00;

    /**
     * 绘制包含时间区域的颜色
     */
    private int mRegionLocalBackColor = 0x80F37F4C;


    /**
     * 刻度已经时间显示字体颜色
     */
    private int mTextColor = 0xFF888888;

    /**
     * 绘制时间轴区域的颜色
     */
    private int mBackColor = 0xFFF5F5F5;

    /**
     * 绘制当前时间轴区域的颜色
     */
    private int mCurrentTimeLineColor = Color.RED;

    /**
     * 当前时间calendar
     */
    private Calendar mPositonCalendar;

    /**
     * 更具播放器进度显示当前位置
     */
    private boolean  isRefereshPlayTimeWithPlayer = false;


    /**
     * 时间轴事件监听
     */
    public interface TimerShaftBarListener {
        /**
         * 当TimerShaftBar滑动时的通知
         *
         * @param calendar 当前calendar
         */
        void onTimerShaftBarPosChanged(Calendar calendar);

        /**
         * 时间轴手指按下时触发
         */
        void onTimerShaftBarDown();
    }



    public TimerShaftBar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TimerShaftBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * 设置时间轴事件监听
     * @param timerShaftBarListener
     */
    @Override
    public void setTimerShaftLayoutListener(TimerShaftBarListener timerShaftBarListener) {
        mTimerShaftBarListener = timerShaftBarListener;
    }

    /**
     * 设置绘制区域
     * @param timeShaftItems 绘制区域item
     */
    public void setTimeShaftItems(ArrayList<TimerShaftRegionItem> timeShaftItems) {
        mTimeShaftItems = timeShaftItems;
        if (mTimeShaftItems != null && mTimeShaftItems.size() > 0) {
            Calendar calendar = mTimeShaftItems.get(0).getStartCalendar();
            mStartCalendar = (Calendar) calendar.clone();

            calendar = mTimeShaftItems.get(mTimeShaftItems.size() - 1).getEndCalendar();
            mEndCalendar = (Calendar) calendar.clone();
            mEndCalendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
            initCalendar();
        }
        initTimebarTicks();
    }

    /**
     * 设置时间轴是否根据接受外部播放进度,当播放成功是需要调用设置用来刷新播放时间
     */
    @Override
    public void setRefereshPlayTimeWithPlayer() {
        isRefereshPlayTimeWithPlayer = true;
    }

    /**
     * 设置播放当前时间
     * @param calendar
     */
    @Override
    public void setPlayCalendar(Calendar calendar) {
        Log.d(TAG, calendar.getTime().toString());
        if (!mLayoutController.computeScrollOffset() && !isTouchScreent && isRefereshPlayTimeWithPlayer) {
            mPositonCalendar = (Calendar) calendar.clone();
            mCurrentSecond = (int) ((mPositonCalendar.getTimeInMillis() - mStartCalendar.getTimeInMillis())/1000);
            mLayoutController.setPosition((int) (mCurrentSecond * mPixelsPerSecond));
            invalidate();
        }
    }

    /**
     * 初始化时间轴显示的等级
     */
    private void initTimebarTicks() {
        if (mTimebarTicks.size()<=0) {
            TimebarTick timebarTick1 = new TimebarTick();
            timebarTick1.setTotalSecondsInOneScreen(24 * 60 * 60);
            timebarTick1.setMaxTickInSecond(4 * 60 * 60);
            timebarTick1.setMinTickInSecond(60 * 60);
            timebarTick1.setDataPattern("HH:mm");
            timebarTick1.setStandardViewLength(((long) ((double) mWidth * TOTOAL_TIME / (double) timebarTick1.getTotalSecondsInOneScreen())));

            TimebarTick timebarTick2 = new TimebarTick();
            timebarTick2.setTotalSecondsInOneScreen(18 * 60 * 60);
            timebarTick2.setMaxTickInSecond(2 * 60 * 60);
            timebarTick2.setMinTickInSecond(30 * 60);
            timebarTick2.setDataPattern("HH:mm");
            timebarTick2.setStandardViewLength(((long) ((double) mWidth * TOTOAL_TIME / (double) timebarTick2.getTotalSecondsInOneScreen())));

            TimebarTick timebarTick3 = new TimebarTick();
            timebarTick3.setTotalSecondsInOneScreen(8 * 60 * 60);
            timebarTick3.setMaxTickInSecond(60 * 60);
            timebarTick3.setMinTickInSecond(12 * 60);
            timebarTick3.setDataPattern("HH:mm");
            timebarTick3.setStandardViewLength(((long) ((double) mWidth * TOTOAL_TIME / (double) timebarTick3.getTotalSecondsInOneScreen())));

            TimebarTick timebarTick4 = new TimebarTick();
            timebarTick4.setTotalSecondsInOneScreen(3 * 30 * 60);
            timebarTick4.setMaxTickInSecond(10 * 60);
            timebarTick4.setMinTickInSecond(2 * 60);
            timebarTick4.setDataPattern("HH:mm");
            timebarTick4.setStandardViewLength(((long) ((double) mWidth * TOTOAL_TIME / (double) timebarTick4.getTotalSecondsInOneScreen())));

            TimebarTick timebarTick5 = new TimebarTick();
            timebarTick5.setTotalSecondsInOneScreen(30 * 60);
            timebarTick5.setMaxTickInSecond(10 * 60);
            timebarTick5.setMinTickInSecond(2 * 60);
            timebarTick5.setDataPattern("HH:mm");
            timebarTick5.setStandardViewLength(((long) ((double) mWidth * TOTOAL_TIME / (double) timebarTick5.getTotalSecondsInOneScreen())));

            mTimebarTicks.add(timebarTick5);
            mTimebarTicks.add(timebarTick4);
            mTimebarTicks.add(timebarTick3);
            mTimebarTicks.add(timebarTick2);
            mTimebarTicks.add(timebarTick1);
        }else{
            for (int i = 0;i<mTimebarTicks.size();i++){
                mTimebarTicks.get(i).setStandardViewLength(((long) ((double) mWidth * TOTOAL_TIME / (double) mTimebarTicks.get(i).getTotalSecondsInOneScreen())));
            }
        }
        setTimebarScaleIndex(4,0);

    }

    private void setTimebarScaleIndex(int position, int length) {
        mTimebarTick = mTimebarTicks.get(position);
        if (length <= 0) {
            mTimebarTick.setViewLength(mTimebarTick.getStandardViewLength());
            mLayoutController.setLayoutSize(mWidth, mHeight, (int) mTimebarTick.getViewLength());
        } else {
            mTimebarTick.setViewLength(length);
            mLayoutController.setLayoutSize(mWidth, mHeight, length);
        }
        mPixelsPerSecond = mTimebarTick.getViewLength() / (float) TOTOAL_TIME;
        Log.d(TAG,"pixelsPerSecond = "+mPixelsPerSecond);
        mLayoutController.setPosition((int) (mCurrentSecond * mPixelsPerSecond));
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidth == 0) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(widthMeasureSpec);
            initTimebarTicks();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        initCalendar();
        mMaxScaleHeight = dip2px(mContext, 12);
        mMidScaleHeight = dip2px(mContext, 7);
        mMinScaleHeight = dip2px(mContext, 5);

        //编辑起始时间绘制
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(dip2px(mContext, mScaleTextSize));
        mFontMetrics = mTextPaint.getFontMetrics();// 得到系统默认字体属性
        mScaleTextHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);

        //编辑当前时间绘制
        mCurrentTimePaint.setColor(mTextColor);
        mCurrentTimePaint.setTextSize(dip2px(mContext, mCurrentScaleTextSize));
        mFontMetrics = mTextPaint.getFontMetrics();// 得到系统默认字体属性
        mCurrentTimeTextHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
        mTop = mCurrentTimeTextHeight + dip2px(mContext, 10);
        mBackParint.setColor(mBackColor);
        mCurrentTimeLinePaint = new Paint();
        mCurrentTimeLinePaint.setColor(mCurrentTimeLineColor);
        mLayoutController = new LayoutController(mContext);
        mScaleGestureDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.d(TAG, "onScale detector = " + detector.getScaleFactor());
                scaleTimebar(detector.getScaleFactor() * (1 + (detector.getScaleFactor() - 1) * 1.2f));
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.d(TAG, "onScaleBegin detector = " + detector.getScaleFactor());
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                Log.d(TAG, "onScaleEnd = " + detector.getScaleFactor());
            }
        });
        mGestureDetector = new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "GestureDetector onDown ");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                Log.d(TAG, "GestureDetector onShowPress ");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "GestureDetector onSingleTapUp ");
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "GestureDetector onScroll ");
                if (mTouchMode == TOUCH_MODE_SCROLLER) {
                    isRefereshPlayTimeWithPlayer = false;
                    mLayoutController.startScroll(Math.round(distanceX), 0, mLayoutController.getScrollLimit());
                    invalidate();
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "GestureDetector onLongPress ");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "GestureDetector onFling ");
                if (mTouchMode == TOUCH_MODE_SCROLLER) {
                    int scrollLimit = mLayoutController.getScrollLimit();
                    if (scrollLimit == 0) return false;
                    mLayoutController.fling((int) (-velocityX), 0, scrollLimit);
                    invalidate();
                }
                return true;
            }
        });
    }

    private void initCalendar() {
        if (mStartCalendar == null) {
            mStartCalendar = Calendar.getInstance();
            mStartCalendar.set(Calendar.DATE, mStartCalendar.get(Calendar.DATE));
        }
        if (mEndCalendar == null) {
            mEndCalendar = (Calendar) mStartCalendar.clone();
            mEndCalendar.set(Calendar.DATE, mEndCalendar.get(Calendar.DATE) + 1);
        }
        mStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mStartCalendar.set(Calendar.MINUTE, 0);
        mStartCalendar.set(Calendar.SECOND, 0);

        mEndCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mEndCalendar.set(Calendar.MINUTE, 0);
        mEndCalendar.set(Calendar.SECOND, 0);
        mPositonCalendar = (Calendar) mStartCalendar.clone();
        TOTOAL_TIME = (mEndCalendar.getTimeInMillis() - mStartCalendar.getTimeInMillis()) / 1000;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long b = System.currentTimeMillis();
        boolean ret = mLayoutController.computeScrollOffset();

        mCurrentPosition = mLayoutController.getPosition();
        //算开始时缩放时不重新获取当前播放时间
//        mCurrentSecond = (int) ((mCurrentPosition) * TOTOAL_TIME / mTimebarTick.getViewLength());
        if (mTouchMode == TOUCH_MODE_SCROLLER) {
            mCurrentSecond = (int) (mCurrentPosition / mPixelsPerSecond);
        }
        drawBack(canvas);
        drawScale(canvas);
        drawRegionRect(canvas);
        drawCurrentPositionLine(canvas);
        drawCurrentTime(canvas);
        Log.d(TAG, "onDraw position = " + mCurrentSecond + "  computeScrollOffset = " + ret);
        if (ret) {
            invalidate();
        } else {
            if (mTouchMode == TOUCH_MODE_SCROLLER){
                Log.d(TAG, "isTouchScreent = " + isTouchScreent);
                if (!isTouchScreent) {
                    mTouchMode = TOUCH_MODE_NONE;
                }
                mPositonCalendar.setTimeInMillis(mStartCalendar.getTimeInMillis() + mCurrentSecond * 1000);
                if (mTimerShaftBarListener != null) {
                    mTimerShaftBarListener.onTimerShaftBarPosChanged(mPositonCalendar);
                }
            }
            if (mTouchMode == TOUCH_MODE_SCALE && !isTouchScreent){
                mTouchMode = TOUCH_MODE_NONE;
            }
        }
        Log.d(TAG, "on draw time = " + String.valueOf(System.currentTimeMillis() - b));
    }

    /**
     * 绘制当前时间
     *
     * @param canvas
     */
    private void drawCurrentTime(Canvas canvas) {
        if (mTouchMode == TOUCH_MODE_SCROLLER || mLayoutController.computeScrollOffset()) {
            mPositonCalendar.setTimeInMillis(mStartCalendar.getTimeInMillis() + mCurrentSecond * 1000);
        }
        String text = getTime(mPositonCalendar.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
        canvas.drawText(text, mWidth / 2 - mCurrentTimePaint.measureText(text) / 2, mCurrentTimeTextHeight, mCurrentTimePaint);
    }

    /**
     * 绘制当前时间
     * k
     *
     * @param canvas
     */
    private void drawBack(Canvas canvas) {
        canvas.drawRect(new Rect(0, mTop, mWidth, getHeight()), mBackParint);
    }

    /**
     * 绘制标尺
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {
        long b = System.currentTimeMillis();
        canvas.drawLine(0, mTop, mWidth, mTop, mTextPaint);
        canvas.drawLine(0, getHeight() - 1, mWidth, getHeight() - 1, mTextPaint);

        mStartSecondOnScreen = (long) ((mCurrentPosition - mLayoutController.getPaddingLeft()) / mPixelsPerSecond);
        mEndSecondOnScreen = (long) (mStartSecondOnScreen + mWidth / mPixelsPerSecond);

        Log.d(TAG, "onDraw startTikeStart = " + mStartSecondOnScreen);
        Log.d(TAG, "onDraw endTikeStart = " + mEndSecondOnScreen);
        if (mEndSecondOnScreen > TOTOAL_TIME) {
            mEndSecondOnScreen = TOTOAL_TIME;
        }
        for (long i = mStartSecondOnScreen - 20; i < mEndSecondOnScreen + 20; i++) {
            if (i < 0 || i > TOTOAL_TIME) {
                continue;
            }
            long startX = (long) ((i - mStartSecondOnScreen) * mPixelsPerSecond);
            if (i % (60 * 60) == 0) {
                canvas.drawLine(startX, mTop, startX, mTop + mMaxScaleHeight, mTextPaint);
                canvas.drawLine(startX, getHeight() - mMaxScaleHeight, startX, getHeight(), mTextPaint);
            }
            if (i % mTimebarTick.getMaxTickInSecond() == 0) {
                if (i % (60 * 60) != 0) {
                    canvas.drawLine(startX, mTop, startX, mTop + mMidScaleHeight, mTextPaint);
                    canvas.drawLine(startX, getHeight() - mMidScaleHeight, startX, getHeight(), mTextPaint);
                    String timeText = getTime((i * 1000l + mStartCalendar.getTimeInMillis()), mTimebarTick.getDataPattern());
                    canvas.drawText(timeText, startX - mTextPaint.measureText(timeText) / 2, mTop + mMidScaleHeight + mScaleTextHeight / 2 + dip2px(mContext, 10), mTextPaint);
                } else {
                    String timeText = getTime((i * 1000l + mStartCalendar.getTimeInMillis()), mTimebarTick.getDataPattern());
                    canvas.drawText(timeText, startX - mTextPaint.measureText(timeText) / 2, mTop + mMaxScaleHeight + mScaleTextHeight / 2 + dip2px(mContext, 10), mTextPaint);
                }
            } else if (i % mTimebarTick.getMinTickInSecond() == 0) {
                canvas.drawLine(startX, mTop, startX, mTop + mMinScaleHeight, mTextPaint);
                canvas.drawLine(startX, getHeight() - mMinScaleHeight, startX, getHeight(), mTextPaint);
            }
        }
        Log.d(TAG, "drawtime   drawScale time = " + (System.currentTimeMillis() - b));
    }

    /**
     * 绘制包含时间所在区域
     *
     * @param canvas
     */
    private void drawRegionRect(Canvas canvas) {
        if (mTimeShaftItems != null) {
            long b = System.currentTimeMillis();
            for (int y = 0; y < mTimeShaftItems.size(); y++) {
                TimerShaftRegionItem timeShaftItem = mTimeShaftItems.get(y);
                if (timeShaftItem.getEndCalendar().getTimeInMillis() - mStartCalendar.getTimeInMillis() - mStartSecondOnScreen*1000l < -10000) {
                    continue;
                }
                if (timeShaftItem.getStartCalendar().getTimeInMillis() - mStartCalendar.getTimeInMillis() - mEndSecondOnScreen*1000l > 10000){
                    continue;
                }
                int startTime = (int) ((timeShaftItem.getStartCalendar().getTimeInMillis() - mStartCalendar.getTimeInMillis()) / 1000);
                int endTime = (int) ((timeShaftItem.getEndCalendar().getTimeInMillis() - mStartCalendar.getTimeInMillis()) / 1000);
                int startX = (int) ((startTime - mStartSecondOnScreen) * mPixelsPerSecond);
                int endX = (int) ((endTime - mStartSecondOnScreen) * mPixelsPerSecond);
                if (timeShaftItem.getRecType() == 1){
                    //云存储
                    mTimeRegionPaint.setColor(mRegionCloudBackColor);
                }else if(timeShaftItem.getRecType() == 2){
                    //本地录像
                    mTimeRegionPaint.setColor(mRegionLocalBackColor);

                }
                canvas.drawRect(new Rect(startX, mTop, endX, getHeight()), mTimeRegionPaint);
            }
            Log.d(TAG, "drawtime   drawRegionRect time = " + (System.currentTimeMillis() - b));
        }
    }

    /**
     * 绘制标示当前位置的线
     *
     * @param canvas
     */
    private void drawCurrentPositionLine(Canvas canvas) {
        long b = System.currentTimeMillis();
        //绘制中心线
        canvas.drawRect(new Rect(mWidth / 2 - dip2px(mContext, 0.5f), mTop, mWidth / 2 + dip2px(mContext, 0.5f), getHeight()), mCurrentTimeLinePaint);
        Path path = new Path();
        path.moveTo(mWidth / 2 - dip2px(mContext, 4), mTop);// 此点为多边形的起点
        path.lineTo(mWidth / 2 + dip2px(mContext, 4), mTop);
        path.lineTo(mWidth / 2, dip2px(mContext, 5) + mTop);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, mCurrentTimeLinePaint);
        Log.d(TAG, "drawtime   drawCurrentPositionLine time = " + (System.currentTimeMillis() - b));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionType = event.getActionMasked();
        switch (actionType) {
            case MotionEvent.ACTION_DOWN:
                mTouchMode = TOUCH_MODE_SCROLLER;
                isTouchScreent = true;
                // 第一个点被按下
                Toast.makeText(getContext(), "第一个点被按下", Toast.LENGTH_SHORT);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Toast.makeText(getContext(), "第一个点被按下", Toast.LENGTH_SHORT);
                mTouchMode = TOUCH_MODE_SCALE;
                isRefereshPlayTimeWithPlayer = true;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE  ");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP  +  "+!mLayoutController.computeScrollOffset());
                isTouchScreent = false;
               if(mTouchMode == TOUCH_MODE_SCALE){
                   isRefereshPlayTimeWithPlayer = true;
                    mTouchMode = TOUCH_MODE_NONE;
                }
                break;
        }
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private void scaleTimebar(float scaleFactor) {
        int newWidth = (int) (mTimebarTick.getViewLength() * scaleFactor);
        Log.d(TAG, "scaleTimebarByFactor  mTimebarTick.getViewLength() = " + mTimebarTick.getViewLength() + "   newWidth = " + newWidth);
        if (newWidth >= mTimebarTicks.get(0).getStandardViewLength()){
            setTimebarScaleIndex(0,0);
        }else if(newWidth <mTimebarTicks.get(0).getStandardViewLength() && newWidth >= mTimebarTicks.get(1).getStandardViewLength()){
            setTimebarScaleIndex(1,newWidth);
        }else if(newWidth <mTimebarTicks.get(1).getStandardViewLength() && newWidth >= mTimebarTicks.get(2).getStandardViewLength()){
            setTimebarScaleIndex(2,newWidth);
        }else if(newWidth <mTimebarTicks.get(2).getStandardViewLength() && newWidth >= mTimebarTicks.get(3).getStandardViewLength()){
            setTimebarScaleIndex(3,newWidth);
        }else if(newWidth <mTimebarTicks.get(3).getStandardViewLength() && newWidth >= mTimebarTicks.get(4).getStandardViewLength()){
            setTimebarScaleIndex(4,newWidth);
        }else if(newWidth <=mTimebarTicks.get(4).getStandardViewLength()){
            setTimebarScaleIndex(4,0);
        }
    }

    private String getTime(long duration, String formate) {
        Date mDate = new Date();
        mDate.setTime(duration);
        SimpleDateFormat mDateFormat = new SimpleDateFormat(formate, Locale.getDefault());
        return mDateFormat.format(mDate);
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
