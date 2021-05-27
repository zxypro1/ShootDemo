package com.example.shooting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;


public class Net extends View{

    Paint  paint;
    public Net(Context context) {
        super(context);
        init();// TODO Auto-generated constructor stub
    }

    public Net(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Net(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Net(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
        paint.setAlpha(100);
    }

    /**
     * 绘制网格线
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
//        canvas.drawColor(Color.BLUE);
        //canvas.drawCircle(100, 100, 90, paint);
        final int width = getDisplay().getWidth(); //hdpi 480x800
        final int height = getDisplay().getHeight();
//        final int width = 480; //hdpi 480x800
//        final int height = 800;
        final int edgeWidth = 10;
        final int space = 100;   //长宽间隔
        int vertz = 0;
        int hortz = 0;
        for (int i = 0; i < 100; i++) {
            canvas.drawLine(0, vertz, width, vertz, paint);
            canvas.drawLine(hortz, 0, hortz, height, paint);
            vertz += space;
            hortz += space;
        }
    }
}
