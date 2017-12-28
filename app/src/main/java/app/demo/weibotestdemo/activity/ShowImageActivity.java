package app.demo.weibotestdemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.app_manager.BaseActivity;
import app.demo.weibotestdemo.app_manager.MyApp;
import app.demo.weibotestdemo.custom_view.DragDismissViewPager;
import app.demo.weibotestdemo.utils.AppBarsUtil;
import app.demo.weibotestdemo.utils.BitmapUtils;
import app.demo.weibotestdemo.utils.DialogUtil;
import app.demo.weibotestdemo.utils.FileUtils;
import cn.bluemobi.dylan.photoview.library.PhotoView;
import cn.bluemobi.dylan.photoview.library.PhotoViewAttacher;

/**
 * 图片查看器
 * create by FrankChoo
 */
public class ShowImageActivity extends BaseActivity {

    /**UI控件*/
    @MyApp.ViewResId(R.id.view_pager) private DragDismissViewPager mViewPager;
    @MyApp.ViewResId(R.id.image_position) private TextView mPagerPosition;
    @MyApp.ViewResId(R.id.progress_bar) private ProgressBar mProgressBar;

    /**参数*/
    private ViewPagerAdapter mAdapter;
    private List<PhotoView> mList = new ArrayList<>();
    private List<String> mUriList;
    private int mPosition;

    /**
     * 调用该方法启动该Activity
     * @param context 上下文
     * @param position 点击哪张图片进入图片查看器
     * @param imageUriList 图片的Url集合
     */
    public static void startActivity(Context context, int position, List<String> imageUriList) {
        Intent intent = new Intent(context, ShowImageActivity.class);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("image_uri_list", (ArrayList<String>) imageUriList);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        MyApp.ViewResId(this, getWindow().getDecorView());
        setViews();
    }

    private void setViews() {
        //透明状态栏
        AppBarsUtil.newInstance(this).setStatusBarStyle(AppBarsUtil.ALL_TRANSPARENT).commit();

        mUriList = getIntent().getStringArrayListExtra("image_uri_list");
        addList();
        mPosition = getIntent().getIntExtra("position", 0);

        //加载图片
        mProgressBar.setVisibility(View.VISIBLE);
        Glide.with(this).load(mUriList.get(mPosition)).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                mList.get(mPosition).setImageBitmap(bitmap);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mPagerPosition.setText(getPagerPositionContent(mPosition));

        mViewPager = (DragDismissViewPager)findViewById(R.id.view_pager);
        mAdapter = new ViewPagerAdapter(mList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPosition);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagerPosition.setText(getPagerPositionContent(position));
                final PhotoView itemView = mList.get(position);
                mViewPager.setCurrentShowView(itemView);
                //若itemView中的图片没有内容, 则加载
                if (itemView.getDrawable() == null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    Glide.with(ShowImageActivity.this).load(mUriList.get(position)).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            itemView.setImageBitmap(bitmap);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private String getPagerPositionContent(int position) {
        int nowPager = position + 1;
        int AllPager = mUriList.size();
        String content = nowPager + "/" + AllPager;
        return content;
    }

    private void addList() {
        mList.clear();
        for (int i = 0; i < mUriList.size(); i++) {
            PhotoView photoView = new PhotoView(this);
            setPhotoViewListeners(photoView);
            mList.add(photoView);
        }
    }

    private void setPhotoViewListeners(final PhotoView photoView) {
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog();
                return false;
            }
        });
    }

    private void showDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_save_photo, null);
        DialogUtil.getInstance().displayDialogWindowWidth(this, view , Gravity.BOTTOM);
        view.findViewById(R.id.save_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
                DialogUtil.getInstance().dismissDialog();
            }
        });

    }

    private void saveImage() {
        String permissions[] = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                Glide.with(ShowImageActivity.this).load(mUriList.get(mPosition)).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //将文件保存到SD卡中,并且刷新文件管理器刷新显示
                        FileUtils fileUtils = FileUtils.getInstance();
                        File file = fileUtils.createSimpleDateNameFile(FileUtils.SD);
                        BitmapUtils.writeBitmapToFile(file, resource, 100);
                        fileUtils.notifyFileChanged(file.getPath());
                        String path = file.getPath();
                        Toast.makeText(MyApp.getContext(), "保存完毕" + path, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**创建内部类ViewPager的Adapter*/
    public static class ViewPagerAdapter extends PagerAdapter {

        private List<PhotoView> mImageList = new ArrayList<>();

        ViewPagerAdapter(List<PhotoView> children) {
            this.mImageList = children;
        }

        /**获取子级布局的数量*/
        @Override
        public int getCount() {
            return mImageList.size();
        }

        /**判断某个View对象是否为当前被添加到ViewPager容器中的对象*/
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**实例化ViewPager容器中指定的position位置需要显示的View对象*/
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mImageList.get(position);
            container.addView(view);
            return view;
        }

        /**在ViewPager中移除指定的positon位置的view对象*/
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = mImageList.get(position);
            container.removeView(view);
        }
    }


}

