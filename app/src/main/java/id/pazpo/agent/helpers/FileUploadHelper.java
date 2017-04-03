package id.pazpo.agent.helpers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wais on 1/18/17.
 */

public class FileUploadHelper {

    public static final int PICK_FROM_CAMERA    = 1;
    public static final int MEDIA_TYPE_IMAGE    = 1;
    public static final int MEDIA_TYPE_VIDEO    = 2;

    PermissionHelper mPermissionHelper;

    private Context mContext;

    public FileUploadHelper(Context mContext) {
        this.mContext = mContext;
    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Pazpo");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Pazpo", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void getUriPhotoFromCamera(Activity activity) {
        mPermissionHelper = new PermissionHelper(activity);

        if (!mPermissionHelper.checkPermissionForCamera()) {
            mPermissionHelper.requestPermissionForCamera();
        } else {
            if (!mPermissionHelper.checkPermissionForExternalStorage()) {
                Log.e("[ uFileUpload ]","- onMethod : getUriPhotoFromCamera || checkPermissionForExternalStorage : true");
                mPermissionHelper.requestPermissionForExternalStorage();
            } else {
                Log.e("[ uFileUpload ]","- onMethod : getUriPhotoFromCamera || checkPermissionForExternalStorage : false");
                File mediaStorageDir = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator
                                + "pazpo/camera"
                                + File.separator
                );

                if (!mediaStorageDir.exists()) {
                    mediaStorageDir.mkdirs();
                }

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File mediaFile;

                try {
                    mediaFile = File.createTempFile(
                            "PazpoIMG_" + timeStamp,  /* prefix */
                            ".jpg",         /* suffix */
                            mediaStorageDir      /* directory */
                    );

                    // Camera.
                    final List<Intent> cameraIntents    = new ArrayList<>();
                    final Intent captureIntent          = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    final PackageManager packageManager = activity.getPackageManager();
                    final List<ResolveInfo> listCam     = packageManager.queryIntentActivities(captureIntent, 0);

                    for (ResolveInfo res : listCam) {
                        final String packageName    = res.activityInfo.packageName;
                        final Intent intent         = new Intent(captureIntent);
                        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                        intent.setPackage(packageName);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
                        cameraIntents.add(intent);
                    }

//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));

                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");

                    if (Build.VERSION.SDK_INT < 19) {
                        galleryIntent = new Intent().setAction(Intent.ACTION_GET_CONTENT);
                    } else {
                        galleryIntent = new Intent().setAction(Intent.ACTION_PICK);
                    }

                    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source"); // Chooser of filesystem options.
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()])); // Add the camera options.

                    activity.startActivityForResult(chooserIntent, PICK_FROM_CAMERA);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images/Pazpo");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriString = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriString;
    }

    public static String getRealPathFromURI(String contentURI, Context ctx) {
        Uri contentUri  = Uri.parse(contentURI);

        @SuppressWarnings("deprecation")
        Cursor cursor   = ctx.getContentResolver().query(contentUri, null, null, null, null);

        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static String compressImage(String imageUri, Context ctx) {
        String filePath = getRealPathFromURI(imageUri, ctx);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

//        float maxHeight = 768.0f;
//        float maxWidth  = 1024.0f;
        float maxHeight = 350.0f;
        float maxWidth  = 350.0f;
        float imgRatio  = actualWidth / actualHeight;
        float maxRatio  = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;

        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height    = options.outHeight;
        final int width     = options.outWidth;
        int inSampleSize    = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static boolean isDeviceSuppottCamera(Context context) {
        if (context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
}
