package app.demo.weibotestdemo.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import app.demo.weibotestdemo.R;
import app.demo.weibotestdemo.activity.publishDynamic.PublishDynamicActivity;
import app.demo.weibotestdemo.adapter.ActivityMainAdapter;
import app.demo.weibotestdemo.app_manager.BaseActivity;
import app.demo.weibotestdemo.app_manager.MyApp;
import app.demo.weibotestdemo.custom_view.LoadingView;
import app.demo.weibotestdemo.model.DynamicModel;
import app.demo.weibotestdemo.utils.AppBarsUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by 99538 on 2017/4/13.
 * 个人主页
 */
public class MainActivity extends BaseActivity implements MainPresenter.MainActivityViewInterface{

    /**静态成员变量*/
    public static final String[] sUrls = new String[]{//图片URL集合
            "http://d.hiphotos.baidu.com/image/h%3D200/sign=201258cbcd80653864eaa313a7dca115/ca1349540923dd54e54f7aedd609b3de9c824873.jpg",
            "http://d.hiphotos.baidu.com/image/h%3D200/sign=ea218b2c5566d01661199928a729d498/a08b87d6277f9e2fd4f215e91830e924b999f308.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3445377427,2645691367&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2644422079,4250545639&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1444023808,3753293381&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=882039601,2636712663&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=4119861953,350096499&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2437456944,1135705439&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=3251359643,4211266111&fm=21&gp=0.jpg",
            "http://img4.duitang.com/uploads/item/201506/11/20150611000809_yFe5Z.jpeg",
            "http://img5.imgtn.bdimg.com/it/u=1717647885,4193212272&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2024625579,507531332&fm=21&gp=0.jpg"};

    /**UI控件*/
    @MyApp.ViewResId(R.id.app_bar_layout)  AppBarLayout mAppBarLayout;
    @MyApp.ViewResId(R.id.title_text)  TextView mTitleText;
    @MyApp.ViewResId(R.id.head_image)  CircleImageView mHeadImage;
    @MyApp.ViewResId(R.id.title_image)  ImageView mTitleImage;
    @MyApp.ViewResId(R.id.swipe_refresh)  SwipeRefreshLayout mSwipeRefreshLayout;
    @MyApp.ViewResId(R.id.recycler_view)  RecyclerView mRecyclerView;
    @MyApp.ViewResId(R.id.fab) FloatingActionButton mFab;
    @MyApp.ViewResId(R.id.loading_view) LoadingView mLoadingView;


    /**成员变量*/
    private ActivityMainAdapter mAdapter;
    private MainPresenter mPresenter;
    private LinkedList<DynamicModel> mList = new LinkedList<>();
    private BroadcastReceiver mDynamicReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp.ViewResId(this, getWindow().getDecorView());
        AppBarsUtil.newInstance(this).setStatusBarStyle(AppBarsUtil.ALL_TRANSPARENT).commit();
        mPresenter = new MainPresenter(this);
        setViews();
        mPresenter.fetchDynamic();
        registerBroadcastReceiver();
    }

    private void setViews() {
        //设置标题名
        mTitleText.setText("Frank Choo");

        //给AppBarLayout注册监听器, 监听其位置的变化展示不同的内容
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout mAppBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {//竖直方向上的偏移量为0, 即完全展开的状态
                    mTitleText.setVisibility(View.INVISIBLE);
                } else if (Math.abs(verticalOffset) >= mAppBarLayout.getTotalScrollRange()) {//完全折叠
                    mTitleText.setVisibility(View.VISIBLE);
                } else {
                    mTitleText.setVisibility(View.INVISIBLE);
                }
            }
        });

        //设置背景图高斯模糊
        Glide.with(this).load(R.mipmap.head_icon).
                bitmapTransform(new BlurTransformation(this, 5, 4)).into(mTitleImage);

        //设置用户头像
        Glide.with(this).load(R.mipmap.head_icon).into(mHeadImage);

        //设置RecyclerView相关
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ActivityMainAdapter(mPresenter, mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
                //使用canScrollVertically(1) 判断mRecyclerView能否向上滚动
                //使用canScrollVertically(-1)判断mRecyclerView能否向下滚动
                //当且仅当mRecyclerView滑动到底部, 且没有达到加载上限时执行
                if (!mRecyclerView.canScrollVertically(1) && mPresenter.isCanLoading()) {
                    mPresenter.loadMoreDynamic();
                }
            }
        });

        //给下拉刷新控件注册监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshDynamic();
            }
        });

        //给悬浮按钮注册监听器
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublishDynamicActivity.startActivity(MainActivity.this);
            }
        });
    }

    /**
     * 注册广播接收器
     */
    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(PublishDynamicActivity.BROADCAST_NEW_DYNAMIC_PUBLISH);
        mDynamicReceive = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DynamicModel item = intent.getParcelableExtra("new_dynamic");
                if (item != null) {
                    mList.addFirst(item);
                    mAdapter.notifyItemInserted(1);
                    mRecyclerView.scrollToPosition(0);
                }
            }
        };
        registerReceiver(mDynamicReceive, intentFilter);
    }

    @Override
    public void loadDynamic(LinkedList<DynamicModel> data) {
        //Adapter中设置了Header和Footer, 所以集合中元素的position在列表中对应的位置应为position + 1
        int insertStart = mList.size() + 1;
        mList.addAll(data);
        mAdapter.notifyItemInserted(insertStart);
    }

    @Override
    public void showRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        mLoadingView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideRefresh() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mDynamicReceive);
        super.onDestroy();
    }

}
