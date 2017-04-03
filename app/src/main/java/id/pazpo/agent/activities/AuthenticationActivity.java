package id.pazpo.agent.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.flipbox.pazpo.R;
import com.onesignal.OneSignal;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pazpo.agent.fragments.RegisterBioFragment;
import id.pazpo.agent.fragments.RegisterPhotoFragment;
import id.pazpo.agent.helpers.FileUploadHelper;
import id.pazpo.agent.helpers.PermissionHelper;
import id.pazpo.agent.services.model.member.Member;
import id.pazpo.agent.services.model.member.MemberGet;
import id.pazpo.agent.services.model.member.NameCard;
import id.pazpo.agent.services.model.member.NameCardGet;
import id.pazpo.agent.services.model.message.Message;
import id.pazpo.agent.services.model.newsfeed.Newsfeed;
import id.pazpo.agent.third_module.facebook_accountkit.model.MainGraphFBAKModel;
import id.pazpo.agent.third_module.facebook_accountkit.model.SubGraphFBAKModel;
import id.pazpo.agent.third_module.facebook_accountkit.service.FacebookClient;
import id.pazpo.agent.third_module.facebook_accountkit.service.endpoint.FacebookEndpoint;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wais on 1/12/17.
 */

public class AuthenticationActivity extends BaseActivity {

    @BindView(R.id.cl_auth)
    CoordinatorLayout cl_auth;

    public static int FB_ACCOUNKIT_APP_REQUEST_CODE         = 99;
    public static final int UPLOAD_CARDNAME_REQUEST_CODE    = 111;
    public static final int MEDIA_TYPE_IMAGE                = 1;
    public static final String IMAGE_DIRECTORY_NAME         = "Pazpo";

    public Context mContext;
    public Map mRegisterData;

    protected FragmentManager mFragmentManager  = getSupportFragmentManager();

    protected RegisterBioFragment mRegisterBioFragment;
    protected RegisterPhotoFragment mRegisterPhotoFragment;
    protected MainGraphFBAKModel mMainGraphFBAKModel;
    protected SubGraphFBAKModel mSubGraphFBAKModel;

    protected Uri OutputFileUri;
    protected PermissionHelper mPermissionHelper;
    protected String mImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);

        mContext            = this;
        mPermissionHelper   = new PermissionHelper(this);
        mRegisterData       = new HashMap();

        hideKeyboard();

        if (mSharedPrefs.getMemberIsLogin()) {
            if (mPazpoApp.mOneSignalData == null) {
                loadActivityMain();
            } else {
                String notifUserAction = mPazpoApp.mOneSignalData.get(mConstant.ONESIGNAL_KEY_USERACTION).toString();

                if (notifUserAction.equalsIgnoreCase(mConstant.ONESIGNAL_VAL_USERACTION_MESSAGE)) {
                    loadActivityMessageDetail();
                } else if(notifUserAction.equalsIgnoreCase(mConstant.ONESIGNAL_VAL_USERACTION_NEWSFEED)) {
                    loadActivityMain();
                }
            }
        } else {
            loadFragmentRegisterBio();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment Fragment = mFragmentManager.findFragmentById(R.id.fr_authentication_container);
        if (Fragment instanceof RegisterPhotoFragment) {
            loadFragmentRegisterBio();
        } else if (Fragment instanceof RegisterBioFragment) {
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FB_ACCOUNKIT_APP_REQUEST_CODE) { // confirm that this response matches your request
            showBlockingLoading("Mencoba login ...");
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                hideLoading();
                showSnackBar(cl_auth, loginResult.getError().getErrorType().getMessage(), 0);
            } else if (loginResult.wasCancelled()) {
                hideLoading();
                showSnackBar(cl_auth, "Login dibatalkan", 0);
            } else {
                apiFBAccountKitGraphMe(loginResult.getAccessToken().getToken());
            }
        } else if (requestCode == UPLOAD_CARDNAME_REQUEST_CODE) {
            Log.e("[ fRegisterPhoto ]", "- onMethod = onActivityResult || requestCode = "+ requestCode);
            final Uri imageURL = getImageUri(data);
            if (imageURL != null) {
                Log.d("[ fRegisterPhoto ]", "- onMethod = onActivityResult || imageURL = "+ imageURL.toString());
                mRegisterPhotoFragment.setImage(imageURL);
                apiUploadCardName(mImagePath);
            } else {
                Toast.makeText(this, "Gagal mengambil foto, coba lagi.", Toast.LENGTH_SHORT).show();
                Log.d("[ fRegisterPhoto ]", "- onMethod = onActivityResult || imageURL = null");
            }
        }
    }

    protected void initOneSignal(final String memberUserID) {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                apiOneSignalSetPlayerID(userId, memberUserID);
            }
        });
    }

    public void loadActivityMain() {
        hideLoading();
        startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
        finish();
    }

    public void loadActivityMessageDetail() {
        Message message = new Message();
        message.ConversationID  = (String) mPazpoApp.mOneSignalData.get(mConstant.ONESIGNAL_KEY_MESSAGE_ID);
        message.UserID_One      = mSharedPrefs.getMemberLogin().UserID;
        message.UserOneName     = mSharedPrefs.getMemberLogin().FirstName;
        message.UserID_Two      = (String) mPazpoApp.mOneSignalData.get(mConstant.ONESIGNAL_KEY_MESSAGE_CLIENT_ID);
        message.UserTwoName     = (String) mPazpoApp.mOneSignalData.get(mConstant.ONESIGNAL_KEY_MESSAGE_CLIENT_NAME);

        String UserTwoImage     = (String) mPazpoApp.mOneSignalData.get(mConstant.ONESIGNAL_KEY_MESSAGE_CLIENT_IMAGE);
        boolean isFromNotif     = (boolean) mPazpoApp.mOneSignalData.get(mConstant.ONESIGNAL_KEY_IS_FROM_NOTIF);

        if (UserTwoImage.equalsIgnoreCase("null")) {
            message.UserTwoImage    = "";
        } else {
            message.UserTwoImage    = UserTwoImage;
        }

        Intent intent = new Intent(AuthenticationActivity.this, MessageDetailActivity.class);
        intent.putExtra(getString(R.string.intent_message_model), message);
        intent.putExtra(mConstant.ONESIGNAL_KEY_IS_FROM_NOTIF, isFromNotif);
        startActivity(intent);
        finish();
    }

    public void loadFragmentRegisterBio() {
        hideLoading();

        Bundle Bundle = new Bundle();
        Bundle.putString("fragment_container", "RegisterBio");

        mRegisterBioFragment = new RegisterBioFragment();
        mRegisterBioFragment.setArguments(Bundle);

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fr_authentication_container, mRegisterBioFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    public void loadFragmentRegisterPhoto() {
        hideLoading();

        Bundle Bundle = new Bundle();
        Bundle.putString("fragment_container", "RegisterPhoto");

        mRegisterPhotoFragment = new RegisterPhotoFragment();
        mRegisterPhotoFragment.setArguments(Bundle);

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.fr_authentication_container, mRegisterPhotoFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    public void onFBAccountKitLoginPhone(String phoneNumber) {
        final Intent intent = new Intent(this, AccountKitActivity.class);

        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN)
                        .setDefaultCountryCode("ID"); // or .ResponseType.TOKEN perform additional configuration ...

        if (phoneNumber != null) {
            com.facebook.accountkit.PhoneNumber initPhone = new PhoneNumber("ID", removeFirstChar(phoneNumber));
            configurationBuilder.setInitialPhoneNumber(initPhone);
        }

        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());

        startActivityForResult(intent, FB_ACCOUNKIT_APP_REQUEST_CODE);

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Account Kit"));
    }

    public void apiCreateAgentRegistration() {
        String pFullName        = (String) mRegisterData.get("pFullName");
        String pEmail           = (String) mRegisterData.get("pEmail");
        String pCompanyID       = (String) mRegisterData.get("pCompanyID");
        String pMobilePhone     = (String) mRegisterData.get("pMobilePhone");
        String pKTP             = "pKTP.jpg";
        String pNameCard        = (String) mRegisterData.get("pNameCard");
        String pCreatedBy       = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        showBlockingLoading("Mendaftarkan agent ...");

        Call<MemberGet> Call = mPazpoApp.mServiceHelper.createMember(pFullName, pEmail, pCompanyID, pMobilePhone, pKTP, pNameCard, pCreatedBy);
        Call.enqueue(new Callback<MemberGet>() {
            @Override
            public void onResponse(Call<MemberGet> call, Response<MemberGet> response) {
                if (response.isSuccessful()) {
                    MemberGet memberGet = new MemberGet();
                    memberGet           = response.body();

                    hideLoading();

                    if (memberGet.status.equalsIgnoreCase("Success")) {
                        Member member       = memberGet.data;
                        onFBAccountKitLoginPhone(member.MobilePhone);
                        Log.d("Retrofit:", "Success request api createMember. UserID = "+member.UserID);
                    } else {
                        if (memberGet.description != null) {
                            showSnackBar(cl_auth, memberGet.description, 0);
                        } else {
                            showSnackBar(cl_auth, "Gagal daftar akun.", 0);
                        }
                        loadFragmentRegisterBio();
                    }
                } else {
                    showSnackBar(cl_auth, "Gagal daftar akun.", 0);
                    loadFragmentRegisterBio();
                    Log.d("Retrofit:", "Failed request api createMember");
                }
            }

            @Override
            public void onFailure(Call<MemberGet> call, Throwable t) {
                Log.d("Retrofit:", "Failure request api createMember. error = "+ t.getMessage());
            }
        });
    }

    public void apiFBAccountKitGraphMe(String accessToken) {
        String baseURL                      = getString(R.string.url_fb_accountkit_graph);
        String typeAdapterTag               = getString(R.string.fb_tag_graph_accountkit_me);
        FacebookEndpoint facebookEndpoint   = new FacebookClient(baseURL, typeAdapterTag).createService(FacebookEndpoint.class);
        Call<MainGraphFBAKModel> Call       = facebookEndpoint.getGraphAccountKitMe(accessToken);

        Call.enqueue(new Callback<MainGraphFBAKModel>() {
            @Override
            public void onResponse(Call<MainGraphFBAKModel> call, Response<MainGraphFBAKModel> response) {
                if (response.isSuccessful()) {
                    mMainGraphFBAKModel = response.body();
                    mSubGraphFBAKModel  = mMainGraphFBAKModel.phone;
                    apiLoginPhone("0"+mSubGraphFBAKModel.national_number);
                    Log.d("Retrofit:", "Success request api fb AccountKitMe. National Number = "+ mSubGraphFBAKModel.national_number);
                } else {
                    showSnackBar(cl_auth, "Gagal verifikasi nomor.", 0);
                    loadFragmentRegisterBio();
                    Log.d("Retrofit:", "Failed request api fb AccountKitMe");
                }
            }

            @Override
            public void onFailure(Call<MainGraphFBAKModel> call, Throwable t) {
                showSnackBar(cl_auth, "Gagal verifikasi nomor.", 0);
                loadFragmentRegisterBio();
                Log.d("Retrofit:", "Failure request api fb AccountKitMe. error = "+ t.getMessage());
            }
        });
    }

    public void apiLoginPhone(String pMobilePhone) {
        Call<MemberGet> CallLoginPhone = mPazpoApp.mServiceHelper.getLoginPhone(pMobilePhone);
        CallLoginPhone.enqueue(new Callback<MemberGet>() {
            @Override
            public void onResponse(Call<MemberGet> call, Response<MemberGet> response) {
                if (response.isSuccessful()) {
                    MemberGet memberGet = new MemberGet();
                    memberGet           = response.body();

                    if (memberGet.status.equalsIgnoreCase("Success")) {
                        Member member = memberGet.data;

                        initOneSignal(member.UserID);
                        apiGetUserMember(member.UserID);
                        Log.d("Retrofit:", "Success request api phone login. UserID = "+ member.UserID);
                    } else {
                        if (memberGet.description != null) {
                            showSnackBar(cl_auth, memberGet.description, 0);
                        } else {
                            showSnackBar(cl_auth, "Gagal login akun.", 0);
                        }
                        loadFragmentRegisterBio();
                    }
                } else {
                    showSnackBar(cl_auth, "Gagal login akun.", 0);
                    loadFragmentRegisterBio();
                    Log.d("Retrofit:", "Failed request api phone login");
                }
            }

            @Override
            public void onFailure(Call<MemberGet> call, Throwable t) {
                showSnackBar(cl_auth, "Gagal login akun.", 0);
                loadFragmentRegisterBio();
                Log.d("Retrofit:", "Failure request api phone login. error = "+ t.getMessage());
            }
        });
    }

    public void apiUploadCardName(String imagePath) {
        File newImageFile;

        String origImage        = imagePath.toString();
        File origImageFile      = new File(origImage);
        long origImageSize      = origImageFile.length() / 350;

        if (origImageSize > 350) {
            newImageFile = new File(FileUploadHelper.compressImage(origImage, this.getBaseContext()));
        } else {
            newImageFile   = origImageFile;
        }

        RequestBody requestFile     = RequestBody.create(MediaType.parse("multipart/form-data"), newImageFile);
        MultipartBody.Part body     = MultipartBody.Part.createFormData("image", newImageFile.getName(), requestFile);

        showBlockingLoading("Uploading photo ...");

        Call<NameCardGet> Call = mPazpoApp.mServiceHelper.uploadNameCard(body);
        Call.enqueue(new Callback<NameCardGet>() {
            @Override
            public void onResponse(Call<NameCardGet> call, Response<NameCardGet> response) {
                if (response.isSuccessful()) {
                    hideLoading();
                    NameCardGet nameCardGet = response.body();
                    if (nameCardGet.status.equalsIgnoreCase("Success")) {
                        NameCard nameCard       = nameCardGet.data;
                        Log.d("Retrofit:", "Success request api uploadNameCard. NameCard.Filename = "+ nameCard.filename);

                        mRegisterData.put("pNameCard", nameCard.filename);
                        apiCreateAgentRegistration();
                    } else {
                        if (nameCardGet.description != null) {
                            showSnackBar(cl_auth, nameCardGet.description, 0);
                        } else {
                            showSnackBar(cl_auth, "Gagal upload kartu nama.", 0);
                            loadFragmentRegisterBio();
                        }
                    }
                } else {
                    showSnackBar(cl_auth, "Gagal upload kartu nama.", 0);
                    loadFragmentRegisterBio();
                    Log.d("Retrofit:", "Failed request api uploadNameCard");
                }
            }

            @Override
            public void onFailure(Call<NameCardGet> call, Throwable t) {
                showSnackBar(cl_auth, "Gagal upload kartu nama.", 0);
                loadFragmentRegisterBio();
                Log.d("Retrofit:", "Failure request api uploadNameCard. error = "+t.getMessage());
            }
        });
    }

    public void apiGetUserMember(String pEmail) {
        Call<MemberGet> Call = mPazpoApp.mServiceHelper.getUserMember(pEmail);
        Call.enqueue(new Callback<MemberGet>() {
            @Override
            public void onResponse(Call<MemberGet> call, Response<MemberGet> response) {
                if (response.isSuccessful()) {
                    MemberGet memberGet = new MemberGet();
                    memberGet           = response.body();

                    if (memberGet.status.equalsIgnoreCase("Success")) {
                        Member member       = memberGet.data;

                        mSharedPrefs.setMemberLogin(member);
                        mSharedPrefs.setMemberIsLogin(true);

                        loadActivityMain();
                        Log.d("Retrofit:", "Success request api getUserMember. UserID = "+member.UserID);
                    } else {
                        showSnackBar(cl_auth, "Gagal login aplikasi.", 0);
                        loadFragmentRegisterBio();
                    }
                } else {
                    showSnackBar(cl_auth, "Gagal login aplikasi.", 0);
                    loadFragmentRegisterBio();
                    Log.d("Retrofit:", "Failed request api getUserMember");
                }
            }

            @Override
            public void onFailure(Call<MemberGet> call, Throwable t) {
                showSnackBar(cl_auth, "Gagal login aplikasi.", 0);
                loadFragmentRegisterBio();
                Log.d("Retrofit:", "Failure request api phone login. error = "+ t.getMessage());
            }
        });
    }

    public void apiOneSignalSetPlayerID(String pPlayerID, String pUserID) {
        Call<MemberGet> Call = mPazpoApp.mServiceHelper.setOneSignalPlayerID(pPlayerID, pUserID);
        Call.enqueue(new Callback<MemberGet>() {
            @Override
            public void onResponse(Call<MemberGet> call, Response<MemberGet> response) {
                if (response.isSuccessful()) {
                    MemberGet memberGet = new MemberGet();
                    memberGet           = response.body();

                    if (memberGet.status.equalsIgnoreCase("Success")) {
                        Member member       = memberGet.data;
                        mSharedPrefs.setOneSignalPlayerID(member.PlayerID);
                        Log.d("Retrofit:", "Success request api phone login. PlayerID = "+ member.PlayerID);
                    } else {
                        Log.d("Retrofit:", "Failed request api setOneSignalPlayerID");
                    }
                } else {
                    Log.d("Retrofit:", "Failed request api setOneSignalPlayerID");
                }
            }

            @Override
            public void onFailure(Call<MemberGet> call, Throwable t) {
                Log.d("Retrofit:", "Failure request api setOneSignalPlayerID. error = "+ t.getMessage());
            }
        });
    }

    public void getImageByCamera(int intentCode) {
        if (!mPermissionHelper.checkPermissionForCamera()) {
            mPermissionHelper.requestPermissionForCamera();
        } else {
            if (!mPermissionHelper.checkPermissionForExternalStorage()) {
                mPermissionHelper.requestPermissionForExternalStorage();
            } else {
//                final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "pazpo/photo_doc" + File.separator);
//                root.mkdirs();
//                final String fname              = System.currentTimeMillis() + ".jpg";
//                final File sdImageMainDirectory = new File(root, fname);
//
//                OutputFileUri = Uri.fromFile(sdImageMainDirectory);

                OutputFileUri = FileUploadHelper.getOutputMediaFileUri(1);

                // Camera.
                final List<Intent> cameraIntents    = new ArrayList<>();
                final Intent captureIntent          = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                final PackageManager packageManager = getPackageManager();
                final List<ResolveInfo> listCam     = packageManager.queryIntentActivities(captureIntent, 0);

                for (ResolveInfo res : listCam) {
                    final String packageName    = res.activityInfo.packageName;
                    final Intent intent         = new Intent(captureIntent);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, OutputFileUri);
                    cameraIntents.add(intent);
                }

                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");

                if (Build.VERSION.SDK_INT < 19) {
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    galleryIntent.setAction(Intent.ACTION_PICK);
                }

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
                super.startActivityForResult(chooserIntent, intentCode);
            }
        }
    }

    protected Uri getImageUri(Intent intent) {
        Uri selectedImageUri = null;
        final boolean isCamera;

        if (intent == null) {
            isCamera = true;
        } else {
            final String action = intent.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        try {
            if (isCamera) {
                selectedImageUri    = OutputFileUri;
                mImagePath          = selectedImageUri.getPath();
            } else {
                selectedImageUri    = intent.getData();
                mImagePath          = getRealPathFromURL(selectedImageUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectedImageUri;
    }

    protected String getRealPathFromURL(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        @SuppressWarnings("deprecation")
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else {
            return uri.getPath();
        }
    }

    protected String removeFirstChar(String s){
        return s.substring(1);
    }

}
