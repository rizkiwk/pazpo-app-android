package id.pazpo.agent.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.flipbox.pazpo.R;
import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.pazpo.agent.fragments.MessageFragment;
import id.pazpo.agent.fragments.NetworkFragment;
import id.pazpo.agent.fragments.NewsfeedFragment;
import id.pazpo.agent.fragments.ProfileFragment;
import id.pazpo.agent.helpers.ConstantHelper;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.services.model.message.Message;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends BaseActivity
        implements BaseMethodInterface {

    @BindView(R.id.cl_main) CoordinatorLayout cl_main;
    @BindView(R.id.toolbar_main) Toolbar toolbar_main;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottom_navigation;

    protected FragmentManager fm = getSupportFragmentManager();
    protected NewsfeedFragment newsfeedFragment;
    protected NetworkFragment networkFragment;
    protected MessageFragment messageFragment;
    protected ProfileFragment profileFragment;
    protected int mNavBottomTabID;
    protected MenuItem menuFilter;
    protected MenuItem menuSettings;

    private Context mContext;
    private String pMemberID;
    private int pListingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_main);
        ButterKnife.bind(this);

        //Segment Tracking
        Analytics.with(this).identify(new Traits()
                .putValue("UserID", mSharedPrefs.getMemberLogin().UserID)
                .putFirstName(mSharedPrefs.getMemberLogin().FirstName)
                .putLastName(mSharedPrefs.getMemberLogin().LastName)
                .putEmail(mSharedPrefs.getMemberLogin().UserID)
                .putValue("Phone Number", mSharedPrefs.getMemberLogin().Mobile)
                .putValue("MemberID", mSharedPrefs.getMemberLogin().MemberID)
                .putValue("Area", mSharedPrefs.getMemberLogin().CompanyName));

        Analytics.with(this).track("Page View", new Properties()
                .putValue("Type", "Activity")
                .putValue("Page", "Main"));

        initData(savedInstanceState);
        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);

        menuFilter = menu.findItem(R.id.action_menu_filter);
        menuFilter.setVisible(false);

        menuSettings = menu.findItem(R.id.action_menu_settings);
        menuSettings.setVisible(false);

        modifyToolbarMenu(mNavBottomTabID);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_menu_filter:
                Analytics.with(this).track("Click", new Properties()
                        .putValue("Type", "Filter Menu")
                        .putValue("Widget", "Toolbar Menu")
                        .putValue("Page Type", "Fragment")
                        .putValue("Page", "Newsfeed"));
                showFilterDialog();
                break;
            case R.id.action_menu_settings:
                Analytics.with(this).track("Click", new Properties()
                        .putValue("Type", "Setting Menu")
                        .putValue("Widget", "Toolbar Menu")
                        .putValue("Page Type", "Fragment")
                        .putValue("Page", "Profile"));
                Intent intent   = new Intent(this, SettingActivity.class);
                intent.putExtra("setting_menu", "profile_edit");
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    protected void goToForm() {
        if (!isFirstUseApp()) {
            Analytics.with(getApplicationContext()).track("Click", new Properties()
                    .putValue("Type", "Create Search Button")
                    .putValue("Widget", "Floating Action Button")
                    .putValue("Page Type", "Activity")
                    .putValue("Page", "Main"));
            startActivity(new Intent(this, CreateSearchActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Anda yakin akan keluar aplikasi?")
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                })
                .setNegativeButton(R.string.dialog_no, null)
                .show();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        // Set SharedPreference Filter Newsfeed Option.
        if (mSharedPrefs.getOptionFilterNewsfeed() == null) {
            Newsfeed sPrefFilterNewsfeed            = new Newsfeed();
            sPrefFilterNewsfeed.OptionMemberID      = null;
            sPrefFilterNewsfeed.OptionListingType   = 0;
            sPrefFilterNewsfeed.OptionNetworkOnly   = false;
            sPrefFilterNewsfeed.OptionItems         = new boolean[]{ false, false, false };
            mSharedPrefs.setOptionFilterNewsfeed(sPrefFilterNewsfeed);
        }

        mContext        = this;
        mNavBottomTabID = getIntent().getIntExtra(mConstant.NAVBOTTOM_TAB_ID, mConstant.NAVBOTTOM_TAB_NEWSFEED);
        pMemberID       = mSharedPrefs.getMemberLogin().MemberID;
        initSocketIO(); // init Socket.io
    }

    @Override
    public void initUI() {
        initToolbar(); // Show toolbar UI.
        initBottomNavigation(); // Show bottom navigation UI.
        initTutorial(); // Show tutorial.
    }

    public void showSnackbar(String message) {
        showEventSnackbar(message);
    }

    protected void initSocketIO() {
        mSocket.on("userEntrance", GetAllChats);

        Map wsLoginParams = new HashMap();
        wsLoginParams.put("UserID", mSharedPrefs.getMemberLogin().UserID);
        wsLoginParams.put("UserName", mSharedPrefs.getMemberLogin().FirstName);
        wsLoginParams.put("UserImage", mSharedPrefs.getMemberLogin().UserImage);
        mSocket.emit("userInfo", new JSONObject(wsLoginParams));
    }

    protected void initToolbar() {
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(1);
    }

    protected void initBottomNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bottom_navigation.setElevation(1);
        }

        bottom_navigation.inflateMenu(R.menu.bottom_navigation_menu);
        disableShiftMode(bottom_navigation);
        if (!isFirstUseApp()) {
            bottom_navigation.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_newsfeed:
                                    Analytics.with(getApplicationContext()).track("Click", new Properties()
                                            .putValue("Type", "Newsfeed Navigation Menu")
                                            .putValue("Widget", "Bottom Navigation View")
                                            .putValue("Page Type", "Activity")
                                            .putValue("Page", "Main"));
                                    loadNewsfeedFragment();
                                    break;
                                case R.id.action_network:
                                    Analytics.with(getApplicationContext()).track("Click", new Properties()
                                            .putValue("Type", "Network Navigation Menu")
                                            .putValue("Widget", "Bottom Navigation View")
                                            .putValue("Page Type", "Activity")
                                            .putValue("Page", "Main"));
                                    loadNetworkFragment();
                                    break;
                                case R.id.action_message:
                                    Analytics.with(getApplicationContext()).track("Click", new Properties()
                                            .putValue("Type", "Message Navigation Menu")
                                            .putValue("Widget", "Bottom Navigation View")
                                            .putValue("Page Type", "Activity")
                                            .putValue("Page", "Main"));
                                    loadMessageFragment();
                                    break;
                                case R.id.action_profile:
                                    Analytics.with(getApplicationContext()).track("Click", new Properties()
                                            .putValue("Type", "Profile Navigation Menu")
                                            .putValue("Widget", "Bottom Navigation View")
                                            .putValue("Page Type", "Activity")
                                            .putValue("Page", "Main"));
                                    loadProfileFragment();
                                    break;
                            }
                            return true;
                        }
                    });
        }

        setNavBottomSelectedTab();
    }

    protected void initTutorial() {
        if (isFirstUseApp()) {
            showJelajahTutorial();
        }
    }

    protected void showFilterDialog() {
        Analytics.with(this)
                .track("Page View", new Properties()
                .putValue("Type", "Dialog")
                .putValue("Page", "Newsfeed Filter"));

        final CharSequence[] items      = { "WTB", "WTS", "Network" };
        final Newsfeed sPrefNewsfeed    = mSharedPrefs.getOptionFilterNewsfeed();

        AlertDialog filterDialog = new AlertDialog.Builder(this)
                .setTitle("Filter Berdasarkan")
                .setMultiChoiceItems(items, sPrefNewsfeed.OptionItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        String itemSelected     = (String) items[indexSelected];

                        if ("WTB".equalsIgnoreCase(itemSelected)) {
                            sPrefNewsfeed .OptionItems[indexSelected] = isChecked;
                        }

                        if ("WTS".equalsIgnoreCase(itemSelected)) {
                            sPrefNewsfeed .OptionItems[indexSelected] = isChecked;
                        }

                        if ("Network".equalsIgnoreCase(itemSelected)) {
                            sPrefNewsfeed .OptionItems[indexSelected] = isChecked;
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Set Option Filter Property Listing Type.
                        if (sPrefNewsfeed .OptionItems[0] && !sPrefNewsfeed .OptionItems[1]) {
                            sPrefNewsfeed.OptionListingType = 1;
                            Analytics.with(getApplicationContext()).track("Click", new Properties()
                                    .putValue("Type", "WTB Checkbox")
                                    .putValue("Widget", "Checkbox")
                                    .putValue("Page Type", "Dialog")
                                    .putValue("Page", "Newsfeed Filter"));
                        } else if (sPrefNewsfeed .OptionItems[1] && !sPrefNewsfeed .OptionItems[0]) {
                            sPrefNewsfeed.OptionListingType = 2;
                            Analytics.with(getApplicationContext()).track("Click", new Properties()
                                    .putValue("Type", "WTS Checkbox")
                                    .putValue("Widget", "Checkbox")
                                    .putValue("Page Type", "Dialog")
                                    .putValue("Page", "Newsfeed Filter"));
                        } else {
                            sPrefNewsfeed.OptionListingType = 0;
                            Analytics.with(getApplicationContext()).track("Click", new Properties()
                                    .putValue("Type", "WTB & WTS Checkbox")
                                    .putValue("Widget", "Checkbox")
                                    .putValue("Page Type", "Dialog")
                                    .putValue("Page", "Newsfeed Filter"));
                        }

                        // Set Option Filter Network Only.
                        if (sPrefNewsfeed .OptionItems[2]) {
                            sPrefNewsfeed.OptionMemberID        = mSharedPrefs.getMemberLogin().MemberID;
                            sPrefNewsfeed.OptionNetworkOnly     = true;
                            Analytics.with(getApplicationContext()).track("Click", new Properties()
                                    .putValue("Type", "Network Only Checkbox")
                                    .putValue("Widget", "Checkbox")
                                    .putValue("Page Type", "Dialog")
                                    .putValue("Page", "Newsfeed Filter"));
                        } else {
                            sPrefNewsfeed.OptionMemberID        = null;
                            sPrefNewsfeed.OptionNetworkOnly     = false;
                            Analytics.with(getApplicationContext()).track("Click", new Properties()
                                    .putValue("Type", "Not Network Only Checkbox")
                                    .putValue("Widget", "Checkbox")
                                    .putValue("Page Type", "Dialog")
                                    .putValue("Page", "Newsfeed Filter"));
                        }

                        mSharedPrefs.setOptionFilterNewsfeed(sPrefNewsfeed); // Set SharedPref Filter Newsfeed.
                        Newsfeed sPrefFilterNewsfeed = mSharedPrefs.getOptionFilterNewsfeed();
                        newsfeedFragment.isRetryNewsfeed = true;
                        newsfeedFragment.showLoadingFragment();
                        newsfeedFragment.apiGetAllNewsfeed(sPrefFilterNewsfeed.OptionMemberID, sPrefFilterNewsfeed.OptionListingType, sPrefFilterNewsfeed.OptionNetworkOnly, 1, 10);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();
    }

    protected void showJelajahTutorial() {
        Analytics.with(this).track("Tutorial Shown", new Properties()
                .putValue("Type", "Tutorial Layer Newsfeed Menu")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Main"));
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.action_newsfeed))
                .setPrimaryText(R.string.content_tutorial_primary_jelajah)
                .setSecondaryText(R.string.content_tutorial_secondary_jelajah)
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                    }

                    @Override
                    public void onHidePromptComplete() {
                        Analytics.with(getApplicationContext()).track("Tutorial Click", new Properties()
                                .putValue("Type", "Tutorial Layer Newsfeed Menu")
                                .putValue("Widget", "Material Tap Prompt")
                                .putValue("Page Type", "Activity")
                                .putValue("Page", "Main"));
                        showProfileTutorial();
                    }
                })
                .show();
    }

    protected void showProfileTutorial() {
        Analytics.with(this).track("Tutorial Shown", new Properties()
                .putValue("Type", "Tutorial Layer Profile Menu")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Main"));
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.action_profile))
                .setPrimaryText(R.string.content_tutorial_primary_profile)
                .setSecondaryText(R.string.content_tutorial_secondary_profile)
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                    }

                    @Override
                    public void onHidePromptComplete() {
                        Analytics.with(getApplicationContext()).track("Tutorial Click", new Properties()
                                .putValue("Type", "Tutorial Layer Profile Menu")
                                .putValue("Widget", "Material Tap Prompt")
                                .putValue("Page Type", "Activity")
                                .putValue("Page", "Main"));
                        showCreateTutorial();
                    }
                })
                .show();
    }

    protected void showCreateTutorial() {
        Analytics.with(this).track("Tutorial Shown", new Properties()
                .putValue("Type", "Tutorial Layer FAB")
                .putValue("Page Type", "Activity")
                .putValue("Page", "Main"));
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(fab)
                .setPrimaryText(R.string.content_tutorial_primary_create)
                .setSecondaryText(R.string.content_tutorial_secondary_create)
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                    }

                    @Override
                    public void onHidePromptComplete() {
                        Analytics.with(getApplicationContext()).track("Tutorial Click", new Properties()
                                .putValue("Type", "Tutorial Layer FAB")
                                .putValue("Widget", "Material Tap Prompt")
                                .putValue("Page Type", "Activity")
                                .putValue("Page", "Main"));
                        mSharedPrefs.setIsFirstUseApp(false);
                        recreate();
                    }
                })
                .show();
    }

    protected void setNavBottomSelectedTab() {
        switch (mNavBottomTabID) {
            case ConstantHelper.NAVBOTTOM_TAB_MESSAGE:
                loadMessageFragment();
                bottom_navigation.getMenu().getItem(mNavBottomTabID).setChecked(true);
                break;
            case ConstantHelper.NAVBOTTOM_TAB_PROFILE:
                loadProfileFragment();
                bottom_navigation.getMenu().getItem(mNavBottomTabID).setChecked(true);
                break;
            case ConstantHelper.NAVBOTTOM_TAB_NETWORK:
                loadNetworkFragment();
                bottom_navigation.getMenu().getItem(mNavBottomTabID).setChecked(true);
                break;
            case ConstantHelper.NAVBOTTOM_TAB_NEWSFEED:
                loadNewsfeedFragment();
                bottom_navigation.getMenu().getItem(mNavBottomTabID).setChecked(true);
                break;
            default:
                loadNewsfeedFragment();
                bottom_navigation.getMenu().getItem(mNavBottomTabID).setChecked(true);
                break;
        }
    }

    public void loadNewsfeedFragment() {
        //========== Toolbar Init ==========//
        modifyToolbarMenu(ConstantHelper.NAVBOTTOM_TAB_NEWSFEED);

        fab.setVisibility(View.VISIBLE);
        newsfeedFragment = new NewsfeedFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, newsfeedFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    public void loadNetworkFragment() {
        //========== Toolbar Init ==========//
        modifyToolbarMenu(ConstantHelper.NAVBOTTOM_TAB_NETWORK);

        fab.setVisibility(View.GONE);
        networkFragment = new NetworkFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, networkFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    public void loadMessageFragment() {
        //========== Toolbar Init ==========//
        modifyToolbarMenu(ConstantHelper.NAVBOTTOM_TAB_MESSAGE);

        fab.setVisibility(View.GONE);
        messageFragment = new MessageFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, messageFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    public void loadProfileFragment() {
        //========== Toolbar Init ==========//
        modifyToolbarMenu(ConstantHelper.NAVBOTTOM_TAB_PROFILE);

        fab.setVisibility(View.GONE);
        profileFragment = new ProfileFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, profileFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    protected void modifyToolbarMenu(int tabPosition) {
        if (toolbar_main.getMenu().size() > 0) {
            switch (tabPosition) {
                case 0:
                    menuFilter.setVisible(true);
                    menuSettings.setVisible(false);
                    break;
                case 1:
                    menuFilter.setVisible(false);
                    menuSettings.setVisible(false);
                    break;
                case 2:
                    menuFilter.setVisible(false);
                    menuSettings.setVisible(false);
                    break;
                case 3:
                    menuFilter.setVisible(false);
                    menuSettings.setVisible(true);
                    break;
            }
        }
    }

    public void showEventSnackbar(String text) {
        showSnackBar(cl_main, text, 0);
    }

    protected Emitter.Listener GetAllChats = new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            try {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JSONArray data              = (JSONArray) args[0];
                        Gson gson                   = new Gson();
                        Type typeList               = new TypeToken<List<Message>>(){}.getType();
                        List<Message> messageList   = gson.fromJson(data.toString(), typeList);
                        Log.e("[ aMain ] :", "- onWS :: userInApp || data userInApp :: "+ messageList.size());
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected boolean isFirstUseApp() {
        return mSharedPrefs.getIsFirstUseApp();
    }

    protected void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
}
