package id.pazpo.agent.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipbox.pazpo.R;
import com.github.nkzawa.emitter.Emitter;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.pazpo.agent.activities.MessageDetailActivity;
import id.pazpo.agent.adapters.MessageDetailAdapter;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.services.model.message.Message;
import id.pazpo.agent.services.model.message.MessageGet;
import id.pazpo.agent.views.AnimatedGifImageView;
import id.pazpo.agent.views.listener.EndRecyclerViewScrollListener;
import id.pazpo.agent.views.listener.TopRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wais on 1/18/17.
 */

public class MessageDetailFragment extends BaseFragment
        implements BaseMethodInterface {

    @BindView(R.id.rv_message_detail)
    RecyclerView rv_message_detail;
    @BindView(R.id.et_msg_detail_action)
    EditText et_msg_detail_action;
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
    private MessageDetailAdapter mMessageDetailAdapter;
    private TopRecyclerViewScrollListener mMessageDetailScrollListener;
    private String mUserEmail;
    private String mClientEmail;
    private int mMessageDetailTotalPage;

    public Message mMessage;
    public boolean isFromNewsfeed;

    protected Activity mActivity;
    protected String mNewsfeedNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Container = inflater.inflate(R.layout.fragment_message_detail, container, false);
        ButterKnife.bind(this, Container);

        initData(savedInstanceState);
        initUI();

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Message Detail"));

        return Container;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mContext        = getContext();
        mActivity       = getActivity();
        isFromNewsfeed  = getArguments().getBoolean(getString(R.string.intent_newsfeed_to_message));
        mNewsfeedNotes  = getArguments().getString(mConstant.INTENT_KEY_NEWSFEED_NOTES);
        mMessage        = getArguments().getParcelable(getString(R.string.intent_message_model));

        initWebsocket();

        if (isFromNewsfeed) {
            mUserEmail      = mMessage.UserID_One;
            mClientEmail    = mMessage.UserID_Two;
            apiCheckMessage(mMessage.UserID_One, mMessage.UserID_Two, MESSAGE_DEFAULT_CURRENT_PAGE, MESSAGE_DEFAULT_RESULT_COUNT);
        } else {
            apiGetMessage(mMessage.ConversationID, MESSAGE_DEFAULT_CURRENT_PAGE, MESSAGE_DEFAULT_RESULT_COUNT);
        }
    }

    @Override
    public void initUI() {
        showLoadingFragment(agiv_loader_fragment);
        initecyclerViewMessageDetail(new ArrayList<Message>());
    }

    @OnClick(R.id.fab_msg_detail_action)
    public void onClickBtnSend() {
        String textMessage = et_msg_detail_action.getText().toString();
        Log.d("[ fMessageDetail ]","mMessage.UserID_One = " + mMessage.UserID_One);
        Log.d("[ fMessageDetail ]","mMessage.UserID_Two = " + mMessage.UserID_Two);
        if (mSocket.connected()) {
            initSendMessage(mMessage, textMessage);
        } else {
            ((MessageDetailActivity) mActivity).showEventSnackbar("Koneksi internet lemah.");
        }

        Analytics.with(mActivity.getApplicationContext()).track("Click", new Properties()
                .putValue("Type", "Message Send Button")
                .putValue("Widget", "Button")
                .putValue("Message Body", textMessage)
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));
    }

    public void initSendMessage(Message message, String textMessage) {
        final Map sendMessageParam  = new HashMap();
        String textWithQuotes       = "";

        if ( (message.socketId == null || message.socketId.isEmpty()) && (message.UserID == null || message.UserID.isEmpty() ) ) {
            if (message.UserID_One.equalsIgnoreCase(mSharedPrefs.getMemberLogin().UserID)) {
                sendMessageParam.put("socket_id_one", message.UserID_Two);
                sendMessageParam.put("toUserTwo", message.UserID_Two);
            }
            else {
                sendMessageParam.put("socket_id_one", message.UserID_One);
                sendMessageParam.put("toUserTwo", message.UserID_One);
            }
        }
        else {
            sendMessageParam.put("socket_id_one", message.socketId);
            sendMessageParam.put("toUserTwo", message.UserID);
        }

        if (message.UserIDTwo == null)
            message.UserIDTwo = "";

        sendMessageParam.put("socket_id_two", message.socketIdTwo);
        sendMessageParam.put("toUserThree", message.UserIDTwo);
        sendMessageParam.put("from", mSharedPrefs.getMemberLogin().UserID);

        if (mNewsfeedNotes != null) {
            textWithQuotes   = mNewsfeedNotes + "\n \n" + textMessage;
            mNewsfeedNotes   = "";

            sendMessageParam.put("msg", textWithQuotes);
        } else {
            sendMessageParam.put("msg", textMessage);
        }

        Log.d("[ fMessageDetail ] :", "- sendMessageParam = "+ sendMessageParam.toString());

        if (textMessage.length() > 0) {
            mSocket.emit("sendMsg", new JSONObject(sendMessageParam));
            et_msg_detail_action.setText("");
            rv_message_detail.scrollToPosition(0 - 1);

            message                 = new Message();
            message.UserID          = mSharedPrefs.getMemberLogin().UserID;
            message.Reply           = (String) sendMessageParam.get("msg");
            message.TransactTime    = new SimpleDateFormat("EEE MMM dd HH:mm:ss").format(new Date());

            mMessageDetailAdapter.addItem(message);

            if (mMessageDetailAdapter.getItemCount() > 0) {
                rv_message_detail.setVisibility(View.VISIBLE);
                tv_loader_fragment_empty.setVisibility(View.GONE);
            }
            Log.d("[ fMessageDetail ] :", "- message = "+ message.toString());
        }
    }

    public void apiGetMessage(String pConversationID, int currentPage, int viewResultCount) {
        String pCurrentPage         = String.valueOf(currentPage);
        String pViewResultCount     = String.valueOf(viewResultCount);

        Call<MessageGet> Call = mPazpoApp.mServiceHelper.getMessage(pConversationID, pCurrentPage, pViewResultCount);
        Call.enqueue(new Callback<MessageGet>() {
            @Override
            public void onResponse(Call<MessageGet> call, Response<MessageGet> response) {
                if (response.isSuccessful()) {
                    MessageGet messageGet       = response.body();
                    List<Message> messageList   = messageGet.data;
                    mMessageDetailTotalPage     = messageGet.totalPage;

                    if (messageList == null) {
                        messageList    = new ArrayList<>();
                    }

                    showRecyclerViewMessageDetail(messageList);
                    hideLoadingFragment(agiv_loader_fragment);
                } else {
                    initecyclerViewMessageDetail(new ArrayList<Message>());
                    hideLoadingFragment(agiv_loader_fragment);
                    Log.d("[ Retrofit ]", "Failed request api GetMessage.");
                }
            }

            @Override
            public void onFailure(Call<MessageGet> call, Throwable t) {
                Analytics.with(mContext).track("Error", new Properties()
                        .putValue("Type", "Request API")
                        .putValue("Name", "GetChat")
                        .putValue("Message", t.getMessage()));

                showRetryGetMessage();
                hideLoadingFragment(agiv_loader_fragment);
                Log.d("[ Retrofit ]", "Failure request api GetMessage. error = "+ t.getMessage());
            }
        });
    }

    protected void apiCheckMessage(String pSender, String pRecipient, int currentPage, int viewResultCount) {
        String pCurrentPage         = String.valueOf(currentPage);
        String pViewResultCount     = String.valueOf(viewResultCount);

        Call<MessageGet> Call = mPazpoApp.mServiceHelper.checkMessage(pSender, pRecipient, pCurrentPage, pViewResultCount);
        Call.enqueue(new Callback<MessageGet>() {
            @Override
            public void onResponse(Call<MessageGet> call, Response<MessageGet> response) {
                if (response.isSuccessful()) {
                    MessageGet messageGet       = response.body();
                    List<Message> messageList   = messageGet.data;
                    mMessageDetailTotalPage     = messageGet.totalPage;

                    if (messageList == null) {
                        messageList    = new ArrayList<>();
                    }

                    Log.d("[ Retrofit ]", "Success request api GetMessage. MESSAGE_DEFAULT_CURRENT_PAGE = "+ MESSAGE_DEFAULT_CURRENT_PAGE);
                    Log.d("[ Retrofit ]", "Success request api GetMessage. mMessageDetailTotalPage = "+ mMessageDetailTotalPage);

                    if (mMessageDetailTotalPage > MESSAGE_DEFAULT_CURRENT_PAGE) {
                        Log.d("[ fMessageDetail ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE");
                        mMessageDetailAdapter.showHeader();
                        MESSAGE_DEFAULT_CURRENT_PAGE   = MESSAGE_DEFAULT_CURRENT_PAGE + 1;

                        if (mMessageDetailTotalPage == MESSAGE_DEFAULT_CURRENT_PAGE) {
                            Log.d("[ fMessageDetail ]", "- onMethod : apiGetAllNewsfeed. || mNewsfeedTotalPage == NEWSFEED_DEFAULT_CURRENT_PAGE");
                            mMessageDetailAdapter.hideHeader();
                        }
                    }

                    showRecyclerViewMessageDetail(messageList);
                    hideLoadingFragment(agiv_loader_fragment);
                } else {
                    initecyclerViewMessageDetail(new ArrayList<Message>());
                    hideLoadingFragment(agiv_loader_fragment);
                    Log.d("[ Retrofit ]", "Failed request api GetMessage.");
                }
            }

            @Override
            public void onFailure(Call<MessageGet> call, Throwable t) {
                Analytics.with(mContext).track("Error", new Properties()
                        .putValue("Type", "Request API")
                        .putValue("Name", "CheckConversation")
                        .putValue("Message", t.getMessage()));

                showRetryGetMessage();
                hideLoadingFragment(agiv_loader_fragment);
                Log.d("[ Retrofit ]", "Failure request api GetMessage. error = "+ t.getMessage());
            }
        });
    }

    private void initWebsocket() {
        try {
            mSocket.on("getMsg", ClientNewMessage);
            mSocket.on("newUserChat", newUserChat);
            mSocket.on("newSendChat", newSendChat);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Log.e("[ fMessageDetail ]", "error load socket in messageDetail.");
        }
    }

    private void initecyclerViewMessageDetail(List<Message> messageList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        rv_message_detail.setLayoutManager(linearLayoutManager);

        if (messageList == null || messageList.size() == 0) {
            mMessageDetailAdapter = new MessageDetailAdapter(this);
        } else {
            mMessageDetailAdapter = new MessageDetailAdapter(this, messageList);
        }
        rv_message_detail.setAdapter(mMessageDetailAdapter);

        mMessageDetailScrollListener = new TopRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mMessageDetailTotalPage > MESSAGE_DEFAULT_CURRENT_PAGE) {
                            if (isFromNewsfeed) {
                                apiCheckMessage(mUserEmail, mClientEmail, MESSAGE_DEFAULT_CURRENT_PAGE, MESSAGE_DEFAULT_RESULT_COUNT);
                            } else {
                                apiGetMessage(mMessage.ConversationID, MESSAGE_DEFAULT_CURRENT_PAGE, MESSAGE_DEFAULT_RESULT_COUNT);
                            }
                        }
                    }
                });
            }
        };
        rv_message_detail.addOnScrollListener(mMessageDetailScrollListener);
    }

    private void showRecyclerViewMessageDetail(List<Message> messageList) {
        if (messageList.size() == 0) {
            rv_message_detail.setVisibility(View.GONE);
            tv_loader_fragment_empty.setText("Anda belum pernah memulai percakapan.");
            tv_loader_fragment_empty.setVisibility(View.VISIBLE);
        } else {
            Log.d("[ fMessageDetail ]", "- onMethod: showRecyclerViewMessageDetail. || MESSAGE_DEFAULT_CURRENT_PAGE = "+ MESSAGE_DEFAULT_CURRENT_PAGE);
            Log.d("[ fMessageDetail ]", "- onMethod: showRecyclerViewMessageDetail. || mMessageDetailTotalPage = "+ mMessageDetailTotalPage);

            if (mMessageDetailTotalPage > MESSAGE_DEFAULT_CURRENT_PAGE) {
                Log.d("[ fMessageDetail ]", "- onMethod : showRecyclerViewMessageDetail. || mNewsfeedTotalPage > NEWSFEED_DEFAULT_CURRENT_PAGE");
                mMessageDetailAdapter.showHeader();
                MESSAGE_DEFAULT_CURRENT_PAGE   = MESSAGE_DEFAULT_CURRENT_PAGE + 1;

                if (mMessageDetailTotalPage == MESSAGE_DEFAULT_CURRENT_PAGE) {
                    Log.d("[ fMessageDetail ]", "- onMethod : showRecyclerViewMessageDetail. || mNewsfeedTotalPage == NEWSFEED_DEFAULT_CURRENT_PAGE");
                    mMessageDetailAdapter.hideHeader();
                }
            }

//            if (mMessageDetailAdapter.getItemCount() > MESSAGE_DEFAULT_RESULT_COUNT) {
                int scrollPosition = (mMessageDetailAdapter.getItemCount() - MESSAGE_DEFAULT_RESULT_COUNT) + 1;
                mMessageDetailAdapter.addItems(messageList, MESSAGE_DEFAULT_RESULT_COUNT);
                Log.d("[ fMessageDetail ]", "- onMethod : showRecyclerViewMessageDetail. || itemCount = "+String.valueOf(mMessageDetailAdapter.getItemCount())+" || smoothScrollToPosition = "+String.valueOf(scrollPosition));
//            } else {
//                mMessageDetailAdapter.addItems(messageList, messageList.size());
//            }

            rv_message_detail.setVisibility(View.VISIBLE);
            ll_loader_fragment_error.setVisibility(View.GONE);
        }
    }

    private void showRetryGetMessage() {
        btn_loader_fragment_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    if (mMessage.ConversationID == null) {
                        apiCheckMessage(mMessage.UserID_One, mMessage.UserID_Two, MESSAGE_DEFAULT_CURRENT_PAGE, MESSAGE_DEFAULT_RESULT_COUNT);
                    } else {
                        apiGetMessage(mMessage.ConversationID, MESSAGE_DEFAULT_CURRENT_PAGE, MESSAGE_DEFAULT_RESULT_COUNT);
                    }
                } else {
                    ((MessageDetailActivity) mActivity).showEventSnackbar("Koneksi internet anda lemah.");
                }
            }
        });
        btn_loader_fragment_error.setVisibility(View.VISIBLE);
        ll_loader_fragment_error.setVisibility(View.VISIBLE);
        rv_message_detail.setVisibility(View.GONE);
    }

    private Emitter.Listener newUserChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data   = (JSONObject) args[0];
                        Message message   = new Message();

                        Log.e("[ fMessageDetail ] :", "- onWebsocket newUserChat. data = "+ data.toString());
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private Emitter.Listener ClientNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data         = (JSONObject) args[0];
                        Message message         = new Message();

                        try {
                            message.UserID          = data.getString("from_id");
                            message.Reply           = data.getString("msg");
                            message.TransactTime    = data.getString("timestamp");

                            Analytics.with(mContext).track("Incoming Socket", new Properties()
                                    .putValue("Type", "Incoming Chat")
                                    .putValue("Widget", "Web Socket")
                                    .putValue("Page Type", "Fragment")
                                    .putValue("Timestamp", data.get("timestamp"))
                                    .putValue("Sender ID", data.get("from_id"))
                                    .putValue("Recipient ID", data.get("to_id"))
                                    .putValue("Message Body", data.get("msg"))
                                    .putValue("Page", "Message Detail"));

                            if (message != null) {
                                ((MessageDetailAdapter) rv_message_detail.getAdapter()).addItem(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e("[ fMessageDetail ] :", "- onWebsocket ClientNewMessage. data = "+ data.toString());
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private Emitter.Listener newSendChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      JSONObject data       = (JSONObject) args[0];
                        Message message     = new Message();

                        Log.e("[ fMessageDetail ] :", "- onWebsocket newSendChat. data = "+ data.toString());
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
}
