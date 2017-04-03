package id.pazpo.agent.fragments;

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
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.flipbox.pazpo.R;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.pazpo.agent.activities.SettingActivity;
import id.pazpo.agent.helpers.FileUploadHelper;
import id.pazpo.agent.helpers.PermissionHelper;
import id.pazpo.agent.interfaces.BaseMethodInterface;
import id.pazpo.agent.services.model.member.Member;
import id.pazpo.agent.services.model.member.MemberUpdate;
import id.pazpo.agent.services.model.member.ProfileImage;
import id.pazpo.agent.services.model.member.ProfileImageGet;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wais on 2/20/17.
 */

public class ProfileEditFragment extends BaseFragment
        implements BaseMethodInterface
{
    @BindView(R.id.civ_profile_edit)
    CircleImageView civ_profile_edit;
    @BindView(R.id.et_profile_edit)
    EditText et_profile_edit;

    private Context mContext;
    private SettingActivity mSettingActivity;
    private FragmentManager mFragmentManager;

    private Uri OutputFileUri;
    private PermissionHelper mPermissionHelper;

    private String pMemberID;

    public static int INTENT_CODE_UPLOAD_PROFILE_IMAGE  = 110;
    public String mProfileImagePath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Container = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        ButterKnife.bind(this, Container);

        initData(savedInstanceState);
        initUI();

        Analytics.with(mContext).track("Page View", new Properties()
                .putValue("Type", "Fragment")
                .putValue("Page", "Edit Profile"));

        return Container;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mContext            = getContext();
        mSettingActivity    = (SettingActivity) getActivity();
        mFragmentManager    = mSettingActivity.getSupportFragmentManager();
        mPermissionHelper   = new PermissionHelper(mSettingActivity);
        pMemberID           = mSharedPrefs.getMemberLogin().MemberID;
    }

    @Override
    public void initUI() {
        String userFullName = mSharedPrefs.getMemberLogin().FirstName;

        //========== Form Profile ==========//
        String imgProfilePath, imgProfileName, imgProfileURL;
        if (mSharedPrefs.getMemberLogin().UserImageURL == null) {
            imgProfilePath   = mPazpoApp.mServiceHelper.IMG_PROFILE_UPLOAD_PATH;
            imgProfileName   = mSharedPrefs.getMemberLogin().UserImage;
            imgProfileURL    = imgProfilePath + imgProfileName;
        } else {
            imgProfileURL    = mSharedPrefs.getMemberLogin().UserImageURL;
        }

        Picasso.with(getContext())
                .load(imgProfileURL)
                .placeholder(R.drawable.ic_account_circle)
                .into(civ_profile_edit);

        et_profile_edit.setText(userFullName);
    }

    @OnClick(R.id.et_profile_edit)
    public void clickEditName() {
        Analytics.with(getContext()).track("Click", new Properties()
                .putValue("Type", "Name EditText")
                .putValue("Widget", "EditText")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Edit Profile"));
    }

    @OnClick(R.id.civ_profile_edit)
    public void onClickCivUploadImage() {
        Analytics.with(mContext).track("Click", new Properties()
                .putValue("Type", "Profile Photo")
                .putValue("Widget", "Circle Image View")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Edit Profile"));
        getChooserIntentProfileImage(INTENT_CODE_UPLOAD_PROFILE_IMAGE);
    }

    @OnClick(R.id.tv_profile_edit_pic)
    public void onClickTvUploadImage() {
        Analytics.with(getContext()).track("Click", new Properties()
                .putValue("Type", "Photo Edit Button")
                .putValue("Widget", "Button")
                .putValue("Page Type", "Fragment")
                .putValue("Page", "Edit Profile"));
        getChooserIntentProfileImage(INTENT_CODE_UPLOAD_PROFILE_IMAGE);
    }

    @OnClick(R.id.btn_profile_edit_save)
    public void onClickBtnEditSave() {
        String pEmail       = mSharedPrefs.getMemberLogin().UserID;
        String pFullName    = et_profile_edit.getText().toString();

        Analytics.with(getContext()).track("Click", new Properties()
                .putValue("Type", "Profile Save Button")
                .putValue("Widget", "Button")
                .putValue("Page Type", "Fragment")
                .putValue("Fullname", pFullName)
                .putValue("Page", "Edit Profile"));

        apiUpdateProfileData(pEmail, pFullName);
    }

    public void apiUpdateProfileImage(String imagePath) {
        File newImageFile;

        String origImage        = imagePath.toString();
        File origImageFile      = new File(origImage);
        long origImageSize      = origImageFile.length() / 350;

        if (origImageSize > 350) {
            newImageFile = new File(FileUploadHelper.compressImage(origImage, mSettingActivity.getBaseContext()));
        } else {
            newImageFile   = origImageFile;
        }

        RequestBody requestFile     = RequestBody.create(MediaType.parse("multipart/form-data"), newImageFile);
        MultipartBody.Part body     = MultipartBody.Part.createFormData("image", newImageFile.getName(), requestFile);

        RequestBody memberId        = RequestBody.create(MediaType.parse("text/plain"), pMemberID);

        showBlockingLoading("Uploading photo ...");

        Call<ProfileImageGet> Call = mPazpoApp.mServiceHelper.uploadProfileImage(body, memberId);
        Call.enqueue(new Callback<ProfileImageGet>() {
            @Override
            public void onResponse(Call<ProfileImageGet> call, Response<ProfileImageGet> response) {
                if (response.isSuccessful()) {
                    ProfileImageGet profileImageGet = response.body();

                    if (profileImageGet.status.equalsIgnoreCase("Success")) {
                        ProfileImage profileImage   = profileImageGet.data;
                        Member member               = mSharedPrefs.getMemberLogin();
                        member.UserImageURL         = profileImage.url;

                        mSharedPrefs.setMemberLogin(member);

                        mSettingActivity.onShowSnackbar("Berhasil upload foto profile.", 1);
                    } else {
                        mSettingActivity.onShowSnackbar("Gagal upload foto profile.", 0);
                    }

                } else {
                    mSettingActivity.onShowSnackbar("Gagal upload foto profile.", 0);
                    Log.d("[ fProfileEdit ]", "Failed request api upload profile image");
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<ProfileImageGet> call, Throwable t) {
                hideLoading();
                mSettingActivity.onShowSnackbar("Gagal proses upload foto profile.", 0);
                Log.d("[ fProfileEdit ]", "Failure request api upload profile image. error = "+t.getMessage());
            }
        });
    }

    private void apiUpdateProfileData(String pEmail, final String pFullName) {
        Call<MemberUpdate> Call = mPazpoApp.mServiceHelper.updateProfileName(pEmail, pFullName);
        Call.enqueue(new Callback<MemberUpdate>() {
            @Override
            public void onResponse(Call<MemberUpdate> call, Response<MemberUpdate> response) {
                if (response.isSuccessful()) {
                    MemberUpdate memberUpdate   = new MemberUpdate();
                    memberUpdate                = response.body();

                    if (memberUpdate.status.equalsIgnoreCase("Success")) {
                        Member member       = mSharedPrefs.getMemberLogin();
                        member.FirstName    = pFullName;

                        mSharedPrefs.setMemberLogin(member);

                        mSettingActivity.onShowSnackbar("Berhasil mengubah data profil.", 3);
                        Log.d("Retrofit:", "Success request api updateProfileName. UserID = "+member.UserID);
                    } else {
                        mSettingActivity.onShowSnackbar("Gagal mengubah data profil.", 0);
                    }
                } else {
                    mSettingActivity.onShowSnackbar("Gagal mengubah data profil.", 0);
                    Log.d("Retrofit:", "Failed request api getUserMember");
                }
            }

            @Override
            public void onFailure(Call<MemberUpdate> call, Throwable t) {
                mSettingActivity.onShowSnackbar("Gagal mengirim data profil.", 0);
                Log.d("Retrofit:", "Failure request api phone login. error = "+ t.getMessage());
            }
        });
    }

    public void setProfileImage(Uri uri) {
        civ_profile_edit.setImageURI(uri);
    }

    public Uri getImageUri(Intent intent) {
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
                mProfileImagePath   = selectedImageUri.getPath();
            } else {
                selectedImageUri    = intent.getData();
                mProfileImagePath   = getRealPathFromURL(selectedImageUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectedImageUri;
    }

    private void getChooserIntentProfileImage(int intentCode) {
        if (!mPermissionHelper.checkPermissionForCamera()) {
            mPermissionHelper.requestPermissionForCamera();
        } else {
            if (!mPermissionHelper.checkPermissionForExternalStorage()) {
                mPermissionHelper.requestPermissionForExternalStorage();
            } else {
//                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "pazpo/photo_pic" + File.separator);
//                root.mkdirs();
//                final String fname              = System.currentTimeMillis() + ".jpg";
//                final File sdImageMainDirectory = new File(root, fname);
//
//                OutputFileUri = Uri.fromFile(sdImageMainDirectory);

                OutputFileUri = FileUploadHelper.getOutputMediaFileUri(1);

                // Camera.
                final List<Intent> cameraIntents    = new ArrayList<>();
                final Intent captureIntent          = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                final PackageManager packageManager = mContext.getPackageManager();
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

                Log.e("[ fProfileEdit ]", "- onMethod : getImageByCamera || intentCode = "+intentCode+" || OutputFileUri = "+OutputFileUri);

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
                mSettingActivity.startActivityForResult(chooserIntent, intentCode);
            }
        }
    }

    private String getRealPathFromURL(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        @SuppressWarnings("deprecation")
        Cursor cursor = mSettingActivity.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else {
            return uri.getPath();
        }
    }
}
