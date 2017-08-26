package app.demo.weibotestdemo.custom_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import app.demo.weibotestdemo.R;

/**
 * 描述:用于显示9宫格图片
 * 作者：FrankChoo
 */
public class NineGridImage extends FrameLayout {

    private static final float DEFUALT_SPACING = 10f;//默认的间距
    private static final int MAX_COUNT = 9;//最多能显示的图片

    private Context mContext;
    private float mSpacing = DEFUALT_SPACING;//图片之间的间距
    private int mColumns;//图片的列数
    private int mRows;//图片的函数

    private int mTotalWidth;//该自定义总宽度
    private int mSingleWidth;//每一张图片的宽度

    private List<String> mUriList = new ArrayList<>();//要显示的图片的URI集合
    private onClickImageListener mListener;//点击监听器
    private boolean mFirstLayout = true;

    public NineGridImage(Context context) {
        super(context);
        init(context);
    }

    public NineGridImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        /*WindowManager ws = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = ws.getDefaultDisplay();
        mTotalWidth = display.getWidth();
        mSingleWidth = (mTotalWidth) * 3 / 10;*/
    }

    /**
     * 设置间距
     */
    public void setSpacing(float spacing) {
        mSpacing = spacing;
    }

    /**
     * 设置图片的URI地址列表
     */
    public void loadUriList(List<String> uriList) {
        if (uriList != null) {
            mUriList.clear();
            mUriList.addAll(uriList);
            setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置点击图片的监听器
     */
    public void setOnClickImageListener(onClickImageListener listener) {
        mListener = listener;
    }

    /**
     * 当它准备好要被加入Window的时候, 执行该方法
     * 做如下判断是为了防止该View复用时出现的数据错乱的现象
     */
    @Override
    protected void onAttachedToWindow() {
        if (mUriList.size() == 0) {
            setVisibility(View.GONE);
        }
        if (getVisibility() == View.GONE) return;
        post(new Runnable() {
            @Override
            public void run() {
                showView();
            }
        });
        super.onAttachedToWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mFirstLayout) {
            mTotalWidth = getMeasuredWidth();
            mSingleWidth = (int) ((mTotalWidth - 2 * mSpacing)/3);
            mFirstLayout = false;
        }

        //勿用changed, 有时候addView之后changed不一定为true, 导致布局错乱
        if (getChildCount() != 0) {
            int childCount = getChildCount();
            if (childCount == 1) {
                //只有一张图片的时候, 图片的位置根据图片的的宽高来定
                View childView = getChildAt(0);
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                childView.layout(0, 0, childWidth, childHeight);

            } else if (childCount > 1) {
                //多于一张图片的时候, 显示固定的9宫格图片, 宽高都是mSingleWidth
                for (int i = 0; i < childCount; i++) {
                    View childView = getChildAt(i);
                    int childWidth = childView.getMeasuredWidth();
                    //设置ImageView的位置
                    int[] position = getImageColumnsAndRows(i);
                    int mLeft = (int) ((childWidth + mSpacing) * position[1]);
                    int mTop = (int) ((childWidth + mSpacing) * position[0]);
                    int mRight = mLeft + childWidth;
                    int mBottom = mTop + childWidth;
                    childView.layout(mLeft, mTop, mRight, mBottom);
                    Log.d("onLayout", "layout_children" + i);
                }
                //只有当图片超过MAX_COUNT的时候才会添加一个TextView即第十个child
                if (childCount == 10) {
                    View childView = getChildAt(childCount-1);
                    int childWidth = childView.getMeasuredWidth();
                    int mLeft = (int) ((childWidth + mSpacing) * 2);
                    int mTop = (int) ((childWidth + mSpacing) * 2);
                    int mRight = mLeft + childWidth;
                    int mBottom = mTop + childWidth;
                    childView.layout(mLeft, mTop, mRight, mBottom);
                }
            }
        }
    }

    /**刷新该自定义View*/
    private void showView() {
        removeAllViews();
        int count = mUriList.size();
        if (count == 1) {
            String url = mUriList.get(0);
            setupLayoutParams(this, mSingleWidth, mSingleWidth); //设置图片未加载出来之前该ViewGroup的宽高
            CustomImageView imageView = createImageView(0, url);
            addView(imageView); //将ImageView加入该ViewGroup中
            return;

        } else if (count <= MAX_COUNT) {
            setColumnsAndRows(count); //设置行数和列数
            setupLayoutParams(this, mTotalWidth, (int) (mSingleWidth * mRows + mSpacing * (mRows - 1))); //设置该ViewGroup的宽高

            //循环layout ImageView确定其位置
            for (int i = 0; i < count; i++) {
                String uri = mUriList.get(i);
                CustomImageView imageView = createImageView(i, uri);
                addView(imageView);//将ImageView加入该ViewGroup中
            }
            return;

        } else if (count > MAX_COUNT) {
            setColumnsAndRows(count);//设置行数和列数
            setupLayoutParams(this, mTotalWidth, (int) (mSingleWidth * mRows + mSpacing * (mRows - 1)));//设置该ViewGroup的宽高

            //循环layout ImageView确定其位置
            for (int i = 0; i < MAX_COUNT; i++) {
                String uri = mUriList.get(i);
                CustomImageView imageView = createImageView(i, uri);
                addView(imageView); //将ImageView加入该ViewGroup中
            }

            //添加超过最大显示数量的文本
            int overCount = mUriList.size() - MAX_COUNT;
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new LayoutParams(mSingleWidth, mSingleWidth));
            textView.setText("+" + String.valueOf(overCount));
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(30);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.BLACK);
            textView.getBackground().setAlpha(120);
            addView(textView);
            return;
        }
    }

    /**创建CustomImageView并且返回实例*/
    private CustomImageView createImageView(final int position, final String uri) {
        CustomImageView imageView = new CustomImageView(mContext);
        //设置ImageView预设宽高
        imageView.setLayoutParams(new LayoutParams(mSingleWidth, mSingleWidth));
        //设置图片占位符
        imageView.setImageResource(R.mipmap.image_default);
        //设置图片显示模式
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //设置图片的点击事件
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickNineImage(position, uri, mUriList);
                }
            }
        });
        loadImageUri(imageView, uri);
        return imageView;
    }

    /**根据图片个数确定行列数量*/
    private void setColumnsAndRows(int count) {
        if (count <= 3) {
            mRows = 1;
            mColumns = count;
        } else if (count <= 6) {
            mRows = 2;
            mColumns = 3;
            if (count == 4) {
                mColumns = 2;
            }
        } else {
            mColumns = 3;
            mRows = 3;
        }
    }

    /**根据ImageView在ViewGroup中的位置获取到它的行和列*/
    private int[] getImageColumnsAndRows(int childNum) {
        int[] position = new int[2];
        if (childNum < MAX_COUNT) {
            for (int i = 0; i < mRows; i++) {
                for (int j = 0; j < mColumns; j++) {
                    if ((i * mColumns + j) == childNum) {
                        position[0] = i;//行
                        position[1] = j;//列
                        break;
                    }
                }
            }
        }

        return position;
    }

    /**加载九宫格图片*/
    private void loadImageUri(final CustomImageView imageView, final String uri) {
        if (mUriList.size() == 1) {
            Glide.with(mContext).load(uri).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    int originalWidth = bitmap.getWidth();
                    int originalHeight = bitmap.getHeight();
                    int newWidth;
                    int newHeight;
                    //宽高的缩放比例为16 : 9
                    if (originalHeight > originalWidth) {
                        newWidth = mTotalWidth / 2;
                        newHeight = newWidth * 16 / 9;
                    } else {
                        newWidth = mTotalWidth * 2 / 3;
                        newHeight = newWidth * 9 / 16;
                    }
                    //重新设置该ViewGroup的宽高
                    setupLayoutParams(NineGridImage.this, newWidth, newHeight);
                    //重新设置ImageView的宽高
                    setupLayoutParams(imageView, newWidth, newHeight);
                    imageView.setImageBitmap(bitmap);
                }
            });
        } else {
            Glide.with(mContext).load(uri).into(imageView);
        }
    }

    /**设置View的宽高*/
    private void setupLayoutParams(View target, int width, int height)  {
        ViewGroup.LayoutParams params = target.getLayoutParams();
        params.width = width;
        params.height = height;
        target.setLayoutParams(params);
    }

    /**自定义一个按下时有点击特效的ImageView*/
    private static class CustomImageView extends ImageView {

        public CustomImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomImageView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Drawable drawable = getDrawable();
                    if (drawable != null) {
                        drawable.mutate().setColorFilter(Color.GRAY,
                                PorterDuff.Mode.MULTIPLY);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    Drawable drawableUp = getDrawable();
                    if (drawableUp != null) {
                        drawableUp.mutate().clearColorFilter();
                    }
                    break;
            }

            return super.onTouchEvent(event);
        }
    }

    /**回调接口*/
    public interface onClickImageListener {
        void onClickNineImage(int position, String url, List<String> urlList);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
