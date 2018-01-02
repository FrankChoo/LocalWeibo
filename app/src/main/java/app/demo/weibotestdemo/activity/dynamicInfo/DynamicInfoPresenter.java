package app.demo.weibotestdemo.activity.dynamicInfo;

import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.List;

import app.demo.weibotestdemo.model.DynamicCommentModel;
import app.demo.weibotestdemo.model.DynamicModel;

/**
 * Created by 99538 on 2017/8/5.
 */

public class DynamicInfoPresenter {

    private DynamicInfoViewInterface mView;
    private DynamicInfoModel mModel = new DynamicInfoModel();

    public DynamicInfoPresenter(DynamicInfoViewInterface view) {
        mView = new WeakReference<>(view).get();
    }

    public void initDynamicState(Intent data) {
        mModel.saveDynamic(data);
        DynamicModel dynamic = mModel.getDynamic();
        if(dynamic.getBaseForwardModel() != null) {
            mView.showForwardDynamic(dynamic);
        } else {
            mView.showOriginalDynamic(dynamic);
        }

        boolean isComment = data.getBooleanExtra("comment", false);
        if(isComment) {
            mView.showDialog();
        }
    }

    public void fetchComments() {
        mView.showComments(mModel.loadComments());
    }

    public void publishComment(String content) {
        mView.showComment(mModel.createComment(content));
    }

    public DynamicModel getDynamic() {
        return mModel.getDynamic();
    }


    public interface DynamicInfoViewInterface {
        void showOriginalDynamic(DynamicModel dynamic);
        void showForwardDynamic(DynamicModel dynamic);
        void showComments(List<DynamicCommentModel> list);
        void showComment(DynamicCommentModel comment);
        void showDialog();
    }
}
