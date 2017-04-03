package id.pazpo.agent.views.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipbox.pazpo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wais on 2/2/17.
 */

public class NewsfeedHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.civ_newsfeed_list_profile)
    public CircleImageView civ_newsfeed_list_profile;
    @BindView(R.id.tv_newsfeed_list_listing_type)
    public TextView tv_newsfeed_list_listing_type;
    @BindView(R.id.tv_newsfeed_list_name)
    public TextView tv_newsfeed_list_name;
    @BindView(R.id.tv_newsfeed_list_office)
    public TextView tv_newsfeed_list_office;
    @BindView(R.id.tv_newsfeed_list_time)
    public TextView tv_newsfeed_list_time;
    @BindView(R.id.iv_newsfeed_list_expand_more)
    public ImageView iv_newsfeed_list_expand_more;
    @BindView(R.id.iv_newsfeed_list_expand_less)
    public ImageView iv_newsfeed_list_expand_less;
    @BindView(R.id.tv_newsfeed_list_description)
    public TextView tv_newsfeed_list_description;
    @BindView(R.id.tv_newsfeed_list_budget)
    public TextView tv_newsfeed_list_budget;
    @BindView(R.id.tv_newsfeed_list_comission)
    public TextView tv_newsfeed_list_comission;
    @BindView(R.id.btn_newsfeed_send_message)
    public Button btn_newsfeed_send_message;

    public NewsfeedHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
