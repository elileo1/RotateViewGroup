package cn.leo.rotateviewgroup;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
/**
 * @description: 自定义旋转木马ViewGroup
 * @author: fanrunqi@qq.com
 * @date: 2018/7/30 11:18
 */
public class RotateViewGroup extends ViewGroup {
    int ChildViewCount;//子view个数
    float ovaLength;//椭圆周长
    float quarterOval;//椭圆的1/ChildViewCount长度
    int TouchSlop;//最小滑动距离
    PathMeasure pathMeasure;//用来计算显示坐标
    int itemViewWidth, itemViewHeight;//子view的宽高
    int ViewGroupWidth, ViewGroupHeight;//ViewGroup的宽高
    float mPosX, mPosY;//手指按下的位置
    float mLPosX;//上次移动的x位置
    float distance = 0;//随着手指滑动的距离
    Boolean isLeft = true;//是否左滑动
    float MaximumZoom = 0.8f;//最大缩放

    public RotateViewGroup(Context context) {
        super(context);
        init(context);
    }
    public RotateViewGroup(Context context, AttributeSet s) {
        super(context, s);
        init(context);
    }
    /**
     * 初始化
     */
    private void init(Context context) {
        setWillNotDraw(false);//使ViewGroup调用onDraw方法
        TouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();//系统所认为的最小滑动距离
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        getViewParams();
        ChildViewCount = getChildCount();
        for (int i = 0; i < ChildViewCount; i++) {
            View v = getChildAt(i);
            v.layout(left, top, left + itemViewWidth, top + itemViewHeight);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOval(canvas);
        drawViewPosition();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLPosX = mPosX = event.getX();
                mPosY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mCurPosX = event.getX();
                if ((mCurPosX - mLPosX) > 3) {//向右滑动
                    distance = mCurPosX - mLPosX;
                    isLeft = false;
                } else if ((mLPosX - mCurPosX) > 3) {
                    distance = mLPosX - mCurPosX;
                    isLeft = true;
                }
                mLPosX = mCurPosX;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - mPosX) > TouchSlop) {
                    simulationScroll();//模拟滚动
                } else {//处理点击事件
                    double m = Math.pow(event.getX() - mPosX, 2) + Math.pow(event.getY() - mPosY, 2);
                    double n = Math.sqrt(m);
                    if (n < TouchSlop) {
                        handleClickEvent(event);
                    }
                }
                break;
        }
        return true;
    }
    /**
     * 处理子view点击事件
     */
    private void handleClickEvent(MotionEvent event) {
        for (int i = 0; i < ChildViewCount; i++) {
            float curX = event.getX() - getChildAt(i).getTranslationX();
            float curY = event.getY() - getChildAt(i).getTranslationY();
            if (curX >= getChildAt(i).getLeft() && curX <= getChildAt(i).getRight()) {
                if (curY >= getChildAt(i).getTop() && curY <= getChildAt(i).getBottom()) {
                    clicklistener.clickViewNo(i,getChildAt(i));
                    clickView2Position(getChildAt(i));
                    break;
                }
            }
        }
    }
    /**
     * 绘制背景椭圆
     */
    private void drawOval(Canvas canvas) {
        //绘制路径
        Paint mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#7f7a8d"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setPathEffect(new DashPathEffect(new float[]{5, 60}, 0));
        //绘制椭圆
        Path path = new Path();
        RectF rectF = new RectF(itemViewWidth / 2,
                itemViewHeight / 2,
                ViewGroupWidth - itemViewWidth / 2,
                ViewGroupHeight - itemViewHeight / 2);
        path.addArc(rectF, 0, 360);
        canvas.drawPath(path, mPaint);
        pathMeasure = new PathMeasure(path, true);
        ovaLength = pathMeasure.getLength();
        quarterOval = ovaLength / ChildViewCount;
    }
    /**
     * 绘制子view的位置
     */
    int selectViewNo = 0;
    private void drawViewPosition() {
        float[] pos = {0, 0};//用于记录坐标位置
        for (int i = 0; i < ChildViewCount; i++) {
            float loc;
            if (getChildAt(i).getTag() == null) {
                loc = quarterOval * i;
            } else {
                loc = (float) getChildAt(i).getTag();
            }
            if (isLeft) {//向左滑动
                loc += distance;
            } else {
                loc -= distance;
            }
            loc = loc % ovaLength;
            if (loc < 0) {
                loc = ovaLength + loc;
            }
            pathMeasure.getPosTan(loc, pos, null);
            if (loc >= 0 && loc <= quarterOval) {//第一区域，缩放效果
                float proportion = MaximumZoom * (loc / quarterOval) + 1;
                getChildAt(i).setScaleX(proportion);
                getChildAt(i).setScaleY(proportion);
                getChildAt(i).setX(pos[0] - itemViewWidth / 2);
                getChildAt(i).setY(pos[1] - itemViewHeight / 2 * proportion);
            } else if (loc > quarterOval && loc < 2 * quarterOval) {//第二区域，缩放效果
                float proportion = MaximumZoom * (1 - (loc % quarterOval) / quarterOval) + 1;
                getChildAt(i).setScaleX(proportion);
                getChildAt(i).setScaleY(proportion);
                getChildAt(i).setX(pos[0] - itemViewWidth / 2);
                getChildAt(i).setY(pos[1] - itemViewHeight / 2 * proportion);
            } else {//正常区域
                getChildAt(i).setScaleX(1.0f);
                getChildAt(i).setScaleY(1.0f);
                getChildAt(i).setX(pos[0] - itemViewWidth / 2);
                getChildAt(i).setY(pos[1] - itemViewHeight / 2);
            }
            if (Math.abs(loc - quarterOval) < TouchSlop) {
                if (selectViewNo != i) {
                    selectViewNo = i;
                    selectlistener.selectViewNo(i, getChildAt(i));
                }
            }
            getChildAt(i).setTag(loc);
        }
    }
    /**
     * 手指离开后模拟滚动
     */
    private void simulationScroll() {
        float difference;
        float loc = (float) getChildAt(0).getTag();
        if (loc % quarterOval > quarterOval / 2) {//向左滚动
            difference = quarterOval - loc % quarterOval;
            isLeft = true;
        } else {//向右滚动
            difference = loc % quarterOval;
            isLeft = false;
        }
        startScrool(difference,(int)(difference*0.7));
    }
    /**
     * 开始模拟滚动
     */
    float LastValue = 0;
    private void startScrool(float difference,int speed) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, difference);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(speed);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                distance = value - LastValue;
                invalidate();
                LastValue = value;
            }
        });
        animator.start();
    }
    /**
     * 获取view的参数
     */
    private void getViewParams() {
        itemViewWidth = getChildAt(0).getLayoutParams().width;
        itemViewHeight = getChildAt(0).getLayoutParams().height;
        ViewGroupHeight = getHeight();
        ViewGroupWidth = getWidth();
    }
    /**
     * 点击子view后移动其位置
     */
    private void clickView2Position(View v){
        float loc = (float)v.getTag();
        int itemNo = Math.round(loc/quarterOval);
        int speed = (int)(quarterOval*0.5);
        if(itemNo==0){
            isLeft =true;
            startScrool(quarterOval,speed);
        }else if(itemNo==2){
            isLeft =false;
            startScrool(quarterOval,speed);
        }else if(itemNo==3){
            isLeft =false;
            startScrool(quarterOval*2,speed*2);
        }
    }
    /**
     * 选择事件回调接口
     */
    rotateViewSelectListener selectlistener;
    public void setSelectListener(rotateViewSelectListener listener) {
        this.selectlistener = listener;
    }
    public interface rotateViewSelectListener {
        void selectViewNo(int i, View v);
    }
    /**
     * 点击事件回调接口
     */
    rotateViewClickListener clicklistener;
    public void setClickListener(rotateViewClickListener listener) {
        this.clicklistener = listener;
    }
    public interface rotateViewClickListener {
        void clickViewNo(int i, View v);
    }

}
