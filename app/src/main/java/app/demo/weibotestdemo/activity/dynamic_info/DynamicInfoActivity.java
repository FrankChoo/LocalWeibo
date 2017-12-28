package app.demo.weibotestdemo.activity.dynamic_info;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;
import java.util.List;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.activity.ShowImageActivity;
import app.demo.weibotestdemo.activity.publish_dynamic.PublishDynamicActivity;
import app.demo.weibotestdemo.adapter.ActivityDynamicInfoAdapter;
import app.demo.weibotestdemo.app_manager.BaseActivity;
import app.demo.weibotestdemo.app_manager.MyApp;
import app.demo.weibotestdemo.custom_view.NineGridImage;
import app.demo.weibotestdemo.model.DynamicCommentModel;
import app.demo.weibotestdemo.model.DynamicModel;
import app.demo.weibotestdemo.utils.DialogUtil;
import app.demo.weibotestdemo.utils.RecyclerViewDividerUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Frank Choo
 * 用户动态的详情
 */
public class DynamicInfoActivity extends BaseActivity implements DynamicInfoPresenter.DynamicInfoViewInterface,
        View.OnClickListener, NineGridImage.onClickImageListener {

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

    /**评论区*/
    @MyApp.ViewResId(R.id.recycler_view)  RecyclerView mRecyclerView;

    /**成员变量*/
    private DynamicInfoPresenter mPresenter;
    private ActivityDynamicInfoAdapter mAdapter;
    private LinkedList<DynamicCommentModel> mList = new LinkedList<>();

    /**
     * 启动该Activity
     */
    public static void start(Context context, DynamicModel model, boolean isComment) {
        Intent intent = new Intent(context, DynamicInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("model", model);
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        intent.putExtra("comment", isComment);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_info);
        MyApp.ViewResId(this, getWindow().getDecorView());
        mPresenter = new DynamicInfoPresenter(this);
        mPresenter.initDynamicState(getIntent());
        mPresenter.fetchComments();
    }

    @Override
    public void showOriginalDynamic(DynamicModel dynamic) {
        //用户自定义区
        Glide.with(MyApp.getContext()).load(dynamic.getUserHeadUri() == null
                ? R.mipmap.head_icon : dynamic.getUserHeadUri()).into(mUserIcon);
        mUserName.setText(dynamic.getUserName());
        mPublishTime.setText(dynamic.getPubTime());
        mPublishContent.setText(dynamic.getPubContent());
        mNineGridImage.loadUriList(dynamic.getImageUriList());
        mNineGridImage.setOnClickImageListener(this);
        mButtonMenu.setVisibility(View.GONE);
        mForwardLayout.setVisibility(View.GONE);

        //点赞区域
        mLikeCount.setText(dynamic.getLikeCount() == 0 ? "点赞" : String.valueOf(dynamic.getLikeCount()));
        mCommentCount.setText(dynamic.getCommentCount() == 0 ? "评论" : String.valueOf(dynamic.getCommentCount()));
        mForwardCount.setText(dynamic.getForwardCount() == 0 ? "转发" : String.valueOf(dynamic.getForwardCount()));
        mLikeClickRegion.setOnClickListener(this);
        mCommentRegion.setOnClickListener(this);
        mForwardRegion.setOnClickListener(this);

        //评论区域的控件
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerViewDividerUtil(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new ActivityDynamicInfoAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        //底部发送评论模块
        findViewById(R.id.send_comment).setOnClickListener(this);
    }

    @Override
    public void showForwardDynamic(DynamicModel dynamic) {
        //用户发布区
        Glide.with(MyApp.getContext()).load(dynamic.getUserHeadUri() == null ? R.mipmap.head_icon : dynamic.getUserHeadUri()).into(mUserIcon);
        mUserName.setText(dynamic.getUserName());
        mPublishTime.setText(dynamic.getPubTime());
        mPublishContent.setText(dynamic.getPubContent());
        mPublishContent.setMovementMethod(LinkMovementMethod.getInstance());//如果SpannableString设置了Click监听器需要调用该方法
        mNineGridImage.setVisibility(View.GONE);
        mButtonMenu.setVisibility(View.GONE);

        //用户转发区
        mForwardLayout.setVisibility(View.VISIBLE);
        mForwardLayout.setOnClickListener(this);
        DynamicModel forwardItem = dynamic.getBaseForwardModel();
        Glide.with(MyApp.getContext()).load(forwardItem.getUserHeadUri() == null
                ? R.mipmap.head_icon : forwardItem.getUserHeadUri()).into(mForwardIcon);
        mForwardName.setText(forwardItem.getUserName());
        mForwardContent.setText(forwardItem.getPubContent());
        mForwardNineGridImage.loadUriList(forwardItem.getImageUriList());
        //转发内容中的九宫格图片的点击事件
        mForwardNineGridImage.setOnClickImageListener(this);

        //点赞区域
        mLikeCount.setText(dynamic.getLikeCount() == 0 ? "点赞" : String.valueOf(dynamic.getLikeCount()));
        mCommentCount.setText(dynamic.getCommentCount() == 0 ? "评论" : String.valueOf(dynamic.getCommentCount()));
        mForwardCount.setText(dynamic.getForwardCount() == 0 ? "转发" : String.valueOf(dynamic.getForwardCount()));
        mLikeClickRegion.setOnClickListener(this);
        mCommentRegion.setOnClickListener(this);
        mForwardRegion.setOnClickListener(this);

        //评论区域的控件
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerViewDividerUtil(this, LinearLayoutManager.HORIZONTAL));
        mAdapter = new ActivityDynamicInfoAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        //底部发送评论模块
        findViewById(R.id.send_comment).setOnClickListener(this);
    }

    @Override
    public void showComments(List<DynamicCommentModel> list) {
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showComment(DynamicCommentModel comment) {
        mList.addFirst(comment);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
        //设置mCommentCount加1
        int count = Integer.parseInt(mCommentCount.getText().toString() == "评论"
                ? "0" : mCommentCount.getText().toString()) + 1;
        mCommentCount.setText(String.valueOf(count));
        closeInputMethod();
        DialogUtil.getInstance().dismissDialog();
    }

    @Override
    public void showDialog() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_comment, null);
        final EditText editText = (EditText) contentView.findViewById(R.id.edit_text);
        DialogUtil.getInstance().displayDialogWindowWidth(this, contentView, Gravity.BOTTOM);
        //该方法用于让Dialog中的EditText获取焦点并且显示输入法
        DialogUtil.getInstance().getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        contentView.findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString().trim();
                if (content.isEmpty()) return;
                mPresenter.publishComment(content);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.forward_layout: {
                start(DynamicInfoActivity.this, mPresenter.getDynamic().getBaseForwardModel(), false);
                break;
            }
            case R.id.like_click_region: {
                mBtnLike.clearAnimation();
                if(!mBtnLike.isChecked()) {
                    setAnimator(mBtnLike);
                    mBtnLike.setChecked(true);
                    int count = Integer.parseInt(mLikeCount.getText().toString() == "点赞" ?
                            "0" : mLikeCount.getText().toString());mLikeCount.setText(String.valueOf(++count));
                } else {
                    mBtnLike.setChecked(false);
                    int count = Integer.parseInt(mLikeCount.getText().toString()); --count;
                    mLikeCount.setText(count == 0 ? "点赞" : String.valueOf(count));
                }
                break;
            }
            case R.id.comment_click_region: {
                showDialog();
                break;
            }
            case R.id.forward_click_region: {
                PublishDynamicActivity.startActivity(MyApp.getContext(), mPresenter.getDynamic());
                finish();
                break;
            }
            case R.id.send_comment: {
                showDialog();
                break;
            }
        }
    }

    @Override
    public void onClickNineImage(int position, String url, List<String> urlList) {
        ShowImageActivity.startActivity(MyApp.getContext(), position, urlList);
    }

    /**用于设置点赞动画*/
    private void setAnimator(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        //设置动画集合
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1.2f, 1f, 0.5f, 0.7f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1.2f, 1f, 0.5f, 0.7f, 1f)
        );
        //开启该动画, 动画时长为150毫秒
        animatorSet.setDuration(500).start();
    }

    /**用于关闭输入法*/
    private void closeInputMethod(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(mUserIcon.getWindowToken(), 0);
        }
    }

}
