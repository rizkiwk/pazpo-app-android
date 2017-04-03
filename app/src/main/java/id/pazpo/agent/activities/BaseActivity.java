package id.pazpo.agent.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.flipbox.pazpo.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import id.pazpo.agent.PazpoApp;
import id.pazpo.agent.helpers.ConstantHelper;
import id.pazpo.agent.utils.OAuthHeaderBuilder;
import id.pazpo.agent.utils.SharedPrefs;

/**
 * Created by wais on 1/11/17.
 */

public class BaseActivity extends AppCompatActivity {

    public PazpoApp mPazpoApp;
    public SharedPrefs mSharedPrefs;
    public ConstantHelper mConstant;
    public Socket mSocket;

    protected Map mServiceParams;
    protected ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPazpoApp       = PazpoApp.getInstance();
        mSharedPrefs    = mPazpoApp.mSharedPrefs;
        mConstant       = mPazpoApp.mConstantHelper;
        mServiceParams  = new HashMap();
        dialog          = new ProgressDialog(BaseActivity.this);

        if (mSharedPrefs == null) mSharedPrefs = new SharedPrefs(this);
        if (mConstant == null) mConstant = new ConstantHelper();

        mSocket = mPazpoApp.getWebsocket();
        mSocket.on(Socket.EVENT_CONNECT, onSocketConnected);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onSocketError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onSocketTimeout);
        mSocket.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void showBlockingLoading(String message) {
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showSnackBar(CoordinatorLayout coordinatorLayout, String message, int type) {
        Snackbar snack = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View view = snack.getView();

        switch (type) {
            case 0:
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_red));
                break;
            case 1:
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_green));
                break;
            case 2:
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_blue));
                break;
            case 3:
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.pastel_green));
                snack.setDuration(Snackbar.LENGTH_INDEFINITE);
                snack.setAction(R.string.dialog_okay, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(mConstant.NAVBOTTOM_TAB_ID, mConstant.NAVBOTTOM_TAB_PROFILE);
                        startActivity(intent);
                    }
                });
                break;
        }

        snack.setActionTextColor(ContextCompat.getColor(this, R.color.white));
        snack.show();
    }

    public void hideLoading() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String generateHeaderOauth(String urlParam) {
        String oauthHeader                      = null;
        String clientKey                        = getString(R.string.oauth_client_key);
        String clientSecret                     = getString(R.string.oauth_client_secret);
        String signMethod                       = getString(R.string.oauth_sign_method);
        String hmacSha1Algorithm                = getString(R.string.oauth_hmac_sha1_algorithm);
        String reqMethodGet                     = getString(R.string.oauth_request_method_get);
        OAuthHeaderBuilder oAuthHeaderBuilder   = new OAuthHeaderBuilder(signMethod, reqMethodGet, "", hmacSha1Algorithm, clientKey, clientSecret);

        oAuthHeaderBuilder.setURL(urlParam);

        try {
            oauthHeader = oAuthHeaderBuilder.generateHeader("","");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return oauthHeader;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() > -1)
                return true;
        }
        return false;
    }

    public void dialog(int rString) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(rString)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    protected Emitter.Listener onSocketConnected = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e("WS is : ", "on connecting");
                }
            });
        }
    };

    protected Emitter.Listener onSocketError = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e("WS is : ", "on error");
//                    app.mSocketError = true;
                }
            });
        }
    };

    protected Emitter.Listener onSocketTimeout = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            Log.e("WS is : ", "on timeout");
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e("WS is : ", "on timeout");
//                    app.mSocketTimeout = true;
                }
            });
        }
    };
}
