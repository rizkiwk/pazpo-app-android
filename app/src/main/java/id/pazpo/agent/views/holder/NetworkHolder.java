package id.pazpo.agent.views.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flipbox.pazpo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by adigunawan on 2/28/17.
 */

public class NetworkHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ll_network_list) public LinearLayout ll_network_list;
    @BindView(R.id.rl_network_list) public RelativeLayout rl_network_list;
    @BindView(R.id.rl_network_list_action_group) public RelativeLayout rl_network_list_action_group;
    @BindView(R.id.civ_network_list) public CircleImageView civ_network_list;
    @BindView(R.id.tv_network_list_name) public TextView tv_network_list_name;
    @BindView(R.id.tv_network_list_office) public TextView tv_network_list_office;
    @BindView(R.id.btn_network_list_follow) public Button btn_network_list_follow;
    @BindView(R.id.tv_network_list_following) public TextView tv_network_list_following;
    @BindView(R.id.pb_network_list) public ProgressBar pb_network_list;

    public NetworkHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
