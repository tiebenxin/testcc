package com.lens.chatmodel.view.photoedit;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lens.chatmodel.ui.image.ImagePagerActivity.ImageSize;
import com.lens.chatmodel.ui.image.PhotoEditActivity;
import java.util.ArrayList;


/**
 * 显示图片并且画图的View
 * -------文字
 * -------线条
 * -------马赛克路径
 * -------马赛克图片
 * -------原图
 * Created by LY309313 on 2017/4/12.
 */
public class CameraSurfaceView extends SurfaceView implements Runnable {
    private static final String TAG = "CameraSurfaceView";
    public static final int DRAW_NOTHING = 0x00;
    public static final int DRAW_PATH = 0X01;
    public static final int DRAW_WORD = 0x02;
    public static final int DRAW_MOSAIC = 0x03;
    public static final float MAX_SCALE = 4;// 最大的放缩比例
    public static final float MIN_SCALE = 0.5f;// 最小的放缩比例
    private static final int DEFAULT_WIDTH = 25;

    private static final int WORD_SIZE = 50;
    private final int mBrushWidth;
    private int currentDraw = DRAW_NOTHING;
    private Paint paint;
    private Paint mPaint;


    private Bitmap mBitmap;
    private Bitmap bmCoverLayer;
    private Bitmap mBitmapCopy;

    private Canvas mCanvas;
    private ArrayList<Word> words = new ArrayList<Word>();
    private ArrayList<Word> wordsTemp = new ArrayList<Word>();
    private ArrayList<MosaicPath> mosaicPaths = new ArrayList<>();

    private ArrayList<LinePath> linePaths = new ArrayList<LinePath>();
    private ArrayList<LinePath> lineTempPaths = new ArrayList<LinePath>();
    private LinePath realLinePath;

    private boolean canDraw;
    private LinePath currentLinPath;
    private MosaicPath currentMosPath;

    private Path mPath;
    private Matrix matrix = new Matrix();
    private OnOptionListener optionListener;
    //判定动作的最小距离
    final float mTouchSlop;
    final float mMinimumVelocity;
    private boolean scaleMode;


    private Point startPoint0;
    private Point startPoint1;

    private float[] center;
    private Rect mImageRect;
    float mLastTouchX;
    float mLastTouchY;
    private boolean tap;


    /**
     * 绘画板宽度
     */
    private int mImageWidth;

    /**
     * 绘画板高度
     */
    private int mImageHeight;


    private VelocityTracker mVelocityTracker;
    private boolean mIsDragging;
    private PointF mid;
    private InputTextWindow popupWindow;
    private int wordBeyondCount;
    private float screenWidth;
    private float screenHeight;
    private float textWidth;
    private float textHeight;
    private float textsLeft;
    private float textTop;
    private float textMargin;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final ViewConfiguration configuration = ViewConfiguration
                .get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(30));
        paint.setStyle(Style.STROKE);
        paint.setTextSize(25);
        paint.setAntiAlias(true);

        mBrushWidth = (int) TDevice.dpToPixel(DEFAULT_WIDTH);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(0xff2a5caa);
        mImageRect = new Rect();

        mPath = new Path();
        mid = new PointF();
        //mode = NONE;
    }


    private float mOldDist = 0;


    float getActiveX(MotionEvent ev) {
        return checkOutOfX(ev.getX());
    }

    float getActiveY(MotionEvent ev) {
        return checkOutOfY(ev.getY());
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (canDraw) {
            switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mVelocityTracker = VelocityTracker.obtain();
                    if (null != mVelocityTracker) {
                        mVelocityTracker.addMovement(ev);
                    } else {
                    }

                    mLastTouchX = getActiveX(ev);
                    mLastTouchY = getActiveY(ev);
                    mIsDragging = false;
                    tap = true;
                    scaleMode = false;
                    //	mode =DRAG;
                    initdraw(ev);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    //多点触摸的事件
                    mOldDist = getDistOfTowPoints(ev);
                    isTextArea(ev);
                    break;
                case MotionEvent.ACTION_MOVE: {
                    final float x = getActiveX(ev);
                    final float y = getActiveY(ev);
                    final float dx = x - mLastTouchX, dy = y - mLastTouchY;

                    if (!mIsDragging) {
                        // Use Pythagoras to see if drag length is larger than
                        // touch slop
                        mIsDragging = Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;


                    }

                    if (mIsDragging) {
                        if (optionListener != null) {
                            optionListener.onStartDraw();
                        }
                        tap = false;
                        //大于最小动作尺度，则可以判定是画画还是拉伸了
                        //	L.i("开始绘制或者拉伸");
                        if (ev.getPointerCount() >= 2) {
                            if (currentDraw != DRAW_MOSAIC) {
                                scaleMode = true;
                                scale(ev);
                            }
                        } else {
                            scaleMode = false;
                            drawEvent(ev, dx, dy);
                        }

                        if (optionListener != null) {
                            //无论是什么模式，先将标题栏隐藏起来
                            optionListener.onStartDraw();
                        }
                        mLastTouchX = x;
                        mLastTouchY = y;

                        if (null != mVelocityTracker) {
                            mVelocityTracker.addMovement(ev);
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    // Recycle Velocity Tracker
                    if (null != mVelocityTracker) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    if (!tap && optionListener != null) {
                        optionListener.OnEndDraw();
                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP: {
                    if (mIsDragging) {
                        if (optionListener != null) {
                            optionListener.OnEndDraw();
                        }
                        if (null != mVelocityTracker) {
                            mLastTouchX = getActiveX(ev);
                            mLastTouchY = getActiveY(ev);

                            // Compute velocity within the last 1000ms
                            mVelocityTracker.addMovement(ev);
                            mVelocityTracker.computeCurrentVelocity(1000);

                            final float vX = mVelocityTracker.getXVelocity(), vY = mVelocityTracker
                                    .getYVelocity();

                            // If the velocity is greater than minVelocity, call
                            // listener
                            if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                                // TODO: 2017/4/5 高速度情况下界面需要起飞
//								mListener.onFling(mLastTouchX, mLastTouchY, -vX,
//										-vY);
                            }
                        }

                        eventUp(ev);
                    } else if (tap) {
                        if (optionListener != null) {
                            optionListener.ontap();
                        }
                    }

                    // Recycle Velocity Tracker
                    if (null != mVelocityTracker) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    break;
                }
            }
        }

        return true;
    }

    private int checkOutOfX(float x) {
        int result = 0;
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            result = 0;
        }

        if (x < mImageRect.left) {
            result = mImageRect.left;
        } else if (x > mImageRect.right) {
            result = mImageRect.right;

        } else {
            result = (int) x;
        }

        return result;
    }

    private int checkOutOfY(float y) {
        int result = 0;
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            result = 0;
        }

        if (y < mImageRect.top) {
            result = mImageRect.top;
        } else if (y > mImageRect.bottom) {
            result = mImageRect.bottom;

        } else {
            result = (int) y;
        }
        return result;
    }

    private void scale(MotionEvent event) {
        Log.i(TAG, "ACTION_MOVE:olddist==" + mOldDist);
        //判断手指的落点位置，如果在文字区域，则处理文字
        //boolean inTextArea = isTextArea(event);
        if (currentDraw == DRAW_WORD) {
            scaleText(event);
        } else {
            scaleImage(event);
        }

    }

    private void scaleText(MotionEvent event) {
        if (wordsTemp.isEmpty()) {
            return;
        }
        float mNewDist = getDistOfTowPoints(event);
        if (Math.abs(mNewDist - mOldDist) > 50) {
            float[] value = new float[9];
            Word word = wordsTemp.get(wordsTemp.size() - 1);
            Matrix matrix = word.getMatrix();
            matrix.getValues(value);
            Log.i(TAG, "文字的缩放量" + value[Matrix.MSCALE_X] + ":" + value[Matrix.MTRANS_Y]);
            float scale = mNewDist / mOldDist;//原来的放缩量

            if (scale < MIN_SCALE) {//如果放缩量小于最低的就置为最低放缩比
                scale = MIN_SCALE;

            } else if (scale > MAX_SCALE) {//如果放缩量大于最高的就置为最高放缩比
                scale = MAX_SCALE;
            }
            Log.i(TAG, "缩放比例" + scale / value[Matrix.MSCALE_X] + ":" + Math.abs(mNewDist - mOldDist));
            scale = scale / value[Matrix.MSCALE_X];//计算出相对的放缩量，使矩阵的放缩量为放缩到计算出来的放缩量。
            //缩放
            matrix.postScale(scale, scale, center[0], center[1]);
            //旋转，最初的两点和现在的两点做比较
            float deg = (float) getDeg(event);
            float degrees = (float) (deg * 180 / Math.PI);
            //}textMatrix
            matrix.postRotate(degrees, center[0], center[1]);
            word.setMatrix(matrix);


            //mOldDist = mNewDist;
            post(this);
        }
    }

    private double getDeg(MotionEvent event) {
        float x0 = event.getX(0);
        float y0 = event.getY(0);
        float x1 = event.getX(1);
        float y1 = event.getY(1);
        //这个角度被叠加了
        double deg0 = Math.atan2(startPoint1.y - startPoint0.y, startPoint1.x - startPoint0.x);
        double deg1 = Math.atan2(y1 - y0, x1 - x0);


        startPoint0.x = (int) x0;
        startPoint0.y = (int) y0;
        startPoint1.x = (int) x1;
        startPoint1.y = (int) y1;
        return deg1 - deg0;
    }

    private void scaleImage(MotionEvent event) {
        float mNewDist = getDistOfTowPoints(event);
        if (Math.abs(mNewDist - mOldDist) > 50) {
            float[] value = new float[9];
            matrix.getValues(value);
            Log.i(TAG, "偏移量" + value[Matrix.MTRANS_X] + ":" + value[Matrix.MTRANS_Y]);
//			float scale = value[Matrix.MSCALE_X];//原来的放缩量
            float scale = mNewDist / mOldDist;
            float px = (event.getX(0) + event.getX(1)) / 2;
            float py = (event.getY(0) + event.getY(1)) / 2;
//			if (mOldDist > mNewDist) {
//				scale -= Math.abs(mNewDist) / Math.abs(mOldDist);//计算现在的放缩量
//				Log.i(TAG, "缩小" + scale + ":" + Math.abs(mNewDist -
//						mOldDist));
//			} else {
//				scale += Math.abs(mNewDist) / Math.abs(mOldDist);//计算现在的放缩量
//				Log.i(TAG, "放大" + scale);
//			}
            if (scale < MIN_SCALE) {//如果放缩量小于最低的就置为最低放缩比
                scale = MIN_SCALE;

            } else if (scale > MAX_SCALE) {//如果放缩量大于最高的就置为最高放缩比
                scale = MAX_SCALE;
            }
//			if (scale == MIN_SCALE) {//如果放缩量为最小就把矩阵重置
//				matrix.reset();
//			} else {
            scale = scale / value[Matrix.MSCALE_X];//计算出相对的放缩量，使矩阵的放缩量为放缩到计算出来的放缩量。
            matrix.postScale(scale, scale, px, py);
            //}
            Log.i(TAG, "" + scale / value[Matrix.MSCALE_X] + ":" + Math.abs(mNewDist - mOldDist));
            //mOldDist = mNewDist;
            post(this);
        }
    }

    private boolean isTextArea(MotionEvent event) {
        if (wordsTemp.isEmpty()) {
            return false;
        }
        float x0 = event.getX(0);
        float y0 = event.getY(0);
        float x1 = event.getX(1);
        float y1 = event.getY(1);

        if (startPoint0 == null) {
            startPoint0 = new Point();
            startPoint1 = new Point();
            center = new float[2];
        }

        startPoint0.x = (int) x0;
        startPoint0.y = (int) y0;
        startPoint1.x = (int) x1;
        startPoint1.y = (int) y1;

        Word word = wordsTemp.get(wordsTemp.size() - 1);
        RectF rectF = word.getRectF();

        //	Matrix inMatrix = new Matrix();
        Matrix matrix = word.getMatrix();
        matrix.mapPoints(center, new float[]{rectF.centerX(), rectF.centerY()});
        //matrix.invert(inMatrix);
        return false;
    }

    private void initdraw(MotionEvent ev) {
        switch (currentDraw) {
            case DRAW_PATH:
                initline(ev);
                break;
            case DRAW_WORD:
                //wordEven(event);
                break;
            case DRAW_MOSAIC:
                initMosaic(ev);
                break;
            case DRAW_NOTHING:
                break;
            default:
                break;
        }

        post(this);
    }

    private void initMosaic(MotionEvent ev) {
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return;
        }
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (x < mImageRect.left || x > mImageRect.right || y < mImageRect.top
                || y > mImageRect.bottom) {
            return;
        }

        float ratio = (mImageRect.right - mImageRect.left)
                / (float) mImageWidth;
        x = (int) ((x - mImageRect.left) / ratio);
        y = (int) ((y - mImageRect.top) / ratio);

        mPath = new Path();
        currentMosPath = new MosaicPath();
        currentMosPath.drawPath = new Path();
        currentMosPath.drawPath.moveTo(x, y);
        currentMosPath.paintWidth = mBrushWidth;
        mosaicPaths.add(currentMosPath);
        pathTo(ev);
    }

    private void initline(MotionEvent ev) {
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return;
        }
        int x = (int) getActiveX(ev);
        int y = (int) getActiveY(ev);
        if (x < mImageRect.left || x > mImageRect.right || y < mImageRect.top
                || y > mImageRect.bottom) {
            return;
        }

        float ratio = (mImageRect.right - mImageRect.left)
                / (float) mImageWidth;
        x = (int) ((x - mImageRect.left) / ratio);
        y = (int) ((y - mImageRect.top) / ratio);

        mPath = new Path();
        currentLinPath = new LinePath(new Path(), new Paint(paint));
        currentLinPath.getPath().moveTo(getActiveX(ev), getActiveY(ev));

        realLinePath = new LinePath(new Path(), paint);
        realLinePath.getPath().moveTo(x, y);
        pathTo(ev);
    }

    /*
    * 计算真实X坐标
    * */
    private int getRealX(float x) {
        int resultX = 0;
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            resultX = 0;
        }

        float ratio = (mImageRect.right - mImageRect.left)
                / (float) mImageWidth;
        resultX = (int) ((x - mImageRect.left) / ratio);
        return resultX;
    }

    /*
     * 计算真实Y坐标
     * */
    private int getRealY(float y) {
        int resultY;
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            resultY = 0;
        }

        float ratio = (mImageRect.bottom - mImageRect.top)
                / (float) mImageHeight;

        resultY = (int) ((y - mImageRect.top) / ratio);

        return resultY;
    }

    /*
    * 计算真实Y坐标
    * */
    private int getRealDistance(float d) {
        float ratio = (mImageRect.bottom - mImageRect.top)
                / (float) mImageHeight;
        return (int) (d / ratio);
    }

    /**
     * 松开之后需要判断：
     * 1、缩放比是否小于1
     * bitmap 上下左右边界是否在屏幕里面
     *
     * @param ev
     */
    private void eventUp(MotionEvent ev) {

        switch (currentDraw) {
            case DRAW_PATH:
                if (!scaleMode) {
                    if (mPath != null) {
                        pathTo(ev);
                        currentLinPath.setPath(mPath);
                        lineTempPaths.add(currentLinPath);
                        linePaths.add(realLinePath);
                    }
                    currentLinPath = null;
                    realLinePath = null;
                    mPath = null;
                }
                break;
            case DRAW_WORD:
                //wordEven(event);
                break;
        }
        float[] value = new float[9];
        matrix.getValues(value);
        Log.i(TAG, "缩放量" + value[Matrix.MSCALE_X]);
        float scalex = value[Matrix.MSCALE_X];//原来的放缩量
        if (scalex < 1) {
            matrix.reset();
        }
        post(this);
    }

    //只有一个移动的动作，都需要在这个里面完成
    private void drawEvent(MotionEvent ev, float dx, float dy) {
        switch (currentDraw) {
            case DRAW_PATH:
                drawline(ev);
                break;
            case DRAW_WORD:
                wordEven(ev, dx, dy);
                break;
            case DRAW_MOSAIC:
                mosaicEven(ev);
                break;
            case DRAW_NOTHING:
                drag(ev, dx, dy);
                break;
            default:
                break;
        }
        post(this);
    }

    private void mosaicEven(MotionEvent ev) {
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return;
        }
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (x < mImageRect.left || x > mImageRect.right || y < mImageRect.top
                || y > mImageRect.bottom) {
            return;
        }
        float ratio = (mImageRect.right - mImageRect.left)
                / (float) mImageWidth;
        x = (int) ((x - mImageRect.left) / ratio);
        y = (int) ((y - mImageRect.top) / ratio);

        currentMosPath.drawPath.lineTo(x, y);
        pathTo(ev);
        updatePathMosaic();
        post(this);
    }

    private void wordEven(MotionEvent ev, float dx, float dy) {
        //移动最后一个文字
        if (wordsTemp.isEmpty()) {
            return;
        }
        Word word = wordsTemp.get(wordsTemp.size() - 1);
        Matrix matrix = word.getMatrix();
        matrix.postTranslate(dx, dy);

        Word wordReal = words.get(words.size() - 1);
        Matrix matrixReal = wordReal.getMatrix();
        matrixReal.postTranslate(getRealDistance(dx), getRealDistance(dy));

        post(this);
    }


    /**
     * 刷新绘画板
     */
    private void updatePathMosaic() {

        if (mBitmapCopy != null) {
            mBitmapCopy.recycle();
        }
        mBitmapCopy = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);

        //触摸层，主要是生成指尖路径
        Bitmap bmTouchLayer = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10));
        paint.setStrokeWidth(30);
        paint.setColor(Color.BLUE);

        Canvas canvas = new Canvas(bmTouchLayer);
        for (MosaicPath path : mosaicPaths) {
            Path pathTemp = path.drawPath;
            int drawWidth = path.paintWidth;
            paint.setStrokeWidth(drawWidth);
            canvas.drawPath(pathTemp, paint);
        }

        //绘制马赛克图层
        canvas.setBitmap(mBitmapCopy);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawBitmap(bmCoverLayer, 0, 0, null);

        paint.reset();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bmTouchLayer, 0, 0, paint);
        paint.setXfermode(null);
        canvas.save();

        bmTouchLayer.recycle();
    }

    private void drag(MotionEvent ev, float dx, float dy) {
        matrix.postTranslate(dx * 2 / 3, dy * 2 / 3);
        post(this);

    }

    private void drawline(MotionEvent event) {
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (x < mImageRect.left || x > mImageRect.right || y < mImageRect.top
                || y > mImageRect.bottom) {
            return;
        }

        float ratio = (mImageRect.right - mImageRect.left)
                / (float) mImageWidth;
        x = (int) ((x - mImageRect.left) / ratio);
        y = (int) ((y - mImageRect.top) / ratio);

        if (currentLinPath == null) {
            currentLinPath = new LinePath(new Path(), paint);
        }

        currentLinPath.getPath().lineTo(event.getX(), event.getY());

        if (realLinePath == null) {
            realLinePath = new LinePath(new Path(), paint);
        }

        realLinePath.getPath().lineTo(x, y);
        if (mPath == null) {
            mPath = new Path();
        }
        pathTo(event);
    }

    /**
     * 缩放处理
     *
     * @param event
     * @return
     */
    private boolean nothingEvent(MotionEvent event) {
        int count = event.getPointerCount();
        if (count > 1) {
            int action = event.getAction();
            action = action & MotionEvent.ACTION_MASK;
            switch (action) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    Log.i(TAG, "ACTION_DOWN");
                    mOldDist = getDistOfTowPoints(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "ACTION_MOVE");
                    float mNewDist = getDistOfTowPoints(event);
                    if (Math.abs(mNewDist - mOldDist) > 50) {
                        float[] value = new float[9];
                        matrix.getValues(value);
                        Log.i(TAG, "偏移量" + value[Matrix.MTRANS_X] + ":" + value[Matrix.MTRANS_Y]);
                        float scale = value[Matrix.MSCALE_X];//原来的放缩量
                        float px = (event.getX(0) + event.getX(1)) / 2;
                        float py = (event.getY(0) + event.getY(1)) / 2;
                        if (mOldDist > mNewDist) {
                            scale -= Math.abs(mNewDist - mOldDist) / 500f;//计算现在的放缩量
                            // Log.i(TAG, "缩小" + scale + ":" + Math.abs(mNewDist -
                            // mOldDist));
                        } else {
                            scale += Math.abs(mNewDist - mOldDist) / 500f;//计算现在的放缩量
                            // Log.i(TAG, "放大" + scale);
                        }
                        if (scale < MIN_SCALE) {//如果放缩量小于最低的就置为最低放缩比
                            scale = MIN_SCALE;

                        } else if (scale > MAX_SCALE) {//如果放缩量大于最高的就置为最高放缩比
                            scale = MAX_SCALE;
                        }
                        if (scale == MIN_SCALE) {//如果放缩量为最小就把矩阵重置
                            matrix.reset();
                        } else {
                            scale = scale / value[Matrix.MSCALE_X];//计算出相对的放缩量，使矩阵的放缩量为放缩到计算出来的放缩量。
                            matrix.postScale(scale, scale, px, py);
                        }
                        Log.i(TAG, "" + scale / value[Matrix.MSCALE_X] + ":" + Math.abs(mNewDist - mOldDist));
                        mOldDist = mNewDist;
                        post(this);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * 获取两点之间的距离
     */
    private float getDistOfTowPoints(MotionEvent event) {
        float x0 = event.getX(0);
        float y0 = event.getY(0);
        float x1 = event.getX(1);
        float y1 = event.getY(1);
        float lengthX = Math.abs(x0 - x1);
        float lengthY = Math.abs(y0 - y1);
        return (float) Math.sqrt(lengthX * lengthX + lengthY * lengthY);
    }


    private void pathTo(MotionEvent event) {
        Point pointD = new Point((int) getActiveX(event), (int) getActiveY(event));
        calculationRealPoint(pointD, matrix);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPath.moveTo(pointD.x, pointD.y);
        } else {
            mPath.lineTo(pointD.x, pointD.y);
        }
    }

    public void calculationRealPoint(Point point, Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        int sX = point.x;
        int sY = point.y;
        point.x = (int) ((sX - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X]);
        point.y = (int) ((sY - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y]);
    }

    /**
     * 显示popupWindow输入文字
     */
    private void showPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }

        View contentView = LayoutInflater.from(getContext()).inflate(
                R.layout.popup_input, null);
        popupWindow = new InputTextWindow(contentView, (int) screenWidth, (int) screenHeight, true);
        popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Context context = getContext();
                if (context instanceof PhotoEditActivity) {
                    ((PhotoEditActivity) context).showBar();
                }
            }
        });
        popupWindow.setOnConfrimListener(new InputTextWindow.OnConfrimListener() {
            @Override
            public void onConfrim(String text, int color) {
                L.i("获取文字：", text);
                intWordDrawLayout(text, color);
                initWordDrawReal(text, color);
                post(CameraSurfaceView.this);
            }
        });
        popupWindow.showAtLocation(this, Gravity.BOTTOM, 0, 0);
    }

    private void initWordDrawReal(String text, int color) {
        TextPaint key = new TextPaint();
        key.setAntiAlias(true);
        key.setStyle(Style.FILL);
        key.setTextSize(getRealDistance(WORD_SIZE));
        key.setColor(color);
        StaticLayout layout = new StaticLayout(text, key, mImageWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);

        Paint paintRec = new Paint();
        paintRec.setStrokeWidth(getRealDistance(2));
        paintRec.setAntiAlias(true);
        paintRec.setStyle(Style.STROKE);
        paintRec.setColor(color);

        float textWidth = getWordsWidthReal(key, text);
        float textHeight = getWordsHeight(key, text);


        float realY = (mImageHeight - textHeight) / 2;
        float realX = (mImageWidth - textWidth) / 2;
        int dx = 10;
        RectF rectFR = new RectF(realX - dx, realY, realX + textWidth + dx, mImageHeight / 2 + textHeight / 2);

        Word worReal = new Word(realX, realY, key, paintRec, text, layout);
        worReal.setRectF(rectFR);
        Matrix matrixReal = new Matrix();
        worReal.setMatrix(matrixReal);
        words.add(worReal);
    }


    private void intWordDrawLayout(String text, int color) {
        TextPaint key = new TextPaint();
        key.setAntiAlias(true);
        key.setStyle(Style.FILL);
        key.setTextSize(WORD_SIZE);
        key.setColor(color);
        StaticLayout layout = new StaticLayout(text, key, (int) screenWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);

        Paint paintRec = new Paint();
        paintRec.setStrokeWidth(2);
        paintRec.setAntiAlias(true);
        paintRec.setStyle(Style.STROKE);
        paintRec.setColor(color);

        textWidth = getWordsWidth(key, text);
        textHeight = getWordsHeight(key, text);
        //text的left坐标点
        textsLeft = (screenWidth - textWidth) / 2;
        //text的top坐标点
        textTop = screenHeight / 2 - textHeight / 2;
        //框与文字间间隙
        textMargin = 10;
        RectF rectF = new RectF(textsLeft - textMargin, screenHeight / 2 - textHeight / 2, textsLeft + textWidth + textMargin, screenHeight / 2 + textHeight / 2);
        Word wor = new Word(textsLeft, textTop, key, paintRec, text, layout);
        wor.setRectF(rectF);
        Matrix matrix = new Matrix();
        wor.setMatrix(matrix);
        wordsTemp.add(wor);
    }

    private int getWordsWidth(TextPaint paint, String s) {
        wordBeyondCount = 0;//一行文字宽度超出屏幕宽度的次数
        int width = 0;
        if (TextUtils.isEmpty(s)) {
            return width;
        }
        String[] arrs = null;
        if (s.contains("\n")) {
            arrs = s.split("\n");
        } else if (s.contains("\r\n")) {
            arrs = s.split("\r\n");
        }

        if (arrs != null && arrs.length > 0) {
            int w;
            int len = arrs.length;
            for (int i = 0; i < len; i++) {
                String line = arrs[i];
                w = (int) paint.measureText(line);
                if (w > screenWidth) {
                    wordBeyondCount++;
                }
                width = Math.max(w, width);
            }
        } else {
            width = (int) paint.measureText(s);
        }

        return width;

    }

    private int getWordsWidthReal(TextPaint paint, String s) {
        wordBeyondCount = 0;//一行文字宽度超出屏幕宽度的次数
        int width = 0;
        if (TextUtils.isEmpty(s)) {
            return width;
        }
        String[] arrs = null;
        if (s.contains("\n")) {
            arrs = s.split("\n");
        } else if (s.contains("\r\n")) {
            arrs = s.split("\r\n");
        }

        if (arrs != null && arrs.length > 0) {
            int w;
            int len = arrs.length;
            for (int i = 0; i < len; i++) {
                String line = arrs[i];
                w = (int) paint.measureText(line);
                if (w > mImageWidth) {
                    wordBeyondCount++;
                }
                width = Math.max(w, width);
            }
        } else {
            width = (int) paint.measureText(s);
        }

        return width;

    }

    private int getWordsHeight(TextPaint paint, String s) {
        int height = 0;
        if (TextUtils.isEmpty(s)) {
            return height;
        }
        Paint.FontMetrics fm = paint.getFontMetrics();
        float mFontHeight = (int) (Math.ceil(fm.descent - fm.top));// 获得每行高度


        String[] arrs = null;
        if (s.contains("\n")) {
            arrs = s.split("\n");
        } else if (s.contains("\r\n")) {
            arrs = s.split("\r\n");
        }

        if (arrs != null && arrs.length > 0) {
            int len = arrs.length;
            height = (int) (mFontHeight * (len + wordBeyondCount));
        } else {
            height = (int) mFontHeight * wordBeyondCount;
        }
        return height;
    }

    @Override
    public void run() {
        if (mBitmap != null) {
            SurfaceHolder surfaceHolder = getHolder();
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) {
                return;
            }
            canvas.drawColor(Color.BLACK);
            canvas.save();
            canvas.setMatrix(matrix);
            canvas.drawBitmap(mBitmap, null, mImageRect, null);
            if (mBitmapCopy != null) {
                canvas.drawBitmap(mBitmapCopy, null, mImageRect, null);
            }
            drawLine(canvas, lineTempPaths);
            canvas.restore();
            drawText(canvas, wordsTemp);
            drawCurrent(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }


    private void drawLine(Canvas canvas, ArrayList<LinePath> linePaths2) {
        for (LinePath linePath : linePaths2) {
            canvas.drawPath(linePath.getPath(), linePath.getPaint());
        }

    }

    private void drawText(Canvas mCanvas2, ArrayList<Word> words2) {
        for (Word word : words2) {
            mCanvas2.save();
            mCanvas2.setMatrix(word.getMatrix());
            if (word.getLayout() != null) {
                mCanvas2.translate(word.getLeft(), word.getTop());//平移画布,StaticLayout默认是从0，0开始画
                word.getLayout().draw(mCanvas2);

                if (currentDraw == DRAW_WORD) {
                    mCanvas2.translate(-word.getLeft(), -word.getTop());//挪回来
                    RectF rectF = word.getRectF();
                    mCanvas2.drawRect(rectF, word.getPaintRect());
                }
            } else {
                mCanvas2.drawText(word.getWordString(), word.getLeft(), word.getTop(), word.getPaint());

                if (currentDraw == DRAW_WORD) {
                    RectF rectF = word.getRectF();
                    mCanvas2.drawRect(rectF, word.getPaintRect());
                }
            }

            mCanvas2.restore();
        }

    }

    /**
     * 画当前的图形
     *
     * @param canvas
     */
    private void drawCurrent(Canvas canvas) {
        switch (currentDraw) {
            case DRAW_PATH:
                if (currentLinPath != null) {
                    canvas.drawPath(currentLinPath.getPath(), currentLinPath.getPaint());
                }
                break;
            case DRAW_MOSAIC:

                break;
            default:
                break;
        }
    }

    /**
     * 矢量旋转函数，求出与结束定点的x，y距离
     *
     * @param px      x分量
     * @param py      y分量
     * @param ang     旋转角度
     * @param isChLen 是否改变长度
     * @param newLen  新长度
     * @return
     */
    public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {

        double mathstr[] = new double[2];
        // double len = Math.sqrt(px*px + py*py);
        // double a = Math.acos(px/len);
        // double vx = Math.cos(a+ang)*len
        // double vy = Math.sin(a+ang)*len
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    public int getCurrentDraw() {
        return currentDraw;
    }

    public void setCurrentDraw(int currentDraw) {
        this.currentDraw = currentDraw;
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else if (currentDraw == DRAW_WORD) {
            showPop();
        } else if (currentDraw == DRAW_MOSAIC) {
            //需要绘制马赛克的时候，不能让缩放，要不然路径也会跟着变
            matrix.reset();
            post(this);
        }
    }


    /**
     * 返回一个画完的图片
     *
     * @return
     */
    public Bitmap getResultBitmap() {
        mCanvas = new Canvas(mBitmap);
        if (mBitmapCopy != null) {
            mCanvas.drawBitmap(mBitmapCopy, 0, 0, null);
        }
        drawLine(mCanvas, linePaths);
        drawText(mCanvas, words);
        return mBitmap;
    }

    public void setOriginBitmap(String uri, final ImageSize imageSize) {
        Glide.with(ContextHelper.getContext())
            .load(uri)
            .asBitmap()
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource,
                    GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null){
                        CameraSurfaceView.this.mBitmap = resource.copy(Bitmap.Config.RGB_565, true);
                        mImageWidth = mBitmap.getWidth();
                        mImageHeight = mBitmap.getHeight();
                        screenWidth = TDevice.getScreenWidth();
                        screenHeight = TDevice.getScreenHeight();
                        int imageLeft = (int) ((screenWidth - imageSize.getWidth()) / 2);
                        int imageTop = (int) ((screenHeight - imageSize.getHeight()) / 2);
                        int imageRight = imageLeft + imageSize.getWidth();
                        int imageBottom = imageTop + imageSize.getHeight();
                        mImageRect = new Rect(imageLeft, imageTop, imageRight, imageBottom);
                        Bitmap bitmapMosaic = getMosaic(resource);
                        setMosaicResource(bitmapMosaic);
                        init();
                        post(CameraSurfaceView.this);
                    }
                }
            });

    }

    private void init() {
        mPath = new Path();
        realLinePath = new LinePath(new Path(), new Paint(paint));
        linePaths = new ArrayList<>();
        lineTempPaths = new ArrayList<>();
        currentLinPath = new LinePath(new Path(), new Paint(paint));

    }


    /**
     * 马赛克效果(Native)
     *
     * @param bitmap 原图
     * @return 马赛克图片
     */
    public static Bitmap getMosaic(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //马赛克半径
        int radius = 100;


        Bitmap mosaicBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mosaicBitmap);

        int horCount = (int) Math.ceil(width / (float) radius);
        int verCount = (int) Math.ceil(height / (float) radius);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        for (int horIndex = 0; horIndex < horCount; ++horIndex) {
            for (int verIndex = 0; verIndex < verCount; ++verIndex) {
                int l = radius * horIndex;
                int t = radius * verIndex;
                int r = l + radius;
                if (r > width) {
                    r = width;
                }
                int b = t + radius;
                if (b > height) {
                    b = height;
                }
                int color = bitmap.getPixel(l, t);
                Rect rect = new Rect(l, t, r, b);
                paint.setColor(color);
                canvas.drawRect(rect, paint);
            }
        }
        canvas.save();

        return mosaicBitmap;
    }

    /**
     * 设置马赛克样式资源
     *
     * @param bitmap 样式图片资源
     */
    public void setMosaicResource(Bitmap bitmap) {
        if (bmCoverLayer != null) {
            bmCoverLayer.recycle();
        }

        bmCoverLayer = getBitmap(bitmap);
        updatePathMosaic();

        invalidate();
    }


    private Bitmap getBitmap(Bitmap bit) {
        Bitmap bitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bit, 0, 0, null);
        canvas.save();
        return bitmap;
    }


    public boolean isCanDraw() {
        return canDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    /**
     * 清除对应的类型的最新的一个图形
     */
    public void back() {
        switch (currentDraw) {
            case DRAW_WORD:
                if (words.size() - 1 >= 0) {
                    words.remove(words.size() - 1);
                }
                if (wordsTemp.size() - 1 >= 0) {
                    wordsTemp.remove(wordsTemp.size() - 1);
                }
                break;
            case DRAW_PATH:
                if (linePaths.size() - 1 >= 0) {
                    linePaths.remove(linePaths.size() - 1);
                }

                if (lineTempPaths.size() - 1 >= 0) {
                    lineTempPaths.remove(lineTempPaths.size() - 1);
                }
                break;
            case DRAW_MOSAIC:
                if (mosaicPaths.size() > 0) {
                    mosaicPaths.remove(mosaicPaths.size() - 1);
                    updatePathMosaic();
                }
                break;
            case DRAW_NOTHING:
                Builder builder = new Builder(getContext());
                builder.setTitle("清除");
                builder.setMessage("是否要清除全部内容？");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        words.clear();
                        mosaicPaths.clear();
                        linePaths.clear();

                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            default:
                break;
        }
        post(this);
    }

    public void setTextSize(float textSize) {
        paint.setTextSize(textSize);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }


    public void setOptionListener(OnOptionListener optionListener) {
        this.optionListener = optionListener;
    }

    public interface OnOptionListener {
        //开始绘制，需要将标题栏和工具栏收起来
        void onStartDraw();

        //结束绘制，需要展现标题栏和工具栏
        void OnEndDraw();

        //一次轻击事件
        void ontap();
    }


}
