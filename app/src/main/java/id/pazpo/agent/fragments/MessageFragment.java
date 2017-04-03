package id.pazpo.agent.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipbox.pazpo.R;
import com.github.nkzawa.emitter.Emitter;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pazpo.agent.activities.MainActivity;
import id.pazpo.agent.activities.MessageDetailActivity;
import id.pazpo.agent.adapters.MessageAdapter;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.services.model.message.Message;
import id.pazpo.agent.services.model.message.MessageGetAll;
import id.pazpo.agent.views.AnimatedGifImageView;
import id.pazpo.agent.views.listener.EndRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adigunawan on 1/12/17.
 */

public class MessageFragment extends BaseFragment implements BaseMethodInterface {

    @BindView(R.id.rv_message_list)
    RecyclerView rv_message_list;
    @BindView(R.id.ll_loader_fragment_error)
    LinearLayout ll_loader_fragment_error;
    @BindView(R.id.tv_loader_fragment_empty)
    TextView tv_loader_fragment_empty;
    @BindView(R.id.btn_loader_fragment_error)
    Button btn_loader_fragment_error;
    @BindView(R.id.agiv_loader_fragment)
    AnimatedGifImageView agiv_loader_fragment;

    private int MESSAGE_DEFAULT_CURRENT_PAGE              = 1;
    private int MESSAGE_DEFAULT_RESULT_COUNT              = 10;
    private Context mContext;
    private MessageAdapter mMessageAdapter;
    private EndRecyclerViewScrollListener mMessageScrollListener;
    private String mUserEmail;
    private int mMessageTotalPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Container = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, Container);

        initData(savedInstanceState);
        initUI();

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Message"));

        return Container;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mContext            = getContext();
        mUserEmail          = mSharedPrefs.getMemberLogin().UserID;
        mMessageTotalPage   = 1;

        initWebsocket();
        apiGetAllMessages( mUserEmail, MESSAGE_DEFAULT_RESULT_COUNT, MESSAGE_DEFAULT_CURRENT_PAGE);
    }

    @Override
    public void initUI() {
        showLoadingFragment(agiv_loader_fragment);
        initRecyclerViewMessage(new ArrayList<Message>());
    }

    public void loadActivityMessageDetail(Message message) {
        Intent intent = new Intent(((MainActivity) getActivity()), MessageDetailActivity.class);
        intent.putExtra(getString(R.string.intent_message_model), message);
        ((MainActivity) getActivity()).startActivity(intent);
    }

    public void apiGetAllMessages(String pUserEmail, int viewResultCount, int currentPage) {
        String pCurrentPage         = String.valueOf(currentPage);
        String pViewResultCount     = String.valueOf(viewResultCount);

        Call<MessageGetAll> Call = mPazpoApp.mServiceHelper.getAllMessages(pUserEmail, pViewResultCount, pCurrentPage);
        Call.enqueue(new Callback<MessageGetAll>() {
            @Override
            public void onResponse(Call<MessageGetAll> call, Response<MessageGetAll> response) {
                if (response.isSuccessful()) {
                    MessageGetAll messageGetAll = response.body();
                    List<Message> messageList   = messageGetAll.data;
                    mMessageTotalPage           = messageGetAll.totalPage;

                    if (messageList == null) {
                        messageList    = new ArrayList<>();
                    }

                    if (mMessageTotalPage > MESSAGE_DEFAULT_CURRENT_PAGE) {
                        Log.d("[ fMessage ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE");
                        mMessageAdapter.showFooter();
                        MESSAGE_DEFAULT_CURRENT_PAGE   = MESSAGE_DEFAULT_CURRENT_PAGE + 1;

                        if (mMessageTotalPage == MESSAGE_DEFAULT_CURRENT_PAGE) {
                            Log.d("[ fMessage ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage == NEWSFEED_DEFAULT_CURRENT_PAGE");
                            mMessageAdapter.hideFooter();
                        }
                    }

                    showRecyclerViewMessage(messageList);
                    hideLoadingFragment(agiv_loader_fragment);
                    Log.d("[ Retrofit ]", "Success request api GetAllMessages. MessageList.size = "+ messageList.size());
                } else {
                    retryGetMessageList();
                    hideLoadingFragment(agiv_loader_fragment);
                    Log.d("[ Retrofit ]", "Failed request api GetAllMessages.");
                }
            }

            @Override
            public void onFailure(Call<MessageGetAll> call, Throwable t) {
//                Analytics.with(mContext).track("Error", new Properties()
//                        .putValue("Type", "Request API")
//                        .putValue("API Name", "GetAllChatsV2")
//                        .putValue("User Email", mPazpoApp.mSharedPrefs.getMemberLogin().UserID)
//                        .putValue("User Phone Name", Build.BRAND)
//                        .putValue("User Phone OS", Build.TYPE)
//                        .putValue("Error Message", t.getMessage()));

                retryGetMessageList();
                hideLoadingFragment(agiv_loader_fragment);
                Log.d("[ Retrofit ]", "Failure request api GetAllMessages. error = "+ t.getMessage());
            }
        });
    }

    private void initWebsocket() {
        try {
            mSocket.on("userEntrance", userEntrance);
            mSocket.on("newClientChat", newClientChat);
            mSocket.on("newUserChat", newUserChat);
            mSocket.on("newSendChat", newSendChat);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Log.e("[ fMessageDetail ]", "error load socket in messageDetail.");
        }
    }

    private void initRecyclerViewMessage(List<Message> messageList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        rv_message_list.setLayoutManager(new LinearLayoutManager(mContext));

        if (messageList == null || messageList.size() == 0) {
            mMessageAdapter = new MessageAdapter(this);
        } else {
            mMessageAdapter = new MessageAdapter(this, messageList);
        }
        rv_message_list.setAdapter(mMessageAdapter);

        mMessageScrollListener = new EndRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, boolean loading, RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mMessageTotalPage > MESSAGE_DEFAULT_CURRENT_PAGE) {
                            apiGetAllMessages(mUserEmail, MESSAGE_DEFAULT_CURRENT_PAGE, MESSAGE_DEFAULT_RESULT_COUNT);
                        }
                    }
                });
            }
        };
        rv_message_list.addOnScrollListener(mMessageScrollListener);
    }

    private void showRecyclerViewMessage(List<Message> messageList) {
        if (messageList.size() == 0) {
            rv_message_list.setVisibility(View.GONE);
            tv_loader_fragment_empty.setText("Anda belum memiliki percakapan.");
            tv_loader_fragment_empty.setVisibility(View.VISIBLE);
        } else {
            mMessageAdapter.addItems(messageList);
            rv_message_list.setVisibility(View.VISIBLE);
            ll_loader_fragment_error.setVisibility(View.GONE);
        }
    }

    private void retryGetMessageList() {
        btn_loader_fragment_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiGetAllMessages( mUserEmail, MESSAGE_DEFAULT_RESULT_COUNT, MESSAGE_DEFAULT_CURRENT_PAGE);
            }
        });
        ll_loader_fragment_error.setVisibility(View.VISIBLE);
        rv_message_list.setVisibility(View.GONE);
    }

    private Emitter.Listener newSendChat = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            try {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JSONObject data = (JSONObject) args[0];
                        Log.e("[ fMessage ]", "- onWebsocket newSendChat. args = "+ data.toString());
                    }
                });
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener newUserChat = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            try
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JSONObject data = (JSONObject) args[0];
                        Log.e("[ fMessage ]", "- onWebsocket newUserChat. args = "+ data.toString());
                    }
                });
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener newClientChat = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            try {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String userID = (String) args[0];
                        Log.e("[ fMessage ]", "- onWebsocket newClientChat. data = "+ userID);

                        apiGetAllMessages( userID, MESSAGE_DEFAULT_RESULT_COUNT, MESSAGE_DEFAULT_CURRENT_PAGE);
//                        ((MessageAdapter) rv_message_list.getAdapter()).addItems(mMessageList);
                    }
                });
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener userEntrance = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JSONArray data = (JSONArray) args[0];
                        try {
                            JSONObject object = data.getJSONObject(0);
                            Analytics.with(mContext).track("Outgoing Socket", new Properties()
                                    .putValue("Type", "Outgoing Chat")
                                    .putValue("Widget", "Web Socket")
                                    .putValue("Page Type", "Fragment")
                                    .putValue("Conversation ID", object.get("ConversationID"))
                                    .putValue("Sender ID", mSharedPrefs.getMemberLogin().UserID)
                                    .putValue("Sender Name", mSharedPrefs.getMemberLogin().FirstName)
                                    .putValue("Recipient ID", object.get("UserID"))
                                    .putValue("Recipient Name", object.get("UserName"))
                                    .putValue("Message Body", object.get("LastMessage"))
                                    .putValue("Page", "Message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("[ fMessage ]", "- onWebsocket userEntrance. data = "+ data.toString());
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
