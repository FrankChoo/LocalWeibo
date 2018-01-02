package app.demo.weibotestdemo.activity.publishDynamic;

import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import app.demo.weibotestdemo.model.DynamicModel;

/**
 * Created by 99538 on 2017/8/7.
 */

public class PublishDynamicPresenter {

    private PublishDynamicViewInterface mView;
    private PublishDynamicModel mModel = new PublishDynamicModel();

    public PublishDynamicPresenter(PublishDynamicViewInterface view) {
        mView = new WeakReference<>(view).get();
    }

    public void initState(Intent data) {
        mModel.saveDynamic(data);
        DynamicModel model = mModel.getForwardDynamic();
        if(model != null) {
            mView.statePubForwardDynamic(model);
        } else {
            mView.statePubOriginalDynamic();
        }
    }

    public void buildDynamic(String content, ArrayList<String> pickedList) {
        DynamicModel dynamic = mModel.createNewDynamic(content, pickedList);
        mView.publishBroadcast(dynamic);
    }

    public interface PublishDynamicViewInterface {
        void statePubOriginalDynamic();
        void statePubForwardDynamic(DynamicModel dynamic);
        void publishBroadcast(DynamicModel dynamic);
    }
}
