package com.ezuikit.open.timeshaftbar;

import android.content.Context;
import android.util.Log;


public class LayoutController {
	protected int mWidth;
	protected int mHeight;

	protected int mScrollPosition;

	protected int mContentLength = 0;
	protected OverScroller mScroller;
	protected int mPaddingLeft = 0;
    protected int mPaddingRight = 0;
	protected int mPaddingTop = 0;

	public LayoutController(Context context) {
		mScroller = new OverScroller(context);
	}
	
	public void setLayoutSize(int width,int height,int contentLength) {
		mWidth = width;
		mHeight = height;
        mPaddingLeft = mWidth/2;
        mPaddingRight = mWidth/2;
        mContentLength = mPaddingLeft + contentLength + mPaddingRight;
	}
	public int getScrollLimit() {
        int limit = mContentLength - mPaddingLeft - mPaddingRight;
        return limit <= 0 ? 0 : limit;
    }
    //for scroll
    public boolean computeScrollOffset() {
    	return mScroller.computeScrollOffset();
    }
    public void forceFinished() {
        mScroller.forceFinished(true);
        mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(),0, 0 , 0);
    }
    public int getPosition() {
        return mScroller.getCurrX();
    }


    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public void fling(int velocity, int min, int max) {
		int currX = getPosition();
		mScroller.fling(currX, 0, velocity, 0, min, max, 0, 0,0,0);
	}
    // Returns the input value x clamped to the range [min, max].
    public static int clamp(int x, int min, int max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }
    // Returns the distance that over the scroll limit.
    public int startScroll(int distance, int min, int max) {
        int currPosition = mScroller.getCurrX();
        int newPosition = clamp(currPosition + distance, min, max);
        if (newPosition != currPosition) {
            Log.d("LayoutController","currPosition  = " + currPosition +" distance = "+distance);
            mScroller.startScroll(currPosition,0, newPosition - currPosition,0, 0);
        }
        return  distance - newPosition;
    }

    public void setPosition(int postion) {
    	mScrollPosition = postion;
    	mScroller.startScroll(postion,mScroller.getCurrY(),0, 0 , 0);
    }
    public boolean isFinished() {
    	return mScroller.isFinished();
    }
}
