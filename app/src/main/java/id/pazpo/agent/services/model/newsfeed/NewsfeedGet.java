package id.pazpo.agent.services.model.newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/17/17.
 */

public class NewsfeedGet implements Parcelable {

    public String status;
    public Newsfeed data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeParcelable(this.data, flags);
    }

    public NewsfeedGet() {
    }

    protected NewsfeedGet(Parcel in) {
        this.status = in.readString();
        this.data = in.readParcelable(Newsfeed.class.getClassLoader());
    }

    public static final Parcelable.Creator<NewsfeedGet> CREATOR = new Parcelable.Creator<NewsfeedGet>() {
        @Override
        public NewsfeedGet createFromParcel(Parcel source) {
            return new NewsfeedGet(source);
        }

        @Override
        public NewsfeedGet[] newArray(int size) {
            return new NewsfeedGet[size];
        }
    };
}
