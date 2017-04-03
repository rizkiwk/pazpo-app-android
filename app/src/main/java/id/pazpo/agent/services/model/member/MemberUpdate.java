package id.pazpo.agent.services.model.member;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/17/17.
 */

public class MemberUpdate implements Parcelable {

    public String status;
    public boolean data;
    public String errCode;
    public String description;
    public int VersionCode;
    public String MessageTitle;
    public String MessageBody;

    public MemberUpdate() {
    }

    @Override
    public String toString() {
        return "MemberUpdate {" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", errCode='" + errCode + '\'' +
                ", description='" + description + '\'' +
                ", VersionCode=" + VersionCode +
                ", MessageTitle='" + MessageTitle + '\'' +
                ", MessageBody='" + MessageBody + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeByte(this.data ? (byte) 1 : (byte) 0);
        dest.writeString(this.errCode);
        dest.writeString(this.description);
        dest.writeInt(this.VersionCode);
        dest.writeString(this.MessageTitle);
        dest.writeString(this.MessageBody);
    }

    protected MemberUpdate(Parcel in) {
        this.status = in.readString();
        this.data = in.readByte() != 0;
        this.errCode = in.readString();
        this.description = in.readString();
        this.VersionCode = in.readInt();
        this.MessageTitle = in.readString();
        this.MessageBody = in.readString();
    }

    public static final Creator<MemberUpdate> CREATOR = new Creator<MemberUpdate>() {
        @Override
        public MemberUpdate createFromParcel(Parcel source) {
            return new MemberUpdate(source);
        }

        @Override
        public MemberUpdate[] newArray(int size) {
            return new MemberUpdate[size];
        }
    };
}
