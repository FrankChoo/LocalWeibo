package app.demo.weibotestdemo.custom_view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.bluemobi.dylan.photoview.library.PhotoView;

/**
 * Created by FrankChoo on 2017/8/7.
 * 拖拽消失的ViewPager
 */
public class DragDismissViewPager extends ViewPager {

    private ViewDragHelper mDragHelper;
    private boolean mIsChecked = false;

    private ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == mCurrentShowView) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
           // if (child == getChildAt(getCuu))
            return top;
        }
    };
    private PhotoView mCurrentShowView;

    public DragDismissViewPager(Context context) {
        this(context, null);
    }

    public DragDismissViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, mDragCallback);
    }

    private float mDownX;
    private float mDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 让ViewDragHelper接收一个完整的事件
                mDragHelper.processTouchEvent(ev);
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // 手指滑动的距离
                if (mIsChecked) break;
                float deltaX = Math.abs(getX() - mDownX);
                float deltaY = Math.abs(getY() - mDownY);
                if (deltaX < deltaY) {
                    mIsChecked = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                // 整个序列事件结束否让该值复原
                mIsChecked = false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsChecked) {
            mDragHelper.processTouchEvent(ev);
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    public void setCurrentShowView(PhotoView currentShowView) {
        mCurrentShowView = currentShowView;
    }

    public interface PictureDragListener {
        void onPictureClick();
        void onPictureRelease(View view);
    }
}