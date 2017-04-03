package id.pazpo.agent.services.model.location;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/19/17.
 */

public class City implements Parcelable {

    public String CityID;
    public String CityName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.CityID);
        dest.writeString(this.CityName);
    }

    public City() {
    }

    protected City(Parcel in) {
        this.CityID = in.readString();
        this.CityName = in.readString();
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public String toString() {
        return CityName;
    }
}
