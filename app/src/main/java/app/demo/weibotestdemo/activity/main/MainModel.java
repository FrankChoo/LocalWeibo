package app.demo.weibotestdemo.activity.main;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import app.demo.weibotestdemo.model.DynamicModel;

/**
 * Created by 99538 on 2017/8/4.
 */
public class MainModel {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mCurrentDynamicCount = -1;
    private int mMaxDynamicCount = 15;

    public void loadDyanmic(final DataListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final LinkedList<DynamicModel> list = new LinkedList<>();
                //item1
                DynamicModel item1 = new DynamicModel.Builder()
                        .setUserHeadUri(MainActivity.sUrls[0])
                        .setUserName("Daniel")
                        .setPubTime("2017-02-26 00:00")
                        .setPubContent("这世界太嘈杂 周边都是假话, 你能不能再找个理由 等我回家")
                        .setImageUriList( new ArrayList<String>())
                        .setLikeCount(5)
                        .setCommentCount(10)
                        .setForwardCount(1)
                        .create();

                //item2(转发item1)
                DynamicModel item2 = new DynamicModel.Builder()
                        .setUserName("FrankChoo")
                        .setPubTime("2017-02-27 16:40")
                        .setPubContent("//@FrankChoo:这世界太嘈杂 周边都是假话, 你能不能再找个理由 等我回家" + "//@Daniel: " + item1.getPubContent())
                        .setBaseForwardModel(item1)
                        .setLikeCount(10)
                        .setCommentCount(2)
                        .setForwardCount(3)
                        .create();
                list.add(item2);

                //item3
                List<String> imageUrls3 = new ArrayList<>(); imageUrls3.add(MainActivity.sUrls[4]);
                DynamicModel item3 = new DynamicModel.Builder()
                        .setUserName("FrankChoo")
                        .setPubTime("2017-02-27 16:30")
                        .setPubContent("这世界太嘈杂 周边都是假话, 你能不能再找个理由 等我回家")
                        .setImageUriList(imageUrls3)
                        .setLikeCount(5)
                        .setCommentCount(5)
                        .setForwardCount(0)
                        .create();
                list.add(item3);

                //item4
                List<String> imageUrls4 = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    imageUrls4.add(MainActivity.sUrls[i]);
                }
                DynamicModel item4 = new DynamicModel.Builder()
                        .setUserName("FrankChoo")
                        .setPubTime("2017-02-27 16:10")
                        .setPubContent("这世界太嘈杂 周边都是假话, 你能不能再找个理由 等我回家")
                        .setImageUriList(imageUrls4)
                        .setLikeCount(5)
                        .setCommentCount(3)
                        .setForwardCount(1)
                        .create();
                list.add(item4);

                //item5
                List<String> imageUrls5 = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    imageUrls5.add(MainActivity.sUrls[i]);
                }
                DynamicModel item5 = new DynamicModel.Builder()
                        .setUserName("FrankChoo")
                        .setPubTime("2017-02-27 15:05")
                        .setPubContent("这世界太嘈杂 周边都是假话, 你能不能再找个理由 等我回家")
                        .setImageUriList(imageUrls5)
                        .setLikeCount(10)
                        .setCommentCount(3)
                        .setForwardCount(5)
                        .create();
                list.add(item5);
                mCurrentDynamicCount = list.size();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete(list);
                    }
                });
            }
        }).start();

    }

    public void loadMoreDynamic(final DataListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //模拟从网络上获取数据
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //具体的加载更多的方法
                final LinkedList<DynamicModel> list = new LinkedList<>();
                for(int i = 0; i < 4; i++) {
                    if (mCurrentDynamicCount < mMaxDynamicCount) {
                        DynamicModel item = new DynamicModel.Builder()
                                .setUserName("FrankChoo")
                                .setPubTime("2017-02-27 14:40")
                                .setPubContent("这是上拉加载出来的第" + (++mCurrentDynamicCount) + "条Item")
                                .setImageUriList(new ArrayList<String>())
                                .setLikeCount(5)
                                .setCommentCount(5)
                                .setForwardCount(0)
                                .create();
                        list.add(item);
                    } else {
                        break;
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete(list);
                    }
                });
            }
        }).start();
    }

    public void refreshDynamic(final DataListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //模拟从网络上拉取数据刷新
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete(null);
                    }
                });
            }
        }).start();
    }

    public int getCurrentDynamicCount() {
       return mCurrentDynamicCount;
    }

    public interface DataListener {
        void onComplete(LinkedList<DynamicModel> data);
    }

}
