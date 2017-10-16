package cn.tianya.light.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.baidu.mobstat.StatService;

import cn.tianya.light.BuildConfig;
import cn.tianya.light.pulltorefresh.PullToRefreshListView;
import cn.tianya.module.EventHandlerManager;
import cn.tianya.module.EventSimpleListener.OnLoginStatusChangedEventListener;
import cn.tianya.module.EventSimpleListener.OnNightModeChangedEventListener;
import cn.tianya.task.LoadDataAsyncTaskDialog;

/**
 * Fragment 基类,做一些统一操作
 *
 * @author wudeng
 */
public abstract class BaseFragment extends Fragment implements OnLoginStatusChangedEventListener,
        OnNightModeChangedEventListener, DialogInterface.OnCancelListener {
    private AlertDialog progressDialog;

    /**
     * 登录状态改变
     */
    @Override
    public void onLoginStatusChanged() {
    }

    /**
     * 夜间模式改变
     */
    @Override
    public void onNightModeChanged() {
    }

    public void onRefresh() {

    }

    @Override
    public void onBrightnessChanged() {
    }

    @Override
    public void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        EventHandlerManager.getInstance().registerEventListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventHandlerManager.getInstance().unregisterEventListener(this);
    }

    public boolean getMultipleChoiceMode() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!BuildConfig.DEBUG) {
            String pageName = this.getClass().getSimpleName();
            StatService.onPageEnd(getActivity(), pageName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!BuildConfig.DEBUG) {
            String pageName = this.getClass().getSimpleName();
            StatService.onPageStart(getActivity(), pageName);
        }
    }

    protected void showLoadingProgress(String infoText, boolean isCancelable) {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = new LoadDataAsyncTaskDialog(getActivity(), infoText);
        progressDialog.setOnCancelListener(this);
        progressDialog.setCancelable(isCancelable);
        progressDialog.show();
    }

    protected void dismissLoadingProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            onDialogCancel();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dismissLoadingProgress();
    }

    protected void onDialogCancel() {

    }

    public PullToRefreshListView getRefreshListView(){
        return null;
    }
}
