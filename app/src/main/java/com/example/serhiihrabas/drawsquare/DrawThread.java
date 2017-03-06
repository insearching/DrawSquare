package com.example.serhiihrabas.drawsquare;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by serhii.hrabas on 3/3/2017.
 */

public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private DrawSurfaceView surfaceView;
    private boolean runFlag = false;


    public DrawThread(SurfaceHolder surfaceHolder, DrawSurfaceView surfaceView) {
        this.surfaceHolder = surfaceHolder;
        this.surfaceView = surfaceView;
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    @Override
    public void run() {
        Canvas c = null;
        while (runFlag) {
            try {
                c = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    surfaceView.onDraw(c);
                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }


}
