package id.pazpo.agent.services.model.location;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by putri on 1/19/17.
 */

public class Area implements Parcelable {

    public String AreaID;
    public String AreaName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AreaID);
        dest.writeString(this.AreaName);
    }

    public Area() {
    }

    protected Area(Parcel in) {
        this.AreaID = in.readString();
        this.AreaName = in.readString();
    }

    public static final Parcelable.Creator<Area> CREATOR = new Parcelable.Creator<Area>() {
        @Override
        public Area createFromParcel(Parcel source) {
            return new Area(source);
        }

        @Override
        public Area[] newArray(int size) {
            return new Area[size];
        }
    };

    @Override
    public String toString() {
        return AreaName;
    }
}
