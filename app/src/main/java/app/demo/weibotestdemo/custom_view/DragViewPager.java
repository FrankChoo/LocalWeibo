package app.demo.weibotestdemo.custom_view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by 99538 on 2017/8/7.
 */
public class DragViewPager extends ViewPager {

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_MOVING = 1;
    public static final int STATUS_BACK = 2;
    public static final String TAG = "ScaleViewPager";

    //最多可缩小比例
    public static final float MIN_SCALE_WEIGHT = 0.25f;
    public static final int BACK_DURATION = 300;//ms
    public static final int DRAG_GAP_PX = 50;

    private int currentStatus = STATUS_NORMAL;
    private int currentPageStatus;

    float mDownX;
    float mDownY;
    float screenHeight;

    private View currentShowView;
    private VelocityTracker mVelocityTracker;
    private PictureDragListener mListener;

    public void setPictureDragListener(PictureDragListener listener) {
        mListener = listener;
    }

    public DragViewPager(Context context) {
        super(context);
        init();
    }

    public DragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setBackgroundColor(Color.BLACK);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e(TAG,"in onPageScrolled positionOffset："+positionOffset+" positionOffsetPixels:"+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                currentPageStatus = state;
            }
        });
    }


    public void setCurrentShowView(View currentShowView) {
        this.currentShowView = currentShowView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        screenHeight = b - t;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (currentStatus == STATUS_BACK)
            return false;
        Log.e("DragViewPager", "onTouchEvent");
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                addIntoVelocity(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                addIntoVelocity(ev);
                int deltaY = (int) (ev.getRawY() - mDownY);
                if (deltaY <= DRAG_GAP_PX && currentStatus!=STATUS_MOVING)
                    return super.onTouchEvent(ev);
                if (currentPageStatus != SCROLL_STATE_DRAGGING
                        && (deltaY>DRAG_GAP_PX||currentStatus==STATUS_MOVING)){
                    setupMoving(ev.getRawX(),ev.getRawY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (currentStatus!=STATUS_MOVING)
                    return super.onTouchEvent(ev);
                final float mUpX = ev.getRawX();//->mDownX
                final float mUpY = ev.getRawY();//->mDownY

                float vY = computeYVelocity();
                if (vY>=1500||Math.abs(mUpY-mDownY)>screenHeight/4){//速度有一定快，或者移动位置超过屏幕一半，那么释放
                    if (mListener != null)
                        mListener.onPictureRelease(currentShowView);
                }else {
                    setupBack(mUpX,mUpY);
                }

                break;
        }
        return super.onTouchEvent(ev);
    }

    private void setupBack(final float mUpX, final float mUpY){
        currentStatus = STATUS_BACK;
        if (mUpY!=mDownY) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mUpY, mDownY);
            valueAnimator.setDuration(BACK_DURATION);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float mY = (float) animation.getAnimatedValue();
                    float percent = (mY - mDownY) / (mUpY - mDownY);
                    float mX = percent * (mUpX - mDownX) + mDownX;
                    setupMoving(mX, mY);
                    if (mY == mDownY) {
                        mDownY = 0;
                        mDownX = 0;
                        currentStatus = STATUS_NORMAL;
                    }
                }
            });
            valueAnimator.start();
        }else if (mUpX!=mDownX){
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mUpX, mDownX);
            valueAnimator.setDuration(BACK_DURATION);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float mX = (float) animation.getAnimatedValue();
                    float percent = (mX - mDownX) / (mUpX - mDownX);
                    float mY = percent * (mUpY - mDownY) + mDownY;
                    setupMoving(mX, mY);
                    if (mX == mDownX) {
                        mDownY = 0;
                        mDownX = 0;
                        currentStatus = STATUS_NORMAL;
                    }
                }
            });
            valueAnimator.start();
        }else if (mListener !=null)
            mListener.onPictureClick();
    }

    private void setupMoving(float movingX ,float movingY) {
        if (currentShowView == null)
            return;
        Log.e("DragViewPager", "setupMoving");
        currentStatus = STATUS_MOVING;
        float deltaX = movingX - mDownX;
        float deltaY = movingY - mDownY;
        float scale = 1f;
        float alphaPercent = 1f;
        if(deltaY>0) {
            scale = 1 - Math.abs(deltaY) / screenHeight;
            alphaPercent = 1- Math.abs(deltaY) / (screenHeight/2);
        }

        ViewHelper.setTranslationX(currentShowView, deltaX);
        ViewHelper.setTranslationY(currentShowView, deltaY);
        setupScale(scale);
        setupBackground(alphaPercent);
    }

    private void setupScale(float scale) {
        scale = Math.min(Math.max(scale, MIN_SCALE_WEIGHT), 1);
        ViewHelper.setScaleX(currentShowView, scale);
        ViewHelper.setScaleY(currentShowView, scale);
    }

    private void setupBackground(float percent){
        setBackgroundColor(convertPercentToBlackAlphaColor(percent));
    }

    private int convertPercentToBlackAlphaColor(float percent){
        percent = Math.min(1, Math.max(0,percent));
        int intAlpha = (int) (percent*255);
        String stringAlpha = Integer.toHexString(intAlpha).toLowerCase();
        String color = "#"+(stringAlpha.length()<2?"0":"")+stringAlpha+"000000";
        return Color.parseColor(color);
    }

    private void addIntoVelocity(MotionEvent event){
        if (mVelocityTracker==null)
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
    }

    private float computeYVelocity(){
        float result = 0;
        if (mVelocityTracker!=null){
            mVelocityTracker.computeCurrentVelocity(1000);
            result = mVelocityTracker.getYVelocity();
            releaseVelocity();
        }
        return result;
    }

    private void releaseVelocity(){
        if (mVelocityTracker!=null){
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public interface PictureDragListener {
        void onPictureClick();
        void onPictureRelease(View view);
    }
}