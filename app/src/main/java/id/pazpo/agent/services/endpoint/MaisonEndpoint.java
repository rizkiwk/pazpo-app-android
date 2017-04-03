package id.pazpo.agent.services.endpoint;

import id.pazpo.agent.services.model.member.Member;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by wais on 1/16/17.
 */

public interface MaisonEndpoint {

    @GET("GetUserMember")
    Call<Member> getUserMember(
            @Query("pUsername") String pUsername
    );

}
