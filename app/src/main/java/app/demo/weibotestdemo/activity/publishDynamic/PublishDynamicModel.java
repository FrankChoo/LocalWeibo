package app.demo.weibotestdemo.activity.publishDynamic;

import android.content.Intent;

import java.util.ArrayList;

import app.demo.weibotestdemo.model.DynamicModel;
import app.demo.weibotestdemo.utils.DateUtils;

/**
 * Created by 99538 on 2017/8/7.
 */

public class PublishDynamicModel {

    private DynamicModel mForwardDynamic;

    public void saveDynamic(Intent data) {
        mForwardDynamic = data.getParcelableExtra("forward_item");
    }

    public DynamicModel getForwardDynamic() {
        return mForwardDynamic;
    }

    public DynamicModel createNewDynamic(String content, ArrayList<String> pickedList) {
        DynamicModel newDynamic;
        if(mForwardDynamic != null) {
            newDynamic = new DynamicModel.Builder()
                    .setUserHeadUri(null)
                    .setUserName("FrankChoo")
                    .setPubTime(DateUtils.getInstance().getNowDate())
                    .setPubContent(content)
                    .setBaseForwardModel(mForwardDynamic.getBaseForwardModel() == null ?
                            mForwardDynamic : mForwardDynamic.getBaseForwardModel())
                    .create();
        } else {
            newDynamic = new DynamicModel.Builder()
                    .setUserHeadUri(null)
                    .setUserName("FrankChoo")
                    .setPubTime(DateUtils.getInstance().getNowDate())
                    .setPubContent(content)
                    .setImageUriList(pickedList)
                    .create();
        }
        return newDynamic;
    }
}
