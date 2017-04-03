package id.pazpo.agent.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.flipbox.pazpo.BuildConfig;
import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pazpo.agent.services.model.member.MemberGet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by wais on 1/12/17.
 */

public class SplashScreenActivity extends BaseActivity {

    @BindView(R.id.tv_splash_version) TextView tv_splash_version;

    private static final int SPLASH_TIME    = 1500;
    private static String GoogleMarket      = "market://details?id=";
    private static String GooglePlaystore   = "https://play.google.com/store/apps/details?id=";
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        ButterKnife.bind(this);

        mContext = this;
        tv_splash_version.setText("v"+BuildConfig.VERSION_NAME);

        Analytics.with(this).track("Page View", new Properties()
                .putValue("Type", "Activity")
                .putValue("Page", "Splash Screen"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkVersion();
            }
        }, SPLASH_TIME);
    }

    protected void checkVersion() {
        if (!isNetworkConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_connection_error)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            checkVersion();
                        }
                    });
            builder.setCancelable(false);
            builder.show();
        } else {
            apiVersionCode();
        }
    }

    protected void apiVersionCode() {
        Call<MemberGet> CallVersionCode = mPazpoApp.mServiceHelper.getVersionCode();
        CallVersionCode.enqueue(new Callback<MemberGet>() {
            @Override
            public void onResponse(Call<MemberGet> call, Response<MemberGet> response) {
                if (response.isSuccessful()) {
                    MemberGet memberGet = new MemberGet();
                    memberGet           = response.body();
                    if (memberGet.VersionCode > BuildConfig.VERSION_CODE) {
                        new AlertDialog.Builder(mContext)
                                .setTitle(memberGet.MessageTitle)
                                .setMessage(memberGet.MessageBody)
                                .setPositiveButton(R.string.btn_update, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String appPackageName = getPackageName();
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GoogleMarket + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GooglePlaystore + appPackageName)));
                                        }
                                        finish();
                                    }

                                })
                                .setNegativeButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        initStartApp();
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberGet> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Gagal cek versi", Toast.LENGTH_SHORT).show();
                initStartApp();
//                checkVersion();       uncomment this to force users to checkVersion
            }
        });
    }

    protected void initStartApp() {
        if (isFirstRunApp()) {
            startActivity(new Intent(SplashScreenActivity.this, FirstRunAppActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashScreenActivity.this, AuthenticationActivity.class));
            finish();
        }
    }

    protected boolean isFirstRunApp() {
        return mSharedPrefs.getIsFirstRunApp();
    }

}
