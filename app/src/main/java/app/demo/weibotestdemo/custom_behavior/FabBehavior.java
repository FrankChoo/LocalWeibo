package app.demo.weibotestdemo.custom_behavior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 99538 on 2017/4/19.
 */

public class FabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private AnimatorSet mAnimatorSet;

    public FabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**设置依赖的控件*/
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        // 依赖控件
        if (dependency instanceof SwipeRefreshLayout) {
            // 初始化属性
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild,
                                       View target, int nestedScrollAxes) {

        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    /*@Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && sinceDirectionChange < 0 || dy < 0 && sinceDirectionChange > 0) {
            child.animate().cancel();
            sinceDirectionChange = 0;
        }
        sinceDirectionChange += dy;
        if (sinceDirectionChange > child.getHeight() && child.getVisibility() == View.VISIBLE) {
            hide(child);
        } else if (sinceDirectionChange < 0 && child.getVisibility() == View.GONE) {
            show(child);
        }
    }*/

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        //上滑
        if (dyConsumed > 0 && dyUnconsumed == 0) {
            setAnimator(child, true);
        }

        //到了边界还在上滑
        if (dyConsumed == 0 && dyUnconsumed > 0) {
            setAnimator(child, true);
        }

        //下滑
        if (dyConsumed < 0 && dyUnconsumed == 0) {
            setAnimator(child, false);
        }

        //到了边界, 还在下滑
        if (dyConsumed == 0 && dyUnconsumed < 0) {
            setAnimator(child, false);
        }

    }

    /**设置属性动画*/
    public void setAnimator(final FloatingActionButton fab, final boolean isUp) {
        //当且仅当处于 上滑状态, Animator动画结束, 且fab为可见状态时才执行下列方法
        if (isUp && mAnimatorSet == null && fab.getVisibility() == View.VISIBLE) {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    ObjectAnimator.ofFloat(fab, "scaleX", 1, 0.0f),
                    ObjectAnimator.ofFloat(fab, "scaleY", 1, 0.0f)
            );
            mAnimatorSet.setDuration(200).start();
        //当且仅当 处于下滑状态, Animator动画结束, 且fab为不可见时才执行下列方法
        } else if(!isUp && mAnimatorSet == null && fab.getVisibility() == View.INVISIBLE)  {
            mAnimatorSet = new AnimatorSet();
            fab.setVisibility(View.VISIBLE);
            mAnimatorSet.playTogether(
                    ObjectAnimator.ofFloat(fab, "scaleX", 0, 1.0f),
                    ObjectAnimator.ofFloat(fab, "scaleY", 0, 1.0f)
            );
            mAnimatorSet.setDuration(200).start();
        }

        if (mAnimatorSet != null) {
            mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isUp) {
                        fab.setVisibility(View.INVISIBLE);
                    }
                    mAnimatorSet = null;
                }
            });
        }
    }


}
