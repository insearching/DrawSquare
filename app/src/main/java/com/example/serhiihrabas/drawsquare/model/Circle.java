package com.example.serhiihrabas.drawsquare.model;

import android.graphics.Paint;

/**
 * Created by serhii.hrabas on 3/3/2017.
 */

public class Circle extends Shape {
    Paint strokePaint;
    Paint fillPaint;
    float initX;
    float initY;
    float radius;

    public Circle(float initX, float initY, float radius, Paint fillPaint, Paint strokePaint){
        this.fillPaint = new Paint(fillPaint);
        this.strokePaint = new Paint(strokePaint);
        this.initX = initX;
        this.initY = initY;
        this.radius = radius;
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

    public float getRadius(){
        return radius;
    }
}
