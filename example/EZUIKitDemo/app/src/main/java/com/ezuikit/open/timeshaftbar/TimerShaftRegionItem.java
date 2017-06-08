package com.ezuikit.open.timeshaftbar;

import java.util.Calendar;

/**
 * 项目名称：EZUIKitDemo
 * 类描述：时间轴显示的区域对象
 * 创建人：dingwei3
 * 创建时间：2017/4/20 14:28
 * 修改人：dingwei3
 * 修改时间：2017/4/20 14:28
 * 修改备注：
 */
public class TimerShaftRegionItem {

    /**
     * 开始时间
     */
    private long mStartTime;
    /**
     * 结束时间
     */
    private long mEndTime;

    private Calendar mStartCalendar;

    private Calendar mEndCalendar;

    private int recType = 0;

    public TimerShaftRegionItem(long startTime, long endTime,int recType) {
        mStartTime = startTime;
        mEndTime = endTime;
        mStartCalendar = Calendar.getInstance();
        mStartCalendar.setTimeInMillis(mStartTime);
        mEndCalendar = Calendar.getInstance();
        mEndCalendar.setTimeInMillis(mEndTime);
        this.recType =recType;
    }

    public void setRecType(int recType) {
        this.recType = recType;
    }

    public int getRecType() {
        return recType;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public Calendar getStartCalendar() {
        return mStartCalendar;
    }

    public void setStartCalendar(Calendar startCalendar) {
        mStartCalendar = startCalendar;
    }

    public Calendar getEndCalendar() {
        return mEndCalendar;
    }

    public void setEndCalendar(Calendar endCalendar) {
        mEndCalendar = endCalendar;
    }
}
