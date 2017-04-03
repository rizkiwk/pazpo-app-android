package id.pazpo.agent.views.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flipbox.pazpo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wais on 2/7/17.
 */

public class MessageHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.rl_message_list)
    public RelativeLayout rl_message_list;
    @BindView(R.id.civ_message_list)
    public CircleImageView civ_message_list;
    @BindView(R.id.tv_message_list_username)
    public TextView tv_message_list_username;
    @BindView(R.id.tv_message_list_time)
    public TextView tv_message_list_time;
    @BindView(R.id.tv_message_list_preview)
    public TextView tv_message_list_preview;
    @BindView(R.id.tv_message_list_notif)
    public TextView tv_message_list_notif;

    public MessageHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
