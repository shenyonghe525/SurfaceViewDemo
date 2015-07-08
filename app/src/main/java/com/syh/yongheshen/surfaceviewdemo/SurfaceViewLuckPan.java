package com.syh.yongheshen.surfaceviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by yongheshen on 15/7/7.
 */
public class SurfaceViewLuckPan extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    //子线程绘制view
    private Thread thread;

    private boolean isRunning;

    private String[] mTitles = {"单反相机","IPAD","恭喜发财","肾六","衣服一套","恭喜发财"};

    private int[] mImgs = new int[]{R.drawable.danfan,R.drawable.ipad,R.drawable.f040,R.drawable.iphone,R.drawable.meizi,R.drawable.f040};

    private Bitmap[] mImgsBitmap;

    private int[] mColors = new int[]{0xffffc300,0xffffe701,0xffffc300,0xffffe701,0xffffc300,0xffffe701};

    private int mItemCount = 6;

    //整个盘块的范围
    private RectF mRange = new RectF();

    //整个盘块的直径
    private int mRadius;

    //绘制盘块的画笔
    private Paint mArcPaint;

    //绘制文本的画笔
    private Paint mTxtPaint;

    //盘块滚动的速度
    private double mSpeed = 0;

    //开始的角度
    private volatile float mStartAngle = 0;

    //是否点击了停止按钮
    private boolean isShoudEnd;

    //转盘中心的位置
    private int mCenter;

    private int mPadding;

    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bg2);

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());

    public SurfaceViewLuckPan(Context context) {
        this(context, null);
    }

    public SurfaceViewLuckPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取宽高中较小的作为直径
        int width = Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPadding = getPaddingLeft();
        //直径
        mRadius = width - mPadding*2;
        //中心点
        mCenter = width/2;
        setMeasuredDimension(width,width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化绘制盘块的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        //初始化绘制文字的画笔
        mTxtPaint = new Paint();
        mTxtPaint.setTextSize(mTextSize);
        mTxtPaint.setColor(0xffffffff);

        //盘块绘制的范围
        mRange = new RectF(mPadding,mPadding,mPadding+mRadius,mPadding+mRadius);

        //初始化图片
        mImgsBitmap = new Bitmap[mItemCount];
        for (int i=0;i<mItemCount;i++){
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),mImgs[i]);
        }

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
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if ((end -start) < 50){
                try {
                    Thread.sleep(50-(end-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null){
                //绘制背景
                drawBg();
                //绘制盘块
                float tmpAngle = mStartAngle;
                //每个盘块所占的角度
                float sweepAngle = 360/mItemCount;
                for (int i=0;i<mItemCount;i++){
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange,tmpAngle,sweepAngle,true,mArcPaint);
                    //绘制文本
                    drawText(tmpAngle,sweepAngle,mTitles[i]);
                    //绘制图片
                    drawIcon(tmpAngle,mImgsBitmap[i]);

                    tmpAngle+=sweepAngle;
                }

                mStartAngle+=mSpeed;
                if (isShoudEnd){
                    mSpeed-=1;
                }

                if (mSpeed <= 0){
                    mSpeed=0;
                    isShoudEnd = false;
                }
            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 是否在旋转
     * @return
     */
    public boolean isStart(){
        return  mSpeed!=0;
    }

    public boolean isShouldEnd(){
        return  isShoudEnd;
    }

    /**
     * 开始转动转盘
     */
    public void luckStart(int index){

        //计算每一个的角度
        float angle = 360/mItemCount;
        //计算每一项的中奖范围（当前index）
        //0-> 210-270
        //1-> 150-210
        float from = 270-(index+1)*angle;
        float end = from + angle;

        //设置停下来需要旋转的距离
        float targetfrom = 5*360 + from;
        float targetend = 5*360 + end;

        float v1 = (float)((-1+Math.sqrt(1+8*targetfrom))/2);
        float v2 = (float)((-1+Math.sqrt(1+8*targetend))/2);

        mSpeed = v1 + Math.random()*(v2-v1);
        isShoudEnd = false;
    }

    /**
     * 停止滚动转盘
     */
    public void luckEnd(){
        mStartAngle = 0;
        isShoudEnd = true;
    }

    /**
     * 绘制图片
     * @param tmpAngle
     * @param bitmap
     */
    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        //设置图片的宽度
        int imgWidth = mRadius/8;
        float angle = (float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);
        //确定图片中心点得坐标
        int x = (int) (mCenter + mRadius/2/2*Math.cos(angle));
        int y = (int) (mCenter + mRadius/2/2*Math.sin(angle));
        //确定图片的位置
        Rect rect = new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
        mCanvas.drawBitmap(bitmap,null,rect,null);
    }

    /**
     * 绘制文本信息
     * @param tmpAngle
     * @param sweepAngle
     * @param mTitle
     */
    private void drawText(float tmpAngle, float sweepAngle, String mTitle) {
        Path path = new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);

        //利用水平偏移量让文字居中
        float textWidth = mTxtPaint.measureText(mTitle);
        //文字水偏移量
        int  hOffset = (int) (mRadius*Math.PI/mItemCount/2 - textWidth/2);
        //文字垂直偏移量
        int vOffset = mRadius/2/6;

        mCanvas.drawTextOnPath(mTitle,path,hOffset,vOffset,mTxtPaint);
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBgBitmap,null,new RectF(mPadding/2,mPadding/2,getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding/2),null);
    }
}
