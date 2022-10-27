package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.UpdateFieldEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.upload.FileUploadManager;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadQnImpl;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.DateFormatUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.utils.MediaRecordUtil;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;
import com.yunbao.main.R;
import com.yunbao.main.event.UpdateSkillEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/7/25.
 */

public class EditVoiceActivity extends AbsActivity implements View.OnClickListener {

    private static final int WHAT_RECORD_COUNT = 1;
    private static final int WHAT_PLAY = 2;
    private View mRecordGroup;
    private View mPlayGroup;
    private View mTip;
    private TextView mRecordTime;
    private ImageView mBtnRecord;
    private ImageView mBtnPlay;
    private TextView mPlayTime;
    private Drawable mVoiceUnPressedDrawable;
    private Drawable mVoicePressedDrawable;
    private MediaRecordUtil mMediaRecordUtil;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    private File mRecordVoiceFile;//录音文件
    private long mRecordVoiceDuration;//录音时长 单位毫秒
    private Handler mHandler;
    private boolean mRecording;
    private boolean mPlayStarted;
    private boolean mPlayPaused;
    private Drawable mPlayDrawable;
    private Drawable mPauseDrawable;
    private int mRecordTotalSec;//录音时长 单位秒
    private int mRecordSec;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoading;
    private String mOriginVoice;
    private int mFrom;
    private String mSkillID;
    private boolean mVoiceChanged;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_voice;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mOriginVoice = intent.getStringExtra(Constants.VOICE);
        mRecordTotalSec = intent.getIntExtra(Constants.VOICE_DURATION, 0);
        mFrom = intent.getIntExtra(Constants.VOICE_FROM, 0);
        mSkillID = intent.getStringExtra(Constants.SKILL_ID);
        mRecordGroup = findViewById(R.id.record_group);
        mPlayGroup = findViewById(R.id.play_group);
        mTip = findViewById(R.id.tip);
        mRecordTime = findViewById(R.id.record_time);
        mBtnRecord = findViewById(R.id.btn_record);
        mBtnPlay = findViewById(R.id.btn_play);
        mPlayTime = findViewById(R.id.play_time);
        mVoiceUnPressedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_voice_record_0);
        mVoicePressedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_voice_record_1);
        mPlayDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_voice_play);
        mPauseDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_voice_pause);
        mBtnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startRecordVoice();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopRecordVoice();
                        break;
                }
                return true;
            }
        });
        mBtnPlay.setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_RECORD_COUNT:
                        mRecordSec++;
                        if (mRecordSec >= 15) {
                            stopRecordVoice();
                        } else {
                            if (mRecordTime != null) {
                                mRecordTime.setText(StringUtil.contact(String.valueOf(mRecordSec), "\""));
                            }
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(WHAT_RECORD_COUNT, 1000);
                            }
                        }
                        break;
                    case WHAT_PLAY:
                        if (mVoiceMediaPlayerUtil != null) {
                            int curPosition = mVoiceMediaPlayerUtil.getCurPosition();
                            if (mPlayTime != null) {
                                mPlayTime.setText(StringUtil.contact(String.valueOf(curPosition), "\""));
                            }
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(WHAT_PLAY, 500);
                            }
                        }
                        break;
                }

            }
        };

        if (!TextUtils.isEmpty(mOriginVoice)) {
            if (mRecordGroup != null && mRecordGroup.getVisibility() == View.VISIBLE) {
                mRecordGroup.setVisibility(View.INVISIBLE);
            }
            if (mPlayGroup != null && mPlayGroup.getVisibility() != View.VISIBLE) {
                mPlayGroup.setVisibility(View.VISIBLE);
            }
            if (mPlayTime != null) {
                mPlayTime.setText(StringUtil.contact(String.valueOf(mRecordTotalSec), "\""));
            }
        }

    }


    /**
     * 开始录音
     */
    public void startRecordVoice() {
        if (mBtnRecord == null) {
            return;
        }
        mBtnRecord.setBackground(mVoicePressedDrawable);
        if (mMediaRecordUtil == null) {
            mMediaRecordUtil = new MediaRecordUtil();
        }
        File dir = new File(CommonAppConfig.MUSIC_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mRecordVoiceFile = new File(dir, DateFormatUtil.getCurTimeString() + ".m4a");
        mMediaRecordUtil.startRecord(mRecordVoiceFile.getAbsolutePath());
        mRecording = true;
        if (mTip != null && mTip.getVisibility() == View.VISIBLE) {
            mTip.setVisibility(View.INVISIBLE);
        }
        mRecordSec = 0;
        if (mRecordTime != null) {
            if (mRecordTime.getVisibility() != View.VISIBLE) {
                mRecordTime.setVisibility(View.VISIBLE);
            }
            mRecordTime.setText(StringUtil.contact(String.valueOf(mRecordSec), "\""));
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_RECORD_COUNT, 1000);
        }
    }

    /**
     * 结束录音
     */
    private void stopRecordVoice() {
        if (!mRecording) {
            return;
        }
        mRecording = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_RECORD_COUNT);
        }
        if (mBtnRecord == null) {
            return;
        }
        mBtnRecord.setBackground(mVoiceUnPressedDrawable);
        mRecordVoiceDuration = mMediaRecordUtil.stopRecord();
        if (mRecordVoiceDuration < 3000) {
            ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.im_record_audio_too_short));
            if (mRecordTime != null && mRecordTime.getVisibility() == View.VISIBLE) {
                mRecordTime.setVisibility(View.INVISIBLE);
            }
            if (mTip != null && mTip.getVisibility() != View.VISIBLE) {
                mTip.setVisibility(View.VISIBLE);
            }
            deleteVoiceFile();
        } else {
            mVoiceChanged = true;
            if (mRecordGroup != null && mRecordGroup.getVisibility() == View.VISIBLE) {
                mRecordGroup.setVisibility(View.INVISIBLE);
            }
            if (mPlayGroup != null && mPlayGroup.getVisibility() != View.VISIBLE) {
                mPlayGroup.setVisibility(View.VISIBLE);
            }
            mRecordTotalSec = (int) (mRecordVoiceDuration / 1000);
            if (mPlayTime != null) {
                mPlayTime.setText(StringUtil.contact(String.valueOf(mRecordTotalSec), "\""));
            }
        }
    }

    /**
     * 删除录音文件
     */
    private void deleteVoiceFile() {
        if (mRecordVoiceFile != null && mRecordVoiceFile.exists()) {
            mRecordVoiceFile.delete();
        }
        mRecordVoiceFile = null;
        mRecordVoiceDuration = 0;
        mRecordTotalSec = 0;
        mRecordSec = 0;
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mMediaRecordUtil != null) {
            mMediaRecordUtil.release();
        }
        mMediaRecordUtil = null;
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        mVoiceMediaPlayerUtil = null;
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_USER_INFO);
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
        mLoading = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_play) {
            play();
        } else if (i == R.id.btn_clear) {
            clear();
        } else if (i == R.id.btn_save) {
            getUploadStrategy();
        }
    }

    private void clear() {
        deleteVoiceFile();
        if (mPlayGroup != null && mPlayGroup.getVisibility() == View.VISIBLE) {
            mPlayGroup.setVisibility(View.INVISIBLE);
        }
        if (mRecordGroup != null && mRecordGroup.getVisibility() != View.VISIBLE) {
            mRecordGroup.setVisibility(View.VISIBLE);
        }
        if (mRecordTime != null && mRecordTime.getVisibility() == View.VISIBLE) {
            mRecordTime.setVisibility(View.INVISIBLE);
        }
        if (mTip != null && mTip.getVisibility() != View.VISIBLE) {
            mTip.setVisibility(View.VISIBLE);
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.stopPlay();
        }
    }


    private void play() {
        if (!mPlayStarted) {
            checkVoiceFileExist();
        } else {
            if (!mPlayPaused) {
                pausePlay();
            } else {
                resumePlay();
            }
        }
    }


    private void getUploadStrategy(){
        FileUploadManager.getInstance().createUploadImpl(mContext, new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                if (strategy == null){
                    ToastUtil.show(WordUtil.getString(R.string.upload_type_error));
                    return;
                }
                mUploadStrategy = strategy;
                save();
            }
        });
    }

    private void save() {
        if (mRecordVoiceFile == null){
            // TODO: 2020-06-24 20200624 修改为可以删除语音
            MainHttpUtil.updateUserInfo(StringUtil.contact("{\"voice\":\"", "", "\",\"voice_l\":\"", "", "\"}"), new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        EventBus.getDefault().post(new UpdateFieldEvent());
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            String voice = obj.getString("voice");
                            int voiceLength = obj.getIntValue("voice_l");
                            UserBean userBean = CommonAppConfig.getInstance().getUserBean();
                            userBean.setVoice(voice);
                            userBean.setVoiceDuration(voiceLength);
                            Intent intent = new Intent();
                            intent.putExtra(Constants.VOICE, voice);
                            intent.putExtra(Constants.VOICE_DURATION, voiceLength);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    ToastUtil.show(msg);
                }
            });
            return;
        }
        if (!mVoiceChanged) {
            onBackPressed();
            return;
        }
        if (mRecordVoiceFile == null || !mRecordVoiceFile.exists()) {
            ToastUtil.show(R.string.edit_profile_voice_2);
            return;
        }
        mLoading = DialogUitl.loadingDialog(mContext);
        mLoading.show();
        List<UploadBean> list = new ArrayList<>();
        list.add(new UploadBean(mRecordVoiceFile));
        mUploadStrategy.upload(list, false, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (mLoading != null && mLoading.isShowing()) {
                    mLoading.dismiss();
                }
                if (success) {
                    if (list != null && list.size() > 0) {
                        String voiceFileName = list.get(0).getRemoteFileName();
                        if (mFrom == Constants.VOICE_FROM_USER) {
                            MainHttpUtil.updateUserInfo(StringUtil.contact("{\"voice\":\"", voiceFileName, "\",\"voice_l\":\"", String.valueOf(mRecordTotalSec), "\"}"), new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 0) {
                                        EventBus.getDefault().post(new UpdateFieldEvent());
                                        if (info.length > 0) {
                                            JSONObject obj = JSON.parseObject(info[0]);
                                            String voice = obj.getString("voice");
                                            int voiceLength = obj.getIntValue("voice_l");
                                            UserBean userBean = CommonAppConfig.getInstance().getUserBean();
                                            userBean.setVoice(voice);
                                            userBean.setVoiceDuration(voiceLength);
                                            Intent intent = new Intent();
                                            intent.putExtra(Constants.VOICE, voice);
                                            intent.putExtra(Constants.VOICE_DURATION, voiceLength);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    }
                                    ToastUtil.show(msg);
                                }
                            });
                        } else if (mFrom == Constants.VOICE_FROM_SKILL) {
                            MainHttpUtil.updateSkillInfo(mSkillID, StringUtil.contact("{\"voice\":\"", voiceFileName, "\",\"voice_l\":\"", String.valueOf(mRecordTotalSec), "\"}"), new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 0) {
                                        EventBus.getDefault().post(new UpdateSkillEvent());
                                        if (info.length > 0) {
                                            JSONObject obj = JSON.parseObject(info[0]);
                                            Intent intent = new Intent();
                                            intent.putExtra(Constants.VOICE, obj.getString("voice"));
                                            intent.putExtra(Constants.VOICE_DURATION, obj.getIntValue("voice_l"));
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    }
                                    ToastUtil.show(msg);
                                }
                            });
                        }
                    }
                }
            }
        });
    }


    private void checkVoiceFileExist() {
        if (mRecordVoiceFile != null && mRecordVoiceFile.exists()) {
            startPlay();
        } else {
            if (TextUtils.isEmpty(mOriginVoice)) {
                return;
            }
            File dir = new File(CommonAppConfig.INNER_PATH + "/voice/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = MD5Util.getMD5(mOriginVoice);
            if (TextUtils.isEmpty(fileName)) {
                return;
            }
            File voiceFile = new File(dir, fileName);
            if (voiceFile.exists()) {
                mRecordVoiceFile = voiceFile;
                startPlay();
            } else {
                DownloadUtil downloadUtil = new DownloadUtil();
                final Dialog dialog = DialogUitl.loadingDialog(mContext);
                dialog.show();
                downloadUtil.download("voice", dir, fileName, mOriginVoice, new DownloadUtil.Callback() {
                    @Override
                    public void onSuccess(File file) {
                        dialog.dismiss();
                        mRecordVoiceFile = file;
                        startPlay();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    /**
     * 播放录音
     */
    private void startPlay() {
        if (mBtnPlay != null) {
            mBtnPlay.setImageDrawable(mPauseDrawable);
        }
        if (mVoiceMediaPlayerUtil == null) {
            mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPrepared() {

                }

                @Override
                public void onError() {
                    onPlayEnd();
                }

                @Override
                public void onPlayEnd() {
                    mPlayStarted = false;
                    if (mBtnPlay != null) {
                        mBtnPlay.setImageDrawable(mPlayDrawable);
                    }
                    if (mHandler != null) {
                        mHandler.removeMessages(WHAT_PLAY);
                    }
                    if (mPlayTime != null) {
                        mPlayTime.setText(StringUtil.contact(String.valueOf(mRecordTotalSec), "\""));
                    }
                }
            });
        }
        mVoiceMediaPlayerUtil.startPlay(mRecordVoiceFile.getAbsolutePath());
        mPlayStarted = true;
        if (mPlayTime != null) {
            mPlayTime.setText("0\"");
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_PLAY, 500);
        }
    }


    /**
     * 暂停播放录音
     */
    public void pausePlay() {
        mPlayPaused = true;
        if (mBtnPlay != null) {
            mBtnPlay.setImageDrawable(mPlayDrawable);
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.pausePlay();
        }
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_PLAY);
        }
    }

    /**
     * 恢复播放录音
     */
    public void resumePlay() {
        mPlayPaused = false;
        if (mBtnPlay != null) {
            mBtnPlay.setImageDrawable(mPauseDrawable);
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.resumePlay();
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(WHAT_PLAY);
        }
    }


}
