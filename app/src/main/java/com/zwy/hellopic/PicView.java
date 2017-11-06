package com.zwy.hellopic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouweiyong on 2017/11/6.
 */

public class PicView extends View {
    private Bitmap mBackBitmap;
    private Paint mBackPaint;
    private Rect mBackRect;
    private Paint mRedPaint;
    private Paint mSrcPaint;
    private float mStartX;
    private float mStartY;
    private Path mSrcPath;
    private float mEndX;
    private float mEndY;
    private ArrayList<PathHolder> mPaths;

    public PicView(Context context) {
        this(context, null);
    }

    public PicView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mBackBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.adv3_03);
        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSrcPaint = createPaint(Color.parseColor("#000000"), 10, Paint.Style.STROKE, 20);
        mSrcPath = new Path();
        mRedPaint = createPaint(Color.RED, 10, Paint.Style.STROKE, 20);
        mPaths = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackRect = new Rect(0, 0, w, h);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBackBitmap, null, mBackRect, mBackPaint);
        canvas.drawPath(mSrcPath, mRedPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                mSrcPath.moveTo(mStartX, mStartY);
                break;
            case MotionEvent.ACTION_MOVE:
                mEndX = event.getX();
                mEndY = event.getY();
                mSrcPath.lineTo(mEndX, mEndY);
                mPaths.add(new PathHolder(mStartX, mStartY, mEndX, mEndY));
                mStartX = event.getX();
                mStartY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }

    class PathHolder {
        public float startX;
        public float startY;
        public float endX;
        public float endY;

        public PathHolder(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }


    private Paint createPaint(int paintColor, int textSize, Paint.Style style, int lineWidth) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        paint.setDither(true);
        paint.setTextSize(textSize);
        paint.setStyle(style);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }


    public Bitmap getBitmap() {
        Bitmap mCBitmap = Bitmap.createBitmap(mBackRect.width(), mBackRect.height(), Bitmap.Config.ARGB_8888);
        Canvas mCCanvas = new Canvas(mCBitmap);
        Paint mCPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCCanvas.drawBitmap(mBackBitmap, null, mBackRect, mCPaint);

        Bitmap mDistBitmap = Bitmap.createBitmap(mBackRect.width(), mBackRect.height(), Bitmap.Config.ARGB_8888);
        mDistBitmap.eraseColor(Color.parseColor("#55000000"));
        int layerId = mCCanvas.saveLayer(0, 0, mCCanvas.getWidth(), mCCanvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mCCanvas.drawBitmap(mDistBitmap, null, mBackRect, mBackPaint);
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        mSrcPaint.setXfermode(porterDuffXfermode);
        Path path = new Path();
        for (PathHolder mPath : mPaths) {
            path.moveTo(mPath.startX, mPath.startY);

            path.lineTo(mPath.endX, mPath.endY);
            mCCanvas.drawPath(path, mSrcPaint);

        }
        mCCanvas.restoreToCount(layerId);
        return mCBitmap;
    }
}
