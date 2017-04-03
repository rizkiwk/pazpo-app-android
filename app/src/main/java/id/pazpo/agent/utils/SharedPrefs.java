package id.pazpo.agent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import id.pazpo.agent.PazpoApp;
import id.pazpo.agent.services.model.location.Province;
import id.pazpo.agent.services.model.member.Member;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;

/**
 * Created by wais on 1/12/17.
 */

public class SharedPrefs {

    public Activity mActivity;
    public PazpoApp PazpoApp;

    private String KEY_PREFERENCES_NAME         = "PazpoAgentPrefs";
    private String KEY_USER_PHONE_NUMBER        = "UserPhoneNumber";
    private String KEY_USER_APP_PLAYERID        = "UserAppPlayerID";
    private String KEY_USER_IS_FIRST_RUN        = "UserIsFirstRun";
    private String KEY_USER_IS_FIRST_USE        = "UserIsFirstUse";
    private String KEY_MEMBER_IS_LOGIN          = "MemberIsLogin";
    private String KEY_MEMBER_LOGIN_DATA        = "MemberLoginData";
    private String KEY_PROVINCE_DATA            = "ProvinceData";
    private String KEY_PROVINCE_COMPANY_DATA    = "ProvinceCompanyData";
    private String KEY_ONE_SIGNAL_PLAYER_ID     = "OneSignalPlayerID";
    private String KEY_FILTER_OPTION_NEWSFEED   = "FilterOptionNewsfeed";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor Editor;

    public SharedPrefs(Activity Activity) {
        mActivity       = Activity;
        PazpoApp        = PazpoApp.getInstance();
        mPreferences     = Activity.getSharedPreferences(KEY_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor          = mPreferences.edit();
    }

    public void setIsFirstRunApp(boolean IsFirstRun) {
        Editor.putBoolean(KEY_USER_IS_FIRST_RUN, IsFirstRun);
        Editor.commit();
    }

    public boolean getIsFirstRunApp() {
        return mPreferences.getBoolean(KEY_USER_IS_FIRST_RUN, true);
    }

    public void setIsFirstUseApp(boolean IsFirstUse) {
        Editor.putBoolean(KEY_USER_IS_FIRST_USE, IsFirstUse);
        Editor.commit();
    }

    public boolean getIsFirstUseApp() {
        return mPreferences.getBoolean(KEY_USER_IS_FIRST_USE, true);
    }

    public void setMemberIsLogin(boolean IsMemberLogin) {
        Editor.putBoolean(KEY_MEMBER_IS_LOGIN, IsMemberLogin);
        Editor.commit();
    }

    public boolean getMemberIsLogin() {
        return mPreferences.getBoolean(KEY_MEMBER_IS_LOGIN, false);
    }

    public void setMemberLogin(Member member) {
        Gson gson           = new Gson();
        String jsonMember   = gson.toJson(member);
        Editor.putString(KEY_MEMBER_LOGIN_DATA, jsonMember);
        Editor.commit();
    }

    public Member getMemberLogin() {
        Gson gson           = new Gson();
        String jsonMember   = mPreferences.getString(KEY_MEMBER_LOGIN_DATA, null);
        return gson.fromJson(jsonMember, new TypeToken<Member>(){}.getType());
    }

    public void setOptionFilterNewsfeed(Newsfeed newsfeed) {
        Gson gson               = new Gson();
        String jsonNewsfeed     = gson.toJson(newsfeed);
        Editor.putString(KEY_FILTER_OPTION_NEWSFEED, jsonNewsfeed);
        Editor.commit();
    }

    public Newsfeed getOptionFilterNewsfeed() {
        Gson gson               = new Gson();
        String jsonNewsfeed     = mPreferences.getString(KEY_FILTER_OPTION_NEWSFEED, null);
        return gson.fromJson(jsonNewsfeed, new TypeToken<Newsfeed>(){}.getType());
    }

    public void setProvince(List<Province> province) {
        Gson gson                   = new Gson();
        String jsonProvinceList     = gson.toJson(province);
        Editor.putString(KEY_PROVINCE_DATA, jsonProvinceList);
        Editor.commit();
    }

    public List<Province> getProvince() {
        Gson gson                   = new Gson();
        String jsonProvinceList     = mPreferences.getString(KEY_PROVINCE_DATA, null);
        return gson.fromJson(jsonProvinceList, new TypeToken<List<Province>>(){}.getType());
    }

    public void setProvinceCompany(List<Province> provinceList) {
        Gson gson                   = new Gson();
        String jsonProvinceList     = gson.toJson(provinceList);
        Editor.putString(KEY_PROVINCE_COMPANY_DATA, jsonProvinceList);
        Editor.commit();
    }

    public List<Province> getProvinceCompany() {
        Gson gson                   = new Gson();
        String jsonProvinceList     = mPreferences.getString(KEY_PROVINCE_COMPANY_DATA, null);
        return gson.fromJson(jsonProvinceList, new TypeToken<List<Province>>(){}.getType());
    }

    public void setOneSignalPlayerID(String playerID) {
        Editor.putString(KEY_ONE_SIGNAL_PLAYER_ID, playerID);
        Editor.commit();
    }

    public String getOneSignalPlayerID() {
        return mPreferences.getString(KEY_ONE_SIGNAL_PLAYER_ID, null);
    }
}
