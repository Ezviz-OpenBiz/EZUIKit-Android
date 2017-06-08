/*
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.ezuikit.open.timeshaftbar;

/**
 * 时间轴显示的等级
 */
public class TimebarTick {
    /**
     * 时间轴总长度
     */
    private long viewLength;

    /**
     * 时间轴未缩放时的标准长度
     */
    private long standardViewLength;

    /**
     * 间隔距离
     */
    private float intervel;


    /**
     * 时间轴未缩放时的屏幕显示的时间（秒）
     */
    private int totalSecondsInOneScreen;

    /**
     * 时间轴中大刻度之间的时间间隔（秒）
     */
    private int maxTickInSecond;

    /**
     * 时间轴中小刻度之间的时间间隔（秒）
     */
    private int minTickInSecond;

    /**
     * 刻度显示的格式
     */
    private String dataPattern;

    public long getViewLength() {
        return viewLength;
    }

    public void setViewLength(long viewLength) {
        this.viewLength = viewLength;
    }

    public int getTotalSecondsInOneScreen() {
        return totalSecondsInOneScreen;
    }

    public void setTotalSecondsInOneScreen(int totalSecondsInOneScreen) {
        this.totalSecondsInOneScreen = totalSecondsInOneScreen;
    }

    public int getMaxTickInSecond() {
        return maxTickInSecond;
    }

    public void setMaxTickInSecond(int maxTickInSecond) {
        this.maxTickInSecond = maxTickInSecond;
    }

    public int getMinTickInSecond() {
        return minTickInSecond;
    }

    public void setMinTickInSecond(int minTickInSecond) {
        this.minTickInSecond = minTickInSecond;
    }

    public String getDataPattern() {
        return dataPattern;
    }

    public void setDataPattern(String dataPattern) {
        this.dataPattern = dataPattern;
    }

    public long getStandardViewLength() {
        return standardViewLength;
    }

    public void setStandardViewLength(long standardViewLength) {
        this.standardViewLength = standardViewLength;
        this.viewLength = this.standardViewLength;
    }

    public void setIntervel(float intervel) {
        this.intervel = intervel;
    }

    public float getIntervel() {
        return intervel;
    }
}
