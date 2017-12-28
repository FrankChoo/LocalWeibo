package app.demo.weibotestdemo.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;
import java.util.List;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.activity.ShowImageActivity;
import app.demo.weibotestdemo.activity.dynamic_info.DynamicInfoActivity;
import app.demo.weibotestdemo.activity.main.MainPresenter;
import app.demo.weibotestdemo.activity.publish_dynamic.PublishDynamicActivity;
import app.demo.weibotestdemo.app_manager.MyApp;
import app.demo.weibotestdemo.custom_view.NineGridImage;
import app.demo.weibotestdemo.model.DynamicModel;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Frank on 2017/3/7.
 */
public class ActivityMainAdapter extends RecyclerView.Adapter {

    private final static int HEAD_VIEW = 1;
    private final static int DYNAMIC_VIEW = 2;
    private final static int FOOT_VIEW = 3;

    private LinkedList<DynamicModel> mDynamicList;
    private MainPresenter mPresenter;
    
    public ActivityMainAdapter(MainPresenter presenter, LinkedList<DynamicModel> dynamicList) {
        mPresenter = presenter;
        mDynamicList = dynamicList;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEAD_VIEW;
        } else if (position == mDynamicList.size() + 1) {
            return FOOT_VIEW;
        } else {
            return DYNAMIC_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return mDynamicList.size() + 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_own_head, parent, false);
            HeadViewHolder holder = new HeadViewHolder(view);
            return holder;
        } else if (viewType == FOOT_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_own_foot, parent, false);
            FootViewHolder holder = new FootViewHolder(view);
            return holder;

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic, parent, false);
            DynamicViewHolder holder = new DynamicViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder vh = (HeadViewHolder) holder;
            bindHeadViewHolder(vh);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder vh = (FootViewHolder) holder;
            bindFootViewHolder(vh);
        } else if (holder instanceof DynamicViewHolder) {
            DynamicViewHolder vh = (DynamicViewHolder) holder;
            bindDynamicViewHolder(vh, position);
        }
    }

    private void bindHeadViewHolder(HeadViewHolder vh) {
        vh.mUserName.setText("FrankChoo");
        vh.mUserSignature.setText("Local Weibo");
    }

    private void bindFootViewHolder(FootViewHolder vh) {
        vh.mLoadingLayout.setVisibility(mPresenter.isCanLoading() ? View.VISIBLE :View.GONE);
        vh.mLoadingFinishLayout.setVisibility(mPresenter.isCanLoading() ? View.GONE : View.VISIBLE);
    }

    private void bindDynamicViewHolder(DynamicViewHolder vh, int position) {
        //由于该Adapter设置了Header与Footer, 所以集合中的数据为Adapter中对应ViewPosition-1
        DynamicModel model = mDynamicList.get(position - 1);
        DynamicModel baseForwardItem = model.getBaseForwardModel();

        //给用户部分赋值
        Glide.with(MyApp.getContext())
                .load(model.getUserHeadUri() == null ? R.mipmap.head_icon : model.getUserHeadUri()).into(vh.mUserIcon);
        vh.mUserName.setText(model.getUserName());
        vh.mPublishTime.setText(model.getPubTime());
        vh.mPublishContent.setText(model.getPubContent());
        vh.mPublishContent.setMovementMethod(LinkMovementMethod.getInstance());//如果SpannableString设置了Click监听器需要调用该方法

        //给转发模块赋值
        if (baseForwardItem != null) {
            vh.mForwardLayout.setVisibility(View.VISIBLE);

            Glide.with(MyApp.getContext()).load(baseForwardItem.getUserHeadUri() == null
                    ? R.mipmap.head_icon : baseForwardItem.getUserHeadUri()).into(vh.mForwardIcon);
            vh.mForwardName.setText(baseForwardItem.getUserName());
            vh.mForwardContent.setText(baseForwardItem.getPubContent());
            vh.mForwardNineGridImage.loadUriList(baseForwardItem.getImageUriList());
            vh.mForwardNineGridImage.setSpacing(5);
        } else {
            vh.mNineGridImage.loadUriList(model.getImageUriList());
            vh.mForwardLayout.setVisibility(View.GONE);
        }

        //给操作区域赋值
        vh.mBtnLike.setChecked(model.isUserLike());
        vh.mLikeCount.setText(model.getLikeCount() == 0 ? "点赞" : String.valueOf(model.getLikeCount()));
        vh.mCommentCount.setText(model.getCommentCount() == 0 ? "评论" : String.valueOf(model.getCommentCount()));
        vh.mForwardCount.setText(model.getForwardCount() == 0 ? "转发" : String.valueOf(model.getForwardCount()));
    }

    /**头布局的ViewHolder*/
    public class HeadViewHolder extends RecyclerView.ViewHolder {

        @MyApp.ViewResId(R.id.head_user_name)  TextView mUserName;
        @MyApp.ViewResId(R.id.head_user_signature)  TextView mUserSignature;

        public HeadViewHolder(View view) {
            super(view);
            MyApp.ViewResId(this, view);
        }
    }

    /**尾布局的ViewHolder*/
    public class FootViewHolder extends RecyclerView.ViewHolder {

        @MyApp.ViewResId(R.id.loading_layout)  LinearLayout mLoadingLayout;
        @MyApp.ViewResId(R.id.loading_finish_layout)  LinearLayout mLoadingFinishLayout;

        public FootViewHolder(View itemView) {
            super(itemView);
            MyApp.ViewResId(this, itemView);
        }
    }

    /**动态Item的ViewHolder*/
    public class DynamicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            NineGridImage.onClickImageListener {

        /**用户发布部分*/
        @MyApp.ViewResId(R.id.user_icon)  CircleImageView mUserIcon;//用户头像
        @MyApp.ViewResId(R.id.button_menu)  CheckableImageButton mButtonMenu;//菜单按钮
        @MyApp.ViewResId(R.id.user_name)  TextView mUserName;//用户名
        @MyApp.ViewResId(R.id.publish_time)  TextView mPublishTime;//动态发布时间
        @MyApp.ViewResId(R.id.publish_content)  TextView mPublishContent;//发布内容
        @MyApp.ViewResId(R.id.nine_grid)  NineGridImage mNineGridImage;

        /**转发部分*/
        @MyApp.ViewResId(R.id.forward_layout)  LinearLayout mForwardLayout;
        @MyApp.ViewResId(R.id.forward_head_icon)  CircleImageView mForwardIcon;//被转发人的头像
        @MyApp.ViewResId(R.id.forward_name)  TextView mForwardName;//被转发人的姓名
        @MyApp.ViewResId(R.id.forward_content)  TextView mForwardContent;//被转发人的内容
        @MyApp.ViewResId(R.id.forward_nine_grid)  NineGridImage mForwardNineGridImage;//被转发人的九宫格图片

        /**操作区域*/
        @MyApp.ViewResId(R.id.checkable_button)  CheckableImageButton mBtnLike;//点赞模块中的点赞按钮(声明为了切换按钮状态)
        @MyApp.ViewResId(R.id.like_click_region) LinearLayout mLikeClickRegion;
        @MyApp.ViewResId(R.id.like_count)  TextView mLikeCount;//点赞数
        @MyApp.ViewResId(R.id.comment_click_region) LinearLayout mCommentRegion;
        @MyApp.ViewResId(R.id.comment_count)  TextView mCommentCount;//评论数
        @MyApp.ViewResId(R.id.forward_click_region) LinearLayout mForwardRegion;
        @MyApp.ViewResId(R.id.forward_count)  TextView mForwardCount;//转发数

        public DynamicViewHolder(View itemView) {
            super(itemView);
            MyApp.ViewResId(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DynamicInfoActivity.start(MyApp.getContext(), mDynamicList.get(getAdapterPosition()-1), false);
                }
            });
            setClickListener();
        }

        private void setClickListener() {
            mButtonMenu.setOnClickListener(this);
            mForwardLayout.setOnClickListener(this);
            //发布内容的九宫格模块
            mNineGridImage.setOnClickImageListener(this);
            //转发内容的九宫格模块
            mForwardNineGridImage.setOnClickImageListener(this);
            mLikeClickRegion.setOnClickListener(this);
            mCommentRegion.setOnClickListener(this);
            mForwardRegion.setOnClickListener(this);
        }

        /**初始化PopupMenu中的一些方法*/
        private void showPopupMenu(View view){
            //初始化PopupMenu菜单
            final PopupMenu popupMenu = new PopupMenu(MyApp.getContext(), view);
            Menu menu = popupMenu.getMenu();
            MenuInflater menuInflater = new MenuInflater(MyApp.getContext());
            menuInflater.inflate(R.menu.item_activity_own_page_menu, menu);
            //给菜单中的Item注册监听事件
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mButtonMenu.setChecked(false);
                    mDynamicList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    popupMenu.dismiss();
                    return true;
                }
            });
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu popupMenu) {
                    mButtonMenu.setChecked(false);
                }
            });

            popupMenu.show();
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.button_menu: {
                    mButtonMenu.setChecked(true);
                    showPopupMenu(view);
                    break;
                }
                case R.id.forward_layout: {
                    DynamicInfoActivity.start(MyApp.getContext(), mDynamicList.get(getAdapterPosition()-1).getBaseForwardModel(), false);
                    break;
                }
                case R.id.like_click_region: {
                    mBtnLike.clearAnimation();
                    if(!mBtnLike.isChecked()) {
                        setAnimator(mBtnLike);
                        mBtnLike.setChecked(true);
                        mDynamicList.get(getAdapterPosition()-1).setUserLike(true);
                        int count = Integer.parseInt(mLikeCount.getText().toString() == "点赞"
                                ? "0" : mLikeCount.getText().toString());
                        mLikeCount.setText(String.valueOf(++count));
                    } else {
                        mBtnLike.setChecked(false);
                        mDynamicList.get(getAdapterPosition()-1).setUserLike(false);
                        int count = Integer.parseInt(mLikeCount.getText().toString()) - 1;
                        mLikeCount.setText(count == 0 ? "点赞" : String.valueOf(count));
                    }
                    break;
                }
                case R.id.comment_click_region: {
                    DynamicInfoActivity.start(MyApp.getContext(), mDynamicList.get(getAdapterPosition()-1), true);
                    break;
                }
                case R.id.forward_click_region: {
                    PublishDynamicActivity.startActivity(MyApp.getContext() , mDynamicList.get(getAdapterPosition()-1));
                    break;
                }
                case R.id.send_comment: {
                    DynamicInfoActivity.start(MyApp.getContext(), mDynamicList.get(getAdapterPosition()-1), true);
                    break;
                }
                default:
                    break;
            }
        }

        @Override
        public void onClickNineImage(int position, String url, List<String> urlList) {
            ShowImageActivity.startActivity(MyApp.getContext(), position, urlList);
        }

        /**用于设置点赞动画*/
        private void setAnimator(final View view) {
            AnimatorSet animatorSet = new AnimatorSet();
            //设置动画集合
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1.2f, 1f, 0.5f, 0.7f, 1f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1.2f, 1f, 0.5f, 0.7f, 1f)
            );
            //开启该动画, 动画时长为150毫秒
            animatorSet.setDuration(500).start();
        }
    }


}