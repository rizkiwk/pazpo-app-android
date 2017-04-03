package id.pazpo.agent.services.model.member;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/17/17.
 */

public class MemberGet implements Parcelable {

    public String status;
    public Member data;
    public String errCode;
    public String description;
    public int VersionCode;
    public String MessageTitle;
    public String MessageBody;

    public MemberGet() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.errCode);
        dest.writeString(this.description);
        dest.writeInt(this.VersionCode);
        dest.writeString(this.MessageTitle);
        dest.writeString(this.MessageBody);
    }

    protected MemberGet(Parcel in) {
        this.status = in.readString();
        this.data = in.readParcelable(Member.class.getClassLoader());
        this.errCode = in.readString();
        this.description = in.readString();
        this.VersionCode = in.readInt();
        this.MessageTitle = in.readString();
        this.MessageBody = in.readString();
    }

    public static final Creator<MemberGet> CREATOR = new Creator<MemberGet>() {
        @Override
        public MemberGet createFromParcel(Parcel source) {
            return new MemberGet(source);
        }

        @Override
        public MemberGet[] newArray(int size) {
            return new MemberGet[size];
        }
    };
}
