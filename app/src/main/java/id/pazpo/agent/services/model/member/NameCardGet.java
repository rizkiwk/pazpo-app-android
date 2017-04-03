package id.pazpo.agent.services.model.member;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/19/17.
 */

public class NameCardGet implements Parcelable {

    public String status;
    public String errCode;
    public String description;
    public NameCard data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeParcelable(this.data, flags);
    }

    public NameCardGet() {
    }

    protected NameCardGet(Parcel in) {
        this.status = in.readString();
        this.data = in.readParcelable(NameCard.class.getClassLoader());
    }

    public static final Parcelable.Creator<NameCardGet> CREATOR = new Parcelable.Creator<NameCardGet>() {
        @Override
        public NameCardGet createFromParcel(Parcel source) {
            return new NameCardGet(source);
        }

        @Override
        public NameCardGet[] newArray(int size) {
            return new NameCardGet[size];
        }
    };
}
