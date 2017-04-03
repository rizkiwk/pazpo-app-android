package id.pazpo.agent.services;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import id.pazpo.agent.services.model.OauthTokenModel;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wais on 1/14/17.
 */

public class APIClient {

    public static String API_BASE_URL_UAT               = "http://223.27.24.155/api_pazpo/v2/";
    public static String API_BASE_URL_LIVE              = "http://your.api-base.url";
    public static String API_BASE_URL;

    private static OkHttpClient.Builder httpClient      = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor logging       = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static Retrofit.Builder builder;
    private static Gson gson;

    public APIClient(String baseURL) {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // set your desired log level

        httpClient.readTimeout(1, TimeUnit.MINUTES);
        httpClient.connectTimeout(1, TimeUnit.MINUTES);
        httpClient.addInterceptor(logging); // add logging as last interceptor

        gson    = new GsonBuilder()
                .registerTypeAdapterFactory(new APITypeAdapter())
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting().setVersion(1.0).create();

        builder = new Retrofit
                .Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static void changeBaseUrl(String newBaseUrl) {
        API_BASE_URL    = newBaseUrl;
        builder         = new Retrofit.Builder()
                .baseUrl(API_BASE_URL_UAT)
                .addConverterFactory(GsonConverterFactory.create(gson));
    }

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        // we shortened this part, because it’s covered in
        // the previous post on basic authentication with Retrofit
        return createService(serviceClass, null);
    }

    public static <S> S createService(Class<S> serviceClass, final OauthTokenModel token) {
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

    public static <S> S createOauthService(Class<S> serviceClass, final String headerOauth) {
        if (headerOauth != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", headerOauth)
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
