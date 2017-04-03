package id.pazpo.agent.services.model.message;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import id.pazpo.agent.services.model.newsfeed.Newsfeed;

/**
 * Created by wais on 1/18/17.
 */

public class MessageGetAll implements Parcelable {
    public String status;
    public int totalPage;
    public List<Message> data;

    public MessageGetAll() {
    }

    @Override
    public String toString() {
        return "MessageGetAll : {" +
                "status='" + status + '\'' +
                ", totalPage=" + totalPage +
                ", data=" + data +
                '}';
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

    protected MessageGetAll(Parcel in) {
        this.status = in.readString();
        this.totalPage = in.readInt();
        this.data = in.createTypedArrayList(Message.CREATOR);
    }

    public static final Parcelable.Creator<MessageGetAll> CREATOR = new Parcelable.Creator<MessageGetAll>() {
        @Override
        public MessageGetAll createFromParcel(Parcel source) {
            return new MessageGetAll(source);
        }

        @Override
        public MessageGetAll[] newArray(int size) {
            return new MessageGetAll[size];
        }
    };
}
