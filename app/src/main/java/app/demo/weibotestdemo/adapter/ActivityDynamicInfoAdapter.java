package app.demo.weibotestdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.app_manager.MyApp;
import app.demo.weibotestdemo.model.DynamicCommentModel;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 99538 on 2017/4/12.
 */

public class ActivityDynamicInfoAdapter extends RecyclerView.Adapter<ActivityDynamicInfoAdapter.ViewHolder>{

    private List<DynamicCommentModel> list;

    public ActivityDynamicInfoAdapter(List<DynamicCommentModel> list) {
        this.list = list;
    }

    @Override
    public ActivityDynamicInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_daynamic_comment, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ActivityDynamicInfoAdapter.ViewHolder holder, int position) {
        DynamicCommentModel item = list.get(position);
        Glide.with(MyApp.getContext()).load(item.getCmtHeadUri() == null
                ? R.mipmap.head_icon : item.getCmtHeadUri()).into(holder.mCmtHeadImage);
        holder.mCmtName.setText(item.getCmtName());
        holder.mCmtPublishTime.setText(item.getCmtPublishTime());
        holder.mCmtContent.setText(item.getCmtContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @MyApp.ViewResId(R.id.cmt_head_icon) private CircleImageView mCmtHeadImage;
        @MyApp.ViewResId(R.id.cmt_name) private TextView mCmtName;
        @MyApp.ViewResId(R.id.cmt_publish_time) private TextView mCmtPublishTime;
        @MyApp.ViewResId(R.id.cmt_content) private TextView mCmtContent;

        public ViewHolder(View itemView) {
            super(itemView);
            MyApp.ViewResId(this, itemView);
        }

    }
}
