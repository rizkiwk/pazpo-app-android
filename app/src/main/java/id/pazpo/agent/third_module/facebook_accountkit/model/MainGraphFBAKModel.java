package id.pazpo.agent.third_module.facebook_accountkit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/15/17.
 */

public class MainGraphFBAKModel implements Parcelable {

    public String id;
    public SubGraphFBAKModel phone;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.phone, flags);
    }

    public MainGraphFBAKModel() {
    }

    protected MainGraphFBAKModel(Parcel in) {
        this.id = in.readString();
        this.phone = in.readParcelable(SubGraphFBAKModel.class.getClassLoader());
    }

    public static final Parcelable.Creator<MainGraphFBAKModel> CREATOR = new Parcelable.Creator<MainGraphFBAKModel>() {
        @Override
        public MainGraphFBAKModel createFromParcel(Parcel source) {
            return new MainGraphFBAKModel(source);
        }

        @Override
        public MainGraphFBAKModel[] newArray(int size) {
            return new MainGraphFBAKModel[size];
        }
    };
}
