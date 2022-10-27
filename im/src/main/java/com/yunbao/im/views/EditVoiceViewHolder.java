package com.yunbao.im.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.DateFormatUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.im.R;
import com.yunbao.im.utils.MediaRecordUtil;
import java.io.File;


public class EditVoiceViewHolder extends AbsViewHolder2 implements View.OnClickListener {

    private static final int WHAT_RECORD_COUNT = 1;
    private static final int WHAT_PLAY = 2;


    private View mRecordGroup;
    private View mPlayGroup;
    private TextView mTip;
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
    private boolean mVoiceChanged;
    private VoiceLisnter mVoiceLisnter;

    private static int startLimit=20;
    private static int endLimit=60;


    public EditVoiceViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_edit_voice;
    }

    @Override
    public void init() {
        mRecordGroup = findViewById(R.id.record_group);
        mPlayGroup = findViewById(R.id.play_group);
        mTip = findViewById(R.id.tip);
        String tip= WordUtil.getString(R.string.record_tips,startLimit,endLimit);
        mTip.setText(tip);
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
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_RECORD_COUNT:
                        mRecordSec++;
                        if (mRecordSec >= endLimit) {
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
        if (mRecordVoiceDuration < startLimit*1000) {
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

            if(mVoiceLisnter!=null){
                mVoiceLisnter .recordSuccess(mRecordVoiceFile,mRecordVoiceDuration);
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

        if(mVoiceLisnter!=null){
            mVoiceLisnter .delete();
        }
    }

    @Override
    public void onDestroy() {
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
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
        mLoading = null;
        mVoiceLisnter=null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_play) {
            play();
        } else if (i == R.id.btn_clear) {
            clear();
        }
    }

    public void clear() {
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

    private void save() {

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

    public File getRecordVoiceFile() {
        return mRecordVoiceFile;
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

    public void setVoiceLisnter(VoiceLisnter voiceLisnter) {
        mVoiceLisnter = voiceLisnter;
    }

    public interface VoiceLisnter{
        public void recordSuccess(File file,long length);
        public void delete();
    }

}
