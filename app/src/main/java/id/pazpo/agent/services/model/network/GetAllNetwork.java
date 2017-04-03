package id.pazpo.agent.services.model.network;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import id.pazpo.agent.services.model.member.Member;

/**
 * Created by wais on 1/17/17.
 */

public class GetAllNetwork implements Parcelable {

    public String status;
    public String messages;
    public List<Network> data;

    public GetAllNetwork() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.messages);
        dest.writeTypedList(this.data);
    }

    protected GetAllNetwork(Parcel in) {
        this.status = in.readString();
        this.messages = in.readString();
        this.data = in.createTypedArrayList(Network.CREATOR);
    }

    public static final Parcelable.Creator<GetAllNetwork> CREATOR = new Parcelable.Creator<GetAllNetwork>() {
        @Override
        public GetAllNetwork createFromParcel(Parcel source) {
            return new GetAllNetwork(source);
        }

        @Override
        public GetAllNetwork[] newArray(int size) {
            return new GetAllNetwork[size];
        }
    };
}
