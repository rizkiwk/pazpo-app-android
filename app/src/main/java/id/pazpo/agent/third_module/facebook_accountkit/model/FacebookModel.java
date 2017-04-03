package id.pazpo.agent.third_module.facebook_accountkit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/14/17.
 */

public class FacebookModel implements Parcelable {

    public String id;
    public PhoneFBAK phone;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.phone, flags);
    }

    public FacebookModel() {

    }

    protected FacebookModel(Parcel in) {
        this.id = in.readString();
        this.phone = in.readParcelable(PhoneFBAK.class.getClassLoader());
    }

    public static final Parcelable.Creator<FacebookModel> CREATOR = new Parcelable.Creator<FacebookModel>() {
        @Override
        public FacebookModel createFromParcel(Parcel source) {
            return new FacebookModel(source);
        }

        @Override
        public FacebookModel[] newArray(int size) {
            return new FacebookModel[size];
        }
    };

    static class PhoneFBAK implements Parcelable {

        public String number;
        public String country_prefix;
        public String national_number;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.number);
            dest.writeString(this.country_prefix);
            dest.writeString(this.national_number);
        }

        public PhoneFBAK() {

        }

        protected PhoneFBAK(Parcel in) {
            this.number = in.readString();
            this.country_prefix = in.readString();
            this.national_number = in.readString();
        }

        public static final Parcelable.Creator<PhoneFBAK> CREATOR = new Parcelable.Creator<PhoneFBAK>() {
            @Override
            public PhoneFBAK createFromParcel(Parcel source) {
                return new PhoneFBAK(source);
            }

            @Override
            public PhoneFBAK[] newArray(int size) {
                return new PhoneFBAK[size];
            }
        };
    }
}
