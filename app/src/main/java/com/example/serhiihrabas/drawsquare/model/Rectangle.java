package com.example.serhiihrabas.drawsquare.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by serhii.hrabas on 3/3/2017.
 */

public class Rectangle extends Shape {
    Paint strokePaint;
    Paint fillPaint;
    int initX;
    int initY;
    int endX;
    int endY;

    public Rectangle(float initX, float initY, float endX, float endY, Paint fillPaint, Paint strokePaint){
        this.fillPaint = new Paint(fillPaint);
        this.strokePaint = new Paint(strokePaint);
        this.initX = (int)initX;
        this.initY = (int)initY;
        this.endX = (int)endX;
        this.endY = (int)endY;
    }

    @Override
    public Paint getFillPaint() {
        return fillPaint;
    }

    @Override
    public Paint getStrokePaint() {
        return strokePaint;
    }

    @Override
    public float getInitX() {
        return initX;
    }

    @Override
    public float getInitY() {
        return initY;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(getRect(), getFillPaint());
        canvas.drawRect(getRect(), getStrokePaint());
    }

    public Rect getRect(){
        return new Rect(initX, initY, endX, endY);
    }
}
