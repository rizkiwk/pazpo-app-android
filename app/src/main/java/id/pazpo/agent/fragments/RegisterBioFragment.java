package id.pazpo.agent.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.pazpo.agent.activities.AuthenticationActivity;
import id.pazpo.agent.services.model.location.CompanyArea;
import id.pazpo.agent.services.model.location.Province;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wais on 1/12/17.
 */

public class RegisterBioFragment extends BaseFragment {

    @BindView(R.id.et_register_bio_name) EditText et_register_bio_name;
    @BindView(R.id.et_register_bio_phone) EditText et_register_bio_phone;
    @BindView(R.id.et_register_bio_email) EditText et_register_bio_email;
    @BindView(R.id.sp_register_bio_province) Spinner sp_register_bio_province;
    @BindView(R.id.sp_register_bio_company) Spinner sp_register_bio_company;
    @BindView(R.id.btn_register_bio_submit) Button btn_register_bio_submit;

    public Context mContext;
    public View mView;

    protected List<Province> mProvinceList;
    protected String mCompanyID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View ContainerView = inflater.inflate(R.layout.fragment_register_bio, container, false);
        ButterKnife.bind(this, ContainerView);

        mContext    = getContext();
        mView       = ContainerView;

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Register Bio")
                .putValue("Wizard", "Register")
                .putValue("Step", "Register Step 1"));

        initData();

        return ContainerView;
    }

    protected void initData() {
        if (mSharedPrefs.getProvinceCompany() == null) {
            apiGetAllProvinceCompany();
        } else {
            initSpinnerProvince(mSharedPrefs.getProvinceCompany());
        }
    }

    @OnClick(R.id.et_register_bio_name)
    public void clickName() {
        Analytics.with(getContext()).track("Click", new Properties()
                .putValue("Type", "Name EditText")
                .putValue("Widget", "EditText")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Register Bio"));
    }

    @OnClick(R.id.et_register_bio_phone)
    public void clickPhone() {
        Analytics.with(getContext()).track("Click", new Properties()
                .putValue("Type", "Phone Number EditText")
                .putValue("Widget", "EditText")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Register Bio"));
    }

    @OnClick(R.id.et_register_bio_email)
    public void clickEmail() {
        Analytics.with(getContext()).track("Click", new Properties()
                .putValue("Type", "Email EditText")
                .putValue("Widget", "EditText")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Register Bio"));
    }

    @OnClick(R.id.btn_register_bio_login)
    public void onClickBtnLogin() {
        hideKeyboard();
        Analytics.with(getContext()).track("Click", new Properties()
                .putValue("Type", "Login Button")
                .putValue("Widget", "Button")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Register Bio"));
        Log.d("onClickBtnLogin:", "Failure request api phone login.");
        ((AuthenticationActivity) getActivity()).onFBAccountKitLoginPhone(null);
    }

    @OnClick(R.id.btn_register_bio_submit)
    public void onClickBtnRegisterBio() {
        hideKeyboard();

        ((AuthenticationActivity) getActivity()).mRegisterData.put("pFullName", et_register_bio_name.getText().toString());
        ((AuthenticationActivity) getActivity()).mRegisterData.put("pEmail", et_register_bio_email.getText().toString());
        ((AuthenticationActivity) getActivity()).mRegisterData.put("pMobilePhone", et_register_bio_phone.getText().toString());
        ((AuthenticationActivity) getActivity()).mRegisterData.put("pCompanyID", mCompanyID);

        Analytics.with(getContext()).track("Click", new Properties()
                .putValue("Type", "Register Button")
                .putValue("Widget", "Button")
                .putValue("Name", et_register_bio_name.getText().toString())
                .putValue("Email", et_register_bio_email.getText().toString())
                .putValue("Phone Number", et_register_bio_phone.getText().toString())
                .putValue("Area ID", mCompanyID)
                .putValue("Area Name", sp_register_bio_company.getSelectedItem().toString())
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Register Bio"));

        if (validationFormBio()) {
            ((AuthenticationActivity) getActivity()).loadFragmentRegisterPhoto();
        }
    }

    protected void initSpinnerProvince(final List<Province> provinceCompanyList) {
        Log.d("[ fRegisterBio ]:", "listProvince.ProvinceName = "+ provinceCompanyList.get(0).ProvinceName);

        ArrayAdapter<Province> provinceArrayAdapter = new ArrayAdapter<Province>(getContext(),
                android.R.layout.simple_spinner_item, provinceCompanyList);

        provinceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_register_bio_province.setAdapter(provinceArrayAdapter);
        sp_register_bio_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String provinceID           = provinceCompanyList.get(position).ProvinceID;
                apiGetAllAreaByProvince(provinceID);
                Analytics.with(getContext()).track("Click", new Properties()
                        .putValue("Type", "Province Spinner")
                        .putValue("Widget", "Spinner")
                        .putValue("Province", sp_register_bio_province.getSelectedItem().toString())
                        .putValue("Page Type", "Fragment")
                        .putValue("Page", "Register Bio"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void initSpinnerAreaByProvince(final List<CompanyArea> areaByProvinceList) {
        ArrayAdapter<CompanyArea> areaArrayAdapter = new ArrayAdapter<CompanyArea>(getContext(),
                android.R.layout.simple_spinner_item, areaByProvinceList);

        areaArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_register_bio_company.setAdapter(areaArrayAdapter);
        sp_register_bio_company.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCompanyID = areaByProvinceList.get(position).CompanyID;
                Analytics.with(getContext()).track("Click", new Properties()
                        .putValue("Type", "Area Spinner")
                        .putValue("Widget", "Spinner")
                        .putValue("Province", sp_register_bio_company.getSelectedItem().toString())
                        .putValue("Page Type", "Fragment")
                        .putValue("Page", "Register Bio"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void apiGetAllProvinceCompany() {
        showBlockingLoading("Load Data Provinsi ...");

        Call<List<Province>> Call = mPazpoApp.mServiceHelper.getAllProvinceCompany();
        Call.enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful()) {
                    List<Province> provinceCompanyList = response.body();
                    mSharedPrefs.setProvinceCompany(provinceCompanyList);
                    initSpinnerProvince(provinceCompanyList);
                    hideLoading();
                    Log.d("Retrofit:", "Success request api getAllProvince. provinceList.size = "+ provinceCompanyList.get(0).ProvinceName);
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

    protected void apiGetAllAreaByProvince(String pProvinceID) {
        showBlockingLoading("Load Data Area ...");

        Call<List<CompanyArea>> Call = mPazpoApp.mServiceHelper.getAllAreaByProvince(pProvinceID);
        Call.enqueue(new Callback<List<CompanyArea>>() {
            @Override
            public void onResponse(Call<List<CompanyArea>> call, Response<List<CompanyArea>> response) {
                if (response.isSuccessful()) {
                    List<CompanyArea> areaList = response.body();
                    initSpinnerAreaByProvince(areaList);
                    hideLoading();
                    Log.d("Retrofit:", "Success request api getAllAreaByProvince. areaList.size = "+ areaList.get(0).CompanyName);
                } else {
                    Log.d("Retrofit:", "Failed request api getAllAreaByProvince");
                }
            }

            @Override
            public void onFailure(Call<List<CompanyArea>> call, Throwable t) {
                Log.d("Retrofit:", "Failure request api getAllAreaByProvince");
            }
        });
    }

    protected boolean validationFormBio() {
        Map registerData    = new HashMap();
        registerData        = ((AuthenticationActivity) getActivity()).mRegisterData;

        if (registerData.get("pFullName") == null) {
            et_register_bio_name.setError("Harap isi nama.");
            return false;
        } else if (registerData.get("pEmail") == null) {
            et_register_bio_email.setError("Harap isi email.");
            return false;
        } else if (registerData.get("pMobilePhone") == null) {
            et_register_bio_phone.setError("Harap isi telepon.");
            return false;
        } else {
            return true;
        }
    }
}
