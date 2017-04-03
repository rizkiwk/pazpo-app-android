package id.pazpo.agent.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipbox.pazpo.R;

import butterknife.ButterKnife;

/**
 * Created by putri on 1/12/17.
 */

public class LoginFragment extends BaseFragment {

    public Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View ContainerView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, ContainerView);
        mContext = getContext();
        return ContainerView;
    }

}
