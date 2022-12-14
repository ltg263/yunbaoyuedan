package com.yunbao.common.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;
import com.yunbao.common.interfaces.ActivityResultCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;

import java.io.File;
import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 * 选择图片 裁剪
 */

public class ProcessImageUtil extends ProcessResultUtil {
    private static final String FILE_PROVIDER = "com.yunbao.shortvideo.fileprovider";
    private Context mContext;
    private String[] mCameraPermissions;
    private String[] mAlumbPermissions;
    private CommonCallback<Boolean> mCameraPermissionCallback;
    private CommonCallback<Boolean> mAlumbPermissionCallback;
    private ActivityResultCallback mCameraResultCallback;
    private ActivityResultCallback mAlumbResultCallback;
    private ActivityResultCallback mCropResultCallback;
    private File mCameraResult;//拍照后得到的图片
    private File mCorpResult;//裁剪后得到的图片
    private ImageResultCallback mResultCallback;
    private boolean mNeedCrop;//是否需要裁剪

    public ProcessImageUtil(FragmentActivity activity) {
        super(activity);
        mContext = activity;
        mCameraPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        mAlumbPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        mCameraPermissionCallback = new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    takePhoto();
                }
            }
        };
        mAlumbPermissionCallback = new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    chooseFile();
                }
            }
        };
        mCameraResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (mNeedCrop) {
                    Uri uri = null;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri = FileProvider.getUriForFile(mContext, FILE_PROVIDER, mCameraResult);
                    } else {
                        uri = Uri.fromFile(mCameraResult);
                    }
                    if (uri != null) {
                        crop(uri);
                    }
                } else {
                    if (mResultCallback != null) {
                        mResultCallback.onSuccess(mCameraResult);
                    }
                }
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.img_camera_cancel);
            }
        };
        mAlumbResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (mNeedCrop) {
                    crop(intent.getData());
                } else {
                    String path = FileUtils.getPath(mContext, intent.getData());
                    if (!TextUtils.isEmpty(path) && mResultCallback != null) {
                        mResultCallback.onSuccess(new File(path));
                    }
                }
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.img_alumb_cancel);
            }
        };
        mCropResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (mResultCallback != null) {
                    mResultCallback.onSuccess(mCorpResult);
                }
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.img_crop_cancel);
            }
        };
    }

    /**
     * 拍照获取图片
     */
    public void getImageByCamera(boolean needCrop) {
        mNeedCrop = needCrop;
        requestPermissions(mCameraPermissions, mCameraPermissionCallback);
    }

    /**
     * 拍照获取图片
     */
    public void getImageByCamera() {
        getImageByCamera(true);
    }

    /**
     * 相册获取图片
     */
    public void getImageByAlumb(boolean needCrop) {
        mNeedCrop = needCrop;
        requestPermissions(mAlumbPermissions, mAlumbPermissionCallback);
    }

    /**
     * 相册获取图片
     */
    public void getImageByAlumb() {
        getImageByAlumb(true);
    }


    /**
     * 开启摄像头，执行照相
     */
    private void takePhoto() {
        if (mResultCallback != null) {
            mResultCallback.beforeCamera();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCameraResult = getNewFile();
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(mContext, FILE_PROVIDER, mCameraResult);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(mCameraResult);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, mCameraResultCallback);
    }

    private File getNewFile() {
        // 裁剪头像的绝对路径
        File dir = new File(CommonAppConfig.CAMERA_IMAGE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, DateFormatUtil.getCurTimeString() + ".png");
    }


    /**
     * 打开相册，选择文件
     */
    private void chooseFile() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else if(isHaveCame(Intent.ACTION_OPEN_DOCUMENT)){
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }else{
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        startActivityForResult(intent, mAlumbResultCallback);
    }


    public static boolean isHaveCame(String intentName) {
        PackageManager packageManager = CommonAppContext.sInstance.getPackageManager();
        Intent intent = new Intent(intentName);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isHaveCame=list.size() > 0;
        if(isHaveCame){
            ToastUtil.show("包含ACTION_OPEN_DOCUMENT权限");
        }
        return isHaveCame;
    }
    /**
     * 裁剪图片
     */

    public void cropImage(File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(mContext, FILE_PROVIDER, file);
        } else {
            uri = Uri.fromFile(file);
        }
        if (uri != null) {
            crop(uri);
        }
    }


    /**
     * 裁剪
     */

    private void crop(Uri inputUri) {
        mCorpResult = getNewFile();
        try {
            Uri resultUri = Uri.fromFile(mCorpResult);
            if (resultUri == null || mFragment == null || mContext == null) {
                return;
            }
            int globalColor = 0xff4998F7;
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(globalColor);
            options.setStatusBarColor(globalColor);
            options.setActiveWidgetColor(globalColor);
            UCrop uCrop = UCrop.of(inputUri, resultUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(400, 400)
                    .withOptions(options);
            Intent intent = uCrop.getIntent(mContext);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, mCropResultCallback);
        } catch (Exception e) {
            try {
                Uri resultUri = FileProvider.getUriForFile(mContext, FILE_PROVIDER, mCorpResult);
                if (resultUri == null || mFragment == null || mContext == null) {
                    return;
                }
                UCrop uCrop = UCrop.of(inputUri, resultUri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(400, 400);
                Intent intent = uCrop.getIntent(mContext);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, mCropResultCallback);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    public void setImageResultCallback(ImageResultCallback resultCallback) {
        mResultCallback = resultCallback;
    }

    @Override
    public void release() {
        super.release();
        mResultCallback = null;
    }
}
