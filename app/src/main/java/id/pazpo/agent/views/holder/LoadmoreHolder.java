package id.pazpo.agent.views.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.flipbox.pazpo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pazpo.agent.views.AnimatedGifImageView;

/**
 * Created by wais on 2/2/17.
 */

public class LoadmoreHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.agiv_holder_loadmore)
    public AnimatedGifImageView agiv_holder_loadmore;
    @BindView(R.id.pb_holder_loadmore)
    ProgressBar pb_holder_loadmore;

    public LoadmoreHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
