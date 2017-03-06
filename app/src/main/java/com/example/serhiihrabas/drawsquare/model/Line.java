package com.example.serhiihrabas.drawsquare.model;

import android.graphics.Paint;

/**
 * Created by serhii.hrabas on 3/3/2017.
 */

public class Line extends Shape {
    Paint strokePaint;
    Paint fillPaint;
    int initX;
    int initY;
    int endX;
    int endY;

    public Line(float initX, float initY, float endX, float endY, Paint fillPaint, Paint strokePaint){
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

    public float getEndX(){
        return endX;
    }

    public float getEndY(){
        return endY;
    }
}
