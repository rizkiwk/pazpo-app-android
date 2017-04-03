package id.pazpo.agent.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.pazpo.agent.fragments.ProfileEditFragment;
import id.pazpo.agent.interfaces.BaseMethodInterface;

/**
 * Created by wais on 2/20/17.
 */

public class SettingActivity extends BaseActivity
        implements BaseMethodInterface
{

    @BindView(R.id.cl_setting)
    CoordinatorLayout cl_setting;
    @BindView(R.id.ib_tbmain_back)
    ImageButton ib_tbmain_back;

    private Context mContext;
    private FragmentManager mFragmentManager;
    private ProfileEditFragment mProfileEditFragment;

    private String mItemSetting;

    public static int INTENT_CODE_UPLOAD_PROFILE_IMAGE  = 110;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_setting);
        ButterKnife.bind(this);

        Analytics.with(this).track("Page View", new Properties()
                .putValue("Type", "Activity")
                .putValue("Page", "Setting"));

        initData(savedInstanceState);
        initUI();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mContext            = this;
        mFragmentManager    = getSupportFragmentManager();
        mItemSetting        = getIntent().getStringExtra("setting_menu");
    }

    @Override
    public void initUI() {
        ib_tbmain_back.setVisibility(View.VISIBLE);
        switch (mItemSetting) {
            default:
                loadFragmentProfileEdit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.putExtra(mConstant.NAVBOTTOM_TAB_ID, mConstant.NAVBOTTOM_TAB_PROFILE);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("[ aSetting ]", "- onMethod = onActivityResult || requestCode = "+requestCode+" || resultCode = "+resultCode);
        if (requestCode == INTENT_CODE_UPLOAD_PROFILE_IMAGE) {
            final Uri imageURL = mProfileEditFragment.getImageUri(data);
            if (imageURL != null) {
                Log.e("[ aSetting ]", "- onMethod = onActivityResult || imageURL = "+ imageURL.toString());
                mProfileEditFragment.setProfileImage(imageURL);
                mProfileEditFragment.apiUpdateProfileImage(mProfileEditFragment.mProfileImagePath);
            } else {
                onShowSnackbar("Gagal mengambil foto, coba lagi.", 0);
                Log.e("[ aSetting ]", "- onMethod = onActivityResult || imageURL = null");
            }
        }
    }

    public void onShowSnackbar(String message, int type) {
        showSnackBar(cl_setting, message, type);
    }

    private void loadFragmentProfileEdit() {
        Bundle Bundle = new Bundle();
        Bundle.putString("fragment_container", "ProfileEdit");

        mProfileEditFragment = new ProfileEditFragment();
        mProfileEditFragment.setArguments(Bundle);

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fl_setting, mProfileEditFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    @OnClick(R.id.ib_tbmain_back)
    public void onClickToolbarBack() {
        Analytics.with(getApplicationContext()).track("Click", new Properties()
                .putValue("Type", "Toolbar Back Button")
                .putValue("Widget", "Toolbar Button")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Setting"));
        onBackPressed();
    }
}
