package com.yunbao.chatroom.ui.view.apply;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.server.observer.LockClickObserver;
import com.yunbao.common.upload.FileUploadManager;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.views.EditVoiceViewHolder;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApplyHostViewHolder extends AbsApplyHostViewHolder implements View.OnClickListener {
    private TextView mTvNotice;
    private FrameLayout mVpVoice;
    private TextView mBtnConfirm;
    private File mRecordFile;
    private int mDurcation = -1;
    private String mRemoteUrl;
    private EditVoiceViewHolder mEditVoiceViewHolder;
    private UploadStrategy uploadStrategy;
    private String mNoticeString;

    public ApplyHostViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mNoticeString = (String) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_apply_host;
    }

    @Override
    public void init() {
        mTvNotice = (TextView) findViewById(R.id.tv_notice);
        mVpVoice = (FrameLayout) findViewById(R.id.vp_voice);
        mBtnConfirm = (TextView) findViewById(R.id.btn_confirm);
        mTvNotice.setText(mNoticeString);

        mEditVoiceViewHolder = new EditVoiceViewHolder(mContext, mVpVoice);
        mEditVoiceViewHolder.subscribeActivityLifeCycle();
        mEditVoiceViewHolder.setVoiceLisnter(new EditVoiceViewHolder.VoiceLisnter() {
            @Override
            public void recordSuccess(File file, long ducation) {
                setParms(file, ducation);
            }

            @Override
            public void delete() {
                setParms(null, -1);
            }
        });
        mEditVoiceViewHolder.addToParent();
        setOnClickListner(R.id.btn_confirm, this);
    }

    @Override
    public void addToParent() {
        super.addToParent();
        if (mEditVoiceViewHolder != null) {
            mEditVoiceViewHolder.clear();
        }
    }

    private void setParms(File file, long ducation) {
        mRecordFile = file;
        mDurcation = (int) (ducation / 1000);
        mBtnConfirm.setEnabled(mRecordFile != null);
    }


    @Override
    public void onClick(View v) {
        reReply(v);
    }

    private void reReply(View v) {
        if (mRemoteUrl == null) {
            uoloadVoice(v);
        } else {
            requestApply(v);
        }
    }

    private Dialog mLoadingDialog;

    /*先上传声音*/
    private void uoloadVoice(final View view) {
        view.setEnabled(false);
        FileUploadManager.getInstance().createUploadImpl(mContext, new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                if (strategy == null){
                    ToastUtil.show(WordUtil.getString(R.string.upload_type_error));
                    return;
                }
                uploadStrategy = strategy;
                showLoadingDialog();
                final List<UploadBean> uploadBeanList = new ArrayList<>(1);
                uploadBeanList.add(new UploadBean(mRecordFile));
                uploadStrategy.upload(uploadBeanList, false, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        dismissLoadingDialog();
                        if (success) {
                            mRemoteUrl = list.get(0).getRemoteFileName();
                            requestApply(view);
                        } else {
                            view.setEnabled(true);
                        }
                    }
                });
            }
        });
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = DialogUitl.loadingDialog(mContext, mContext.getString(R.string.up_load));
            mLoadingDialog.show();
        }
    }

    private void requestApply(final View view) {
        if (mLifecycleProvider == null) {
            view.setEnabled(true);
            return;
        }
        ChatRoomHttpUtil.applyLiveHost(mDurcation, mRemoteUrl).compose(mLifecycleProvider.<Boolean>bindToLifecycle()).subscribe(new LockClickObserver<Boolean>(view) {
            @Override
            public void onSucc(Boolean aBoolean) {
                if (aBoolean) {
                    notifyRefresh();
                    removeFromParent();
                }
            }
        });
    }
}
