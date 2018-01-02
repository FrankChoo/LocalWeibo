package app.demo.weibotestdemo.activity.publishDynamic;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.activity.picturePicker.PicturePickerActivity;
import app.demo.weibotestdemo.adapter.ActivityPublishDynamicAdapter;
import app.demo.weibotestdemo.app_manager.BaseActivity;
import app.demo.weibotestdemo.app_manager.MyApp;
import app.demo.weibotestdemo.model.DynamicModel;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 99538 on 2017/4/10.
 * 发布动态和转发动态的页面
 */
public class PublishDynamicActivity extends BaseActivity implements View.OnClickListener,
        PublishDynamicPresenter.PublishDynamicViewInterface{

    public static final String BROADCAST_NEW_DYNAMIC_PUBLISH = "broadcast.user.publish.new.dynamic";

    /**UI*/
    @MyApp.ViewResId(R.id.toolbar)  Toolbar mToolbar;
    @MyApp.ViewResId(R.id.edit_text)  EditText mEditText;
    @MyApp.ViewResId(R.id.forward_layout)  LinearLayout mForwardLayout;
    @MyApp.ViewResId(R.id.recycler_view)  RecyclerView mRecyclerView;
    @MyApp.ViewResId(R.id.icon_image)  CircleImageView mUserIcon;
    @MyApp.ViewResId(R.id.user_name)  TextView mUserName;
    @MyApp.ViewResId(R.id.user_content)  TextView mUserContent;
    @MyApp.ViewResId(R.id.publish_msg)  ImageButton mSendButton;

    private BroadcastReceiver mBroadcastReceiver;
    private ArrayList<String> mPickedList = new ArrayList<>();
    private ActivityPublishDynamicAdapter mAdapter;
    private PublishDynamicPresenter mPresenter;

    /**使用静态该方法启动当前Activity(用于发布动态)*/
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PublishDynamicActivity.class);
        context.startActivity(intent);
    }
    /**使用静态该方法启动当前Activity(用于转发动态)*/
    public static void startActivity(Context context, DynamicModel item) {
        Intent intent = new Intent(context, PublishDynamicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("forward_item", item);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_dynamic);
        MyApp.ViewResId(this, getWindow().getDecorView());
        mPresenter = new PublishDynamicPresenter(this);
        mPresenter.initState(getIntent());
        registerBroadcast();
    }

    @Override
    public void statePubOriginalDynamic() {
        mToolbar.setTitle("发布动态");
        //RecyclerView
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new ActivityPublishDynamicAdapter(mPickedList, new StartPicturePickerListener() {
            @Override
            public void startPicturePicker() {
                String permissions[] = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
                requestRuntimePermission(permissions, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        PicturePickerActivity.startActivity(PublishDynamicActivity.this, mPickedList);
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mForwardLayout.setVisibility(View.GONE);

        //发布动态
        mSendButton.setOnClickListener(this);
    }

    @Override
    public void statePubForwardDynamic(DynamicModel dynamic) {
        mToolbar.setTitle("转发动态");
        mRecyclerView.setVisibility(View.GONE);
        mForwardLayout.setVisibility(View.VISIBLE);
        String existContent = "";
        if (dynamic.getBaseForwardModel() != null) {
            existContent = "//@" + dynamic.getUserName() + ": " + dynamic.getPubContent();
            mEditText.setText(existContent);
            Glide.with(this).load(dynamic.getBaseForwardModel().getUserHeadUri() == null ?
                    R.mipmap.head_icon : dynamic.getBaseForwardModel().getUserHeadUri()).into(mUserIcon);
            mUserName.setText(dynamic.getBaseForwardModel().getUserName());
            mUserContent.setText(dynamic.getBaseForwardModel().getPubContent());

        } else {
            mEditText.setText(existContent);
            Glide.with(this).load(dynamic.getUserHeadUri() == null ?
                    R.mipmap.head_icon : dynamic.getUserHeadUri()).into(mUserIcon);
            mUserName.setText(dynamic.getUserName());
            mUserContent.setText(dynamic.getPubContent());
        }

        //发布动态
        mSendButton.setOnClickListener(this);
    }

    @Override
    public void publishBroadcast(DynamicModel dynamic) {
        Intent intent = new Intent(BROADCAST_NEW_DYNAMIC_PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putParcelable("new_dynamic", dynamic);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        String content = mEditText.getText().toString().trim();
        if (content.length() == 0 && mPickedList.size() == 0) {
            Toast.makeText(MyApp.getContext(), "您暂未写入任何信息", Toast.LENGTH_SHORT).show();
        } else {
            mPresenter.buildDynamic(content, mPickedList);
        }
    }

    /**注册广播用于接收从图片选择器传递过来的广播*/
    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter(PicturePickerActivity.BROADCAST_URI_PICKED);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                refreshList(bundle.getStringArrayList("picked_uri_list"));
            }
        };
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    public void refreshList(List<String> newList) {
        mPickedList.clear();
        for (String uri: newList) {
            mPickedList.add(uri);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    public interface StartPicturePickerListener {
        void startPicturePicker();
    }

}
