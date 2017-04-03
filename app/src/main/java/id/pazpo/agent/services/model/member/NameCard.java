package id.pazpo.agent.services.model.member;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/19/17.
 */

public class NameCard implements Parcelable {

    public String status;
    public String filename;
    public String full_path;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.filename);
        dest.writeString(this.full_path);
    }

    public NameCard() {
    }

    protected NameCard(Parcel in) {
        this.status = in.readString();
        this.filename = in.readString();
        this.full_path = in.readString();
    }

    public static final Parcelable.Creator<NameCard> CREATOR = new Parcelable.Creator<NameCard>() {
        @Override
        public NameCard createFromParcel(Parcel source) {
            return new NameCard(source);
        }

        @Override
        public NameCard[] newArray(int size) {
            return new NameCard[size];
        }
    };
}
