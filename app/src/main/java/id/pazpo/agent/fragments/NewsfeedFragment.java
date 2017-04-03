package id.pazpo.agent.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.flipbox.pazpo.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pazpo.agent.activities.MainActivity;
import id.pazpo.agent.activities.MessageDetailActivity;
import id.pazpo.agent.adapters.NewsfeedAdapter;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.services.model.RestModel;
import id.pazpo.agent.services.model.message.Message;
import id.pazpo.agent.services.model.network.Network;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import id.pazpo.agent.services.model.newsfeed.NewsfeedGetAll;
import id.pazpo.agent.views.AnimatedGifImageView;
import id.pazpo.agent.views.listener.EndRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adigunawan on 1/12/17.
 */

public class NewsfeedFragment extends BaseFragment
        implements BaseMethodInterface, SwipeRefreshLayout.OnRefreshListener  {

    @BindView(R.id.rv_newsfeed_list)
    public RecyclerView rv_newsfeed_list;
    @BindView(R.id.ll_loader_fragment_error)
    public LinearLayout ll_loader_fragment_error;
    @BindView(R.id.btn_loader_fragment_error)
    public Button btn_loader_fragment_error;
    @BindView(R.id.agiv_loader_fragment)
    public AnimatedGifImageView agiv_loader_fragment;
    @BindView(R.id.swipe_container)
    public SwipeRefreshLayout swipe_layout;

    public static int NEWSFEED_DEFAULT_CURRENT_PAGE              = 1;
    public static int NEWSFEED_DEFAULT_RESULT_COUNT              = 10;
    public boolean isRetryNewsfeed                               = false;

    private NewsfeedAdapter mNewsfeedAdapter;
    private List<Newsfeed> mNewsfeedList;
    private String mMemberID;
    private int mNewsfeedTotalPage;

    protected boolean isNullNewsfeedData                     = true;
    protected Activity mActivity;
    protected Context mContext;

    public EndRecyclerViewScrollListener mNewsfeedScrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Container = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        ButterKnife.bind(this, Container);

        initData(savedInstanceState);
        initUI();

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Newsfeed"));

        return Container;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mActivity               = getActivity();
        mContext                = getContext();
        mNewsfeedList           = new ArrayList<>();
        mNewsfeedTotalPage      = 0;
        mMemberID               = mSharedPrefs.getMemberLogin().MemberID;

        Newsfeed sPrefFilterNewsfeed = mSharedPrefs.getOptionFilterNewsfeed();
        apiGetAllNewsfeed(sPrefFilterNewsfeed.OptionMemberID, sPrefFilterNewsfeed.OptionListingType, sPrefFilterNewsfeed.OptionNetworkOnly, NEWSFEED_DEFAULT_CURRENT_PAGE, NEWSFEED_DEFAULT_RESULT_COUNT);
    }

    @Override
    public void initUI() {
        showLoadingFragment(agiv_loader_fragment);
        initSwipeRefresh();
        initRecyclerViewNewsfeed(mNewsfeedList);
    }

    @Override
    public void onRefresh() {
        Analytics.with(mContext).track("Pull To Refresh", new Properties()
                .putValue("Type", "Swipe Refresh Newsfeed")
                .putValue("Widget", "Swipe Layout")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Newsfeed"));

        NEWSFEED_DEFAULT_CURRENT_PAGE   = 1;

        rv_newsfeed_list.setVisibility(View.GONE);

        Newsfeed sPrefFilterNewsfeed = mSharedPrefs.getOptionFilterNewsfeed();
        apiGetAllNewsfeed(sPrefFilterNewsfeed.OptionMemberID, sPrefFilterNewsfeed.OptionListingType, sPrefFilterNewsfeed.OptionNetworkOnly, NEWSFEED_DEFAULT_CURRENT_PAGE, NEWSFEED_DEFAULT_RESULT_COUNT);
    }

    public void showLoadingFragment() {
        showLoadingFragment(agiv_loader_fragment);
        rv_newsfeed_list.setVisibility(View.GONE);
    }

    protected void retryGetAllNewsfeed() {
        btn_loader_fragment_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    isRetryNewsfeed = true;

                    Newsfeed sPrefFilterNewsfeed = mSharedPrefs.getOptionFilterNewsfeed();
                    apiGetAllNewsfeed(sPrefFilterNewsfeed.OptionMemberID, sPrefFilterNewsfeed.OptionListingType, sPrefFilterNewsfeed.OptionNetworkOnly, NEWSFEED_DEFAULT_CURRENT_PAGE, NEWSFEED_DEFAULT_RESULT_COUNT);
                } else {
                    ((MainActivity) mActivity).showEventSnackbar("Koneksi internet anda lemah.");
                }
            }
        });
        rv_newsfeed_list.setVisibility(View.GONE);
        ll_loader_fragment_error.setVisibility(View.VISIBLE);
    }

    protected void initSwipeRefresh() {
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));
    }

    public void initRecyclerViewNewsfeed(List<Newsfeed> newsfeedList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        rv_newsfeed_list.setLayoutManager(linearLayoutManager);

        if (newsfeedList == null || newsfeedList.size() == 0) {
            mNewsfeedAdapter = new NewsfeedAdapter(this);
        } else {
            mNewsfeedAdapter = new NewsfeedAdapter(this, newsfeedList);
        }
        rv_newsfeed_list.setAdapter(mNewsfeedAdapter);

        mNewsfeedScrollListener = new EndRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, boolean loading, RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE) {
                            Log.d("[ fNewsfeed ]", "onLoadmore newsfeed adapter. mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE");

                            Newsfeed sPrefFilterNewsfeed = mSharedPrefs.getOptionFilterNewsfeed();
                            apiGetAllNewsfeed(sPrefFilterNewsfeed.OptionMemberID, sPrefFilterNewsfeed.OptionListingType, sPrefFilterNewsfeed.OptionNetworkOnly, NEWSFEED_DEFAULT_CURRENT_PAGE, NEWSFEED_DEFAULT_RESULT_COUNT);
                        }
                    }
                });
            }
        };
        rv_newsfeed_list.addOnScrollListener(mNewsfeedScrollListener);
        mNewsfeedScrollListener.resetState();
    }

    public void showRecyclerViewNewsfeed(List<Newsfeed> newsfeedList) {
        if (newsfeedList.size() < 0) {
            rv_newsfeed_list.setVisibility(View.GONE);
            ll_loader_fragment_error.setVisibility(View.VISIBLE);
        } else {
            if (isRetryNewsfeed) {
                mNewsfeedAdapter.clearItem();
            }

            mNewsfeedAdapter.addItems(newsfeedList);
            mNewsfeedList.addAll(newsfeedList);

            isRetryNewsfeed = false;

            rv_newsfeed_list.setVisibility(View.VISIBLE);
            ll_loader_fragment_error.setVisibility(View.GONE);
        }

        hideLoadingFragment(agiv_loader_fragment);
    }

    public void loadActivityMessageDetail(Message message, String notes) {
        message.UserID_One      = mSharedPrefs.getMemberLogin().UserID;
        message.UserOneName     = mSharedPrefs.getMemberLogin().FirstName;
        message.UserOneImage    = mSharedPrefs.getMemberLogin().UserImage;

        Log.d("[ fNewsfeed ]", "data Message.UserID_One = "+ message.UserID_One);

        Intent intent           = new Intent(((MainActivity) getActivity()), MessageDetailActivity.class);
        intent.putExtra(getString(R.string.intent_newsfeed_to_message), true);
        intent.putExtra(mConstant.INTENT_KEY_NEWSFEED_NOTES, notes);
        intent.putExtra(getString(R.string.intent_message_model), message);
        ((MainActivity) getActivity()).startActivity(intent);
    }

    public void apiGetAllNewsfeed(String pMemberID, int listingType, boolean pNetworkOnly, int currentPage, int resultCode) {
        String pListingType = String.valueOf(listingType);
        String pCurrentPage = String.valueOf(currentPage);
        String pResultCode  = String.valueOf(resultCode);

        Call<RestModel> Call = mPazpoApp.mServiceHelper.getFilterNewsfeed(pMemberID, pListingType, pNetworkOnly, pCurrentPage, pResultCode);
        Call.enqueue(new Callback<RestModel>() {
            @Override
            public void onResponse(Call<RestModel> call, Response<RestModel> response) {
                if (response.isSuccessful()) {
                    Gson gson                       = new Gson();
                    RestModel restModel             = response.body();
                    JsonElement jsonNewsfeed        = restModel.data.getAsJsonObject().get("newsfeed");
                    JsonElement jsonNewsfeedTotal   = restModel.data.getAsJsonObject().get("total_page");

                    List<Newsfeed> newsfeedList     = gson.fromJson(jsonNewsfeed, new TypeToken<List<Newsfeed>>(){}.getType());
                    mNewsfeedTotalPage              = gson.fromJson(jsonNewsfeedTotal, new TypeToken<Integer>(){}.getType());
                    mNewsfeedList                   = newsfeedList;

                    isNullNewsfeedData              = false;

                    if (newsfeedList == null) {
                        newsfeedList    = new ArrayList<>();
                    }

                    if (mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE) {
                        Log.d("[ fNewsfeed ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE");
                        mNewsfeedAdapter.showFooter();
                        NEWSFEED_DEFAULT_CURRENT_PAGE   = NEWSFEED_DEFAULT_CURRENT_PAGE + 1;

                        if (mNewsfeedTotalPage == NEWSFEED_DEFAULT_CURRENT_PAGE) {
                            Log.d("[ fNewsfeed ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage == NEWSFEED_DEFAULT_CURRENT_PAGE");
                            mNewsfeedAdapter.hideFooter();
                        }
                    }

                    showRecyclerViewNewsfeed(newsfeedList);
                    hideLoadingFragment(agiv_loader_fragment);
                    Log.d("[ Retrofit ]", "Success request api GetAllNewsfeed. totalPages = " + mNewsfeedTotalPage);
                } else {
                    hideLoadingFragment(agiv_loader_fragment);
                    Log.d("[ Retrofit ]", "Failed request api GetAllNewsfeed.");
                }

                if (swipe_layout.isRefreshing()) {
                    swipe_layout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<RestModel> call, Throwable t) {
                if (mNewsfeedList.size() > 0) {
//                    showRecyclerViewNewsfeed(mNewsfeedList);
                } else {
                    if (swipe_layout.isRefreshing()) {
                        swipe_layout.setRefreshing(false);
                    }
                    rv_newsfeed_list.setVisibility(View.GONE);
                    ll_loader_fragment_error.setVisibility(View.VISIBLE);
                    isNullNewsfeedData  = true;
                    retryGetAllNewsfeed();
                    hideLoadingFragment(agiv_loader_fragment);
                    Log.d("Retrofit:", "Failure request api GetAllNewsfeed. error = "+ t.getMessage());
                }
            }
        });
    }
}
