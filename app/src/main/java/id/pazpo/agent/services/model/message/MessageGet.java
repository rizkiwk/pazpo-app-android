package id.pazpo.agent.services.model.message;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by wais on 1/18/17.
 */

public class MessageGet implements Parcelable {
    public String status;
    public int totalPage;
    public List<Message> data;

    public MessageGet() {
    }

    @Override
    public String toString() {
        return "MessageGet : {" +
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

    protected MessageGet(Parcel in) {
        this.status = in.readString();
        this.totalPage = in.readInt();
        this.data = in.createTypedArrayList(Message.CREATOR);
    }

    public static final Parcelable.Creator<MessageGet> CREATOR = new Parcelable.Creator<MessageGet>() {
        @Override
        public MessageGet createFromParcel(Parcel source) {
            return new MessageGet(source);
        }

        @Override
        public MessageGet[] newArray(int size) {
            return new MessageGet[size];
        }
    };
}
