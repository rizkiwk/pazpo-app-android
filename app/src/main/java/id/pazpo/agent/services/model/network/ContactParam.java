package id.pazpo.agent.services.model.network;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by adigunawan on 3/6/17.
 */

public class ContactParam implements Parcelable {

    public String pMemberID;
    public ArrayList<String> pMobile;

    @Override
    public String toString() {
        return "ContactParam{" +
                "pMemberID='" + pMemberID + '\'' +
                ", pMobile=" + pMobile +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pMemberID);
        dest.writeStringList(this.pMobile);
    }

    public ContactParam() {
    }

    protected ContactParam(Parcel in) {
        this.pMemberID = in.readString();
        this.pMobile = in.createStringArrayList();
    }

    public static final Parcelable.Creator<ContactParam> CREATOR = new Parcelable.Creator<ContactParam>() {
        @Override
        public ContactParam createFromParcel(Parcel source) {
            return new ContactParam(source);
        }

        @Override
        public ContactParam[] newArray(int size) {
            return new ContactParam[size];
        }
    };
}
