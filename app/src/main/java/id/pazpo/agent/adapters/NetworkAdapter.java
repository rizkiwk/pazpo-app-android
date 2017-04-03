package id.pazpo.agent.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipbox.pazpo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import id.pazpo.agent.fragments.NetworkFragment;
import id.pazpo.agent.services.model.network.Network;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import id.pazpo.agent.views.holder.LoadmoreHolder;
import id.pazpo.agent.views.holder.NetworkHolder;
import id.pazpo.agent.views.holder.NewsfeedHolder;

/**
 * Created by adigunawan on 2/28/17.
 */

public class NetworkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected NetworkFragment mNetworkFragment;
    public List<Network> mListNetwork;

    private final int VIEW_TYPE_HEADER              = 0;
    private final int VIEW_TYPE_ITEM                = 1;
    private final int VIEW_TYPE_FOOTER              = 2;
    private boolean isHasHeader                     = false;
    private boolean isHasFooter                     = false;

    public NetworkAdapter(NetworkFragment fragment) {
        this.mNetworkFragment   = fragment;
        this.mListNetwork       = new ArrayList<>();
    }

    public NetworkAdapter(NetworkFragment fragment, List<Network> mListNetwork) {
        this.mNetworkFragment   = fragment;
        this.mListNetwork       = mListNetwork;
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
                container   = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_network_list, parent, false);
                viewHolder  = new NetworkHolder(container);
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
        Log.e("[ adpNewsfeed ]", "- method: onBindViewHolder || mListNetwork = "+mListNetwork.toString());

        if (holder instanceof NetworkHolder) {
            if (mListNetwork != null && mListNetwork.size() > 0) {
                initNetworkHolder(holder, position);
            }
        } else if (holder instanceof LoadmoreHolder) {
            initLoadmoreHolder(holder);
        }
    }

    @Override
    public int getItemCount() {
        if (isHasFooter) {
            return mListNetwork.size() + 1;
        }
        return mListNetwork.size();
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

    public void showFooter() {
        this.isHasFooter = true;
    }

    public void hideFooter() {
        this.isHasFooter = false;
    }

    private boolean isPositionHeader(int position) {
        if (mListNetwork.size() > 0 && position == 0) {
            return true;
        }
        return false;
    }

    private boolean isPositionFooter(int position) {
        if (mListNetwork.size() > 0 && position == getItemCount() - 1) {
            return true;
        }
        return false;
    }

    public void addItem(Network network) {
        mListNetwork.add(network);
        this.notifyItemInserted(mListNetwork.size() - 1);
    }

    public void addItems(List<Network> networkList) {
        mListNetwork.addAll(networkList);
        this.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mListNetwork.remove(position);
        this.notifyItemRemoved(mListNetwork.size());
    }

    public void clearItem() {
        mListNetwork.clear();
        this.notifyDataSetChanged();
    }

    private void initLoadmoreHolder(RecyclerView.ViewHolder holder) {
        LoadmoreHolder loadmoreHolder   = (LoadmoreHolder) holder;
        loadmoreHolder.agiv_holder_loadmore.setVisibility(View.VISIBLE);
    }

    private void initNetworkHolder(RecyclerView.ViewHolder holder, int position) {
        final Network network           = mListNetwork.get(position);
        NetworkHolder networkHolder     = (NetworkHolder) holder;

        String name             = network.FirstName == null ? "-" : network.FirstName;
        String company          = network.CompanyName == null ? "-" : network.CompanyName;
        String mobile           = network.Mobile == null ? "-" : network.Mobile;

        networkHolder.tv_network_list_name.setText(name);
        networkHolder.tv_network_list_office.setText(company);

        String imgProfilePath = mNetworkFragment.mPazpoApp.mServiceHelper.IMG_PROFILE_UPLOAD_PATH;
        Picasso.with(mNetworkFragment.getContext())
                .load(imgProfilePath + network.MemberID + "/" + network.MemberImage)
                .fit()
                .placeholder(R.drawable.ic_account_circle)
                .into(networkHolder.civ_network_list);
    }
}
