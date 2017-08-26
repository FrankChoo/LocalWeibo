package app.demo.weibotestdemo.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.demo.weibotestdemo.app_manager.MyApp;

/**
 * Created by 99538 on 2017/4/5.
 * 用户发布的动态Item
 */

public class DynamicModel implements Parcelable {

    private String userHeadUri;//用户头像
    private String userName;//用户名
    private String pubTime;//动态发布的实践
    private String pubContent;//动态内容
    private List<String> imageUriList;//图片资源的Url集合
    private DynamicModel baseForwardModel;//动态的转发源头
    private int likeCount;
    private int commentCount;
    private int forwardCount;
    private boolean isUserLike = false;

    private DynamicModel() {
    }

    protected DynamicModel(Parcel in) {
        userHeadUri = in.readString();
        userName = in.readString();
        pubTime = in.readString();
        pubContent = in.readString();
        imageUriList = in.createStringArrayList();
        baseForwardModel = in.readParcelable(DynamicModel.class.getClassLoader());
        likeCount = in.readInt();
        commentCount = in.readInt();
        forwardCount = in.readInt();
        isUserLike = in.readByte() != 0;
    }

    public String getUserHeadUri() {
        return userHeadUri;
    }

    public void setUserHeadUri(String userHeadUri) {
        this.userHeadUri = userHeadUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public SpannableString getPubContent() {
        return setSpannableString(pubContent);
    }

    public void setPubContent(String pubContent) {
        this.pubContent = pubContent;
    }

    public List<String> getImageUriList() {
        return imageUriList;
    }

    public void setImageUriList(List<String> imageUriList) {
        this.imageUriList = imageUriList;
    }

    public DynamicModel getBaseForwardModel() {
        return baseForwardModel;
    }

    public void setBaseForwardModel(DynamicModel baseForwardModel) {
        this.baseForwardModel = baseForwardModel;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
    }

    public boolean isUserLike() {
        return isUserLike;
    }

    public void setUserLike(boolean userLike) {
        isUserLike = userLike;
    }

    public static class Builder {
        private String userHeadUri;//用户头像
        private String userName;//用户名
        private String pubTime;//动态发布的实践
        private String pubContent;//动态内容
        private List<String> imageUriList;//图片资源的Url集合
        private DynamicModel baseForwardModel;//动态的转发源头
        private int likeCount;
        private int commentCount;
        private int forwardCount;

        public Builder() {
        }

        public Builder setUserHeadUri(String userHeadUri) {
            this.userHeadUri = userHeadUri;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setPubTime(String pubTime) {
            this.pubTime = pubTime;
            return this;
        }

        public Builder setPubContent(String pubContent) {
            this.pubContent = pubContent;
            return this;
        }

        public Builder setImageUriList(List<String> imageUriList) {
            this.imageUriList = imageUriList;
            return this;
        }

        public Builder setBaseForwardModel(DynamicModel baseForwardModel) {
            this.baseForwardModel = baseForwardModel;
            return this;
        }

        public Builder setLikeCount(int likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public Builder setCommentCount(int commentCount) {
            this.commentCount = commentCount;
            return this;
        }

        public Builder setForwardCount(int forwardCount) {
            this.forwardCount = forwardCount;
            return this;
        }

        public DynamicModel create() {
            DynamicModel model = new DynamicModel();
            model.setUserHeadUri(userHeadUri);
            model.setUserName(userName);
            model.setPubTime(pubTime);
            model.setPubContent(pubContent);
            model.setImageUriList(imageUriList);
            model.setBaseForwardModel(baseForwardModel);
            model.setLikeCount(likeCount);
            model.setCommentCount(commentCount);
            model.setForwardCount(forwardCount);
            return model;
        }
    }

    /**
     * 以下是Parcelable类型必要的构造方法
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userHeadUri);
        dest.writeString(userName);
        dest.writeString(pubTime);
        dest.writeString(pubContent);
        dest.writeStringList(imageUriList);
        dest.writeParcelable(baseForwardModel, flags);
        dest.writeInt(likeCount);
        dest.writeInt(commentCount);
        dest.writeInt(forwardCount);
        dest.writeByte((byte) (isUserLike ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DynamicModel> CREATOR = new Creator<DynamicModel>() {
        @Override
        public DynamicModel createFromParcel(Parcel in) {
            return new DynamicModel(in);
        }

        @Override
        public DynamicModel[] newArray(int size) {
            return new DynamicModel[size];
        }
    };

    /***用于处理用户PublishContent的内容*/
    private SpannableString setSpannableString(String str) {
        SpannableString spannableString = new SpannableString(str);
        //判断字符串中是否有 "//@...:" 类型的字符串
        int beginIndex = -1;
        int endIndex = -1;
        Pattern pattern = Pattern.compile("//@\\S+:");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            beginIndex = matcher.start() + 2;
            endIndex = matcher.end();
            final String userName = str.substring(beginIndex + 1, endIndex-1);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#0099EE"));
                    ds.setUnderlineText(false);
                }
                @Override
                public void onClick(View view) {
                    Toast.makeText(MyApp.getContext(), "你点击了" + userName + "的个人主页", Toast.LENGTH_SHORT).show();
                }
            }, beginIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }

}
