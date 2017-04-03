package id.pazpo.agent.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.pazpo.agent.activities.MainActivity;
import id.pazpo.agent.activities.SettingActivity;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.utils.DataFormatter;
import id.pazpo.agent.views.AnimatedGifImageView;

/**
 * Created by adigunawan on 1/12/17.
 */

public class ProfileFragment extends BaseFragment implements BaseMethodInterface {

    @BindView(R.id.civ_profile) CircleImageView civ_profile;
    @BindView(R.id.tv_profile_name) TextView tv_profile_name;
    @BindView(R.id.tv_profile_office) TextView tv_profile_office;
    @BindView(R.id.tv_profile_join_since) TextView tv_profile_join_since;
    @BindView(R.id.btn_profile_edit) Button btn_profile_edit;
    @BindView(R.id.rv_profile_list) RecyclerView rv_profile_list;
    @BindView(R.id.ll_loader_fragment_error) LinearLayout ll_loader_fragment_error;
    @BindView(R.id.btn_loader_fragment_error) Button btn_loader_fragment_error;
    @BindView(R.id.agiv_loader_fragment) AnimatedGifImageView agiv_loader_fragment;

    private Context mContext;
    private MainActivity mMainActivity;
    private FragmentManager mFragmentManager;
    private ProfileEditFragment mProfileEditFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Container = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, Container);

        initData(savedInstanceState);
        initUI();

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Profile"));

        return Container;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mContext            = getContext();
        mMainActivity       = (MainActivity) getActivity();
        mFragmentManager    = mMainActivity.getSupportFragmentManager();
    }

    @Override
    public void initUI() {
        showLoadingFragment(agiv_loader_fragment);
        showContentProfile();
    }

    @OnClick(R.id.btn_profile_edit)
    public void onClickBtnEditProfile() {
        loadActivitySetting();
    }

    private void loadActivitySetting() {
        Intent intent   = new Intent(mContext, SettingActivity.class);
        intent.putExtra("setting_menu", "profile_edit");
        mMainActivity.startActivity(intent);
        mMainActivity.finish();
    }

    private void showContentProfile() {
        String imgProfilePath, imgProfileName, imgProfileURL;

        if (mSharedPrefs.getMemberLogin().UserImageURL == null) {
            imgProfilePath   = mPazpoApp.mServiceHelper.IMG_PROFILE_UPLOAD_PATH;
            imgProfileName   = mSharedPrefs.getMemberLogin().UserImage;
            imgProfileURL    = imgProfilePath + imgProfileName;
        } else {
            imgProfileURL    = mSharedPrefs.getMemberLogin().UserImageURL;
        }

        String firstname        = mSharedPrefs.getMemberLogin().FirstName;
        String lastname         = mSharedPrefs.getMemberLogin().LastName;
        String officename       = mSharedPrefs.getMemberLogin().CompanyName;
        String memberJoin       = DataFormatter.formatTime(mSharedPrefs.getMemberLogin().CreatedDate, 0);

        Picasso.with(getContext())
                .load(imgProfileURL)
                .fit()
                .placeholder(R.drawable.ic_account_circle)
                .into(civ_profile);

        civ_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.with(mContext).track("Click", new Properties()
                        .putValue("Type", "Profile Photo")
                        .putValue("Widget", "Circle Image View")
                        .putValue("Page Type", "Fragment")
                        .putValue("Page", "Profile"));
            }
        });

        tv_profile_name.setText(firstname + " " + lastname);
        tv_profile_office.setText(officename);
        tv_profile_join_since.setText(memberJoin);

        hideLoadingFragment(agiv_loader_fragment);
    }
}
