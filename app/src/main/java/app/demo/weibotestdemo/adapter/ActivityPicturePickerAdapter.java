package app.demo.weibotestdemo.adapter;

import android.content.Context;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.activity.picturePicker.PicturePickerActivity;
import app.demo.weibotestdemo.activity.picturePicker.PicturePickerPresenter;
import app.demo.weibotestdemo.app_manager.MyApp;

/**
 * Created by 99538 on 2017/7/25.
 */
public class ActivityPicturePickerAdapter extends RecyclerView.Adapter {

    private List<String> mList;
    private PicturePickerPresenter mPresenter;

    public ActivityPicturePickerAdapter(List<String> list, PicturePickerPresenter presenter) {
        mList = list;
        mPresenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_picture_picker, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder)holder;
        final String uri = mList.get(position);
        Glide.with(MyApp.getContext()).load(uri).into(vh.mImageView);
        //通过遍历已选中图片的列表来设置mPick的状态
        vh.mPick.setChecked(mPresenter.fetchPickedList().contains(uri));
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vh.mPick.isChecked()){
                    vh.mPick.setChecked(false);
                    //回调给Activity已移除该图片
                    mPresenter.pictureRemove(uri);
                } else if(!vh.mPick.isChecked() && mPresenter.fetchPickedList().size() < PicturePickerActivity.MAX_PICKED_COUNT) {
                    vh.mPick.setChecked(true);
                    //回调给Activity已选中了该图片
                    mPresenter.picturePicked(uri);
                }else {
                    Toast.makeText(MyApp.getContext(), "图片选择达到最大上限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @MyApp.ViewResId(R.id.image) private ImageView mImageView;
        @MyApp.ViewResId(R.id.image_pick) private CheckableImageButton mPick;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            MyApp.ViewResId(this, itemView);
            this.itemView = itemView;
            //根据屏幕宽度, 动态的设置ImageView的尺寸
            ViewGroup.LayoutParams imageLP = mImageView.getLayoutParams();
            imageLP.width = getDisplayWidth()/3; imageLP.height = imageLP.width;
            mImageView.setLayoutParams(imageLP);

            //根据ImageView尺寸, 动态的设置Pick按钮的尺寸
            ViewGroup.LayoutParams pickLP = mPick.getLayoutParams();
            pickLP.width = imageLP.width/6; pickLP.height= pickLP.width;
            mPick.setLayoutParams(pickLP);
            mPick.setChecked(false);
        }

        private int getDisplayWidth() {
            WindowManager wm = (WindowManager) MyApp.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            return display.getWidth();
        }
    }
}
