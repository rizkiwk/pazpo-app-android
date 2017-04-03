package id.pazpo.agent;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.accountkit.AccountKit;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.segment.analytics.Analytics;
import com.segment.analytics.Options;
import com.segment.analytics.Properties;
import com.segment.analytics.android.integrations.localytics.LocalyticsIntegration;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import id.pazpo.agent.helpers.ConstantHelper;
import id.pazpo.agent.helpers.ServiceHelper;
import id.pazpo.agent.utils.SharedPrefs;
import io.fabric.sdk.android.Fabric;

/**
 * Created by wais on 1/11/17.
 */

public class PazpoApp extends Application {

    public SharedPrefs mSharedPrefs;
    public Gson Gson;
    public ServiceHelper mServiceHelper;
    public ConstantHelper mConstantHelper;
    public Map mOneSignalData;

    private static PazpoApp sPazpoApp;

    protected Socket mSocket;

    public PazpoApp() {
        Gson            = new Gson();
        mServiceHelper  = new ServiceHelper();

        try {
            mSocket = IO.socket(mServiceHelper.URL_PAZPO_WEBSOCKET_BASE);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sPazpoApp = this;

        //ACCOUNT KIT
        AccountKit.initialize(getApplicationContext());

        //SEGMENT
        Options options     = new Options().setIntegration("Localytics", true);
        Analytics analytics = new Analytics.Builder(this, mServiceHelper.SEGMENT_API_KEY)
                .defaultOptions(options)
                .logLevel(Analytics.LogLevel.VERBOSE)
                .trackApplicationLifecycleEvents()
                .trackAttributionInformation()
                .use(LocalyticsIntegration.FACTORY)
                .build();
        Analytics.setSingletonInstance(analytics);

        //FABRIC
        Fabric.with(this, new Crashlytics());

        //ONE SIGNAL
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
                    @Override
                    public void notificationOpened(OSNotificationOpenResult result) {
                        OSNotificationAction.ActionType actionType = result.action.actionType;
                        JSONObject data                            = result.notification.payload.additionalData;
                        String userAction                          = "";
                        String conversationID                      = "";
                        String senderID                            = "";
                        String sender                              = "";
                        String senderImage                         = "";

                        if (data != null) {
                            Log.d("[ PazpoApp ]", "One Signal data = "+ data.toString());
                            try {
                                mOneSignalData = new HashMap();

                                if (data.has("UserActionType")) {
                                    userAction = mConstantHelper.ONESIGNAL_VAL_USERACTION_MESSAGE;
                                    mOneSignalData.put(mConstantHelper.ONESIGNAL_KEY_USERACTION, data.get("UserActionType"));
                                } else {
                                    userAction = mConstantHelper.ONESIGNAL_VAL_USERACTION_NEWSFEED;
                                    mOneSignalData.put(mConstantHelper.ONESIGNAL_KEY_USERACTION, mConstantHelper.ONESIGNAL_VAL_USERACTION_NEWSFEED);
                                }
                                if (data.has("pConversationID")) {
                                    conversationID = data.get("pConversationID").toString();
                                    mOneSignalData.put(mConstantHelper.ONESIGNAL_KEY_MESSAGE_ID, data.get("pConversationID"));
                                }
                                if (data.has("senderID")) {
                                    senderID = data.get("senderID").toString();
                                    mOneSignalData.put(mConstantHelper.ONESIGNAL_KEY_MESSAGE_CLIENT_ID, data.get("senderID"));
                                }
                                if (data.has("sender")) {
                                    sender = data.get("sender").toString();
                                    mOneSignalData.put(mConstantHelper.ONESIGNAL_KEY_MESSAGE_CLIENT_NAME, data.get("sender"));
                                }
                                if (data.has("senderImage")) {
                                    senderImage = data.get("senderImage").toString();
                                    mOneSignalData.put(mConstantHelper.ONESIGNAL_KEY_MESSAGE_CLIENT_IMAGE, data.get("senderImage"));
                                }

                                mOneSignalData.put(mConstantHelper.ONESIGNAL_KEY_IS_FROM_NOTIF, true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Analytics.with(getApplicationContext()).track("Click Notification", new Properties()
                                .putValue("Notification ID", result.notification.androidNotificationId)
                                .putValue("Notification Type", userAction)
                                .putValue("Conversation ID", conversationID)
                                .putValue("Sender ID", senderID)
                                .putValue("Sender", sender)
                                .putValue("Sender Image", senderImage)
                                .putValue("Action Type", actionType.name())
                                .putValue("Display Type", result.notification.displayType.name()));
                    }
                })
                .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
                    @Override
                    public void notificationReceived(OSNotification notification) {
                        String userAction       = "";
                        String conversationID   = "";
                        String senderID         = "";
                        String sender           = "";
                        String senderImage      = "";
                        try {
                            JSONObject data     = notification.payload.additionalData;
                            userAction          = data.get("UserActionType").toString();
                            conversationID      = data.get("pConversationID").toString();
                            senderID            = data.get("senderID").toString();
                            sender              = data.get("sender").toString();
                            senderImage         = data.get("senderImage").toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Analytics.with(getApplicationContext()).track("Receive Notification", new Properties()
                                .putValue("Notification ID", notification.androidNotificationId)
                                .putValue("Notification Type", userAction)
                                .putValue("Conversation ID", conversationID)
                                .putValue("Sender ID", senderID)
                                .putValue("Sender", sender)
                                .putValue("Sender Image", senderImage)
                                .putValue("Display Type", notification.displayType.name()));
                    }
                })
                .autoPromptLocation(true)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
    }

    public static PazpoApp getInstance() {
        if (sPazpoApp == null) {
            sPazpoApp = new PazpoApp();
        }
        return sPazpoApp;
    }

    public Socket getWebsocket() {
        return mSocket;
    }
}
