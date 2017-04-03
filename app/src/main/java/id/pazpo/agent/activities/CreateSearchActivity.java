package id.pazpo.agent.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.services.model.location.Area;
import id.pazpo.agent.services.model.location.City;
import id.pazpo.agent.services.model.location.Province;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import id.pazpo.agent.utils.DecimalTextWatcher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adigunawan on 1/16/17.
 */

public class CreateSearchActivity extends BaseActivity implements BaseMethodInterface {

    public String mSelectedPropertyType   = "WTB";
    public String mSelectedPropertyTypeID = "12";

    @BindView(R.id.cl_newsfeed_form)
    CoordinatorLayout cl_newsfeed_form;
    @BindView(R.id.tv_newsfeed_form_wtb)
    TextView tv_newsfeed_form_wtb;
    @BindView(R.id.tv_newsfeed_form_wts)
    TextView tv_newsfeed_form_wts;
    @BindView(R.id.tv_newsfeed_form_wtr)
    TextView tv_newsfeed_form_wtr;
    @BindView(R.id.tv_newsfeed_form_wtl)
    TextView tv_newsfeed_form_wtl;
    @BindView(R.id.tv_newsfeed_form_label_price)
    TextView tv_newsfeed_form_label_price;
    @BindView(R.id.et_newsfeed_form_price_min)
    EditText et_newsfeed_form_price_min;
    @BindView(R.id.et_newsfeed_form_price_max)
    EditText et_newsfeed_form_price_max;
    @BindView(R.id.et_newsfeed_form_fee)
    EditText et_newsfeed_form_fee;
    @BindView(R.id.et_newsfeed_form_notes)
    EditText et_newsfeed_form_notes;
    @BindView(R.id.sp_newsfeed_form_proptype)
    Spinner sp_newsfeed_form_proptype;
    @BindView(R.id.sp_newsfeed_form_province)
    Spinner sp_newsfeed_form_province;
    @BindView(R.id.sp_newsfeed_form_city)
    Spinner sp_newsfeed_form_city;
    @BindView(R.id.sp_newsfeed_form_area)
    Spinner sp_newsfeed_form_area;
    @BindView(R.id.btn_newsfeed_form_save)
    Button btn_newsfeed_form_save;
    @BindView(R.id.btn_newsfeed_form_cancel)
    Button btn_newsfeed_form_cancel;

    protected Map mSpinnerLocationData;
    protected Map mFormNewsfeedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_newsfeed_form);
        ButterKnife.bind(this);
        initData(savedInstanceState);
        initUI();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mFormNewsfeedData = new HashMap();

        Analytics.with(this).track("Page View", new Properties()
                .putValue("Type", "Activity")
                .putValue("Page", "Create Search"));

        apiGetAllProvince();
    }

    @Override
    public void initUI() {
        sp_newsfeed_form_proptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setValueFormSelectedPropertyType(position);
                Analytics.with(getApplicationContext()).track("Click", new Properties()
                        .putValue("Type", "Property Type Spinner")
                        .putValue("Widget", "Spinner")
                        .putValue("Property Type", sp_newsfeed_form_proptype.getSelectedItem().toString())
                        .putValue("Page Type", "Activity")
                        .putValue("Page", "Create Search"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_newsfeed_form_price_min.addTextChangedListener(new DecimalTextWatcher(et_newsfeed_form_price_min));
        et_newsfeed_form_price_max.addTextChangedListener(new DecimalTextWatcher(et_newsfeed_form_price_max));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.tv_newsfeed_form_wtb)
    protected void setTvBuy() {
        setmSelectedPropertyType(tv_newsfeed_form_wtb);
    }

    @OnClick(R.id.tv_newsfeed_form_wts)
    protected void setTvSell() {
        setmSelectedPropertyType(tv_newsfeed_form_wts);
    }

    @OnClick(R.id.tv_newsfeed_form_wtr)
    protected void setTvRent() {
        setmSelectedPropertyType(tv_newsfeed_form_wtr);
    }

    @OnClick(R.id.tv_newsfeed_form_wtl)
    protected void setTvLoan() {
        setmSelectedPropertyType(tv_newsfeed_form_wtl);
    }

    @OnClick(R.id.et_newsfeed_form_price_min)
    public void onClickPriceMin() {
        Analytics.with(this).track("Click", new Properties()
                .putValue("Type", "Minimum Budget EditText")
                .putValue("Widget", "EditText")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));
    }

    @OnClick(R.id.et_newsfeed_form_price_max)
    public void onClickPriceMax() {
        Analytics.with(this).track("Click", new Properties()
                .putValue("Type", "Maximum Budget EditText")
                .putValue("Widget", "EditText")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));
    }

    @OnClick(R.id.et_newsfeed_form_fee)
    public void onClickFee() {
        Analytics.with(this).track("Click", new Properties()
                .putValue("Type", "Fee EditText")
                .putValue("Widget", "EditText")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));
    }

    @OnClick(R.id.et_newsfeed_form_notes)
    public void onClickNotes() {
        Analytics.with(this).track("Click", new Properties()
                .putValue("Type", "Notes EditText")
                .putValue("Widget", "EditText")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));
    }

    @OnClick(R.id.btn_newsfeed_form_cancel)
    protected void cancelSearch() {
        Analytics.with(this).track("Click", new Properties()
                .putValue("Type", "Cancel Create Search Button")
                .putValue("Widget", "Button")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));
        finish();
    }

    @OnClick(R.id.btn_newsfeed_form_save)
    protected void submitSearch() {
        Analytics.with(this).track("Click", new Properties()
                .putValue("Type", "Submit Create Search Button")
                .putValue("Listing Type", mSelectedPropertyType)
                .putValue("Property Type", sp_newsfeed_form_proptype.getSelectedItem().toString())
                .putValue("Province", sp_newsfeed_form_province.getSelectedItem().toString())
                .putValue("City", sp_newsfeed_form_city.getSelectedItem().toString())
                .putValue("Area", sp_newsfeed_form_area.getSelectedItem().toString())
                .putValue("Minimum Price", et_newsfeed_form_price_min.getText().toString())
                .putValue("Maximum Price", et_newsfeed_form_price_max.getText().toString())
                .putValue("Fee", et_newsfeed_form_fee.getText().toString())
                .putValue("Notes", et_newsfeed_form_notes.getText().toString())
                .putValue("Widget", "Button")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));

        mFormNewsfeedData.put("pPropertyTypeID", mSelectedPropertyTypeID);
        mFormNewsfeedData.put("pMinPrice",  et_newsfeed_form_price_min.getText().toString());
        mFormNewsfeedData.put("pMaxPrice", et_newsfeed_form_price_max.getText().toString());
        mFormNewsfeedData.put("pFee", et_newsfeed_form_fee.getText().toString());
        mFormNewsfeedData.put("pNotes", et_newsfeed_form_notes.getText().toString());
        mFormNewsfeedData.put("pCreatedBy", mSharedPrefs.getMemberLogin().UserID);

        if (validationFormNewsfeed()) {
            apiCreateNewsfeed();
        }

    }

    protected void loadActivityMain() {
        startActivity(new Intent(CreateSearchActivity.this, MainActivity.class));
        finish();
    }

    protected void initSpinnerProvince(final List<Province> provinceCompanyList) {
        Log.d("[ fRegisterBio ]:", "listProvince.ProvinceName = "+ provinceCompanyList.get(0).ProvinceName);

        ArrayAdapter<Province> provinceArrayAdapter = new ArrayAdapter<Province>(this,
                android.R.layout.simple_spinner_item, provinceCompanyList);

        provinceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_newsfeed_form_province.setAdapter(provinceArrayAdapter);
        sp_newsfeed_form_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String provinceID           = provinceCompanyList.get(position).ProvinceID;
                String provinceName         = provinceCompanyList.get(position).ProvinceName;
                mSpinnerLocationData        = new HashMap();

                mSpinnerLocationData.put("pProvinceID", provinceID);
                mSpinnerLocationData.put("pProvinceName", provinceName);
                apiGetAllCity(provinceID);

                Analytics.with(getApplicationContext()).track("Click", new Properties()
                        .putValue("Type", "Province Spinner")
                        .putValue("Widget", "Spinner")
                        .putValue("Property Type", sp_newsfeed_form_province.getSelectedItem().toString())
                        .putValue("Page Type", "Activity")
                        .putValue("Page", "Create Search"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void initSpinnerCity(final List<City> cityList) {
        Log.d("[ aNewsfeedAdd ]:", "cityList.CityName = "+ cityList.get(0).CityName);

        ArrayAdapter<City> provinceArrayAdapter = new ArrayAdapter<City>(this,
                android.R.layout.simple_spinner_item, cityList);

        provinceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_newsfeed_form_city.setAdapter(provinceArrayAdapter);
        sp_newsfeed_form_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cityID       = cityList.get(position).CityID;
                String cityName     = cityList.get(position).CityName;

                mSpinnerLocationData.put("pcityID", cityID);
                mSpinnerLocationData.put("pCityName", cityName);
                apiGetAllArea(cityID);

                Analytics.with(getApplicationContext()).track("Click", new Properties()
                        .putValue("Type", "City Spinner")
                        .putValue("Widget", "Spinner")
                        .putValue("Property Type", sp_newsfeed_form_city.getSelectedItem().toString())
                        .putValue("Page Type", "Activity")
                        .putValue("Page", "Create Search"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void initSpinnerArea(final List<Area> areaList) {
        Log.d("[ aNewsfeedAdd ]:", "cityList.CityName = "+ areaList.get(0).AreaName);

        ArrayAdapter<Area> provinceArrayAdapter = new ArrayAdapter<Area>(this,
                android.R.layout.simple_spinner_item, areaList);

        provinceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_newsfeed_form_area.setAdapter(provinceArrayAdapter);
        sp_newsfeed_form_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String areaID       = areaList.get(position).AreaID;
                String areaName     = areaList.get(position).AreaName;

                mSpinnerLocationData.put("pAreaID", areaID);
                mSpinnerLocationData.put("pAreaName", areaName);

                mFormNewsfeedData.put("pLocation", mSpinnerLocationData.get("pProvinceName") + ","
                        + mSpinnerLocationData.get("pCityName") + ","
                        + mSpinnerLocationData.get("pAreaName"));

                Analytics.with(getApplicationContext()).track("Click", new Properties()
                        .putValue("Type", "Area Spinner")
                        .putValue("Widget", "Spinner")
                        .putValue("Property Type", sp_newsfeed_form_area.getSelectedItem().toString())
                        .putValue("Page Type", "Activity")
                        .putValue("Page", "Create Search"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void apiGetAllProvince() {
        showBlockingLoading("Load data province ...");

        Call<List<Province>> Call = mPazpoApp.mServiceHelper.getAllProvince();
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

    protected void apiGetAllCity(String pProvince) {
        showBlockingLoading("Load data kota ...");

        Call<List<City>> Call = mPazpoApp.mServiceHelper.getAllCityByProvince(pProvince);
        Call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful()) {
                    List<City> cityList = response.body();
                    initSpinnerCity(cityList);
                    hideLoading();
                    Log.d("Retrofit:", "Success request api getAllProvince. provinceList.size = "+ cityList.get(0).CityName);
                } else {
                    Log.d("Retrofit:", "Failed request api getAllProvince");
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                Log.d("Retrofit:", "Failure request api getAllProvince");
            }
        });
    }

    protected void apiGetAllArea(String pCityID) {
        showBlockingLoading("Load data area ...");

        Call<List<Area>> Call = mPazpoApp.mServiceHelper.getAllAreaByCity(pCityID);
        Call.enqueue(new Callback<List<Area>>() {
            @Override
            public void onResponse(Call<List<Area>> call, Response<List<Area>> response) {
                if (response.isSuccessful()) {
                    List<Area> areaList = response.body();
                    initSpinnerArea(areaList);
                    hideLoading();
                    Log.d("Retrofit:", "Success request api getAllProvince. provinceList.size = " + areaList.get(0).AreaName);
                } else {
                    Log.d("Retrofit:", "Failed request api getAllProvince");
                }
            }

            @Override
            public void onFailure(Call<List<Area>> call, Throwable t) {
                Log.d("Retrofit:", "Failure request api getAllProvince");
            }
        });
    }

    protected void apiCreateNewsfeed() {
        showBlockingLoading("Membuat newsfeed ...");

        String pPropertyTypeID  = (String) mFormNewsfeedData.get("pPropertyTypeID");
        String pLocation        = (String) mFormNewsfeedData.get("pLocation");
        String pMinPrice        = ((String) mFormNewsfeedData.get("pMinPrice")).replace(",", "").replace(".", "");
        String pMaxPrice        = ((String) mFormNewsfeedData.get("pMaxPrice")).replace(",", "").replace(".", "");
        String pFee             = ((String) mFormNewsfeedData.get("pFee")).replace(",", "").replace(".", "");
        String pNotes           = (String) mFormNewsfeedData.get("pNotes");
        String pCreatedBy       = (String) mFormNewsfeedData.get("pCreatedBy");

        Call<Newsfeed> Call = mPazpoApp.mServiceHelper.createNewsfeed(pPropertyTypeID, pLocation, pMinPrice, pMaxPrice, pFee, pNotes, pCreatedBy);
        Call.enqueue(new Callback<Newsfeed>() {
            @Override
            public void onResponse(Call<Newsfeed> call, Response<Newsfeed> response) {
                if (response.isSuccessful()) {
                    Newsfeed newsfeed = response.body();
                    hideLoading();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CreateSearchActivity.this);
                    builder.setMessage("Iklan jelajah berhasil di posting.")
                            .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    loadActivityMain();
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                    Log.d("Retrofit:", "Success request api createNewsfeed.");
                } else {
                    hideLoading();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CreateSearchActivity.this);
                    builder.setMessage("Iklan jelajah gagal di posting.")
                            .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    loadActivityMain();
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                    Log.d("Retrofit:", "Failed request api createNewsfeed");
                }
            }

            @Override
            public void onFailure(Call<Newsfeed> call, Throwable t) {
                hideLoading();
                final AlertDialog.Builder builder = new AlertDialog.Builder(CreateSearchActivity.this);
                builder.setMessage("Iklan jelajah proses gagal di posting.")
                        .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                loadActivityMain();
                                dialog.dismiss();
                            }
                        });
                builder.show();
                Log.d("Retrofit:", "Failure request api createNewsfeed. error = "+ t.getMessage());
            }
        });
    }

    protected boolean validationFormNewsfeed() {
        if (mFormNewsfeedData.get("pPropertyTypeID") == null || mFormNewsfeedData.get("pPropertyTypeID").toString().length() == 0) {
            showSnackBar(cl_newsfeed_form, "Harap isi jenis properti.", 0);
            return false;
        } else if (mFormNewsfeedData.get("pLocation") == null || mFormNewsfeedData.get("pLocation").toString().length() == 0) {
            showSnackBar(cl_newsfeed_form, "Harap pilih lokasi.", 0);
            return false;
        }
//        else if (mFormNewsfeedData.get("pMinPrice") == null || mFormNewsfeedData.get("pMinPrice").toString().length() == 0) {
//            showSnackBar(cl_newsfeed_form, "Harap isi minimal harga.", 0);
//            return false;
//        }
        else if (mFormNewsfeedData.get("pMaxPrice") == null || mFormNewsfeedData.get("pMaxPrice").toString().length() == 0) {
            showSnackBar(cl_newsfeed_form, "Harap isi maksimal harga.", 0);
            return false;
        }
//        else if (mFormNewsfeedData.get("pFee") == null || mFormNewsfeedData.get("pFee").toString().length() == 0) {
//            showSnackBar(cl_newsfeed_form, "Harap isi komisi.", 0);
//            return false;
//        }
        else if (mFormNewsfeedData.get("pNotes") == null || mFormNewsfeedData.get("pNotes").toString().length() == 0) {
            showSnackBar(cl_newsfeed_form, "Harap isi deskripsi.", 0);
            return false;
        } else {
            return true;
        }
    }

    protected void setValueFormSelectedPropertyType(int position) {
        switch (position) {
            case 0:
                if (mSelectedPropertyType.equalsIgnoreCase("WTS"))
                    mSelectedPropertyTypeID = "1";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTL"))
                    mSelectedPropertyTypeID = "2";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTB"))
                    mSelectedPropertyTypeID = "12";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTR"))
                    mSelectedPropertyTypeID = "18";
                break;
            case 1:
                if (mSelectedPropertyType.equalsIgnoreCase("WTS"))
                    mSelectedPropertyTypeID = "3";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTL"))
                    mSelectedPropertyTypeID = "4";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTB"))
                    mSelectedPropertyTypeID = "13";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTR"))
                    mSelectedPropertyTypeID = "19";
                break;
            case 2:
                if (mSelectedPropertyType.equalsIgnoreCase("WTS"))
                    mSelectedPropertyTypeID = "5";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTL"))
                    mSelectedPropertyTypeID = "6";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTB"))
                    mSelectedPropertyTypeID = "14";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTR"))
                    mSelectedPropertyTypeID = "20";
                break;
            case 3:
                if (mSelectedPropertyType.equalsIgnoreCase("WTS"))
                    mSelectedPropertyTypeID = "7";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTL"))
                    mSelectedPropertyTypeID = "8";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTB"))
                    mSelectedPropertyTypeID = "15";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTR"))
                    mSelectedPropertyTypeID = "21";
                break;
            case 4:
                if (mSelectedPropertyType.equalsIgnoreCase("WTS"))
                    mSelectedPropertyTypeID = "9";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTL"))
                    mSelectedPropertyTypeID = "10";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTB"))
                    mSelectedPropertyTypeID = "16";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTR"))
                    mSelectedPropertyTypeID = "22";
                break;
            case 5:
                if (mSelectedPropertyType.equalsIgnoreCase("WTS"))
                    mSelectedPropertyTypeID = "11";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTL"))
                    mSelectedPropertyTypeID = "24";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTB"))
                    mSelectedPropertyTypeID = "17";
                else if (mSelectedPropertyType.equalsIgnoreCase("WTR"))
                    mSelectedPropertyTypeID = "23";
                break;
        }
    }

    private void setmSelectedPropertyType(TextView selected) {
        Analytics.with(this).track("Click", new Properties()
                .putValue("Type", "Listing Type TextView")
                .putValue("Widget", "TextView")
                .putValue("Listing Type", selected.getText().toString())
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));

        mSelectedPropertyType = selected.getText().toString();
        selected.setTypeface(Typeface.DEFAULT_BOLD);
        selected.setTextColor(ContextCompat.getColor(this, R.color.black));
        selected.setBackground(ContextCompat.getDrawable(this, R.drawable.tv_yellow_border_background));

        List<TextView> tvOthers = new ArrayList<>();
        tvOthers.add(tv_newsfeed_form_wtb);
        tvOthers.add(tv_newsfeed_form_wts);
        tvOthers.add(tv_newsfeed_form_wtr);
        tvOthers.add(tv_newsfeed_form_wtl);
        tvOthers.remove(selected);
        for (int a = 0; a < tvOthers.size(); a++) {
            tvOthers.get(a).setTypeface(Typeface.DEFAULT);
            tvOthers.get(a).setTextColor(ContextCompat.getColor(this, R.color.grey));
            tvOthers.get(a).setBackground(null);
        }

        setValueFormSelectedPropertyType(sp_newsfeed_form_proptype.getSelectedItemPosition());

        switch (mSelectedPropertyType) {
            case "WTS":
                tv_newsfeed_form_label_price.setText("Harga");
                break;
            case "WTB":
            default:
                tv_newsfeed_form_label_price.setText("Anggaran");
                break;
        }
    }
}
