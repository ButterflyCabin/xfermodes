package com.yehu.paint_xfermode.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by ButterflyCabin on 2020/5/3 12:32.
 */
public class XferdemoView extends View {

    private Paint mPaint;
    private static int W = 64;
    private static int H = 64;
    private static final int ROW_MAX = 4;   // number of samples per row

    private static final Xfermode[] sModes = {
            new PorterDuffXfermode(PorterDuff.Mode.CLEAR),//所绘制不会提交到画布上
            new PorterDuffXfermode(PorterDuff.Mode.SRC), //显示上层绘制的图像
            new PorterDuffXfermode(PorterDuff.Mode.DST), //显示下层绘制图像
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),//正常绘制显示，上下层绘制叠盖
            new PorterDuffXfermode(PorterDuff.Mode.DST_OVER), //上下层都显示，下层居上显示
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),//取两层绘制交集，显示上层
            new PorterDuffXfermode(PorterDuff.Mode.DST_IN),//取两层绘制交集，显示下层
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT), //取上层绘制非交集部分，交集部分变成透明
            new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),//取下层绘制非交集部分，交集部分变成透明
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP), //取上层交集部分与下层非交集部分
            new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),//取下层交集部分与上层非交集部分
            new PorterDuffXfermode(PorterDuff.Mode.XOR),//去除两图层交集部分
            new PorterDuffXfermode(PorterDuff.Mode.DARKEN),//取两图层全部区域，交集部分颜色加深
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),//取两图层全部区域，交集部分颜色点亮
            new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY), //取两图层交集部分，颜色叠加
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN),//取两图层全部区域，交集部分滤色
            new PorterDuffXfermode(PorterDuff.Mode.ADD),//取两图层全部区域，交集部分饱和度相加
            new PorterDuffXfermode(PorterDuff.Mode.OVERLAY) //取两图层全部区域，交集部分叠加
    };

    private static final String[] sLabels = {
            "Clear", "Src", "Dst", "SrcOver",
            "DstOver", "SrcIn", "DstIn", "SrcOut",
            "DstOut", "SrcATop", "DstATop", "Xor",
            "Darken", "Lighten", "Multiply", "Screen",
            "Add", "Overlay"
    };
    private Bitmap mDstBitmap;
    private Bitmap mSrcBitmap;
    private Paint mLabelP;
    private BitmapShader mBG;

    public XferdemoView(Context context) {
        this(context, null);
    }

    public XferdemoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XferdemoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // 设置图层画笔
        mPaint = new Paint();// 创建画笔实例
        mPaint.setColor(Color.RED);// 设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);// 设置画笔样式
        mPaint.setAntiAlias(true);// 设置画笔抗锯齿
        // 设置标题画笔
        mLabelP = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelP.setTextAlign(Paint.Align.CENTER);
        mLabelP.setTextSize(20);
        mLabelP.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (width != 0 && width > W) {
            W = width / (ROW_MAX + 1);
        }
        H = W;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         *  1、ComposeShader 组合渲染
         * 2、paint.setxfermod() 设置画笔图层混合
         * 3、PorterDuffColorFilter  颜色过滤器
         */
        // 禁止硬件加速
        canvas.drawColor(Color.WHITE);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        init_mbg();
//        xfermodes(canvas);
        xfermodes2(canvas);
    }

    private void xfermodes(Canvas canvas) {
        canvas.translate(W / 2, 20);
//        mDstBitmap = makeDst(W, H);
//        mSrcBitmap = makeSrc(W, H);
        mDstBitmap = makeDst2(2 * W / 3, 2 * H / 3);
        mSrcBitmap = makeSrc2(2 * W / 3, 2 * H / 3);
        // draw the border
        drawBackground(canvas, 0, 0);
        // draw the checker-board pattern
        drawBoard(canvas, 0, 0);
        int saveId = canvas.saveLayer(0, 0, W, H, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.translate(0, 0);
        canvas.drawBitmap(mDstBitmap, 0, 0, mPaint);
        canvas.restoreToCount(saveId);
        // draw the border
        drawBackground(canvas, W, 0);
        // draw the checker-board pattern
        drawBoard(canvas, W, 0);
        saveId = canvas.saveLayer(W, 0, W * 2, H, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.translate(W, 0);
        canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint);
        canvas.restoreToCount(saveId);
        canvas.translate(0, 35);
        int x = W;
        int y = H;
        for (int index = 0; index < sModes.length; index++) {
            x = W * (index % ROW_MAX);
            y = (H + 30) * (index / ROW_MAX) + H;
            // draw the border
            drawBackground(canvas, x, y);
            // draw the checker-board pattern
            drawBoard(canvas, x, y);
            System.out.println("forXfermodes " + index + " x = " + x + " y = " + y);
            saveId = canvas.saveLayer(x, y, x + W, y + H, mPaint, Canvas.ALL_SAVE_FLAG);
            canvas.translate(x, y);
            canvas.drawBitmap(mDstBitmap, 0, 0, mPaint);
            mPaint.setXfermode(sModes[index]);
            canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint);
            mPaint.setXfermode(null);
            canvas.restoreToCount(saveId);
            canvas.drawText(sLabels[index],
                    x + W / 2, y - mLabelP.getTextSize() / 2, mLabelP);
        }
    }

    private void drawBoard(Canvas canvas, int x, int y) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mBG);
        canvas.drawRect(x, y, x + W, y + H, mPaint);
    }

    static Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFFFFCC44);
        c.drawOval(new RectF(w / 20, h / 20, w * 3 / 4, h * 3 / 4), p);
        return bm;
    }

    // create a bitmap with a rect, used for the "src" image
    static Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFF66AAFF);
        c.drawRect(w / 3, h / 3, w * 19 / 20, h * 19 / 20, p);
        return bm;
    }

    static Bitmap makeDst2(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFFFFCC44);
        c.drawOval(new RectF(w / 20, h / 20, w, h), p);
        return bm;
    }

    // create a bitmap with a rect, used for the "src" image
    static Bitmap makeSrc2(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFF66AAFF);
        c.drawRect(0, 0, w * 19 / 20, h * 19 / 20, p);
        return bm;
    }

    private void init_mbg() {
        Bitmap bm = Bitmap.createBitmap(new int[]{0xFFFFFFFF, 0xFFCCCCCC,
                        0xFFCCCCCC, 0xFFFFFFFF}, 2, 2,
                Bitmap.Config.RGB_565);
        mBG = new BitmapShader(bm,
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        Matrix m = new Matrix();
        m.setScale(6, 6);
        mBG.setLocalMatrix(m);
    }

    private void xfermodes2(Canvas canvas) {
        canvas.translate(W / 2, 20);
        mDstBitmap = makeDst2(2 * W / 3, 2 * H / 3);
        mSrcBitmap = makeSrc2(2 * W / 3, 2 * H / 3);
        // draw the border
        drawBackground(canvas, 0, 0);
        // draw the checker-board pattern
        drawBoard(canvas, 0, 0);
        int saveId = canvas.saveLayer(0, 0, W, H, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mDstBitmap, 0, 0, mPaint);
        canvas.restoreToCount(saveId);
        // draw the border
        drawBackground(canvas, W, 0);
        // draw the checker-board pattern
        drawBoard(canvas, W, 0);
        saveId = canvas.saveLayer(W, 0, W * 2, H, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.translate(W, 0);
        canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint);
        canvas.restoreToCount(saveId);
        canvas.translate(0, 30);
        int x = W;
        int y = H;
        for (int index = 0; index < sModes.length; index++) {
            x = W * (index % ROW_MAX);
            y = (H + 30) * (index / ROW_MAX) + H;
            // draw the border
            drawBackground(canvas, x, y);
            // draw the checker-board pattern
            drawBoard(canvas, x, y);
            System.out.println("forXfermodes " + index + " x = " + x + " y = " + y);
            saveId = canvas.saveLayer(x, y, x + W, y + H, mPaint, Canvas.ALL_SAVE_FLAG);
            canvas.translate(x, y);
            canvas.drawBitmap(mDstBitmap, 0, 0, mPaint);
            mPaint.setXfermode(sModes[index]);
            canvas.drawBitmap(mSrcBitmap, W / 3, H / 3, mPaint);
            mPaint.setXfermode(null);
            canvas.restoreToCount(saveId);
            canvas.drawText(sLabels[index],
                    x + W / 2, y - mLabelP.getTextSize() / 2, mLabelP);
        }
    }

    private void drawBackground(Canvas canvas, int x, int y) {
        // draw the border
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShader(null);
        canvas.drawRect(x - 0.5f, y - 0.5f,
                x + W + 0.5f, y + H + 0.5f, mPaint);
    }

}
