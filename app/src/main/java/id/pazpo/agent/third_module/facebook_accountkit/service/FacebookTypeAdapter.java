package id.pazpo.agent.third_module.facebook_accountkit.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by wais on 1/14/17.
 */

public class FacebookTypeAdapter implements TypeAdapterFactory {

    protected String mRequestTag;

    public FacebookTypeAdapter(String requestTag) {
        this.mRequestTag = requestTag;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {

            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            public T read(JsonReader in) throws IOException {

                JsonElement jsonElement = elementAdapter.read(in);
//                if (jsonElement.isJsonObject()) {
//                    JsonObject jsonObject = jsonElement.getAsJsonObject();
//                    if (jsonObject.has("phone") && jsonObject.get("phone").isJsonObject()) {
//                        jsonElement = jsonObject.get("phone");
//                    }
//                }

                switch (mRequestTag) {
                    case "FacebookGraphAccountKitMe":
                        jsonAccountKitGraphMe(jsonElement);
                        break;
                    default:
                        break;
                }

                return delegate.fromJsonTree(jsonElement);
            }
        }.nullSafe();
    }

    public JsonElement jsonAccountKitGraphMe(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("phone") && jsonObject.get("phone").isJsonObject()) {
                jsonElement = jsonObject.get("phone");
                Log.d("Data","fbak = "+jsonElement.getAsJsonObject().get("national_number"));
            }
        }
        return jsonElement;
    }

}
