package com.fph.arcprogressdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by fengpeihao on 2017/5/26.
 */

public class ArcProgressView extends View {

    private int max;//最大进度
    private float roundWidth;//圆环的宽度
    private Paint mPaint;
    private float progress = 0;
    private float frac = 0;
    private float preProgress;
    private int mCurrentQuota;
    private int mPreQuota;
    private float mCentre;
    private OnExplainClickListener mListener;

    public void setListener(OnExplainClickListener listener) {
        mListener = listener;
    }

    public ArcProgressView(Context context) {
        super(context);
        obtainStyledAttributes();
    }

    public ArcProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainStyledAttributes();
    }

    public ArcProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttributes();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //获取圆心的x坐标
        mCentre = getWidth() / 2;
        float radius = getWidth() / 2 - roundWidth - 8f; //圆环的半径
        RectF rectF = new RectF();//用于定义的圆弧的形状和大小的界限
        mPaint.setAntiAlias(true);  //消除锯齿

        //外圆环高亮背景
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(getResources().getColor(R.color.color_F76480));
        rectF.left = 6 + roundWidth / 2;
        rectF.top = 6 + roundWidth / 2;
        rectF.right = getWidth() - 6 - roundWidth / 2;
        rectF.bottom = getWidth() - 6 - roundWidth / 2;
        mPaint.setStrokeWidth(roundWidth + 12f); //设置圆的宽度
        //画外环高亮背景
        canvas.drawArc(rectF, 130, 280, false, mPaint);

        mPaint.setColor(getResources().getColor(R.color.color_E52C4E)); //设置圆环的颜色
        mPaint.setStrokeWidth(roundWidth); //设置圆的宽度
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShader(null);

        rectF.left = mCentre - radius - roundWidth / 2;
        rectF.top = mCentre - radius - roundWidth / 2;
        rectF.right = mCentre + radius + roundWidth / 2;
        rectF.bottom = mCentre + radius + roundWidth / 2;
        //画外环
        canvas.drawArc(rectF, 130, 280, false, mPaint);
        int colors[] = {getResources().getColor(R.color.color_FF8234), getResources().getColor(R.color.color_FFF197), getResources().getColor(R.color.color_FFDC39), getResources().getColor(R.color.color_FF8234)};
        //画进度
        if (max != 0) {
            SweepGradient sg = new SweepGradient(mCentre, mCentre, colors, new float[]{0.0f, 0.35f, 0.35f + progress / (max * 3) * frac, 0.35f + progress / max * frac});
            mPaint.setShader(sg);
            mPaint.setStrokeWidth(roundWidth - 8); //设置圆环的宽度
            canvas.drawArc(rectF, 130, 280 * (preProgress + (progress - preProgress) * frac) / max, false, mPaint);  //根据进度画圆弧
        }

        //內圆高亮背景
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.color_F76480));
        canvas.drawCircle(mCentre, mCentre, radius - 12f, mPaint);

        //画进度文字
        mPaint.setStrokeWidth(0);
        mPaint.setShader(null);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(sp2px(getContext(), 48));
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = (int) (preProgress + (progress - preProgress) * frac);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = mPaint.measureText(percent + "");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

            canvas.drawText(percent + "", mCentre - textWidth / 2, mCentre + getFontHeight(sp2px(getContext(), 48)) * 1 / 3, mPaint); //画出进度百分比
            mPaint.setTextSize(sp2px(getContext(), 14));
            canvas.drawText("¥", mCentre - (mPaint.measureText("¥") + textWidth) / 2 - 20f, mCentre + getFontHeight(sp2px(getContext(), 48)) * 1 / 3, mPaint);
            canvas.drawText("可领额度", mCentre - mPaint.measureText("可领额度") / 2, mCentre - getFontHeight(sp2px(getContext(), 48)) * 2 / 5, mPaint);
            //画说明图标
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.rule_icon);
            mPaint.setFilterBitmap(true);
            mPaint.setDither(true);
            canvas.drawBitmap(bitmap, mCentre + mPaint.measureText("可领额度") / 2 + dip2px(getContext(), 2), mCentre - dip2px(getContext(), 35), mPaint);
            //画底部背景
            float bottomY = mCentre + radius + roundWidth;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(getResources().getColor(R.color.color_D70336));
            float strokeWidth = radius / 2;
            mPaint.setStrokeWidth(strokeWidth);
            canvas.drawLine(mCentre - radius * 3 / 10, bottomY - strokeWidth / 2 - dip2px(getContext(), 2), mCentre + radius * 3 / 10, bottomY - strokeWidth / 2 - dip2px(getContext(), 2), mPaint);
            //画底部文字
            int quota = (int) (mPreQuota + (mCurrentQuota - mPreQuota) * frac);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(sp2px(getContext(), 24));
            textWidth = mPaint.measureText(quota + "") - dip2px(getContext(), 8);
            canvas.drawText(quota + "", mCentre - textWidth / 2, bottomY - getFontHeight(sp2px(getContext(), 24)) / 3, mPaint);
            mPaint.setTextSize(sp2px(getContext(), 10));
            canvas.drawText("¥", mCentre - (mPaint.measureText("¥") + textWidth) / 2 - 10f, bottomY - getFontHeight(sp2px(getContext(), 24)) / 3, mPaint);
            canvas.drawText("目前额度", mCentre - mPaint.measureText("目前额度") / 2, bottomY - getFontHeight(sp2px(getContext(), 24)) - 10f, mPaint);

            //画两侧文字
            mPaint.setTextSize(sp2px(getContext(), 12));
            canvas.drawText("0", mCentre - radius + dip2px(getContext(), 5), bottomY - getFontHeight(sp2px(getContext(), 24)) * 3 / 5, mPaint);
            canvas.drawText(max + "", mCentre + radius - mPaint.measureText(max + "") / 2, bottomY - getFontHeight(sp2px(getContext(), 24)) * 3 / 5, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(dip2px(getContext(), 220), dip2px(getContext(), 220));
        }
    }

    private void obtainStyledAttributes() {
        mPaint = new Paint();
        TypedArray typedArray = getContext().obtainStyledAttributes(R.styleable.ArcProgressView);
        roundWidth = typedArray.getDimension(R.styleable.ArcProgressView_roundWidth, dip2px(getContext(), 15));
        max = typedArray.getInteger(R.styleable.ArcProgressView_max, 500);
        typedArray.recycle();
    }

    /**
     * @param spVal
     * @return 根据设备的分辨率从 ps 的单位 转成为 px(像素)
     */
    public int sp2px(Context context, int spVal) {
        return Math.round(spVal * context.getResources().getDisplayMetrics().density);
    }

    /**
     * @param dpValue
     * @return 根据设备的分辨率从 dp的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置最大可领额度
     *
     * @param max 最大可领额度
     */
    public void setMax(int max) {
        if (max < 0) throw new IllegalArgumentException("max not less than 0");
        if (this.max == max) return;
        this.max = max;
        setAnim();
    }

    /**
     * 设置可领额度和当前额度
     *
     * @param progress
     * @param currentQuota
     */
    public void setProgressAndCurrentQuota(int progress, int currentQuota) {
        preProgress = this.progress;
        this.progress = progress;
        mPreQuota = mCurrentQuota;
        mCurrentQuota = currentQuota;
        setAnim();
    }

    /**
     * 设置变化的额度
     *
     * @param changeQuota 变化的额度
     */
    public void setChangeQuota(int changeQuota) {
        preProgress = progress;
        progress = progress + changeQuota;
        mPreQuota = mCurrentQuota;
        if (changeQuota < 0) {
            mCurrentQuota = mCurrentQuota - changeQuota;
        }
        setAnim();
    }

    /**
     * 设置当前额度
     *
     * @param currentQuota 当前额度
     */
    public void setCurrentQuota(int currentQuota) {
        if (mCurrentQuota == currentQuota) return;
        mPreQuota = mCurrentQuota;
        mCurrentQuota = currentQuota;
        setAnim();
    }

    /**
     * 设置可领额度
     *
     * @param progress 可领额度
     */
    public void setProgress(int progress) {
        if (progress < 0) throw new IllegalArgumentException("progress not less than 0");
        if (progress == this.progress) return;
        preProgress = this.progress;
        if (progress > max) {
            this.progress = max;
        } else {
            this.progress = progress;
        }
        setAnim();
    }

    /**
     * 设置最大可领额度和可领额度
     *
     * @param max      最大可领额度
     * @param progress 可领额度
     */
    public void setMaxAndProgress(int max, int progress) {
        if (max < 0) throw new IllegalArgumentException("max not less than 0");
        if (progress < 0) throw new IllegalArgumentException("progress not less than 0");
        if (progress == this.progress && this.max == max) return;
        if (this.progress > max)
            preProgress = max;
        else
            preProgress = this.progress;
        this.max = max;
        if (progress > max) {
            this.progress = max;
        } else {
            this.progress = progress;
        }
        setAnim();
    }

    private void setAnim() {
        frac = 0;
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frac = animation.getAnimatedFraction();
                postInvalidate();
            }
        });
        anim.setDuration(1000);
        anim.start();
    }

    /**
     * 获取字体高度
     *
     * @param fontSize
     * @return
     */
    public float getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }


    float cX;
    float cY;
    boolean isDown = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().getParent().requestDisallowInterceptTouchEvent(true);
        cX = mCentre + mPaint.measureText("可领额度") / 2 + dip2px(getContext(), 15);
        cY = mCentre - dip2px(getContext(), 20);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (x < cX + dip2px(getContext(), 20) && x > cX - dip2px(getContext(), 20) && y > cY - dip2px(getContext(), 20) && y < cY + dip2px(getContext(), 20)) {
                    isDown = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                if (upX < cX + dip2px(getContext(), 20) && upX > cX - dip2px(getContext(), 20) && upY > cY - dip2px(getContext(), 20) && upY < cY + dip2px(getContext(), 20) && isDown) {
                    if (mListener != null)
                        mListener.onClick();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    interface OnExplainClickListener{
        void onClick();
    }
}
