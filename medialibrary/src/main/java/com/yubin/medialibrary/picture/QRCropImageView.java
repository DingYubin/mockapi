package com.yubin.medialibrary.picture;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.yubin.medialibrary.R;
import com.yubin.medialibrary.util.UnitHelper;


/**
 * @introduce : 扫描二维码控件
 * <p>
 * creattime : 2021/11/25
 * <p>
 * author : xiongyp
 **/
@SuppressLint("AppCompatCustomView")
public class QRCropImageView extends ImageView {

    private float x_down = 0;
    private float y_down = 0;
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float oldRotation = 0;
    private Matrix matrix;
    private Matrix matrix1 = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private boolean matrixCheck = false;

    private int widthScreen;
    private int heightScreen;

    private Bitmap gintama;
    private Bitmap drawBitmap;


    private Paint paintLine;
    private Paint paintShade;
    private Paint paintCorner;

    private Rect rectTop;
    private Rect rectBottom;
    private Rect rectLeft;
    private Rect rectRight;
    private Rect cropRect;

    private int lineWidth;
    private int cornerWidth;
    private int cornerLength;
    private int paddingH;
    private Activity activity;

    private int left, top, right, bottom;

    private int rectHeight = 0;

    private int marginTop = 0;
    private int width2;

    public QRCropImageView(Activity activity, Bitmap bitmap) {
        super(activity);
        gintama = bitmap;
        this.activity = activity;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthScreen = dm.widthPixels;
        heightScreen = dm.heightPixels;
        matrix = new Matrix();

        paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLine.setColor(Color.WHITE);
        paintLine.setStyle(Paint.Style.STROKE);//设置空心
        lineWidth = (int) UnitHelper.dp2px(activity, 0.5f);
        paintLine.setStrokeWidth(lineWidth);

        paintShade = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShade.setColor(getResources().getColor(R.color.color_cc000000));

        cornerWidth = (int) UnitHelper.dp2px(activity, 4);
        cornerLength = (int) UnitHelper.dp2px(activity, 25);
        paintCorner = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCorner.setColor(Color.WHITE);
        paintCorner.setStrokeWidth(cornerWidth);

        paddingH = (int) UnitHelper.dp2px(activity, 30);

        // 初始化居中缩放
        int bw = gintama.getWidth();
        int bh = gintama.getHeight();
        float scale = Math.min(1f * widthScreen / bw, 1f * heightScreen / bh);
        Log.e("scale==", "" + scale);
        drawBitmap = scaleBitmap(gintama, scale);
        matrix.postTranslate(0, 1f * heightScreen / 2 - (bh * scale / 2));
    }



    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        canvas.save();
        canvas.drawBitmap(drawBitmap, matrix, null);
        canvas.restore();

        paddingH = (width - rectHeight) / 2;
        // 四边线
        left = paddingH;
        top = marginTop;
        right = width - paddingH;
//        bottom = (int) (height * 0.51);
        bottom = top + rectHeight;
        if (rectTop == null) {
            rectTop = new Rect(0, 0, width, top);
            rectBottom = new Rect(0, bottom, width, height);
            rectLeft = new Rect(0, top, left, bottom);
            rectRight = new Rect(right, top, width, bottom);
        }
        //绘制上下蒙层
        canvas.drawRect(rectTop, paintShade);
        canvas.drawRect(rectBottom, paintShade);
        //绘制左右蒙层
        canvas.drawRect(rectLeft, paintShade);
        canvas.drawRect(rectRight, paintShade);

        //  裁切区域矩形
        if (cropRect == null) {
            cropRect = new Rect(left, top, right, bottom);
        }
        canvas.drawRect(cropRect, paintLine);

        width2 = cornerWidth / 2;
        // 绘制四角
        canvas.drawLine(left, top + width2, left + cornerLength, top + width2, paintCorner);
        canvas.drawLine(left + width2, top, left + width2, top + cornerLength, paintCorner);

        canvas.drawLine(right, top + width2, right - cornerLength, top + width2, paintCorner);
        canvas.drawLine(right - width2, top, right - width2, top + cornerLength, paintCorner);

        canvas.drawLine(left, bottom - width2, left + cornerLength, bottom - width2, paintCorner);
        canvas.drawLine(left + width2, bottom, left + width2, bottom - cornerLength, paintCorner);

        canvas.drawLine(right, bottom - width2, right - cornerLength, bottom - width2, paintCorner);
        canvas.drawLine(right - width2, bottom, right - width2, bottom - cornerLength, paintCorner);

    }


    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    matrix1.set(savedMatrix);
                    float rotation = rotation(event) - oldRotation;
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;
                    matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
                    matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                } else if (mode == DRAG) {
                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                    matrixCheck = matrixCheck();
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }

    private boolean matrixCheck() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * drawBitmap.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * drawBitmap.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * drawBitmap.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * drawBitmap.getHeight() + f[5];
        float x4 = f[0] * drawBitmap.getWidth() + f[1] * drawBitmap.getHeight() + f[2];
        float y4 = f[3] * drawBitmap.getWidth() + f[4] * drawBitmap.getHeight() + f[5];
        // 图片现宽度
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        // 缩放比率判断
        if (width < widthScreen / 3 || width > widthScreen * 3) {
            return true;
        }
        // 出界判断
        if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
                && x3 < widthScreen / 3 && x4 < widthScreen / 3)
                || (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
                && x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
                || (y1 < heightScreen / 3 && y2 < heightScreen / 3
                && y3 < heightScreen / 3 && y4 < heightScreen / 3)
                || (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
                && y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
            return true;
        }
        return false;
    }

    // 旋转
    public void drawRotation(int rotation) {
        matrix.preRotate(rotation, (float) drawBitmap.getWidth() / 2, (float) drawBitmap.getHeight() / 2);  //要旋转的角度
        invalidate();
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 将移动，缩放以及旋转后的图层保存为新图片
    // 本例中沒有用到該方法，需要保存圖片的可以參考
    public Bitmap createCropBitmap() {
        // 创建旋转缩放样本
        Bitmap bitmap = Bitmap.createBitmap(widthScreen, heightScreen,
                Bitmap.Config.ARGB_8888); // 背景图片
        Canvas canvas = new Canvas(bitmap); // 新建画布
        canvas.drawBitmap(drawBitmap, matrix, null); // 画图片
        canvas.save(); // 保存画布
        canvas.restore();
        // 返回裁切样本
        return Bitmap.createBitmap(bitmap, cropRect.left, cropRect.bottom - (cropRect.bottom - cropRect.top)
                , cropRect.right-cropRect.left, cropRect.bottom - cropRect.top);
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin 原图
     * @param scale  缩放比例
     * @return new Bitmap
     */
    private Bitmap scaleBitmap(Bitmap origin, float scale) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        return newBM;
    }
    public int getRectHeight() {
        return rectHeight;
    }

    //二维码矩形的宽高
    public void setRectHeight(int rectHeight) {
        this.rectHeight = rectHeight;
        invalidate();
    }

    //距离顶部的高度
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        invalidate();
    }
    public int getMarginTop() {
        return marginTop;
    }


}