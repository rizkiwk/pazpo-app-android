package id.pazpo.agent.views.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipbox.pazpo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by adigunawan on 3/1/17.
 */

public class ProfileListHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.civ_profile_list_image) CircleImageView civ_profile_list_image;
    @BindView(R.id.tv_profile_list_listing_type) TextView tv_profile_list_listing_type;
    @BindView(R.id.tv_profile_list_name) TextView tv_profile_list_name;
    @BindView(R.id.tv_profile_list_office) TextView tv_profile_list_office;
    @BindView(R.id.tv_profile_list_time) TextView tv_profile_list_time;
    @BindView(R.id.iv_profile_list_expand_more) ImageView iv_profile_list_expand_more;
    @BindView(R.id.iv_profile_list_expand_less) ImageView iv_profile_list_expand_less;
    @BindView(R.id.tv_profile_list_description) TextView tv_profile_list_description;
    @BindView(R.id.tv_profile_list_budget) TextView tv_profile_list_budget;
    @BindView(R.id.tv_profile_list_comission) TextView tv_profile_list_comission;

    public ProfileListHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
