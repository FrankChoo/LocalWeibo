package app.demo.weibotestdemo.activity.main;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import app.demo.weibotestdemo.model.DynamicModel;

/**
 * Created by 99538 on 2017/8/4.
 */
public class MainPresenter {

    private MainActivityViewInterface mView;
    private MainModel mModel = new MainModel();

    public MainPresenter(MainActivityViewInterface view) {
        mView = new WeakReference<>(view).get();
    }

    public void fetchDynamic() {
        mView.hideRefresh();
        mModel.loadDyanmic(new MainModel.DataListener() {
            @Override
            public void onComplete(LinkedList<DynamicModel> data) {
                mView.loadDyanmic(data);
            }
        });
    }

    public void loadMoreDynamic() {
        mModel.loadMoreDynamic(new MainModel.DataListener() {
            @Override
            public void onComplete(LinkedList<DynamicModel> data) {
                if(data != null) {
                    mView.loadDyanmic(data);
                }
            }
        });
    }

    public void refreshDynamic() {
        mView.showRefresh();
        mModel.refreshDynamic(new MainModel.DataListener() {
            @Override
            public void onComplete(LinkedList<DynamicModel> data) {
                mView.hideRefresh();
            }
        });
    }

    public boolean isCanLoading() {
        return mModel.getCurrentDynamicCount() < 15;
    }

    public interface MainActivityViewInterface {
        void loadDyanmic(LinkedList<DynamicModel> data);
        void showRefresh();
        void hideRefresh();
    }

}
