package com.example.shooting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * 九宫格解锁控件
 * Created by Jerry on 2015/9/21.
 */
public class RandomBoll extends View {
    public RandomBoll(Context context) {
        this(context, null);
    }
    public RandomBoll(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public RandomBoll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    //初始化
    Paint paint;
    private void init(){
        //this.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));//背景
        //初始画笔
        paint = new Paint();
        paint.setColor(Color.parseColor("#00B7EE"));
        paint.setAntiAlias(true);//消除锯齿
        paint.setStrokeWidth(33);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);

        canvas.drawLine(startX,startY,endX,endY,paint);
        for (int i = 0; i < list.size(); i++) {
            float [] data=list.get(i);
            canvas.drawLine(data[0],data[1],data[2],data[3],paint);
        }
    }

    float startX = 0;
    float startY = 0;

    float endX = 0;
    float endY = 0;

    ArrayList<float[]> list=new ArrayList<>();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                endY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                float [] data={startX,startY,endX,endY};
                list.add(data);

                break;
        }

        return true;

    }

    public void print(String str){
        Log.d("mft",str);
    }
}