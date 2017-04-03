package id.pazpo.agent.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.flipbox.pazpo.R;

import id.pazpo.agent.fragments.GuideFragment;

/**
 * Created by adigunawan on 1/12/17.
 */

public class GuideAdapter extends FragmentPagerAdapter {

    public static int[] images = new int[] {
            R.drawable.walkthrough_1,
            R.drawable.walkthrough_2,
            R.drawable.walkthrough_3
    };

    public static String[] title = new String[] {
            "Cari Listing",
            "Update Listing",
            "Hubungi Agen Lain"
    };

    public static String[] subtitle = new String[] {
            "Cari dan temukan tawaran menarik dari agen professional lainnya",
            "Upload foto dan detail listing lebih mudah untuk meningkatkan konversi penjualan anda",
            "Hubungi agen lain dengan cepat dan mudah pada aplikasi Pazpo"
    };

    public GuideAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return GuideFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return images.length;
    }

}
