package com.example.serhiihrabas.drawsquare.model;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by serhii.hrabas on 3/3/2017.
 */

public abstract class Shape {
    public abstract Paint getFillPaint();
    public abstract Paint getStrokePaint();
    public abstract float getInitX();
    public abstract float getInitY();

    public abstract void draw(Canvas canvas);
}
