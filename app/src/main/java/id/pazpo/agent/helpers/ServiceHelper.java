package id.pazpo.agent.helpers;

import com.flipbox.pazpo.BuildConfig;

import java.util.List;

import id.pazpo.agent.services.APIClient;
import id.pazpo.agent.services.RestClient;
import id.pazpo.agent.services.endpoint.ApiEndpoint;
import id.pazpo.agent.services.model.RestModel;
import id.pazpo.agent.services.model.location.Area;
import id.pazpo.agent.services.model.location.City;
import id.pazpo.agent.services.model.location.CompanyArea;
import id.pazpo.agent.services.model.location.Province;
import id.pazpo.agent.services.model.member.MemberGet;
import id.pazpo.agent.services.model.member.MemberUpdate;
import id.pazpo.agent.services.model.member.NameCardGet;
import id.pazpo.agent.services.model.member.ProfileImageGet;
import id.pazpo.agent.services.model.message.MessageGet;
import id.pazpo.agent.services.model.message.MessageGetAll;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import id.pazpo.agent.services.model.newsfeed.NewsfeedGetAll;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by wais on 1/15/17.
 */

public class ServiceHelper {

    //############### Static Segment API KEY ###############//
    public static final String SEGMENT_UAT                      = "B0dD4I7LrLyIw4FOQQy8Td7HB01xImDW";
    public static final String SEGMENT_LIVE                     = "BSwgTK3OM68slQJAIldY09VW6qZ7fwhP";

    //############### Static URL Endpoint Constant ###############//
    public static final String URL_PAZPO_API_UAT                = "http://223.27.24.155/api_pazpo/v2/";
    public static final String URL_PAZPO_API_LIVE               = "http://api.pazpo.id/v2/";
    public static final String URL_PAZPO_API_UAT_BASE           = "http://223.27.24.155/api_pazpo/";
    public static final String URL_PAZPO_API_LIVE_BASE          = "http://api.pazpo.id/";
    public static final String URL_PAZPO_API_V21_UAT            = "http://223.27.24.155/api_pazpo/v2.1/";
    public static final String URL_PAZPO_API_V21_LIVE           = "http://api.pazpo.id/v2.1/";
    public static final String URL_PAZPO_API_UAT_NEW            = "http://223.27.24.155/";
    public static final String URL_PAZPO_API_LIVE_NEW           = "http://api.pazpo.id/";
    public static final String URL_PAZPO_WEB_UAT                = "http://223.27.24.155/";
    public static final String URL_PAZPO_WEB_LIVE               = "http://pazpo.id/";
    public static final String URL_PAZPO_WEBSOCKET_UAT          = "http://223.27.24.155:3000/";
    public static final String URL_PAZPO_WEBSOCKET_LIVE         = "http://182.253.236.164:6070/";
    public static final String URL_PAZPO_BRIDGE_UAT             = "http://oldadmin.nptstudio.xyz/";
    public static final String URL_PAZPO_BRIDGE_LIVE            = "http://mimpiproperti.com/";
    public static final String URL_FB_ACCOUNTKIT_GRAPH          = "https://graph.accountkit.com/v1.1/";

    public static final String IMG_UPLOAD_PATH_UAT              = "http://223.27.24.155/assets/uploader/upload/";
    public static final String IMG_UPLOAD_PATH_LIVE             = "http://img.pazpo.id/";
    public static final String IMG_UPLOAD_URL_UAT               = "http://223.27.24.155/assets/uploader/pazpo_upload/index.php";
    public static final String IMG_UPLOAD_URL_LIVE              = "http://mimpiproperti.com/assets/uploader/pazpo_upload/index.php";
    public static final String IMG_PROFILE_UPLOAD_URL_UAT       = "http://223.27.24.155/assets/uploader/";
    public static final String IMG_PROFILE_UPLOAD_URL_LIVE      = "http://mimpiproperti.com/assets/uploader/";
    public static final String IMG_CARDNAME_UPLOAD_URL_UAT      = "http://223.27.24.155/assets/uploader/";
    public static final String IMG_CARDNAME_UPLOAD_URL_LIVE     = "http://mimpiproperti.com/assets/uploader/";
    public static final String IMG_IDENTITY_UPLOAD_URL_UAT      = "http://223.27.24.155/assets/uploader/pazpo_upload/identity.php";
    public static final String IMG_IDENTITY_UPLOAD_URL_LIVE     = "http://mimpiproperti.com/assets/uploader/pazpo_upload/identity.php";

    //############### Static Path Maison Endpoint Constant ###############//

    //############### URL Endpoint Constant ###############//
    public String URL_PAZPO_API_BASE;
    public String URL_PAZPO_API_BASE_NEW;
    public String URL_PAZPO_API_V21_BASE;
    public String URL_PAZPO_WEB_BASE;
    public String URL_PAZPO_WEBSOCKET_BASE;
    public String URL_PAZPO_BRIDGE_BASE;

    public String IMG_UPLOAD_PATH;
    public String IMG_UPLOAD_URL;
    public String IMG_PROFILE_UPLOAD_PATH;
    public String IMG_PROFILE_UPLOAD_URL;
    public String IMG_CARDNAME_UPLOAD_URL;
    public String IMG_IDENTITY_UPLOAD_URL;

    public String SEGMENT_API_KEY;

    public ServiceHelper() {
        if (BuildConfig.DEBUG) {
            URL_PAZPO_API_BASE_NEW      = URL_PAZPO_API_UAT_BASE;
            URL_PAZPO_API_BASE          = URL_PAZPO_API_UAT;
            URL_PAZPO_API_V21_BASE      = URL_PAZPO_API_V21_UAT;
            URL_PAZPO_WEB_BASE          = URL_PAZPO_WEB_UAT;
            URL_PAZPO_WEBSOCKET_BASE    = URL_PAZPO_WEBSOCKET_UAT;
            URL_PAZPO_BRIDGE_BASE       = URL_PAZPO_BRIDGE_UAT;

            IMG_UPLOAD_PATH             = IMG_UPLOAD_PATH_UAT;
            IMG_UPLOAD_URL              = IMG_UPLOAD_URL_UAT;
            IMG_PROFILE_UPLOAD_URL      = IMG_PROFILE_UPLOAD_URL_UAT;
            IMG_CARDNAME_UPLOAD_URL     = IMG_CARDNAME_UPLOAD_URL_UAT;
            IMG_IDENTITY_UPLOAD_URL     = IMG_IDENTITY_UPLOAD_URL_UAT;

            SEGMENT_API_KEY             = SEGMENT_UAT;
        } else {
            URL_PAZPO_API_BASE_NEW      = URL_PAZPO_API_LIVE_BASE;
            URL_PAZPO_API_BASE          = URL_PAZPO_API_LIVE;
            URL_PAZPO_API_V21_BASE      = URL_PAZPO_API_V21_LIVE;
            URL_PAZPO_WEB_BASE          = URL_PAZPO_WEB_LIVE;
            URL_PAZPO_WEBSOCKET_BASE    = URL_PAZPO_WEBSOCKET_LIVE;
            URL_PAZPO_BRIDGE_BASE       = URL_PAZPO_BRIDGE_LIVE;

            IMG_UPLOAD_PATH             = IMG_UPLOAD_PATH_LIVE;
            IMG_UPLOAD_URL              = IMG_UPLOAD_URL_LIVE;
            IMG_PROFILE_UPLOAD_URL      = IMG_PROFILE_UPLOAD_URL_LIVE;
            IMG_CARDNAME_UPLOAD_URL     = IMG_CARDNAME_UPLOAD_URL_LIVE;
            IMG_IDENTITY_UPLOAD_URL     = IMG_IDENTITY_UPLOAD_URL_LIVE;

            SEGMENT_API_KEY             = SEGMENT_LIVE;
        }

        IMG_PROFILE_UPLOAD_PATH = IMG_UPLOAD_PATH + "agent_image/";
    }

    public Call<MemberGet> setOneSignalPlayerID(String pPlayerID, String pUserID) {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MemberGet> Call        = ApiEndpoint.setOneSignalPlayerID(pPlayerID, pUserID);
        return Call;
    }

    public Call<MemberGet> getVersionCode() {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MemberGet> Call        = ApiEndpoint.getVersionCode();
        return Call;
    }

    public Call<MemberGet> getLoginPhone(String pMobilePhone) {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MemberGet> Call        = ApiEndpoint.getLoginPhone(pMobilePhone);
        return Call;
    }

    public Call<MemberGet> getUserMember(String pEmail) {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MemberGet> Call        = ApiEndpoint.getUserMember(pEmail);
        return Call;
    }

    public Call<MemberGet> createMember(String pFullName, String pEmail, String pCompanyID, String pMobilePhone, String pKTP, String pNameCard, String pCreatedBy) {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MemberGet> Call        = ApiEndpoint.createMember(pFullName, pEmail, pCompanyID, pMobilePhone, pKTP, pNameCard, pCreatedBy);
        return Call;
    }

    public Call<MemberUpdate> updateProfileName(String pEmail, String pFullName) {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MemberUpdate> Call     = ApiEndpoint.updateProfileName(pEmail, pFullName);
        return Call;
    }

    public Call<NameCardGet> uploadNameCard(MultipartBody.Part file) {
        ApiEndpoint ApiEndpoint     = new APIClient(IMG_CARDNAME_UPLOAD_URL).createService(ApiEndpoint.class);
        Call<NameCardGet> Call      = ApiEndpoint.uploadNameCard(file);
        return Call;
    }

    public Call<ProfileImageGet> uploadProfileImage(MultipartBody.Part file, RequestBody memberID) {
        ApiEndpoint ApiEndpoint     = new APIClient(IMG_PROFILE_UPLOAD_URL).createService(ApiEndpoint.class);
        Call<ProfileImageGet> Call  = ApiEndpoint.uploadProfileImage(file, memberID);
        return Call;
    }

    public Call<NewsfeedGetAll> getAllNewsfeed(String pMemberID, String pCurrentPage, String pViewResultCount) {
        ApiEndpoint ApiEndpoint        = new APIClient(URL_PAZPO_API_V21_BASE).createService(ApiEndpoint.class);
        Call<NewsfeedGetAll> Call      = ApiEndpoint.getAllNewsfeed(pMemberID, pCurrentPage, pViewResultCount);
        return Call;
    }

    public Call<RestModel> getFilterNewsfeed(String pMemberID, String pListingType, boolean pNetworkOnly, String pCurrentPage, String pViewResultCount) {
        ApiEndpoint ApiEndpoint   = new APIClient(URL_PAZPO_API_BASE_NEW).createService(ApiEndpoint.class);
        Call<RestModel> Call      = ApiEndpoint.getFilterNewsfeed(pMemberID, pListingType, pNetworkOnly, pCurrentPage, pViewResultCount);
        return Call;
    }

    public Call<Newsfeed> createNewsfeed(String pPropertyTypeID, String pLocation, String pMinPrice, String pMaxPrice, String pFee, String pNotes, String pCreatedBy) {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_V21_BASE).createService(ApiEndpoint.class);
        Call<Newsfeed> Call         = ApiEndpoint.createNewsfeed(pPropertyTypeID, pLocation, pMinPrice, pMaxPrice, pFee, pNotes, pCreatedBy);
        return Call;
    }

    public Call<RestModel> getAllNetwork(String pMemberID, String pViewResultCount, String pCurrentPage) {
        ApiEndpoint ApiEndpoint   = new RestClient(URL_PAZPO_API_BASE_NEW).createService(ApiEndpoint.class);
        Call<RestModel> Call      = ApiEndpoint.getAllNetwork(pMemberID, pViewResultCount, pCurrentPage);
        return Call;
    }

//    public Call<RestModel> setNetworkByContact(RequestBody requestBody) {
//        ApiEndpoint ApiEndpoint   = new RestClient(URL_PAZPO_API_BASE_NEW).createService(ApiEndpoint.class);
//        Call<RestModel> Call      = ApiEndpoint.setNetworkByContact(requestBody);
//        return Call;
//    }

    public Call<RestModel> setNetworkByContact(String pMemberID, List<String> pMobile) {
        ApiEndpoint ApiEndpoint   = new RestClient(URL_PAZPO_API_BASE_NEW).createService(ApiEndpoint.class);
        Call<RestModel> Call      = ApiEndpoint.setNetworkByContact(pMemberID, pMobile);
        return Call;
    }

    public Call<MessageGetAll> getAllMessages(String pUserEmail, String pViewResultCount, String pCurrentPage) {
        ApiEndpoint ApiEndpoint             = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MessageGetAll> Call            = ApiEndpoint.getAllMessages(pUserEmail, pViewResultCount, pCurrentPage);
        return Call;
    }

    public Call<MessageGet> getMessage(String pConversationID, String pCurrentPage, String pViewResultCount) {
        ApiEndpoint ApiEndpoint          = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MessageGet> Call            = ApiEndpoint.getMessage(pConversationID, pCurrentPage, pViewResultCount);
        return Call;
    }

    public Call<MessageGet> checkMessage(String pSender, String pRecipient, String pCurrentPage, String pViewResultCount) {
        ApiEndpoint ApiEndpoint          = new APIClient(URL_PAZPO_API_BASE).createService(ApiEndpoint.class);
        Call<MessageGet> Call            = ApiEndpoint.checkMessage(pSender, pRecipient, pCurrentPage, pViewResultCount);
        return Call;
    }

    public Call<List<Province>> getAllProvince() {
        ApiEndpoint ApiEndpoint         = new APIClient(URL_PAZPO_API_V21_BASE).createService(ApiEndpoint.class);
        Call<List<Province>> Call       = ApiEndpoint.getAllProvince();
        return Call;
    }

    public Call<List<City>> getAllCityByProvince(String pProvinceID) {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_V21_BASE).createService(ApiEndpoint.class);
        Call<List<City>> Call       = ApiEndpoint.getAllCityByProvince(pProvinceID);
        return Call;
    }

    public Call<List<Area>> getAllAreaByCity(String pCityID) {
        ApiEndpoint ApiEndpoint     = new APIClient(URL_PAZPO_API_V21_BASE).createService(ApiEndpoint.class);
        Call<List<Area>> Call       = ApiEndpoint.getAllAreaByCity(pCityID);
        return Call;
    }

    public Call<List<Province>> getAllProvinceCompany() {
        ApiEndpoint ApiEndpoint         = new APIClient(URL_PAZPO_API_V21_BASE).createService(ApiEndpoint.class);
        Call<List<Province>> Call       = ApiEndpoint.getAllProvinceCompany();
        return Call;
    }

    public Call<List<CompanyArea>> getAllAreaByProvince(String pProvinceID) {
        ApiEndpoint ApiEndpoint            = new APIClient(URL_PAZPO_API_V21_BASE).createService(ApiEndpoint.class);
        Call<List<CompanyArea>> Call       = ApiEndpoint.getAllAreaByProvince(pProvinceID);
        return Call;
    }
}
