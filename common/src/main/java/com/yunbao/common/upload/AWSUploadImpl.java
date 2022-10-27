package com.yunbao.common.upload;


import android.content.Context;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import java.io.File;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * Created by Sky.L on 2020-12-22
 */
public class AWSUploadImpl implements UploadStrategy {
    private static final String TAG = "AWSUploadImpl";
    private Context mContext;
    private List<UploadBean> mList;
    private int mIndex;
    private boolean mNeedCompress;
    private Luban.Builder mLubanBuilder;
    private UploadCallback mUploadCallback;
    private boolean mCompressSuccess;//压缩成功
    private AWSTransferUtil mAwsTransferUtil;
    private TransferUtility mTransferUtility;
    private UploadInfoBean mUploadInfoBean;


    public AWSUploadImpl(Context context,UploadInfoBean infoBean) {
        mContext = context;
        mUploadInfoBean = infoBean;
        mAwsTransferUtil = new AWSTransferUtil();
        mTransferUtility = mAwsTransferUtil.getTransferUtility(mContext,infoBean.getAWSInfo().getAWSRegion(),infoBean.getAWSInfo().getAWSPoolId());
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
        if (mUploadCallback != null) {
            mUploadCallback.onFinish(null, false);
        }
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
                        .ignoreBy(1)//8k以下不压缩
                        .setTargetDir(CommonAppConfig.CAMERA_IMAGE_PATH)
                        .setRenameListener(new OnRenameListener() {
                            @Override
                            public String rename(String filePath) {
                                L.e(TAG,"rename--->"+mList.get(mIndex).getRemoteFileName()+"---"+filePath);
                                return mList.get(mIndex).getRemoteFileName();
                            }
                        }).setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                                mCompressSuccess = true;
                            }

                            @Override
                            public void onSuccess(File file) {
                                mCompressSuccess = true;
                                L.e(TAG,"setOriginFile--->"+file.getName());
                                UploadBean uploadBean = mList.get(mIndex);
                                uploadBean.setOriginFile(file);
                                beginUpload(uploadBean);
                            }

                            @Override
                            public void onError(Throwable e) {
                                L.e(TAG,"compress_error");
                                mCompressSuccess = false;
                                beginUpload(mList.get(mIndex));
                            }
                        });
            }
            mLubanBuilder.load(bean.getOriginFile()).launch();
        } else {
            beginUpload(bean);
        }
    }

    private void beginUpload(final UploadBean bean) {
        if (bean == null) {
            if (mUploadCallback != null) {
                mUploadCallback.onFinish(mList, false);
            }
            ToastUtil.show(WordUtil.getString(R.string.upload_failure));
            return;
        }
        try {
            final TransferObserver observer =
                    mTransferUtility.upload(
                            mUploadInfoBean.getAWSInfo().getAWSBucket(),
                            bean.getRemoteFileName(),
                            bean.getOriginFile());
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    //这个函数会调用多次,根据不同状态来通知调用者
                    L.e(TAG,"--onStateChanged---state--"+state);
                    if(TransferState.COMPLETED==state){
                        L.e(TAG,"COMPLETED---->num"+(mIndex+1)+"---"+bean.getOriginFile().getAbsolutePath());
                        if (mNeedCompress && mCompressSuccess) {
                            //上传完成后把 压缩后的图片 删掉
                            File compressedFile = bean.getOriginFile();
                            if (compressedFile != null && compressedFile.exists()) {
                                L.e(TAG,"compressedFile---delete");
                                compressedFile.delete();
                            }
                        }
                        mIndex++;
                        if (mIndex < mList.size()) {
                            uploadNext();
                        } else {
                            if (mUploadCallback != null) {
                                mUploadCallback.onFinish(mList, true);
                            }
                        }
                    }else if (TransferState.FAILED == state){
                        if (mUploadCallback != null) {
                            mUploadCallback.onFinish(mList, false);
                        }
                    }
                    else{
                        //注意:当TransferState.COMPLETED!=state,并不意味着这里上传失败
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    //这个函数会调用多次,根据不同进度来通知调用者
                    L.e(TAG,"----onProgressChanged--bytesCurrent--"+bytesCurrent+"--bytesTotal--"+bytesTotal);
                    if(bytesCurrent>=bytesTotal){
                        //这里代码依然会调用多次
                    }
                }

                @Override
                public void onError(int id, Exception ex) {
                    L.e(TAG,"----onError----");
                    ToastUtil.show(WordUtil.getString(R.string.upload_failure));
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            ToastUtil.show(WordUtil.getString(R.string.upload_failure));
            e.printStackTrace();
        }

    }
}
