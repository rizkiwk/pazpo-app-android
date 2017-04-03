package id.pazpo.agent.views.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipbox.pazpo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wais on 2/8/17.
 */

public class MessageDetailHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ll_msg_detail_rv)
    public LinearLayout ll_msg_detail_rv;
    @BindView(R.id.ll_msg_detail_bubble)
    public LinearLayout ll_msg_detail_bubble;
    @BindView(R.id.tv_msg_detail_name)
    public TextView tv_msg_detail_name;
    @BindView(R.id.tv_msg_detail_company)
    public TextView tv_msg_detail_company;
    @BindView(R.id.tv_msg_detail_text)
    public TextView tv_msg_detail_text;
    @BindView(R.id.ll_msg_detail_info)
    public LinearLayout ll_msg_detail_info;
    @BindView(R.id.tv_msg_detail_time)
    public TextView tv_msg_detail_time;
    @BindView(R.id.iv_msg_detail_status)
    public ImageView iv_msg_detail_status;

    public MessageDetailHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
