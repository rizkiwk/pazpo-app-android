package id.pazpo.agent.services.model.location;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/19/17.
 */

public class BaseLocation implements Parcelable {

    public String ProvinceID;
    public String ProvinceName;
    public String CityID;
    public String CityName;
    public String AreaID;
    public String AreaName;
    public String CompanyID;
    public String CompanyName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ProvinceID);
        dest.writeString(this.ProvinceName);
        dest.writeString(this.CityID);
        dest.writeString(this.CityName);
        dest.writeString(this.AreaID);
        dest.writeString(this.AreaName);
        dest.writeString(this.CompanyID);
        dest.writeString(this.CompanyName);
    }

    public BaseLocation() {
    }

    protected BaseLocation(Parcel in) {
        this.ProvinceID = in.readString();
        this.ProvinceName = in.readString();
        this.CityID = in.readString();
        this.CityName = in.readString();
        this.AreaID = in.readString();
        this.AreaName = in.readString();
        this.CompanyID = in.readString();
        this.CompanyName = in.readString();
    }

    public static final Parcelable.Creator<BaseLocation> CREATOR = new Parcelable.Creator<BaseLocation>() {
        @Override
        public BaseLocation createFromParcel(Parcel source) {
            return new BaseLocation(source);
        }

        @Override
        public BaseLocation[] newArray(int size) {
            return new BaseLocation[size];
        }
    };
}
