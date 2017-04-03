package id.pazpo.agent.services.model.network;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adigunawan on 3/2/17.
 */

public class Network implements Parcelable {

    public String MemberID;
    public String FirstName;
    public String Mobile;
    public String MemberImage;
    public String CompanyName;
    public int TotalFollowing;
    public String row_num;

    public Network() {

    }

    @Override
    public String toString() {
        return "Network{" +
                "MemberID='" + MemberID + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", MemberImage='" + MemberImage + '\'' +
                ", CompanyName='" + CompanyName + '\'' +
                ", row_num='" + row_num + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.MemberID);
        dest.writeString(this.FirstName);
        dest.writeString(this.Mobile);
        dest.writeString(this.MemberImage);
        dest.writeString(this.CompanyName);
        dest.writeString(this.row_num);
    }

    protected Network(Parcel in) {
        this.MemberID = in.readString();
        this.FirstName = in.readString();
        this.Mobile = in.readString();
        this.MemberImage = in.readString();
        this.CompanyName = in.readString();
        this.row_num = in.readString();
    }

    public static final Creator<Network> CREATOR = new Creator<Network>() {
        @Override
        public Network createFromParcel(Parcel source) {
            return new Network(source);
        }

        @Override
        public Network[] newArray(int size) {
            return new Network[size];
        }
    };
}
