package id.pazpo.agent.services.endpoint;

import java.util.ArrayList;
import java.util.List;

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
import id.pazpo.agent.services.model.network.ContactParam;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import id.pazpo.agent.services.model.newsfeed.NewsfeedGetAll;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by wais on 1/16/17.
 */

public interface ApiEndpoint {

    // Member API Endpoint //
    @GET("SetUserPlayerID")
    Call<MemberGet> setOneSignalPlayerID(
            @Query("pPlayerID") String pPlayerID,
            @Query("pUserID") String pUserID
    );

    @GET("LoginProcess")
    Call<MemberGet> getLoginPhone(
            @Query("pMobilePhone") String pMobilePhone
    );

    @GET("GetVersionCode")
    Call<MemberGet> getVersionCode();

    @GET("GetUserMember")
    Call<MemberGet> getUserMember(
            @Query("pEmail") String pEmail
    );

    @GET("CreateAgentRegistration")
    Call<MemberGet> createMember(
            @Query("pFullName") String pFullName,
            @Query("pEmail") String pEmail,
            @Query("pCompanyID") String pCompanyID,
            @Query("pMobilePhone") String pMobilePhone,
            @Query("pKTP") String pKTP,
            @Query("pNameCard") String pNameCard,
            @Query("pCreatedBy") String pCreatedBy
    );

    @GET("UpdateProfileName")
    Call<MemberUpdate> updateProfileName(
            @Query("pEmail") String pEmail,
            @Query("pFullName") String pFullName
    );

    @Multipart
    @POST("pazpo_upload/identity.php")
    Call<NameCardGet> uploadNameCard(
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("pazpo_upload/agent_image.php")
    Call<ProfileImageGet> uploadProfileImage(
            @Part MultipartBody.Part file,
            @Part("memberId") RequestBody memberID
    );

    // Newsfeed API Endpoint //
    @GET("GetAllNewsFeed")
    Call<NewsfeedGetAll> getAllNewsfeed(
            @Query("pMemberID") String pMemberID,
            @Query("pCurrentPage") String pCurrentPage,
            @Query("pViewResultCount") String pViewResultCount
    );

    @GET("v2.2/getFilterNewsfeed")
    Call<RestModel> getFilterNewsfeed(
            @Query("pMemberID") String pMemberID,
            @Query("pListingType") String pListingType,
            @Query("pNetworkOnly") boolean pNetworkOnly,
            @Query("pCurrentPage") String pCurrentPage,
            @Query("pViewResultCount") String pViewResultCount
    );

    @GET("InsertClient")
    Call<Newsfeed> createNewsfeed(
            @Query("pPropertyTypeID") String pPropertyTypeID,
            @Query("pLocation") String pLocation,
            @Query("pMinPrice") String pMinPrice,
            @Query("pMaxPrice") String pMaxPrice,
            @Query("pFee") String pFee,
            @Query("pNotes") String pNotes,
            @Query("pCreatedBy") String pCreatedBy
    );

    // Network API Endpoint //
    @GET("v2.2/getAllNetwork")
    Call<RestModel> getAllNetwork(
            @Query("pMemberID") String pMemberID,
            @Query("pCurrentPage") String pCurrentPage,
            @Query("pViewResultCount") String pViewResultCount
    );

//    @FormUrlEncoded
//    @POST("v2.2/setFollowByContact")
//    Call<RestModel> setNetworkByContact(
//            @Body RequestBody pContact,
//            @Field("pMemberID") String pMemberID,
//            @Field("pMobile[]") ArrayList<String> pMobile
//    );

//    @Headers("Content-Type: application/json")
//    @Headers("Content-Type: application/json")
    @FormUrlEncoded
    @POST("v2.2/setFollowByContact")
    Call<RestModel> setNetworkByContact(
            @Field("pMemberID") String pMemberID,
            @Field("pMobile[]") List<String> pMobile
    );

    // Message API Endpoint //
    @GET("GetAllChatsV2")
    Call<MessageGetAll> getAllMessages(
            @Query("pUserEmail") String pUserEmail,
            @Query("pViewResultCount") String pViewResultCount,
            @Query("pCurrentPage") String pCurrentPage
    );

    @GET("GetChat")
    Call<MessageGet> getMessage(
            @Query("pConversationID") String pConversationID,
            @Query("pCurrentPage") String pCurrentPage,
            @Query("pViewResultCount") String pViewResultCount
    );

    @GET("CheckConversation")
    Call<MessageGet> checkMessage(
            @Query("pSender") String pSender,
            @Query("pRecipient") String pRecipient,
            @Query("pCurrentPage") String pCurrentPage,
            @Query("pViewResultCount") String pViewResultCount
    );

    // Location API Endpoint //
    @GET("GetAllProvince")
    Call<List<Province>> getAllProvince();

    @GET("GetAllCityByProvinceID")
    Call<List<City>> getAllCityByProvince(
            @Query("pProvinceID") String pProvinceID
    );

    @GET("GetAllAreaByCityID")
    Call<List<Area>> getAllAreaByCity(
            @Query("pCityID") String pCityID
    );

    @GET("GetAllProvinceCompany")
    Call<List<Province>> getAllProvinceCompany();

    @GET("LoadAllCompanyAreaByProvince")
    Call<List<CompanyArea>> getAllAreaByProvince(
            @Query("pProvinceID") String pProvinceID
    );
}
