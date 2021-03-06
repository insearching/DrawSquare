package com.example.serhiihrabas.drawsquare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.serhiihrabas.drawsquare.model.Circle;
import com.example.serhiihrabas.drawsquare.model.Curve;
import com.example.serhiihrabas.drawsquare.model.Line;
import com.example.serhiihrabas.drawsquare.model.Rectangle;
import com.example.serhiihrabas.drawsquare.model.Shape;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Action;


/**
 * Created by serhii.hrabas on 3/3/2017.
 */

public class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String LOG_TAG = DrawSurfaceView.class.getSimpleName();
    //    boolean saving = false;
    private Bitmap mBitmap;
    private DrawThread drawThread;
    private float initX, initY, radius, endX, endY;
    private Path path = new Path();
    private Paint fillPaint;
    private Paint strokePaint;
    private Paint bgPaint;
    private Instrument curInstrument;
    private volatile ArrayList<Shape> layers = new ArrayList<>();
    private Bitmap loadBitmap = null;

    private CanvasState curCanvasState;

    public DrawSurfaceView(Context context) {
        super(context);
        init();
    }

    public DrawSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        setFocusable(true);

        setCurrentInstrument(Instrument.PENCIL);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.TRANSPARENT);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(10f);
        strokePaint.setColor(Color.BLACK);

        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.WHITE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d(LOG_TAG, "surfaceChanged");
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceCreated");
        drawThread = new DrawThread(getHolder(), this);
        drawThread.setRunning(true);
        drawThread.start();
        postDrawingState(CanvasState.INITIATING);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceDestroyed");
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            switch (curInstrument) {
                case CIRCLE:
                    radius = (float) Math.sqrt(Math.pow(x - initX, 2)
                            + Math.pow(y - initY, 2));
                    break;
                case RECTANGLE:
                case LINE:
                    endX = x;
                    endY = y;
                    break;
                case PENCIL:
                    path.lineTo(x, y);
                    break;
            }

        } else if (action == MotionEvent.ACTION_DOWN) {
            initX = endX = event.getX();
            initY = endY = event.getY();
            switch (curInstrument) {
                case CIRCLE:
                    radius = 1;
                    break;
                case PENCIL:
                    path.moveTo(initX, initY);
                    break;
            }

            curCanvasState = CanvasState.DRAWING;
            Log.d(LOG_TAG, "ACTION_DOWN x=" + initX + " y=" + initY + " radius=" + radius);
        } else if (action == MotionEvent.ACTION_UP) {
            Observable.empty().delay(500, TimeUnit.MILLISECONDS)
                    .doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            curCanvasState = CanvasState.IDLE;
                            switch (curInstrument) {
                                case CIRCLE:
                                    layers.add(new Circle(initX, initY, radius, fillPaint, strokePaint));
                                    break;
                                case RECTANGLE:
                                    layers.add(new Rectangle(initX, initY, endX, endY, fillPaint, strokePaint));
                                    break;
                                case LINE:
                                    layers.add(new Line(initX, initY, endX, endY, fillPaint, strokePaint));
                                    break;
                                case PENCIL:
                                    layers.add(new Curve(path, fillPaint, strokePaint));
                                    path = new Path();
                                    break;
                            }

                        }
                    }).subscribe();
            Log.d(LOG_TAG, "ACTION_UP radius=" + radius);
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (curCanvasState == CanvasState.INITIATING) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
        }
        if (curCanvasState == CanvasState.DRAWING
                || curCanvasState == CanvasState.SAVING) {

            if (curCanvasState == CanvasState.SAVING) {
                canvas = new Canvas(mBitmap);
            }
            canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);

            if(loadBitmap != null){
                canvas.drawBitmap(loadBitmap, 0, 0, null);
            }
            for (Shape shape : new ArrayList<>(layers)) {
                shape.draw(canvas);
            }

            switch (curInstrument) {
                case CIRCLE:
                    canvas.drawCircle(initX, initY, radius, fillPaint);
                    canvas.drawCircle(initX, initY, radius, strokePaint);
                    break;
                case RECTANGLE:
                    canvas.drawRect(initX, initY, endX, endY, fillPaint);
                    canvas.drawRect(initX, initY, endX, endY, strokePaint);
                    break;
                case LINE:
                    canvas.drawLine(initX, initY, endX, endY, fillPaint);
                    canvas.drawLine(initX, initY, endX, endY, strokePaint);
                    break;
                case PENCIL:
                    canvas.drawPath(path, strokePaint);
                    break;
            }
        }
    }

    public void postDrawingState(CanvasState state) {
        curCanvasState = state;
        Observable.empty().delay(500, TimeUnit.MILLISECONDS)
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        curCanvasState = CanvasState.IDLE;
                    }
                }).subscribe();
    }

    public void clearCanvas() {
        postDrawingState(CanvasState.CLEARING);
    }

    public boolean saveToFile(String filePath) {
        postDrawingState(CanvasState.SAVING);
        Log.d(LOG_TAG, "Saving file " + filePath + " width=" + this.getWidth() + " height=" + this.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        File file = new File(filePath);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mBitmap = null;
        }
    }

    public void loadBitmap(Bitmap bitmap) {
        loadBitmap = Bitmap.createBitmap(bitmap);
    }

    public int getFillColor() {
        return fillPaint.getColor();
    }

    public void setFillColor(int fillColor) {
        fillPaint.setColor(fillColor);
    }

    public int getStrokeColor() {
        return strokePaint.getColor();
    }

    public void setStrokeColor(int strokeColor) {
        strokePaint.setColor(strokeColor);
    }

    public Instrument getCurrentInstrument() {
        return curInstrument;
    }

    public void setCurrentInstrument(Instrument instrument) {
        curInstrument = instrument;
    }

    public int getStrokeWidth() {
        return (int) strokePaint.getStrokeWidth();
    }

    public void setStrokeWidth(float width) {
        strokePaint.setStrokeWidth(width);
    }

    public enum CanvasState {
        IDLE,
        INITIATING,
        DRAWING,
        NEW_LAYER,
        SAVING,
        CLEARING
    }

    public enum Instrument {
        PENCIL,
        CIRCLE,
        RECTANGLE,
        LINE
    }
}
