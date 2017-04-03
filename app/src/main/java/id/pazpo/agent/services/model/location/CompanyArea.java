package id.pazpo.agent.services.model.location;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/18/17.
 */

public class CompanyArea implements Parcelable {

    public String CompanyID;
    public String CompanyName;
    public String ProvinceID;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.CompanyID);
        dest.writeString(this.CompanyName);
        dest.writeString(this.ProvinceID);
    }

    public CompanyArea() {
    }

    protected CompanyArea(Parcel in) {
        this.CompanyID = in.readString();
        this.CompanyName = in.readString();
        this.ProvinceID = in.readString();
    }

    public static final Parcelable.Creator<CompanyArea> CREATOR = new Parcelable.Creator<CompanyArea>() {
        @Override
        public CompanyArea createFromParcel(Parcel source) {
            return new CompanyArea(source);
        }

        @Override
        public CompanyArea[] newArray(int size) {
            return new CompanyArea[size];
        }
    };

    @Override
    public String toString() {
        return CompanyName  ;
    }
}
