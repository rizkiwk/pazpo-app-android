package id.pazpo.agent.services;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;

import id.pazpo.agent.services.model.OauthTokenModel;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by putri on 1/16/17.
 */

public class MaisonClient {

    public static String MAISON_BASE_URL;

    private static OkHttpClient.Builder httpClient      = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor logging       = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static Retrofit.Builder builder;
    private static Gson gson;

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createOauthService(Class<S> serviceClass, final String authHeader) {
        if (authHeader != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", authHeader)
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit   = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        // we shortened this part, because it’s covered in
        // the previous post on basic authentication with Retrofit
        return createService(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, @Nullable final OauthTokenModel token) {
        if (token != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization",
                                    token.getTokenType() + " " + token.getAccessToken())
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit   = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
