package app.demo.weibotestdemo.custom_behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Design by FrankChoo on 2017/4/13.
 */
public class CircleImageViewBehavior extends CoordinatorLayout.Behavior<CircleImageView> {

    private int mStartYPosition; // 起始时CircleImageView中心Y轴位置
    private int mStartXPosition; // 起始时CircleImageView图片中心X轴位置

    private int mStartHeight; // CircleImageView原始高度
    private int mStartWidth; // CircleImageView原始宽度

    private float mDependencyStartPosition; // 依赖View的起始位置
    private float mPercent = 0.4f;

    private final Context mContext;

    public CircleImageViewBehavior(Context context, AttributeSet attrs) {
        mContext = context;
    }

    /**
     * 设置依赖的控件
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
        // 依赖控件
        if (dependency instanceof AppBarLayout) {
            // 初始化属性
            return true;
        } else {
            return false;
        }
    }

    /**
     * 当依赖的控件变化时
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, final CircleImageView child, View dependency) {
        initVariables(child, dependency);

        // 最大滑动距离: 起始位置-状态栏高度
        final int maxScrollDistance = (int) (mDependencyStartPosition);

        // 滑动的百分比
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

        if(Math.abs(expandedPercentageFactor) > mPercent) {
            child.setVisibility(View.INVISIBLE);
        } else {
            child.setVisibility(View.VISIBLE);
        }

        // 图片位置
        float nowYStation = mStartYPosition + dependency.getY() - child.getHeight()/2;
        float nowXStation = mStartXPosition - child.getWidth()/2;

        child.setY(nowYStation);
        child.setX(nowXStation);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = (int) (mStartHeight * (1 - Math.abs(expandedPercentageFactor)/mPercent));
        lp.height = (int) (mStartWidth * (1 - Math.abs(expandedPercentageFactor)/mPercent));
        child.setLayoutParams(lp);

        return true;
    }

    private void initVariables(CircleImageView child, View dependency) {

        // CircleImageView控件中心Y坐标
        if (mStartYPosition == 0)
            mStartYPosition = (int) (child.getY() + child.getHeight()/2);

        // CircleImageView控件中心X坐标
        if (mStartXPosition == 0)
            mStartXPosition = (int) (child.getX() + child.getWidth()/2);

        // CircleImageView原始高度
        if (mStartHeight == 0)
            mStartHeight = child.getHeight();

        // CircleImageView原始宽度
        if (mStartWidth == 0)
            mStartWidth = child.getWidth();

        // Dependency的起始位置
        if (mDependencyStartPosition == 0)
            mDependencyStartPosition = dependency.getY() + (dependency.getHeight() / 2);
    }

}
