package id.pazpo.agent.services.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by wais on 3/3/17.
 */

public class RestModel implements Parcelable {

    public String status;
    public JsonElement data;
    public String message;

    public RestModel() {
    }

    @Override
    public String toString() {
        return "RestModel {" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeParcelable((Parcelable) this.data, flags);
        dest.writeString(this.message);
    }

    protected RestModel(Parcel in) {
        this.status = in.readString();
        this.data = in.readParcelable(JsonObject.class.getClassLoader());
        this.message = in.readString();
    }

    public static final Creator<RestModel> CREATOR = new Creator<RestModel>() {
        @Override
        public RestModel createFromParcel(Parcel source) {
            return new RestModel(source);
        }

        @Override
        public RestModel[] newArray(int size) {
            return new RestModel[size];
        }
    };
}
