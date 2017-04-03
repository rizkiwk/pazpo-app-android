package id.pazpo.agent.adapters;

import android.support.v4.content.ContextCompat;
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

import id.pazpo.agent.fragments.NewsfeedFragment;
import id.pazpo.agent.services.model.message.Message;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import id.pazpo.agent.utils.DataFormatter;
import id.pazpo.agent.views.holder.LoadmoreHolder;
import id.pazpo.agent.views.holder.NewsfeedHolder;

/**
 * Created by wais on 1/16/17.
 */

public class NewsfeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected NewsfeedFragment mNewsfeedFragment;

    private final int VIEW_TYPE_HEADER              = 0;
    private final int VIEW_TYPE_ITEM                = 1;
    private final int VIEW_TYPE_FOOTER              = 2;
    private boolean isHasHeader                     = false;
    private boolean isHasFooter                     = false;

    public List<Newsfeed> mListNewsfeed;

    public NewsfeedAdapter(NewsfeedFragment fragment) {
        this.mNewsfeedFragment  = fragment;
        this.mListNewsfeed      = new ArrayList<>();
    }

    public NewsfeedAdapter(NewsfeedFragment fragment, List<Newsfeed> mListNewsfeed) {
        this.mNewsfeedFragment  = fragment;
        this.mListNewsfeed      = mListNewsfeed;
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
                container   = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_newsfeed_list, parent, false);
                viewHolder  = new NewsfeedHolder(container);
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
        if (holder instanceof NewsfeedHolder) {
            if (mListNewsfeed != null && mListNewsfeed.size() > 0) {
                initNewsfeedHolder(holder, position);
            }
        } else if (holder instanceof LoadmoreHolder) {
            initLoadmoreHolder(holder);
        }
    }

    @Override
    public int getItemCount() {
        if (isHasFooter) {
            return mListNewsfeed.size() + 1;
        }
        return mListNewsfeed.size();
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

    public void addItem(Newsfeed newsfeed) {
        mListNewsfeed.add(newsfeed);
        this.notifyItemInserted(mListNewsfeed.size() - 1);
    }

    public void addItems(List<Newsfeed> listNewsfeed) {
        mListNewsfeed.addAll(listNewsfeed);
        this.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mListNewsfeed.remove(position);
        this.notifyItemRemoved(mListNewsfeed.size());
    }

    public void clearItem() {
        mListNewsfeed.clear();
        this.notifyDataSetChanged();
    }

    public void showFooter() {
        this.isHasFooter = true;
    }

    public void hideFooter() {
        this.isHasFooter = false;
    }

    private boolean isPositionHeader(int position) {
        if (mListNewsfeed.size() > 0 && position == 0) {
            return true;
        }
        return false;
    }

    private boolean isPositionFooter(int position) {
        if (mListNewsfeed.size() > 0 && position == getItemCount() - 1) {
            return true;
        }
        return false;
    }

    private void initLoadmoreHolder(RecyclerView.ViewHolder holder) {
        LoadmoreHolder loadmoreHolder   = (LoadmoreHolder) holder;
        loadmoreHolder.agiv_holder_loadmore.setVisibility(View.VISIBLE);
    }

    private void initNewsfeedHolder(RecyclerView.ViewHolder holder, int position) {
        final Newsfeed newsfeed         = mListNewsfeed.get(position);
        NewsfeedHolder newsfeedHolder   = (NewsfeedHolder) holder;

        String name             = newsfeed.UserName == null ? "-" : newsfeed.UserName;
        String company          = newsfeed.CompanyName == null ? "-" : newsfeed.CompanyName;
        String createdDate      = newsfeed.CreatedDate == null ? "-" : DataFormatter.formatTime(newsfeed.CreatedDate, 0);
        final String notes      = newsfeed.Notes == null || newsfeed.Notes.equalsIgnoreCase("null") ? "-" : newsfeed.Notes;
        String budget           = newsfeed.MaxPrice == null ? "-" : DataFormatter.formatBudget(newsfeed.MaxPrice);
        String fee              = newsfeed.Fee == null ? "-" : newsfeed.Fee + "%";

        newsfeedHolder.tv_newsfeed_list_name.setText(name);
        newsfeedHolder.tv_newsfeed_list_office.setText(company);
        newsfeedHolder.tv_newsfeed_list_time.setText(mNewsfeedFragment.getResources().getString(R.string.content_posted) + " " + createdDate);
        newsfeedHolder.tv_newsfeed_list_description.setText(notes);
        newsfeedHolder.tv_newsfeed_list_comission.setText(mNewsfeedFragment.getResources().getString(R.string.content_fee)+ " : " + fee);

        String imgProfilePath = mNewsfeedFragment.mPazpoApp.mServiceHelper.IMG_PROFILE_UPLOAD_PATH;
        Picasso.with(mNewsfeedFragment.getContext())
                .load(imgProfilePath + newsfeed.UserImage)
                .fit()
                .placeholder(R.drawable.ic_account_circle)
                .into(newsfeedHolder.civ_newsfeed_list_profile);

        newsfeedHolder.civ_newsfeed_list_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.with(mNewsfeedFragment.getContext()).track("Click", new Properties()
                        .putValue("Type", "Profile Photo")
                        .putValue("Widget", "Circle Image View")
                        .putValue("Page Type", "Adapter")
                        .putValue("Page", "Newsfeed"));
            }
        });

        newsfeedHolder.btn_newsfeed_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.with(mNewsfeedFragment.getContext()).track("Click", new Properties()
                        .putValue("Type", "Message Button")
                        .putValue("Property Type", newsfeed.PropertyTypeDesc)
                        .putValue("Listing Type", newsfeed.ListingType)
                        .putValue("Location", newsfeed.Location)
                        .putValue("Min Price", newsfeed.MinPrice)
                        .putValue("Max Price", newsfeed.MaxPrice)
                        .putValue("Notes", newsfeed.Notes)
                        .putValue("Fee", newsfeed.Fee)
                        .putValue("Created Date", newsfeed.CreatedDate)
                        .putValue("User ID", newsfeed.UserID)
                        .putValue("User Image", newsfeed.UserImage)
                        .putValue("Company Name", newsfeed.CompanyName)
                        .putValue("User Name", newsfeed.UserName)
                        .putValue("Phone Number", newsfeed.mobile)
                        .putValue("Widget", "Button")
                        .putValue("Page Type", "Adapter")
                        .putValue("Page", "Newsfeed"));

                Message mMessage        = new Message();
                mMessage.UserID_Two     = newsfeed.UserID;
                mMessage.UserTwoName    = newsfeed.UserName;
                mMessage.UserTwoImage   = newsfeed.UserImage;

                mNewsfeedFragment.loadActivityMessageDetail(mMessage, notes);
            }
        });

        switch (newsfeed.ListingType) {
            case ("WTS") :
                newsfeedHolder.tv_newsfeed_list_listing_type.setBackground(ContextCompat.getDrawable(mNewsfeedFragment.getContext(), R.drawable.tv_red_background));
                newsfeedHolder.tv_newsfeed_list_listing_type.setText(R.string.content_wts);
                newsfeedHolder.tv_newsfeed_list_budget.setText(mNewsfeedFragment.getResources().getString(R.string.content_price) + " : " + budget);
                break;
            case ("WTB") :
                newsfeedHolder.tv_newsfeed_list_listing_type.setBackground(ContextCompat.getDrawable(mNewsfeedFragment.getContext(), R.drawable.tv_green_background));
                newsfeedHolder.tv_newsfeed_list_listing_type.setText(R.string.content_wtb);
                newsfeedHolder.tv_newsfeed_list_budget.setText(mNewsfeedFragment.getResources().getString(R.string.content_budget) + " : " + budget);
                break;
            case ("WTR") :
                newsfeedHolder.tv_newsfeed_list_listing_type.setBackground(ContextCompat.getDrawable(mNewsfeedFragment.getContext(), R.drawable.tv_blue_background));
                newsfeedHolder.tv_newsfeed_list_listing_type.setText(R.string.content_wtr);
                break;
            case ("WTL") :
                newsfeedHolder.tv_newsfeed_list_listing_type.setBackground(ContextCompat.getDrawable(mNewsfeedFragment.getContext(), R.drawable.tv_orange_background));
                newsfeedHolder.tv_newsfeed_list_listing_type.setText(R.string.content_wtl);
                break;
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
