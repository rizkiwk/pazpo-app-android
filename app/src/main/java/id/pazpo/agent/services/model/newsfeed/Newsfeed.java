package id.pazpo.agent.services.model.newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by wais on 1/17/17.
 */

public class Newsfeed implements Parcelable {

    public String PropertyTypeDesc;
    public String ListingType;
    public String Location;
    public String MinPrice;
    public String MaxPrice;
    public String Notes;
    public String Fee;
    public String CreatedDate;
    public String UserID;
    public String UserImage;
    public String CompanyName;
    public String UserName;
    public String mobile;
    public String ClientID;
    public int TotalPage;
    public String OptionMemberID;
    public int OptionListingType;
    public boolean OptionNetworkOnly;
    public boolean[] OptionItems;

    public Newsfeed() {}

    @Override
    public String toString() {
        return "Newsfeed{" +
                "PropertyTypeDesc='" + PropertyTypeDesc + '\'' +
                ", ListingType='" + ListingType + '\'' +
                ", Location='" + Location + '\'' +
                ", MinPrice='" + MinPrice + '\'' +
                ", MaxPrice='" + MaxPrice + '\'' +
                ", Notes='" + Notes + '\'' +
                ", Fee='" + Fee + '\'' +
                ", CreatedDate='" + CreatedDate + '\'' +
                ", UserID='" + UserID + '\'' +
                ", UserImage='" + UserImage + '\'' +
                ", CompanyName='" + CompanyName + '\'' +
                ", UserName='" + UserName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", ClientID='" + ClientID + '\'' +
                ", TotalPage=" + TotalPage +
                ", OptionMemberID='" + OptionMemberID + '\'' +
                ", OptionListingType=" + OptionListingType +
                ", OptionNetworkOnly=" + OptionNetworkOnly +
                ", OptionItems=" + Arrays.toString(OptionItems) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.PropertyTypeDesc);
        dest.writeString(this.ListingType);
        dest.writeString(this.Location);
        dest.writeString(this.MinPrice);
        dest.writeString(this.MaxPrice);
        dest.writeString(this.Notes);
        dest.writeString(this.Fee);
        dest.writeString(this.CreatedDate);
        dest.writeString(this.UserID);
        dest.writeString(this.UserImage);
        dest.writeString(this.CompanyName);
        dest.writeString(this.UserName);
        dest.writeString(this.mobile);
        dest.writeString(this.ClientID);
        dest.writeInt(this.TotalPage);
        dest.writeString(this.OptionMemberID);
        dest.writeInt(this.OptionListingType);
        dest.writeByte(this.OptionNetworkOnly ? (byte) 1 : (byte) 0);
        dest.writeBooleanArray(this.OptionItems);
    }

    protected Newsfeed(Parcel in) {
        this.PropertyTypeDesc = in.readString();
        this.ListingType = in.readString();
        this.Location = in.readString();
        this.MinPrice = in.readString();
        this.MaxPrice = in.readString();
        this.Notes = in.readString();
        this.Fee = in.readString();
        this.CreatedDate = in.readString();
        this.UserID = in.readString();
        this.UserImage = in.readString();
        this.CompanyName = in.readString();
        this.UserName = in.readString();
        this.mobile = in.readString();
        this.ClientID = in.readString();
        this.TotalPage = in.readInt();
        this.OptionMemberID = in.readString();
        this.OptionListingType = in.readInt();
        this.OptionNetworkOnly = in.readByte() != 0;
        this.OptionItems = in.createBooleanArray();
    }

    public static final Creator<Newsfeed> CREATOR = new Creator<Newsfeed>() {
        @Override
        public Newsfeed createFromParcel(Parcel source) {
            return new Newsfeed(source);
        }

        @Override
        public Newsfeed[] newArray(int size) {
            return new Newsfeed[size];
        }
    };
}
