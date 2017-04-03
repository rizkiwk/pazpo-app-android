package id.pazpo.agent.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.flipbox.pazpo.R;
import com.github.nkzawa.socketio.client.Socket;

import id.pazpo.agent.PazpoApp;
import id.pazpo.agent.helpers.ConstantHelper;
import id.pazpo.agent.utils.SharedPrefs;
import id.pazpo.agent.views.AnimatedGifImageView;

/**
 * Created by wais on 1/12/17.
 */

public class BaseFragment extends Fragment {

    public PazpoApp mPazpoApp;
    public SharedPrefs mSharedPrefs;
    public ConstantHelper mConstant;
    public Socket mSocket;

    protected ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPazpoApp       = PazpoApp.getInstance();
        mSharedPrefs    = mPazpoApp.mSharedPrefs;
        mConstant       = mPazpoApp.mConstantHelper;
        dialog          = new ProgressDialog(getContext());
        mSocket         = mPazpoApp.getWebsocket();

        if (mSharedPrefs == null) mSharedPrefs = new SharedPrefs(this.getActivity());
        if (mConstant == null) mConstant = new ConstantHelper();

        mSocket.connect();
    }

    protected void showBlockingLoading(String message) {
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    protected void showLoadingFragment(AnimatedGifImageView animatedGifImageView) {
        animatedGifImageView.setAnimatedGif(R.raw.load_circular, AnimatedGifImageView.TYPE.AS_IS);
        animatedGifImageView.setVisibility(View.VISIBLE);
    }

    protected void hideLoading() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected void hideLoadingFragment(AnimatedGifImageView animatedGifImageView) {
        animatedGifImageView.setVisibility(View.GONE);
    }

    protected void hideKeyboard() {
        View View = getActivity().getCurrentFocus();
        if (View != null) {
            ((InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(View.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() > -1)
                return true;
        }
        return false;
    }
}
