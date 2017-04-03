package id.pazpo.agent.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.pazpo.agent.activities.AuthenticationActivity;
import id.pazpo.agent.helpers.PermissionHelper;

/**
 * Created by wais on 1/12/17.
 */

public class RegisterPhotoFragment extends BaseFragment {

    @BindView(R.id.civ_register_photo_btn)
    CircleImageView civ_register_photo_btn;

    public static final int INTENT_UPLOAD_CARDNAME = 111;

    protected AuthenticationActivity mAuthenticationActivity;
    protected Context mContext;
    protected Uri OutputFileUri;
    protected PermissionHelper mPermissionHelper;
    protected String mImagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View ContainerView = inflater.inflate(R.layout.fragment_register_photo, container, false);
        ButterKnife.bind(this, ContainerView);

        mContext                = getContext();
        mAuthenticationActivity = (AuthenticationActivity) getActivity();
        mPermissionHelper       = new PermissionHelper(getActivity());

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Register Photo")
                .putValue("Wizard", "Register")
                .putValue("Step", "Register Step 2"));

        civ_register_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.with(getContext()).track("Click", new Properties()
                        .putValue("Type", "Camera Button")
                        .putValue("Widget", "Button")
                        .putValue("Page Type", "Fragment")
                        .putValue("Page", "Register Photo"));
                ((AuthenticationActivity) getActivity()).getImageByCamera(INTENT_UPLOAD_CARDNAME);
            }
        });

        return ContainerView;
    }

    public void setImage(Uri uri) {
        civ_register_photo_btn.setImageURI(uri);
    }
}
