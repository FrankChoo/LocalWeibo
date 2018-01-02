package app.demo.weibotestdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.activity.picturePicker.PicturePickerPresenter;
import app.demo.weibotestdemo.app_manager.MyApp;
import app.demo.weibotestdemo.model.PictureFolderModel;
import app.demo.weibotestdemo.utils.DialogUtil;

/**
 * Created by 99538 on 2017/7/25.
 */

public class DialogPickDirAdapter extends RecyclerView.Adapter{

    private PicturePickerPresenter mPresenter;

    public DialogPickDirAdapter(PicturePickerPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_picture_dir, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder vh = (ViewHolder)holder;
        PictureFolderModel item = mPresenter.fetchFolderModelList().get(position);
        if (item.getImageUriList().size() != 0) {
            Glide.with(MyApp.getContext()).load(item.getImageUriList().get(0)).into(vh.mImageView);
        }
        vh.mDirText.setText(item.getFolderName());
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.showCurrentFolder(position);
                DialogUtil.getInstance().dismissDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPresenter.fetchFolderModelList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @MyApp.ViewResId(R.id.preview_picture) private ImageView mImageView;
        @MyApp.ViewResId(R.id.folder_dir) private TextView mDirText;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            MyApp.ViewResId(this, itemView);
            this.itemView = itemView;
        }
    }

}
