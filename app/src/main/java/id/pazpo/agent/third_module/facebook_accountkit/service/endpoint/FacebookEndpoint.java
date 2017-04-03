package id.pazpo.agent.third_module.facebook_accountkit.service.endpoint;

import id.pazpo.agent.third_module.facebook_accountkit.model.MainGraphFBAKModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by wais on 1/14/17.
 */

public interface FacebookEndpoint {

    @GET("me")
    Call<MainGraphFBAKModel> getGraphAccountKitMe(
            @Query("access_token") String access_token
    );

}
