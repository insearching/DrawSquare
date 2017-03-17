package com.example.serhiihrabas.drawsquare.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by serhii.hrabas on 3/3/2017.
 */

public class Curve extends Shape {
    Paint strokePaint;
    Paint fillPaint;
    Path path;


    public Curve(Path path, Paint fillPaint, Paint strokePaint){
        this.path = new Path(path);
        this.fillPaint = new Paint(fillPaint);
        this.strokePaint = new Paint(strokePaint);

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
        return 0;
    }

    @Override
    public float getInitY() {
        return 0;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(getPath(), getStrokePaint());
    }

    public Path getPath() {
        return path;
    }
}
