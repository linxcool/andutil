package com.linxcool.app;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

/**
 * @author Yrom
 *
 */
public class SwipeBackActivityHelper {
    private Activity mActivity;
    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackActivityHelper(Activity activity) {
        mActivity = activity;
    }
    
    @SuppressWarnings("deprecation")
	public void onActivtyCreate(){
    	//获得当期Activity的Window设置颜色为黑色
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //获得当期Activity的Window的根视图DecorView设置颜色无
        mActivity.getWindow().getDecorView().setBackgroundDrawable(null);
        //加载布局
        mSwipeBackLayout = new SwipeBackLayout(mActivity);
    }
   
    public void onPostCreate(){
        mSwipeBackLayout.attachToActivity(mActivity);
    }
    
    public View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }
    
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

}
