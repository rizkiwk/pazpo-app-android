package id.pazpo.agent.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import id.pazpo.agent.fragments.MessageFragment;
import id.pazpo.agent.services.model.message.Message;
import id.pazpo.agent.views.holder.LoadmoreHolder;
import id.pazpo.agent.views.holder.MessageHolder;
import id.pazpo.agent.utils.DataFormatter;

/**
 * Created by wais on 1/18/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_HEADER              = 0;
    private final int VIEW_TYPE_ITEM                = 1;
    private final int VIEW_TYPE_FOOTER              = 2;
    private boolean isHasHeader                     = false;
    private boolean isHasFooter                     = false;

    protected MessageFragment mMessageFragment;

    public List<Message> mMessageList;

    public MessageAdapter(MessageFragment messageFragment) {
        this.mMessageFragment   = messageFragment;
        this.mMessageList       = new ArrayList<>();
    }

    public MessageAdapter(MessageFragment messageFragment, List<Message> messageList) {
        this.mMessageFragment   = messageFragment;
        this.mMessageList       = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View container;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case VIEW_TYPE_HEADER:
                container   = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_loadmore, parent, false);
                viewHolder  = new LoadmoreHolder(container);
                break;
            case VIEW_TYPE_ITEM:
                container   = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_list, parent, false);
                viewHolder  = new MessageHolder(container);
                break;
            case VIEW_TYPE_FOOTER:
                container   = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_loadmore, parent, false);
                viewHolder  = new LoadmoreHolder(container);
                break;
            default:
                viewHolder  = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageHolder) {
            if (mMessageList != null && mMessageList.size() > 0) {
                initMessageHolder(holder, position);
            }
        } else if (holder instanceof LoadmoreHolder) {
            initLoadmoreHolder(holder);
        }
    }

    @Override
    public int getItemCount() {
        if (isHasFooter) {
            return mMessageList.size() + 1;
        }
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHasHeader) {
            if (isPositionHeader(position)) {
                return VIEW_TYPE_HEADER;
            }
        }

        if (isHasFooter) {
            if (isPositionFooter(position)) {
                return VIEW_TYPE_FOOTER;
            }
        }
        return VIEW_TYPE_ITEM;
    }

    public void addItem(Message message) {
        mMessageList.add(message);
        this.notifyItemInserted(mMessageList.size() - 1);
    }

    public void addItems(List<Message> messageList) {
        mMessageList.addAll(messageList);
        this.notifyDataSetChanged();
    }

    public void clearAll() {
        mMessageList = new ArrayList<>();
    }

    public void showFooter() {
        this.isHasFooter = true;
    }

    public void hideFooter() {
        this.isHasFooter = false;
    }

    private boolean isPositionHeader(int position) {
        if (mMessageList.size() > 0 && position == 0) {
            return true;
        }
        return false;
    }

    private boolean isPositionFooter(int position) {
        if (mMessageList.size() > 0 && position == getItemCount() - 1) {
            return true;
        }
        return false;
    }

    private void initLoadmoreHolder(RecyclerView.ViewHolder holder) {
        LoadmoreHolder loadmoreHolder   = (LoadmoreHolder) holder;
        loadmoreHolder.agiv_holder_loadmore.setVisibility(View.VISIBLE);
    }

    private void initMessageHolder(RecyclerView.ViewHolder holder, int position) {
        String clientName, clientImage, clientImageURL;
        final Message message         = mMessageList.get(position);
        MessageHolder messageHolder   = (MessageHolder) holder;

        Log.d("[ adpMessage ]", "Message.Username = "+ message.UserOneName);

        String userID               = mMessageFragment.mSharedPrefs.getMemberLogin().UserID;
        String userID_One           = message.UserID_One == null ? "" : message.UserID_One;
        String userID_Two           = message.UserID_Two == null ? "" : message.UserID_Two;
        String replyTransactTime    = message.ReplyTransactTime == null ? "" : DataFormatter.formatTime(message.ReplyTransactTime, 2);
        String lastMessage          = message.LastMessage == null ? "" : message.LastMessage;
        String NewMessage           = message.NewMessage == null ? "" : message.NewMessage;

        String imageClientPath      = mMessageFragment.mPazpoApp.mServiceHelper.IMG_PROFILE_UPLOAD_PATH;

        if (userID.equalsIgnoreCase(userID_One)) {
            clientName          = message.UserTwoName == null ? "" : message.UserTwoName;
            clientImage         = message.UserTwoImage == null ? "" : message.UserTwoImage;
            clientImageURL      = imageClientPath + clientImage;
        } else if (userID.equalsIgnoreCase(userID_Two)) {
            clientName          = message.UserOneName == null ? "" : message.UserOneName;
            clientImage         = message.UserOneImage == null ? "" : message.UserOneImage;
            clientImageURL      = imageClientPath + clientImage;
        } else {
            clientName          = "Pazpo User";
            clientImageURL      = "";
        }

        Picasso.with(mMessageFragment.getContext())
                .load(clientImageURL)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(messageHolder.civ_message_list);

        messageHolder.tv_message_list_username.setText(clientName);
        messageHolder.tv_message_list_time.setText(replyTransactTime);
        messageHolder.tv_message_list_preview.setText(lastMessage);

        if (message.NewMessage == null || message.NewMessage.equalsIgnoreCase("0")) {
            messageHolder.tv_message_list_notif.setVisibility(View.GONE);
        } else {
            messageHolder.tv_message_list_preview.setText(lastMessage);
            messageHolder.tv_message_list_notif.setVisibility(View.VISIBLE);
        }

        messageHolder.tv_message_list_notif.setText(NewMessage);

        messageHolder.rl_message_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.with(mMessageFragment.getContext()).track("Click", new Properties()
                        .putValue("Type", "Message Item")
                        .putValue("Conversation ID", message.ConversationID)
                        .putValue("Conversation Subject", message.ConversationSubject)
                        .putValue("User ID", message.UserID)
                        .putValue("User ID One", message.UserID_One)
                        .putValue("User One Name", message.UserOneName)
                        .putValue("User One Image", message.UserOneImage)
                        .putValue("User One Status", message.UserOneStatus)
                        .putValue("User ID Two", message.UserID_Two)
                        .putValue("User Two Name", message.UserTwoName)
                        .putValue("User Two Image", message.UserTwoImage)
                        .putValue("User Two Status", message.UserTwoStatus)
                        .putValue("User ID Three", message.UserID_Three)
                        .putValue("User Three Name", message.UserThreeName)
                        .putValue("User Three Image", message.UserThreeImage)
                        .putValue("User Three Status", message.UserThreeStatus)
                        .putValue("Reply ID", message.ReplyID)
                        .putValue("Reply", message.Reply)
                        .putValue("Reply Type", message.ReplyType)
                        .putValue("Reply User ID", message.ReplyUserID)
                        .putValue("Reply Transact Time", message.ReplyTransactTime)
                        .putValue("Reply Status", message.ReplyStatus)
                        .putValue("New Message", message.NewMessage)
                        .putValue("Last Message", message.LastMessage)
                        .putValue("Total Page", message.TotalPage)
                        .putValue("Property ID", message.PropertyID)
                        .putValue("Transact Time", message.TransactTime)
                        .putValue("Status", message.Status)
                        .putValue("Widget", "List Item")
                        .putValue("Page Type", "Adapter")
                        .putValue("Page", "Message"));
                mMessageFragment.loadActivityMessageDetail(message);
            }
        });
    }
}
