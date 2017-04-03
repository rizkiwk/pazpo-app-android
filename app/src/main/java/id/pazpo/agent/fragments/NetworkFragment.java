package id.pazpo.agent.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flipbox.pazpo.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pazpo.agent.activities.MainActivity;
import id.pazpo.agent.adapters.NetworkAdapter;
import id.pazpo.agent.adapters.NewsfeedAdapter;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.services.model.RestModel;
import id.pazpo.agent.services.model.network.ContactParam;
import id.pazpo.agent.services.model.network.Network;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import id.pazpo.agent.views.AnimatedGifImageView;
import id.pazpo.agent.views.listener.EndRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adigunawan on 2/27/17.
 */

public class NetworkFragment extends BaseFragment
        implements BaseMethodInterface, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_network_list) RecyclerView rv_network_list;
    @BindView(R.id.ll_loader_fragment_error) LinearLayout ll_loader_fragment_error;
    @BindView(R.id.tv_loader_fragment_empty) TextView tv_loader_fragment_empty;
    @BindView(R.id.btn_loader_fragment_error) Button btn_loader_fragment_error;
    @BindView(R.id.agiv_loader_fragment) AnimatedGifImageView agiv_loader_fragment;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipe_layout;

    private  int PERMISSIONS_REQUEST_READ_CONTACTS          = 101;
    private String mMemberID;

    protected int NETWORK_DEFAULT_CURRENT_PAGE              = 1;
    protected int NETWORK_DEFAULT_RESULT_COUNT              = 10;
    protected boolean isNullNetworkData                     = true;

    protected MainActivity mActivity;
    protected Context mContext;
    protected ArrayList<String> mLocalContact;

    private boolean isRetryNetwork                          = false;

    private NetworkAdapter mNetworkAdapter;
    private List<Network> mNetworkList;
    private Integer mNetworkTotalPage;

    private EndRecyclerViewScrollListener mNetworkScrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Container = inflater.inflate(R.layout.fragment_network, container, false);
        ButterKnife.bind(this, Container);

        initData(savedInstanceState);
        initUI();

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Network"));

        return Container;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mActivity           = (MainActivity) getActivity();
        mContext            = getContext();
        mNetworkList        = new ArrayList<>();
        mMemberID           = mSharedPrefs.getMemberLogin().MemberID;

        onCheckRequestReadContact();
    }

    @Override
    public void initUI() {
        initSwipeRefresh();
        initRecyclerView(mNetworkList);
        showLoadingFragment(agiv_loader_fragment);
    }

    @Override
    public void onRefresh() {
        Analytics.with(mContext).track("Pull To Refresh", new Properties()
                .putValue("Type", "Swipe Refresh Network")
                .putValue("Widget", "Swipe Layout")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Network"));

        isRetryNetwork                  = true;
        NETWORK_DEFAULT_CURRENT_PAGE    = 1;
        NETWORK_DEFAULT_RESULT_COUNT    = 10;

        apiGetAllNetwork(mMemberID, NETWORK_DEFAULT_CURRENT_PAGE, NETWORK_DEFAULT_RESULT_COUNT);

        rv_network_list.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mActivity, "Permission Granted", Toast.LENGTH_SHORT).show();
                    getAllContacts();
                } else {
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                }
            }
        }
    }

    public void initSwipeRefresh() {
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));
    }

    public void initRecyclerView(List<Network> networkList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        rv_network_list.setLayoutManager(linearLayoutManager);

        if (networkList == null || networkList.size() == 0) {
            mNetworkAdapter = new NetworkAdapter(this);
        } else {
            mNetworkAdapter = new NetworkAdapter(this, networkList);
        }
        rv_network_list.setAdapter(mNetworkAdapter);

        mNetworkScrollListener = new EndRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, boolean loading, RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mNetworkTotalPage > NETWORK_DEFAULT_CURRENT_PAGE) {
                            Log.d("[ fNewsfeed ]", "onLoadmore newsfeed adapter. mNetworkTotalPage > NETWORK_DEFAULT_CURRENT_PAGE");
                            apiGetAllNetwork(mMemberID, NETWORK_DEFAULT_CURRENT_PAGE, NETWORK_DEFAULT_RESULT_COUNT);
                        }
                    }
                });
            }
        };

        rv_network_list.addOnScrollListener(mNetworkScrollListener);
        mNetworkScrollListener.resetState();
    }

    public void showRecyclerView(List<Network> networkList) {
        if (networkList.size() < 0) {
            rv_network_list.setVisibility(View.GONE);
            ll_loader_fragment_error.setVisibility(View.VISIBLE);
        } else {
            if (isRetryNetwork) {
                mNetworkAdapter.clearItem();
            }

            Log.e("[ fNetwork ]", "- onMethod : showRecyclerView || networkList = "+ networkList.toString());

            mNetworkAdapter.addItems(networkList);
            mNetworkList.addAll(networkList);

            isRetryNetwork = false;

            rv_network_list.setVisibility(View.VISIBLE);
            ll_loader_fragment_error.setVisibility(View.GONE);
        }
        hideLoadingFragment(agiv_loader_fragment);
    }

    private void onCheckRequestReadContact() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_CONTACTS)) {
                Log.e("[ fNetwork ]", "- onMethod : onCheckRequestReadContact || checkSelfPermission = true || shouldShowRequestPermissionRationale = if.");
                getAllContacts();
            } else {
                Log.e("[ fNetwork ]", "- onMethod : onCheckRequestReadContact || checkSelfPermission = true || shouldShowRequestPermissionRationale = else.");
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            Log.e("[ fNetwork ]", "- onMethod : onCheckRequestReadContact || checkSelfPermission = else.");
            getAllContacts();
        }
    }

    private void reloadNetworkList() {
        btn_loader_fragment_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    isRetryNetwork                  = true;
                    NETWORK_DEFAULT_CURRENT_PAGE    = 1;
                    NETWORK_DEFAULT_RESULT_COUNT    = 10;

                    apiGetAllNetwork(mMemberID, NETWORK_DEFAULT_CURRENT_PAGE, NETWORK_DEFAULT_RESULT_COUNT);
                } else {
                    mActivity.showEventSnackbar("Gagal sinkronisasi jaringan dengan kontak.");
                }
            }
        });
        rv_network_list.setVisibility(View.GONE);
        ll_loader_fragment_error.setVisibility(View.VISIBLE);
    }

    private void apiSetNetworkByContact(String pMemberID, List<String> pMobile) {
        Call<RestModel> Call = mPazpoApp.mServiceHelper.setNetworkByContact(pMemberID, pMobile);
        Call.enqueue(new Callback<RestModel>() {
            @Override
            public void onResponse(Call<RestModel> call, Response<RestModel> response) {
                List<Network> networkList = new ArrayList<>();

                if (response.isSuccessful()) {
                    Gson gson                       = new Gson();
                    RestModel restModel             = response.body();
                    JsonElement jsonNetwork         = restModel.data.getAsJsonObject().get("network");
                    JsonElement jsonNetworkTotal    = restModel.data.getAsJsonObject().get("total_network");
                    JsonElement jsonLastPage        = restModel.data.getAsJsonObject().get("last_page");

                    if (restModel.status.equalsIgnoreCase("error")) {
                        showRecyclerView(networkList);
                        mActivity.showEventSnackbar("Gagal sinkronisasi jaringan dengan kontak.");
                    } else {
                        networkList         = gson.fromJson(jsonNetwork, new TypeToken<List<Network>>(){}.getType());
                        mNetworkTotalPage   = gson.fromJson(jsonLastPage, new TypeToken<Integer>(){}.getType());
                        mNetworkList        = networkList;

                        if (networkList == null) {
                            networkList = new ArrayList<>();
                        }

                        if (mNetworkTotalPage > NETWORK_DEFAULT_CURRENT_PAGE) {
                            Log.d("[ fNewsfeed ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE");
                            mNetworkAdapter.showFooter();
                            NETWORK_DEFAULT_CURRENT_PAGE   = NETWORK_DEFAULT_CURRENT_PAGE + 1;

                            if (mNetworkTotalPage == NETWORK_DEFAULT_CURRENT_PAGE) {
                                Log.d("[ fNewsfeed ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage == NEWSFEED_DEFAULT_CURRENT_PAGE");
                                mNetworkAdapter.hideFooter();
                            }
                        }

                        showRecyclerView(networkList);
                        Log.e("[ fNetwork ]", "- onMethod : apiSetNetworkByContact || networkList = "+ networkList.toString());
                    }
                } else {
                    showRecyclerView(networkList);
                    mActivity.showEventSnackbar("Gagal sinkronisasi jaringan dengan kontak.");
                }

                hideLoadingFragment(agiv_loader_fragment);

                if (swipe_layout.isRefreshing()) {
                    swipe_layout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<RestModel> call, Throwable t) {
                Log.d("[ fNetwork ]", "- onMethod : apiSetNetworkByContact || error_message = "+ t.getMessage());
                reloadNetworkList();
                mActivity.showEventSnackbar("Gagal sinkronisasi jaringan dengan kontak.");
                hideLoadingFragment(agiv_loader_fragment);

                if (swipe_layout.isRefreshing()) {
                    swipe_layout.setRefreshing(false);
                }
            }
        });
    }

    private void apiGetAllNetwork(final String pMemberID, int currentPage, int resultCode) {
        String pCurrentPage = String.valueOf(currentPage);
        String pResultCode  = String.valueOf(resultCode);

        Call<RestModel> Call = mPazpoApp.mServiceHelper.getAllNetwork(pMemberID, pCurrentPage, pResultCode);
        Call.enqueue(new Callback<RestModel>() {
            @Override
            public void onResponse(Call<RestModel> call, Response<RestModel> response) {
                List<Network> networkList = new ArrayList<>();

                if (response.isSuccessful()) {
                    Gson gson                       = new Gson();
                    RestModel restModel             = response.body();
                    JsonElement jsonNetwork         = restModel.data.getAsJsonObject().get("network");
                    JsonElement jsonNetworkTotal    = restModel.data.getAsJsonObject().get("total_network");
                    JsonElement jsonLastPage        = restModel.data.getAsJsonObject().get("last_page");

                    if (restModel.status.equalsIgnoreCase("error")) {
                        showRecyclerView(networkList);
                        mActivity.showEventSnackbar("Gagal sinkronisasi jaringan dengan kontak.");
                    } else {
                        networkList         = gson.fromJson(jsonNetwork, new TypeToken<List<Network>>(){}.getType());
                        mNetworkTotalPage   = gson.fromJson(jsonLastPage, new TypeToken<Integer>(){}.getType());
                        mNetworkList        = networkList;

                        if (networkList == null) {
                            networkList = new ArrayList<>();
                        }

                        if (mNetworkTotalPage > NETWORK_DEFAULT_CURRENT_PAGE) {
                            Log.d("[ fNewsfeed ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE");
                            mNetworkAdapter.showFooter();
                            NETWORK_DEFAULT_CURRENT_PAGE   = NETWORK_DEFAULT_CURRENT_PAGE + 1;

                            if (mNetworkTotalPage == NETWORK_DEFAULT_CURRENT_PAGE) {
                                Log.d("[ fNewsfeed ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage == NEWSFEED_DEFAULT_CURRENT_PAGE");
                                mNetworkAdapter.hideFooter();
                            }
                        }

                        showRecyclerView(networkList);
                        Log.e("[ fNetwork ]", "- onMethod : apiGetAllNetwork || networkList = "+ networkList.toString());
                    }
                } else {
                    showRecyclerView(networkList);
                    mActivity.showEventSnackbar("Gagal sinkronisasi jaringan dengan kontak.");
                }

                hideLoadingFragment(agiv_loader_fragment);

                if (swipe_layout.isRefreshing()) {
                    swipe_layout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<RestModel> call, Throwable t) {
                Log.d("[ fNetwork ]", "- onMethod : apiGetAllNetwork || error_message = "+ t.getMessage());
                reloadNetworkList();
                mActivity.showEventSnackbar("Gagal sinkronisasi jaringan dengan kontak.");
                hideLoadingFragment(agiv_loader_fragment);

                if (swipe_layout.isRefreshing()) {
                    swipe_layout.setRefreshing(false);
                }
            }
        });
    }

    private void getAllContacts() {
        HashSet<String> localContact    = new HashSet<>();
        ContentResolver cr              = mActivity.getContentResolver();
        Cursor cur                      = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String phoneNumber      = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String formattedNumber  = phoneNumber.replace("+62", "0").replace("-", "").replace(" ", "");
                localContact.add(formattedNumber);
            }

            if (localContact.size() == 0) {
                mLocalContact = new ArrayList<>();
            } else {
                mLocalContact = new ArrayList<>(localContact);
            }

            apiSetNetworkByContact(mMemberID, mLocalContact);
        } else {
            mActivity.showEventSnackbar("Tidak ada kontak tersedia.");
            hideLoadingFragment(agiv_loader_fragment);
        }
        cur.close();
    }

}
