package app.demo.weibotestdemo.custom_view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.demo.weibotestdemo.R;

/**
 * Created by FrankChoo on 2017/8/24.
 */
public class LoadingView extends LinearLayout {

    private Context mContext;
    private ShapeView mShapeView;
    private ImageView mShadowView;
    private TextView mLoadingText;
    private AnimatorSet mFallAnimSet;
    private AnimatorSet mThrowAnimSet;
    private boolean isStop = false;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        removeAllViews();
        /*小球*/
        mShapeView = new ShapeView(mContext);
        mShapeView.setLayoutParams(new LayoutParams(dip2px(25), dip2px(25)));
        addView(mShapeView);

        /*阴影*/
        mShadowView = new ImageView(mContext);
        mShadowView.setBackgroundResource(R.drawable.ic_circle);
        mShadowView.setLayoutParams(new LayoutParams(dip2px(30), dip2px(5)));
        //设置margin
        LayoutParams lp = (LayoutParams) mShadowView.getLayoutParams();
        lp.setMargins(0, dip2px(85), 0, 0);
        mShadowView.setLayoutParams(lp);
        addView(mShadowView);

        /*文字*/
        mLoadingText = new TextView(mContext);
        mLoadingText.setText("正在加载中...");
        mLoadingText.setPadding(0, dip2px(5), 0, 0);
        mLoadingText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mLoadingText);

        /*设置Layout的布局参数*/
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        /*尝试启动动画, post的方法会在View绘制完成之后执行*/
        post(new Runnable() {
            @Override
            public void run() {
                if (getVisibility() == VISIBLE) {
                    // startFallAnimator();
                }
            }
        });
    }

    /**
     * 执行下落动画
     */
    private void startFallAnimator() {
        if (getVisibility() != View.VISIBLE) return;
        Log.e("TAG", "startFallAnimator");
        mFallAnimSet = new AnimatorSet();
        mFallAnimSet.playTogether(
                ObjectAnimator.ofFloat(mShapeView, "translationY", 0, dip2px(80)),
                ObjectAnimator.ofFloat(mShadowView, "scaleX", 1f, 0.3f)
        );
        mFallAnimSet.setDuration(500);
        mFallAnimSet.setInterpolator(new AccelerateInterpolator());
        mFallAnimSet.start();

        mFallAnimSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShapeView.exchange();
                startThrowAnimator();
            }
        });
    }

    /**
     * 执行上抛动画
     */
    private void startThrowAnimator() {
        if (getVisibility() != View.VISIBLE) return;
        Log.e("TAG", "startThrowAnimator");
        mThrowAnimSet = new AnimatorSet();
        mThrowAnimSet.playTogether(
                ObjectAnimator.ofFloat(mShapeView, "translationY", dip2px(80), 0),
                ObjectAnimator.ofFloat(mShapeView, "rotation", 0, getRotaryAngle()),
                ObjectAnimator.ofFloat(mShadowView, "scaleX", 0.3f, 1f)
        );
        mThrowAnimSet.setDuration(500);
        mThrowAnimSet.setInterpolator(new DecelerateInterpolator());
        mThrowAnimSet.start();

        mThrowAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startFallAnimator();
            }
        });
    }

    /**
     * 获取图形旋转的角度
     */
    private int getRotaryAngle() {
        // 三角形旋转度数
        if (mShapeView.getCurrentShape() == ShapeView.SHAPE_TRIANGLE) {
            return -120;
        //正方形旋转度数
        } else if(mShapeView.getCurrentShape() == ShapeView.SHAPE_SQUARE) {
            return -90;
        }
        return 0;
    }

    /**
     * 在mdpi(160ppi)下 1 dp = 1px;
     * 在xdpi(320ppi)下 1 dp = 2px;
     * 该方法可以将我们想要获取的dp(dip)值转化为对应dpi下的px大小供控件使用
     */
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    /**
     * 当当前View可视变化时, 会回调该方法
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        Log.e("TAG", "startFallAnimator");
        if (visibility == VISIBLE) {
            // startFallAnimator();
        } else {
            isStop = true;
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    /**
     * 当当前View在从Window中移除时会回调该方法
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAllAnimator();
    }

    private void clearAllAnimator() {
        isStop = true;
        if(mFallAnimSet != null) {
            mFallAnimSet.cancel();
            mFallAnimSet = null;
        }
        if(mThrowAnimSet != null) {
            mThrowAnimSet.cancel();
            mThrowAnimSet = null;
        }
    }

    public static class ShapeView extends View {
        public static final int SHAPE_CIRCLE = 0;
        public static final int SHAPE_TRIANGLE = 1;
        public static final int SHAPE_SQUARE = 2;

        private int mCurrentShape = SHAPE_CIRCLE;
        private Paint mPaint;
        private Path mPath;

        public ShapeView(Context context) {
            this(context, null);
        }

        public ShapeView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public ShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            // 只保证是正方形
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(Math.min(width, height), Math.min(width, height));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            switch (mCurrentShape) {
                case SHAPE_CIRCLE:
                    // 画圆形
                    int center = getWidth() / 2;
                    mPaint.setColor(getResources().getColor(R.color.colorCircle));
                    canvas.drawCircle(center, center, center, mPaint);
                    break;
                case SHAPE_SQUARE:
                    // 画正方形
                    mPaint.setColor(getResources().getColor(R.color.colorSquare));
                    canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
                    break;
                case SHAPE_TRIANGLE:
                    // 画三角  Path 画路线
                    mPaint.setColor(getResources().getColor(R.color.colorTriangle));
                    if (mPath == null) {
                        // 画路径
                        mPath = new Path();
                        mPath.moveTo(getWidth() / 2, 0);
                        mPath.lineTo(0, (float) ((getWidth()/2)*Math.sqrt(3)));
                        mPath.lineTo(getWidth(), (float) ((getWidth()/2)*Math.sqrt(3)));
                        // path.lineTo(getWidth()/2,0);
                        mPath.close();// 把路径闭合
                    }
                    canvas.drawPath(mPath, mPaint);
                    break;
            }
        }

        public void exchange() {
            switch (mCurrentShape) {
                case SHAPE_CIRCLE:
                    mCurrentShape = SHAPE_SQUARE;
                    break;
                case SHAPE_SQUARE:
                    mCurrentShape = SHAPE_TRIANGLE;
                    break;
                case SHAPE_TRIANGLE:
                    mCurrentShape = SHAPE_CIRCLE;
                    break;
            }
            invalidate();
        }

        public int getCurrentShape() {
            return mCurrentShape;
        }
    }

}
