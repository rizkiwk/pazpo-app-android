package id.pazpo.agent.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.flipbox.pazpo.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.pazpo.agent.adapters.GuideAdapter;
import id.pazpo.agent.services.model.location.Province;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wais on 1/12/17.
 */

public class FirstRunAppActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    @BindView(R.id.fr_guide_main_container) FrameLayout fr_guide_main_container;
    @BindView(R.id.vp_guide_container) ViewPager vp_guide_container;
    @BindView(R.id.cpi_guide_container) CirclePageIndicator cpi_guide_container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        initData();

        GuideAdapter adapter = new GuideAdapter(getSupportFragmentManager());
        vp_guide_container.setAdapter(adapter);
        cpi_guide_container.setViewPager(vp_guide_container);
        vp_guide_container.setCurrentItem(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initPermissionCheck();
        } else {
            fr_guide_main_container.setVisibility(View.VISIBLE);
        }
    }

    protected void initData() {
        apiGetAllProvince();
        apiGetAllProvinceCompany();
    }

    @OnClick(R.id.btn_guide_login)
    protected void authenticate() {
        mSharedPrefs.setIsFirstRunApp(false);
        startActivity(new Intent(this, AuthenticationActivity.class));
        finish();
    }

    protected void initPermissionCheck() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED)) {
            dialogPermission();
        } else {
            fr_guide_main_container.setVisibility(View.VISIBLE);
        }
    }

    protected void dialogPermission() {
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.VIBRATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.RECEIVE_BOOT_COMPLETED }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[2] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[3] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[4] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[5] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[6] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[7] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[8] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[9] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[10] != PackageManager.PERMISSION_GRANTED) {
                        dialogPermission();
                    } else {
                        fr_guide_main_container.setVisibility(View.VISIBLE);
                    }
                } else {
                    dialogPermission();
                }
            }
        }
    }

    protected void apiGetAllProvince() {
        Call<List<Province>> Call = mPazpoApp.mServiceHelper.getAllProvince();
        Call.enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful()) {
                    List<Province> provinceList = response.body();
                    mSharedPrefs.setProvince(provinceList);
                    Log.d("Retrofit:", "Success request api getAllProvince. provinceList.size = "+ provinceList.size());
                    Log.d("Retrofit:", "Success request api getAllProvince. provinceList.size = "+ provinceList.get(0).ProvinceName);
                } else {
                    Log.d("Retrofit:", "Failed request api getAllProvince");
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                Log.d("Retrofit:", "Failure request api getAllProvince");
            }
        });
    }

    protected void apiGetAllProvinceCompany() {
        Call<List<Province>> Call = mPazpoApp.mServiceHelper.getAllProvinceCompany();
        Call.enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful()) {
                    List<Province> provinceCompanyList = response.body();
                    mSharedPrefs.setProvinceCompany(provinceCompanyList);
                    Log.d("Retrofit:", "Success request api getAllProvinceCompany. provinceList.size = "+ provinceCompanyList.size());
                    Log.d("Retrofit:", "Success request api getAllProvinceCompany. provinceList.ProvinceName = "+ provinceCompanyList.get(0).ProvinceName);
                } else {
                    Log.d("Retrofit:", "Failed request api getAllProvinceCompany");
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                Log.d("Retrofit:", "Failure request api getAllProvinceCompany");
            }
        });
    }
}
