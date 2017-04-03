package id.pazpo.agent.third_module.facebook_accountkit.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wais on 1/14/17.
 */

public class FacebookClient {

    public static String FB_BASE_URL;

    private static OkHttpClient.Builder httpClient      = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor logging       = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static Retrofit.Builder builder;
    private static Gson gson;

    public FacebookClient(String baseURL, String typeAdapterTag) {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // set your desired log level
        httpClient.addInterceptor(logging); // add logging as last interceptor

        FB_BASE_URL = baseURL;

        gson    = new GsonBuilder()
                .registerTypeAdapterFactory(new FacebookTypeAdapter(typeAdapterTag))
                .enableComplexMapKeySerialization()
                .serializeNulls().setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting().setVersion(1.0).create();

        builder = new Retrofit
                .Builder()
                .baseUrl(FB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
