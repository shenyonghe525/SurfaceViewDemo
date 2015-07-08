package com.syh.yongheshen.surfaceviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by yongheshen on 15/7/7.
 */
public class SurfaceViewTempalte extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    //子线程绘制view
    private Thread thread;

    private boolean isRunning;

    public SurfaceViewTempalte(Context context) {
        this(context, null);
    }

    public SurfaceViewTempalte(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init(){
        mHolder = getHolder();
        mHolder.addCallback(this);
        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        //不断的进行绘制
        while (isRunning){
            draw();
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null){
                //画一些东西
            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
