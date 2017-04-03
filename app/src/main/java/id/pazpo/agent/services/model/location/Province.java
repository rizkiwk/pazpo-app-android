package id.pazpo.agent.services.model.location;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/18/17.
 */

public class Province implements Parcelable {

    public String ProvinceID;
    public String ProvinceName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ProvinceID);
        dest.writeString(this.ProvinceName);
    }

    public Province() {
    }

    protected Province(Parcel in) {
        this.ProvinceID = in.readString();
        this.ProvinceName = in.readString();
    }

    public static final Parcelable.Creator<Province> CREATOR = new Parcelable.Creator<Province>() {
        @Override
        public Province createFromParcel(Parcel source) {
            return new Province(source);
        }

        @Override
        public Province[] newArray(int size) {
            return new Province[size];
        }
    };

    @Override
    public String toString() {
        return ProvinceName;
    }
}
