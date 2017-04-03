package id.pazpo.agent.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipbox.pazpo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.pazpo.agent.fragments.MessageDetailFragment;
import id.pazpo.agent.services.model.message.Message;
import id.pazpo.agent.utils.DataFormatter;
import id.pazpo.agent.views.holder.LoadmoreHolder;
import id.pazpo.agent.views.holder.MessageDetailHolder;

/**
 * Created by wais on 1/18/17.
 */

public class MessageDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_HEADER              = 0;
    private final int VIEW_TYPE_ITEM                = 1;
    private final int VIEW_TYPE_FOOTER              = 2;
    private boolean isHasHeader                     = false;
    private boolean isHasFooter                     = false;

    protected MessageDetailFragment mMessageDetailFragment;

    public List<Message> mMessageList;

    public MessageDetailAdapter(MessageDetailFragment mMessageDetailFragment) {
        this.mMessageDetailFragment = mMessageDetailFragment;
        this.mMessageList           = new ArrayList<>();
    }

    public MessageDetailAdapter(MessageDetailFragment mMessageDetailFragment, List<Message> mMessageList) {
        this.mMessageDetailFragment = mMessageDetailFragment;
        this.mMessageList           = mMessageList;
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
                container   = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_detail, parent, false);
                viewHolder  = new MessageDetailHolder(container);
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
        if (holder instanceof MessageDetailHolder) {
            if (mMessageList != null && mMessageList.size() > 0) {
                initMessageDetailHolder(holder, position);
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
        this.notifyDataSetChanged();
    }

    public void addItems(List<Message> messageList, int limitItem) {
        Collections.reverse(mMessageList);
        mMessageList.addAll(messageList);
        Collections.reverse(mMessageList);

        int scrollPosition  = (mMessageList.size() - limitItem) + 1;
        this.notifyItemRangeInserted(mMessageList.size()  , mMessageList.size());
        Log.d("[ adpMessageDetail ]", "- onMethod : addItems. || mMessageList.size() = "+mMessageList.size()+" || scrollToPosition = "+String.valueOf(scrollPosition));

    }

    public void showHeader() {
        this.isHasHeader = true;
    }

    public void hideHeader() {
        this.isHasHeader = false;
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

    private void onFocusList(int itemCount) {

    }

    private void initLoadmoreHolder(RecyclerView.ViewHolder holder) {
        LoadmoreHolder loadmoreHolder   = (LoadmoreHolder) holder;
        loadmoreHolder.agiv_holder_loadmore.setVisibility(View.VISIBLE);
    }

    private void initMessageDetailHolder(RecyclerView.ViewHolder holder, int position) {
        final Message message = mMessageList.get(position);
        MessageDetailHolder messageDetailHolder = (MessageDetailHolder) holder;
        boolean isUserLogin = message.UserID.equalsIgnoreCase(mMessageDetailFragment.mSharedPrefs.getMemberLogin().UserID);

        if (isUserLogin) {
            messageDetailHolder.ll_msg_detail_rv.setGravity(Gravity.RIGHT);
            messageDetailHolder.ll_msg_detail_rv.setPadding(60, 0, 0, 0);
            messageDetailHolder.ll_msg_detail_bubble.setBackgroundResource(R.drawable.bubble_i);
            messageDetailHolder.ll_msg_detail_info.setGravity(Gravity.RIGHT);
        } else {
            messageDetailHolder.ll_msg_detail_rv.setGravity(Gravity.LEFT);
            messageDetailHolder.ll_msg_detail_rv.setPadding(0, 0, 60, 0);
            messageDetailHolder.ll_msg_detail_bubble.setBackgroundResource(R.drawable.bubble_u);
            messageDetailHolder.ll_msg_detail_info.setGravity(Gravity.LEFT);
        }

        messageDetailHolder.tv_msg_detail_text.setText(message.Reply);
        messageDetailHolder.tv_msg_detail_time.setText(DataFormatter.formatTime(message.TransactTime, 1));
    }

}
