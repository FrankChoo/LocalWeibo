package app.demo.weibotestdemo.model;

/**
 * Created by 99538 on 2017/4/12.
 * 动态详情中评论的Item
 */

public class DynamicCommentModel {
    private String cmtHeadUri;

    private String cmtName;

    private String cmtPublishTime;

    private String cmtContent;

    public DynamicCommentModel(String cmtHeadUri, String cmtName, String cmtPublishTime, String cmtContent) {
        this.cmtHeadUri = cmtHeadUri;
        this.cmtName = cmtName;
        this.cmtPublishTime = cmtPublishTime;
        this.cmtContent = cmtContent;
    }

    public String getCmtHeadUri() {
        return cmtHeadUri;
    }

    public String getCmtName() {
        return cmtName;
    }

    public String getCmtPublishTime() {
        return cmtPublishTime;
    }

    public String getCmtContent() {
        return cmtContent;
    }
}
