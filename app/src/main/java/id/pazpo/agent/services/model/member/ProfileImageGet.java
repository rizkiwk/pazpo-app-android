package id.pazpo.agent.services.model.member;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/19/17.
 */

public class ProfileImageGet implements Parcelable {

    public String status;
    public String errCode;
    public String description;
    public ProfileImage data;

    public ProfileImageGet() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.errCode);
        dest.writeString(this.description);
        dest.writeParcelable(this.data, flags);
    }

    protected ProfileImageGet(Parcel in) {
        this.status = in.readString();
        this.errCode = in.readString();
        this.description = in.readString();
        this.data = in.readParcelable(ProfileImage.class.getClassLoader());
    }

    public static final Creator<ProfileImageGet> CREATOR = new Creator<ProfileImageGet>() {
        @Override
        public ProfileImageGet createFromParcel(Parcel source) {
            return new ProfileImageGet(source);
        }

        @Override
        public ProfileImageGet[] newArray(int size) {
            return new ProfileImageGet[size];
        }
    };
}
