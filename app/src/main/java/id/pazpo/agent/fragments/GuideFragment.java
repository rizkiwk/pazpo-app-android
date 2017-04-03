package id.pazpo.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import id.pazpo.agent.adapters.GuideAdapter;

/**
 * A simple {@link Fragment} subclass.
 */

public class GuideFragment extends Fragment {
    public static String KEY_POSITION = "position";
    TextView title;
    TextView subtitle;
    private int position;
    private ImageView imgItem;

    public GuideFragment() {
        // Required empty public constructor
    }

    public static GuideFragment newInstance(int position) {
        final GuideFragment f = new GuideFragment();
        final Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(KEY_POSITION);
        } else {
            position = getArguments() != null ? getArguments().getInt(KEY_POSITION) : 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View ContainerView = inflater.inflate(R.layout.fragment_guide, container, false);
        imgItem = (ImageView) ContainerView.findViewById(R.id.img_item_guide);
        title = (TextView) ContainerView.findViewById(R.id.txt_item_guide);
        subtitle = (TextView) ContainerView.findViewById(R.id.txt_item_guide_secondary);

        Analytics.with(getContext()).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Guide"));

        return ContainerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imgItem.setImageResource((GuideAdapter.images[position]));
        title.setText(GuideAdapter.title[position]);
        subtitle.setText(GuideAdapter.subtitle[position]);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
