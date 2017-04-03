package id.pazpo.agent.third_module.facebook_accountkit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by putri on 1/15/17.
 */

public class SubGraphFBAKModel implements Parcelable {

    public String number;
    public String country_prefix;
    public String national_number;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.country_prefix);
        dest.writeString(this.national_number);
    }

    public SubGraphFBAKModel() {
    }

    protected SubGraphFBAKModel(Parcel in) {
        this.number = in.readString();
        this.country_prefix = in.readString();
        this.national_number = in.readString();
    }

    public static final Parcelable.Creator<SubGraphFBAKModel> CREATOR = new Parcelable.Creator<SubGraphFBAKModel>() {
        @Override
        public SubGraphFBAKModel createFromParcel(Parcel source) {
            return new SubGraphFBAKModel(source);
        }

        @Override
        public SubGraphFBAKModel[] newArray(int size) {
            return new SubGraphFBAKModel[size];
        }
    };
}
