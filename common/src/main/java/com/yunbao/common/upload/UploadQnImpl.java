package com.yunbao.common.upload;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DecryptUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * Created by cxf on 2019/4/16.
 * 七牛上传文件
 */

public class UploadQnImpl implements UploadStrategy {

    private static final String TAG = "UploadQnImpl";
    private Context mContext;
    private List<UploadBean> mList;
    private int mIndex;
    private boolean mNeedCompress;
    private UploadCallback mUploadCallback;
    private HttpCallback mGetUploadTokenCallback;
    private String mUploadToken;
    private UploadManager mUploadManager;
    private UpCompletionHandler mCompletionHandler;//上传回调
    private Luban.Builder mLubanBuilder;

    private String mQiNiuHost;
    private Configuration mConfiguration;

    public UploadQnImpl(Context context, UploadInfoBean bean) {
        mContext = context;
        UploadInfoBean.QiniuInfoBean qiNiuInfoBean = bean.getQNInfo();
        mQiNiuHost = qiNiuInfoBean.getQNDomain();
        mUploadToken = DecryptUtil.decrypt(qiNiuInfoBean.getQNToken());
        Zone zone = FixedZone.zone0;//默认华东
        String qiNiuZone = qiNiuInfoBean.getQNZone();
        if (UploadInfoBean.UPLOAD_QI_NIU_HB.equals(qiNiuZone)) {
            zone = FixedZone.zone1;
        } else if (UploadInfoBean.UPLOAD_QI_NIU_HN.equals(qiNiuZone)) {
            zone = FixedZone.zone2;
        } else if (UploadInfoBean.UPLOAD_QI_NIU_BM.equals(qiNiuZone)) {
            zone = FixedZone.zoneNa0;
        } else if (UploadInfoBean.UPLOAD_QI_NIU_XJP.equals(qiNiuZone)) {
            zone = FixedZone.zoneAs0;
        }
        mConfiguration = new Configuration.Builder().zone(zone).build();
        mCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                UploadBean uploadBean = mList.get(mIndex);
                uploadBean.setSuccess(true);
                if (mNeedCompress) {
                    //上传完成后把 压缩后的图片 删掉
                    File compressedFile = uploadBean.getOriginFile();
                    if (compressedFile != null && compressedFile.exists()) {
                        compressedFile.delete();
                    }
                    //String filePath=SiliCompressor.with(this).compressVideo(videoPath, destinationDirectory);

                }
                mIndex++;
                if (mIndex < mList.size()) {
                    uploadNext();
                } else {
                    if (mUploadCallback != null) {
                        mUploadCallback.onFinish(mList, true);
                    }
                }
            }
        };
    }

    @Override
    public void upload(List<UploadBean> list, boolean needCompress, UploadCallback callback) {
        if (callback == null) {
            return;
        }
        if (list == null || list.size() == 0) {
            callback.onFinish(list, false);
            return;
        }
        boolean hasFile = false;
        for (UploadBean bean : list) {
            if (bean.getOriginFile() != null) {
                hasFile = true;
                break;
            }
        }
        if (!hasFile) {
            callback.onFinish(list, true);
            return;
        }
        mList = list;
        mNeedCompress = needCompress;
        mUploadCallback = callback;
        mIndex = 0;
        uploadNext();
    }

    @Override
    public void cancelUpload() {
    }

    private void uploadNext() {
        UploadBean bean = null;
        while (mIndex < mList.size() && (bean = mList.get(mIndex)).getOriginFile() == null) {
            mIndex++;
        }
        if (mIndex >= mList.size() || bean == null) {
            if (mUploadCallback != null) {
                mUploadCallback.onFinish(mList, true);
            }
            return;
        }
        if (bean.getOriginFile().getName().endsWith(".mp4") && TextUtils.isEmpty(bean.getRemoteFileName())){
            bean.setRemoteFileName(StringUtil.generateFileName()+".mp4");
        }else if (bean.getOriginFile().getName().endsWith(".jpg") && TextUtils.isEmpty(bean.getRemoteFileName())){
            bean.setRemoteFileName(StringUtil.generateFileName() + ".jpg");
        }else if (bean.getOriginFile().getName().endsWith(".png") && TextUtils.isEmpty(bean.getRemoteFileName())){
            bean.setRemoteFileName(StringUtil.generateFileName() + ".png");
        }else {
            bean.setRemoteFileName(StringUtil.generateFileName());
        }
        if (mNeedCompress) {
            if (mLubanBuilder == null) {
                mLubanBuilder = Luban.with(mContext)
                        .ignoreBy(8)//8k以下不压缩
                        .setTargetDir(CommonAppConfig.CAMERA_IMAGE_PATH)
                        .setRenameListener(new OnRenameListener() {
                            @Override
                            public String rename(String filePath) {
                                return mList.get(mIndex).getRemoteFileName();
                            }
                        }).setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                UploadBean uploadBean = mList.get(mIndex);
                                uploadBean.setOriginFile(file);
                                upload(uploadBean);
                            }

                            @Override
                            public void onError(Throwable e) {
                                upload(mList.get(mIndex));
                            }
                        });
            }
            mLubanBuilder.load(bean.getOriginFile()).launch();
        } else {
            upload(bean);
        }
    }



    private void upload(UploadBean bean) {
        if (bean != null && !TextUtils.isEmpty(mUploadToken) && mCompletionHandler != null) {
            if (mUploadManager == null) {
                mUploadManager = new UploadManager(mConfiguration);
            }
            mUploadManager.put(bean.getOriginFile(), bean.getRemoteFileName(), mUploadToken, mCompletionHandler, null);
        } else {
            if (mUploadCallback != null) {
                mUploadCallback.onFinish(mList, false);
            }
        }
    }
}
