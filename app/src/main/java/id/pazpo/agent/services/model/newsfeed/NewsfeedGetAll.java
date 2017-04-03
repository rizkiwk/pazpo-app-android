package id.pazpo.agent.services.model.newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by wais on 1/17/17.
 */

public class NewsfeedGetAll implements Parcelable {

    public String status;
    public int totalPage;
    public List<Newsfeed> data;

    public NewsfeedGetAll() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeInt(this.totalPage);
        dest.writeTypedList(this.data);
    }

    protected NewsfeedGetAll(Parcel in) {
        this.status = in.readString();
        this.totalPage = in.readInt();
        this.data = in.createTypedArrayList(Newsfeed.CREATOR);
    }

    public static final Creator<NewsfeedGetAll> CREATOR = new Creator<NewsfeedGetAll>() {
        @Override
        public NewsfeedGetAll createFromParcel(Parcel source) {
            return new NewsfeedGetAll(source);
        }

        @Override
        public NewsfeedGetAll[] newArray(int size) {
            return new NewsfeedGetAll[size];
        }
    };

    @Override
    public String toString() {
        return "NewsfeedGetAll : {" +
                "status='" + status + '\'' +
                ", totalPage=" + totalPage +
                ", data=" + data +
                '}';
    }
}
