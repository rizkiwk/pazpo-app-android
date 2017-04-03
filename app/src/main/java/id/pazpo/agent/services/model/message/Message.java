package id.pazpo.agent.services.model.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wais on 1/18/17.
 */

public class Message implements Parcelable {

    public String ConversationID;
    public String UserID;
    public String UserID_One;
    public String UserOneName;
    public String UserOneImage;
    public String UserOneStatus;
    public String UserID_Two;
    public String UserTwoName;
    public String UserTwoImage;
    public String UserTwoStatus;
    public String UserID_Three;
    public String UserThreeName;
    public String UserThreeImage;
    public String UserThreeStatus;
    public String ConversationSubject;
    public String ReplyID;
    public String Reply;
    public String ReplyType;
    public String ReplyUserID;
    public String ReplyTransactTime;
    public String ReplyStatus;
    public String NewMessage;
    public String LastMessage;
    public String PropertyID;
    public String TransactTime;
    public String Status;
    public String senderID;
    public String receiverID;
    public String socketId;
    public String UserIDTwo;
    public String socketIdTwo;
    public int TotalPage;

    public Message() {

    }

    @Override
    public String toString() {
        return "Message : {" +
                "ConversationID='" + ConversationID + '\'' +
                ", UserID='" + UserID + '\'' +
                ", UserID_One='" + UserID_One + '\'' +
                ", UserOneName='" + UserOneName + '\'' +
                ", UserOneImage='" + UserOneImage + '\'' +
                ", UserOneStatus='" + UserOneStatus + '\'' +
                ", UserID_Two='" + UserID_Two + '\'' +
                ", UserTwoName='" + UserTwoName + '\'' +
                ", UserTwoImage='" + UserTwoImage + '\'' +
                ", UserTwoStatus='" + UserTwoStatus + '\'' +
                ", UserID_Three='" + UserID_Three + '\'' +
                ", UserThreeName='" + UserThreeName + '\'' +
                ", UserThreeImage='" + UserThreeImage + '\'' +
                ", UserThreeStatus='" + UserThreeStatus + '\'' +
                ", ConversationSubject='" + ConversationSubject + '\'' +
                ", ReplyID='" + ReplyID + '\'' +
                ", Reply='" + Reply + '\'' +
                ", ReplyType='" + ReplyType + '\'' +
                ", ReplyUserID='" + ReplyUserID + '\'' +
                ", ReplyTransactTime='" + ReplyTransactTime + '\'' +
                ", ReplyStatus='" + ReplyStatus + '\'' +
                ", NewMessage='" + NewMessage + '\'' +
                ", LastMessage='" + LastMessage + '\'' +
                ", PropertyID='" + PropertyID + '\'' +
                ", TransactTime='" + TransactTime + '\'' +
                ", Status='" + Status + '\'' +
                ", senderID='" + senderID + '\'' +
                ", receiverID='" + receiverID + '\'' +
                ", socketId='" + socketId + '\'' +
                ", UserIDTwo='" + UserIDTwo + '\'' +
                ", socketIdTwo='" + socketIdTwo + '\'' +
                ", TotalPage=" + TotalPage +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ConversationID);
        dest.writeString(this.UserID);
        dest.writeString(this.UserID_One);
        dest.writeString(this.UserOneName);
        dest.writeString(this.UserOneImage);
        dest.writeString(this.UserOneStatus);
        dest.writeString(this.UserID_Two);
        dest.writeString(this.UserTwoName);
        dest.writeString(this.UserTwoImage);
        dest.writeString(this.UserTwoStatus);
        dest.writeString(this.UserID_Three);
        dest.writeString(this.UserThreeName);
        dest.writeString(this.UserThreeImage);
        dest.writeString(this.UserThreeStatus);
        dest.writeString(this.ConversationSubject);
        dest.writeString(this.ReplyID);
        dest.writeString(this.Reply);
        dest.writeString(this.ReplyType);
        dest.writeString(this.ReplyUserID);
        dest.writeString(this.ReplyTransactTime);
        dest.writeString(this.ReplyStatus);
        dest.writeString(this.NewMessage);
        dest.writeString(this.LastMessage);
        dest.writeString(this.PropertyID);
        dest.writeString(this.TransactTime);
        dest.writeString(this.Status);
        dest.writeString(this.senderID);
        dest.writeString(this.receiverID);
        dest.writeString(this.socketId);
        dest.writeString(this.UserIDTwo);
        dest.writeString(this.socketIdTwo);
        dest.writeInt(this.TotalPage);
    }

    protected Message(Parcel in) {
        this.ConversationID = in.readString();
        this.UserID = in.readString();
        this.UserID_One = in.readString();
        this.UserOneName = in.readString();
        this.UserOneImage = in.readString();
        this.UserOneStatus = in.readString();
        this.UserID_Two = in.readString();
        this.UserTwoName = in.readString();
        this.UserTwoImage = in.readString();
        this.UserTwoStatus = in.readString();
        this.UserID_Three = in.readString();
        this.UserThreeName = in.readString();
        this.UserThreeImage = in.readString();
        this.UserThreeStatus = in.readString();
        this.ConversationSubject = in.readString();
        this.ReplyID = in.readString();
        this.Reply = in.readString();
        this.ReplyType = in.readString();
        this.ReplyUserID = in.readString();
        this.ReplyTransactTime = in.readString();
        this.ReplyStatus = in.readString();
        this.NewMessage = in.readString();
        this.LastMessage = in.readString();
        this.PropertyID = in.readString();
        this.TransactTime = in.readString();
        this.Status = in.readString();
        this.senderID = in.readString();
        this.receiverID = in.readString();
        this.socketId = in.readString();
        this.UserIDTwo = in.readString();
        this.socketIdTwo = in.readString();
        this.TotalPage = in.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
