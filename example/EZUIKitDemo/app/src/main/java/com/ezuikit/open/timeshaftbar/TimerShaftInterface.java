package com.ezuikit.open.timeshaftbar;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 项目名称：EZUIKitDemo
 * 类描述：
 * 创建人：dingwei3
 * 创建时间：2017/5/2 15:15
 * 修改人：dingwei3
 * 修改时间：2017/5/2 15:15
 * 修改备注：
 */
public interface TimerShaftInterface {

    /**
     * 设置时间轴事件监听
     * @param timerShaftBarListener
     */
    void setTimerShaftLayoutListener(TimerShaftBar.TimerShaftBarListener timerShaftBarListener);

    /**
     * 设置绘制区域
     * @param timeShaftItems 绘制区域item
     */
    void setTimeShaftItems(ArrayList<TimerShaftRegionItem> timeShaftItems);

    /**
     * 设置时间轴是否根据接受外部播放进度,当播放成功时需要调用此方法，用来刷新播放时间进度
     */
    void setRefereshPlayTimeWithPlayer();

    /**
     * 设置播放当前时间
     * @param calendar
     */
    void setPlayCalendar(Calendar calendar);
}
