package id.pazpo.agent.services.model.member;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by wais on 1/17/17.
 */

public class MemberGetAll implements Parcelable {

    public String status;
    public List<Member> data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeTypedList(this.data);
    }

    public MemberGetAll() {
    }

    protected MemberGetAll(Parcel in) {
        this.status = in.readString();
        this.data = in.createTypedArrayList(Member.CREATOR);
    }

    public static final Parcelable.Creator<MemberGetAll> CREATOR = new Parcelable.Creator<MemberGetAll>() {
        @Override
        public MemberGetAll createFromParcel(Parcel source) {
            return new MemberGetAll(source);
        }

        @Override
        public MemberGetAll[] newArray(int size) {
            return new MemberGetAll[size];
        }
    };
}
