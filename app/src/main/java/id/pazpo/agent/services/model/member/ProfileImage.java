package id.pazpo.agent.services.model.member;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/19/17.
 */

public class ProfileImage implements Parcelable {

    public String status;
    public String filename;
    public String url;
    public String full_path;

    public ProfileImage() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.filename);
        dest.writeString(this.url);
        dest.writeString(this.full_path);
    }

    protected ProfileImage(Parcel in) {
        this.status = in.readString();
        this.filename = in.readString();
        this.url = in.readString();
        this.full_path = in.readString();
    }

    public static final Creator<ProfileImage> CREATOR = new Creator<ProfileImage>() {
        @Override
        public ProfileImage createFromParcel(Parcel source) {
            return new ProfileImage(source);
        }

        @Override
        public ProfileImage[] newArray(int size) {
            return new ProfileImage[size];
        }
    };
}
