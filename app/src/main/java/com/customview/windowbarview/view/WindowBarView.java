package com.customview.windowbarview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.customview.windowbarview.R;

/**
 * 方法名：
 * 方法描述：
 * 作者：韩雪松 on 2017/7/8 13:02
 * 邮箱：15245605689@163.com
 */

public class WindowBarView extends View {

    private int progress = 0;
    private int maxprogress = 0;
    private int nowrogress = 0;
    private int bgcolor = 0;
    private int Foreground = 0;     //窗帘下拉的覆盖的颜色
    private Paint paint;
    private Paint foregrodpaint;
    private Drawable cendiagram = null;     //中心圆点的图片
    private Bitmap cendiagrambp = null;

    // 获得图片的宽高
    int widthbm = 0;
    int heightbm = 0;
    // 取得想要缩放的matrix参数
    Matrix matrix;
    Bitmap newbm;

    private float mPreX, mPreY;

    int width, height;
    Rect rect;
    Rect rectforegroud;

    public setonBarTouthnListener touthnListener = null;

    public interface setonBarTouthnListener {
        public void GetProgress(int progress);
    }

    public void getonBarProgressListener(setonBarTouthnListener touthnListener) {
        this.touthnListener = touthnListener;
    }

    public WindowBarView(Context context) {
        super(context);
    }

    public WindowBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public WindowBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public WindowBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        paint = new Paint();
        foregrodpaint = new Paint();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WindowBarView);
        Foreground = array.getInteger(R.styleable.WindowBarView_foreground, Color.WHITE);
        cendiagram = array.getDrawable(R.styleable.WindowBarView_cendiagram);
        maxprogress = array.getInteger(R.styleable.WindowBarView_maxprogress, 100);
        nowrogress = array.getInteger(R.styleable.WindowBarView_nowrogress, 0);
        bgcolor = array.getInteger(R.styleable.WindowBarView_bgcolor, Color.WHITE);
        setProgress(array.getInteger(R.styleable.WindowBarView_nowrogress, nowrogress));
        array.recycle();
        cendiagrambp = drawableToBitmap(cendiagram);

        // 获得图片的宽高
        widthbm = cendiagrambp.getWidth();
        heightbm = cendiagrambp.getHeight();
        // 设置想要的大小
        int newWidth = 100;
        int newHeight = 100;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / widthbm;
        float scaleHeight = ((float) newHeight) / heightbm;
        // 取得想要缩放的matrix参数
        matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        newbm = Bitmap.createBitmap(cendiagrambp, 0,
                0, widthbm, heightbm, matrix, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wideSize = MeasureSpec.getSize(widthMeasureSpec);
        int wideMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (wideMode == MeasureSpec.EXACTLY) { //精确值 或matchParent
            width = wideSize;
        } else {
            width = getPaddingLeft() + getPaddingRight();
            if (wideMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, wideSize);
            }

        }

        if (heightMode == MeasureSpec.EXACTLY) { //精确值 或matchParent
            height = heightSize;
        } else {
            height = getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }

        }
        setMeasuredDimension(width, height);
        rect = new Rect(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectforegroud = new Rect(0, 0, width, (int) mPreY);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bgcolor);
        paint.setAntiAlias(true);
        canvas.drawRect(rect, paint);
        foregrodpaint.setStyle(Paint.Style.FILL);
        foregrodpaint.setAntiAlias(true);
        foregrodpaint.setColor(Foreground);
        canvas.drawRect(rectforegroud, foregrodpaint);
        canvas.drawBitmap(newbm, width / 2 - newbm.getWidth() / 2, (int) mPreY - newbm.getHeight() / 2, paint);     //绘制滑块
    }

    /**
     * 获取当前位置
     */
    public int getProgress() {
        progress = (int) ((float) nowrogress / (float) maxprogress * 100);  //当前进度
        return progress;
    }

    /**
     * 设置当前进度
     */
    public synchronized void setProgress(final int nowrogress) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(100);
                    if (height != 0) {
                        mPreY = ((int) ((float) nowrogress / (float) maxprogress * height));
                        postInvalidate();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 判断，如果滑动的位置大于控件的高度，说明此事滑块已经滑到下面
     * 如果小于0说明在上面
     *
     * @param event
     * @return
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreX = event.getX();
                mPreY = event.getY();
                touthnListener.GetProgress((int) (mPreY / height * maxprogress));
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                mPreY = event.getY();
                if (mPreY < 0) {
                    mPreY = 0;
                } else if (mPreY > height) {
                    mPreY = height;
                } else {
                    mPreY = event.getY() + 8;
                }
                touthnListener.GetProgress((int) (mPreY / height * maxprogress));
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }
}
