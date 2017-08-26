package app.demo.weibotestdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.activity.publish_dynamic.PublishDynamicActivity;
import app.demo.weibotestdemo.app_manager.MyApp;

/**
 * Created by 99538 on 2017/7/26.
 */

public class ActivityPublishDynamicAdapter extends RecyclerView.Adapter {

    private List<String> mList;
    private PublishDynamicActivity.StartPicturePickerListener mListener;
    private int mImageSize;

    public ActivityPublishDynamicAdapter(List<String> list, PublishDynamicActivity.StartPicturePickerListener listener) {
        mList = list;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mImageSize = parent.getMeasuredWidth() / 4;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_publish_dynamic, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder) holder;
        if(mList.size() == position) {
            vh.mPickedImage.setImageResource(R.drawable.ic_picture_add);
            vh.mPickedImage.setClickable(true);
            vh.mDeleteImage.setVisibility(View.GONE);
        } else {
            Glide.with(MyApp.getContext()).load(mList.get(position)).into(vh.mPickedImage);
            vh.mPickedImage.setClickable(false);
            vh.mDeleteImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @MyApp.ViewResId(R.id.picked_image) private ImageView mPickedImage;
        @MyApp.ViewResId(R.id.delete_picked) private ImageView mDeleteImage;

        public ViewHolder(View itemView) {
            super(itemView);
            MyApp.ViewResId(this, itemView);
            mPickedImage.setLayoutParams(new FrameLayout.LayoutParams(mImageSize, mImageSize));
            mPickedImage.setOnClickListener(this);
            mDeleteImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.picked_image: {
                    mListener.startPicturePicker();
                    break;
                }
                case R.id.delete_picked: {
                    // 这里不能直接移除position的对象,
                    // 因为是匿名类所以得用final修饰position,
                    // 即使notify了, position的值不会改变, 移除会导致item错乱程序crash
                    mList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    break;
                }
            }
        }
    }

}
