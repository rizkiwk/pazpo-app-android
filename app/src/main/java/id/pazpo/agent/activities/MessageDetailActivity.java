package id.pazpo.agent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.pazpo.agent.fragments.MessageDetailFragment;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.services.model.message.Message;

/**
 * Created by wais on 1/17/17.
 */

public class MessageDetailActivity extends BaseActivity implements BaseMethodInterface {

    @BindView(R.id.toolbar_msg_detail)
    Toolbar toolbar_msg_detail;
    @BindView(R.id.ib_toolbar_msg_detail_back)
    ImageButton ib_toolbar_msg_detail_back;
    @BindView(R.id.civ_toolbar_msg_detail)
    CircleImageView civ_toolbar_msg_detail;
    @BindView(R.id.tv_toolbar_msg_detail_name)
    TextView tv_toolbar_msg_detail_name;
    @BindView(R.id.tv_toolbar_msg_detail_status)
    TextView tv_toolbar_msg_detail_status;
    @BindView(R.id.cl_msg_detail)
    CoordinatorLayout cl_msg_detail;

    protected Message mMessage;
    protected String mNewsfeedNotes;
    protected boolean isFromNotif;
    protected boolean isFromNewsfeed;

    protected FragmentManager fm = getSupportFragmentManager();

    protected MessageDetailFragment mMessageDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_message_detail);
        ButterKnife.bind(this);

        initData(savedInstanceState);
        initUI();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MessageDetailActivity.this, MainActivity.class);
        intent.putExtra(mConstant.NAVBOTTOM_TAB_ID, mConstant.NAVBOTTOM_TAB_MESSAGE);
        startActivity(intent);
        finish();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        isFromNotif     = getIntent().getBooleanExtra(mConstant.ONESIGNAL_KEY_IS_FROM_NOTIF, false);
        isFromNewsfeed  = getIntent().getBooleanExtra(getString(R.string.intent_newsfeed_to_message), false);
        mNewsfeedNotes  = getIntent().getStringExtra(mConstant.INTENT_KEY_NEWSFEED_NOTES);
        mMessage        = getIntent().getParcelableExtra(getString(R.string.intent_message_model));
        Log.d("[ aMessageDetail ]", "data Message.UserID_One = "+ mMessage.UserID_One);
    }

    @Override
    public void initUI() {
        initToolbar();
        loadFragmentMessageDetail();
    }

    @OnClick(R.id.ib_toolbar_msg_detail_back)
    public void onClickToolbarBack() {
        Analytics.with(getApplicationContext()).track("Click", new Properties()
                .putValue("Type", "Toolbar Back Button")
                .putValue("Widget", "Toolbar Button")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Create Search"));
        onBackPressed();
    }

    public void initToolbar() {
        String clientName, clientImage, clientImageURL;
        String userID               = mSharedPrefs.getMemberLogin().UserID;
        String imageClientPath      = mPazpoApp.mServiceHelper.IMG_PROFILE_UPLOAD_PATH;

        if (userID.equalsIgnoreCase(mMessage.UserID_One)) {
            clientName          = mMessage.UserTwoName == null ? "" : mMessage.UserTwoName;
            clientImage         = mMessage.UserTwoImage == null ? "" : mMessage.UserTwoImage;
            clientImageURL      = imageClientPath + clientImage;
        } else if (userID.equalsIgnoreCase(mMessage.UserID_Two)) {
            clientName          = mMessage.UserOneName == null ? "" : mMessage.UserOneName;
            clientImage         = mMessage.UserOneImage == null ? "" : mMessage.UserOneImage;
            clientImageURL      = imageClientPath + clientImage;
        } else {
            clientName          = "Pazpo User";
            clientImageURL      = "";
        }

        Log.e("[ aMessageDetail ]", "clientImageURL = "+ clientImageURL);

        tv_toolbar_msg_detail_name.setText(clientName);
        tv_toolbar_msg_detail_status.setText("");

        Picasso.with(this)
                .load(clientImageURL)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(civ_toolbar_msg_detail);

        setSupportActionBar(toolbar_msg_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setElevation(1);
    }

    public void loadFragmentMessageDetail() {
        Analytics.with(this).track("Page View", new Properties()
                .putValue("Type", "Activity")
                .putValue("Conversation ID", mMessage.ConversationID)
                .putValue("Page", "Message Detail"));

        Bundle Bundle = new Bundle();
        Bundle.putString("fragment_container", "MessageDetailFragment");
        Bundle.putBoolean(getString(R.string.intent_newsfeed_to_message), isFromNewsfeed);
        Bundle.putString(mConstant.INTENT_KEY_NEWSFEED_NOTES, mNewsfeedNotes);
        Bundle.putParcelable(getString(R.string.intent_message_model), mMessage);

        Log.d("[ aMessageDetail ]", "Message.ConversationID = "+ mMessage.ConversationID);

        mMessageDetailFragment = new MessageDetailFragment();
        mMessageDetailFragment.setArguments(Bundle);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_msg_detail, mMessageDetailFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    public void showEventSnackbar(String text) {
        showSnackBar(cl_msg_detail, text, 0);
    }
}
