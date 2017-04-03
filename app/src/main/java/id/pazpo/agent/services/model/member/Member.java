package id.pazpo.agent.services.model.member;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/17/17.
 */

public class Member
        implements Parcelable {

    public String MobilePhone;
    public String Mobile;
    public String UserID;
    public String MemberID;
    public String FirstName;
    public String LastName;
    public String UserImage;
    public String UserImageURL;
    public String CompanyName;
    public String CreatedDate;
    public String PlayerID;

    public Member() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.MobilePhone);
        dest.writeString(this.Mobile);
        dest.writeString(this.UserID);
        dest.writeString(this.MemberID);
        dest.writeString(this.FirstName);
        dest.writeString(this.LastName);
        dest.writeString(this.UserImage);
        dest.writeString(this.UserImageURL);
        dest.writeString(this.CompanyName);
        dest.writeString(this.CreatedDate);
        dest.writeString(this.PlayerID);
    }

    protected Member(Parcel in) {
        this.MobilePhone = in.readString();
        this.Mobile = in.readString();
        this.UserID = in.readString();
        this.MemberID = in.readString();
        this.FirstName = in.readString();
        this.LastName = in.readString();
        this.UserImage = in.readString();
        this.UserImageURL = in.readString();
        this.CompanyName = in.readString();
        this.CreatedDate = in.readString();
        this.PlayerID = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel source) {
            return new Member(source);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
}
