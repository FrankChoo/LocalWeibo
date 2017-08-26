package app.demo.weibotestdemo.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import app.demo.weibotestdemo.R;

public class DialogUtil {

    private static DialogUtil sInstance;
    private Dialog mDialog;

    private DialogUtil() {
    }

    public static DialogUtil getInstance() {
        if (sInstance == null) {
            synchronized(DialogUtil.class) {
                if(sInstance == null) {
                    sInstance = new DialogUtil();
                }
            }
        }
        return sInstance;
    }

    /**指定Dialog的宽度为屏幕的宽度*/
    public void displayDialogWindowWidth(Context context, View view, int gravity) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.ShareMenuDialog);
            mDialog.setContentView(view);
            mDialog.setCanceledOnTouchOutside(true);
            //设置显示Dialog的Window的相关属性
            Window window = mDialog.getWindow();
            window.setGravity(gravity);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.ShareAnimation);
            //获取WMS
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);//获取WMS
            //获取当前Window的参数
            WindowManager.LayoutParams mParams = window.getAttributes();//获取属性
            //将当前Window的宽度设置为屏幕的宽度
            Display display = windowManager.getDefaultDisplay();
            mParams.width = (int) (display.getWidth() * 1.0);
            window.setAttributes(mParams);
            mDialog.show();
        } else {
            throw new RuntimeException("显示Dialog必须传入Activity类型的Context");
        }
    }

    /**Dialog的宽度为布局wrap_content的宽度*/
    public void displayDialog(Context context, View view, int gravity) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            dismissDialog();
            mDialog = new Dialog(context, R.style.ShareMenuDialog);
            mDialog.setContentView(view);
            mDialog.setCanceledOnTouchOutside(true);
            //设置显示Dialog的Window的相关属性
            Window window = mDialog.getWindow();
            window.setGravity(gravity);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.ShareAnimation);
            mDialog.show();
        } else {
            throw new RuntimeException("显示Dialog必须传入Activity类型的Context");
        }
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog.cancel();
        }
        mDialog = null;
    }

    public Dialog getDialog() {
        return mDialog;
    }
}
