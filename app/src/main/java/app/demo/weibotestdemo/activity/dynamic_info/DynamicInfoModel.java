package app.demo.weibotestdemo.activity.dynamic_info;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import app.demo.weibotestdemo.activity.main.MainActivity;
import app.demo.weibotestdemo.model.DynamicCommentModel;
import app.demo.weibotestdemo.model.DynamicModel;

/**
 * Created by 99538 on 2017/8/5.
 */
public class DynamicInfoModel {

    private DynamicModel mDynamic;
    
    public void saveDynamic(Intent data) {
        mDynamic = data.getParcelableExtra("model");
    }

    public DynamicModel getDynamic() {
        return mDynamic;
    }

    /**
     * 加载评论的具体内容
     */
    public List<DynamicCommentModel> loadComments() {
        List<DynamicCommentModel> list = new ArrayList<>();
        for (int i = 1; i <= mDynamic.getCommentCount(); i++) {
            DynamicCommentModel item = new DynamicCommentModel(
                    MainActivity.sUrls[1],
                    "Daniel",
                    "15 : 0" + i,
                    "还有这种操作?");
            list.add(item);
        }
        return list;
    }

    public DynamicCommentModel createComment(String content) {
        DynamicCommentModel comment = new DynamicCommentModel(MainActivity.sUrls[0], "FrankChoo", "0:00", content);
        return comment;
    }

}
