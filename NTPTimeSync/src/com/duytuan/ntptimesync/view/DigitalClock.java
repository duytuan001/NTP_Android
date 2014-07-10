package com.duytuan.ntptimesync.view;

import java.util.Timer;
import java.util.TimerTask;

import com.duytuan.ntptimesync.R;
import com.duytuan.ntptimesync.R.styleable;
import com.duytuan.ntptimesync.view.AnalogClock.MyCount;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.TextView;

public class DigitalClock extends TextView{

	private Time mCalendar;
	Context context;
	private String fontName = "DS-DIGIT.TTF";
	
	public DigitalClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}
	
	public DigitalClock(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DigitalClock);
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+fontName);
				 setTypeface(myTypeface);
			 a.recycle();
		}
	}
	
//	MyCount counter = new MyCount(10000, 1000);
//    public class MyCount extends CountDownTimer{
//    	public MyCount(long millisInFuture, long countDownInterval) {
//    	super(millisInFuture, countDownInterval);
//    	}
//
//    	@Override
//    	public void onFinish() {
//    		counter.start();
//    	}
//
//    	@Override
//    	public void onTick(long millisUntilFinished) {
//            int hours 		= mCalendar.hour;
//            int minutes 	= mCalendar.minute;
//            int seconds 	= mCalendar.second;
//            seconds++;
//            setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
//         }
//    }
}
