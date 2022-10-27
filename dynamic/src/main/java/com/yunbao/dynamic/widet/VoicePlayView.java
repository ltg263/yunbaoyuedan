package com.yunbao.dynamic.widet;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.business.liveobsever.LifeVoiceMediaHelper;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;
import io.reactivex.annotations.Nullable;

public class VoicePlayView extends FrameLayout implements View.OnClickListener,LifeVoiceMediaHelper.SingleSoundListner {
    private Context mContext;
    private int mVoiceSumTime;
    private int mVoicePlayTime;
    private String mPlayUrl;
    private boolean mIsPlaying;
    private TextView mTvVoiceTime;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    private AnimationDrawable mAnimationDrawable;
    private boolean canDelete;
    private LifeVoiceMediaHelper mLifeVoiceMediaHelper;
    private static final String TAG = "VoicePlayView";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mVoicePlayTime--;
            if (mVoicePlayTime > 0) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
                mTvVoiceTime.setText(mVoicePlayTime + "s");
            } else {
                if (mVoicePlayTime == 0) {
                    mVoicePlayTime = mVoiceSumTime;
                }
            }
        }
    };
    private VoicePlayCallBack mVoicePlayCallBack;

    @Override
    public void single(int hashCode) {
        if(hashCode!=this.hashCode()){
            resetView();
        }
    }
    public interface VoicePlayCallBack {
        void play(VoicePlayView playView);
    }

    public VoicePlayView(@NonNull Context context) {
        this(context, null);
    }

    public VoicePlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoicePlayView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VoicePlayView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    private ImageView imgDelete;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLifeVoiceMediaHelper=LifeVoiceMediaHelper.getByContext((AppCompatActivity)mContext);
        mLifeVoiceMediaHelper.addSingleSoundLisnter(this);

        mVoiceMediaPlayerUtil=mLifeVoiceMediaHelper .getMediaPlayer();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_voice_group, this, false);
        addView(view);
        mTvVoiceTime = view.findViewById(R.id.voice_time);
        imgDelete = view.findViewById(R.id.img_delete);

        ImageView imageView = view.findViewById(R.id.voice_play);
        mAnimationDrawable = (AnimationDrawable) imageView.getDrawable();
//        imageView.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        findViewById(R.id.btn_voice).setOnClickListener(this);
        L.e("---onFinishInflate-----");
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.img_delete){
            delteButton();
        }else if (id == R.id.btn_voice){
            onClickVoice();
        }
    }

    public void setDeleteListner(OnDeleteListner deleteListner) {
        this.deleteListner = deleteListner;
    }

    private OnDeleteListner deleteListner;

    public interface OnDeleteListner{
        public void delete();
    }

    private void delteButton() {
        if(deleteListner!=null){
            deleteListner.delete();
        }
        setVoiceInfo(0,"");
    }

    public void setVoiceInfo(int sumTime, String playUrl) {
        mVoiceSumTime = sumTime;
        mVoicePlayTime = mVoiceSumTime;
        mPlayUrl = playUrl;
        mTvVoiceTime.setText(mVoiceSumTime + "s");
    }



    public void setVoicePlayCallBack(VoicePlayCallBack voicePlayCallBack) {
        mVoicePlayCallBack = voicePlayCallBack;
    }

    private void onClickVoice() {
        if (CommonAppConfig.getInstance().isFloatButtonShowing()){
            ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.can_not_do_this_in_opening_live_room));
            return;
        }
        L.e(TAG,"onClickVoice--->"+mPlayUrl);
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        if (mIsPlaying) {
            L.e(TAG,"pausePlay--->"+mPlayUrl);
            mIsPlaying = false;
            mVoiceMediaPlayerUtil.pausePlay();
            if (mAnimationDrawable != null) {
                mAnimationDrawable.stop();
            }
        } else {
            if(mLifeVoiceMediaHelper!=null){
                mLifeVoiceMediaHelper.watchPlay(this.hashCode());
            }
            mIsPlaying = true;
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPrepared() {
                    L.e(TAG,"onPrepared--->"+mPlayUrl);
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                }
                @Override
                public void onError() {
                    L.e(TAG,"onError--->"+mPlayUrl);
                }
                @Override
                public void onPlayEnd() {
                    mVoicePlayTime=mVoiceSumTime;
                    mTvVoiceTime.setText(mVoicePlayTime + "s");
                    mIsPlaying = false;
                    if (mHandler != null) {
                        mHandler.removeMessages(0);
                    }
                    if (mAnimationDrawable != null) {
                        mAnimationDrawable.stop();
                    }
                    L.e(TAG,"onPlayEnd--->"+mPlayUrl);
                }
            });
            mAnimationDrawable.start();
            if (mVoiceMediaPlayerUtil.isPaused()) {
                L.e(TAG,"resume--->"+mPlayUrl);
                mVoiceMediaPlayerUtil.resumePlay();
            } else {
                L.e(TAG,"start--->"+mPlayUrl);
                mVoiceMediaPlayerUtil.startPlay(mPlayUrl);
            }
            if (mVoicePlayCallBack != null) {
                mVoicePlayCallBack.play(this);
            }
        }
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void resume(){
        if (!mIsPlaying&&mVoiceMediaPlayerUtil!=null){
            if (mVoiceMediaPlayerUtil.isPaused()){
                mVoiceMediaPlayerUtil.resumePlay();
                mIsPlaying=true;
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.start();
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        }

    }

    public void pause(){
        if (mIsPlaying){
            mVoiceMediaPlayerUtil.pausePlay();
            mIsPlaying=false;
        }
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

    public void resetView() {
        mIsPlaying=false;
        mVoicePlayTime = mVoiceSumTime;
        mTvVoiceTime.setText(mVoiceSumTime + "s");
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.stopPlay();
        }
    }

    public void release() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }

        mVoiceMediaPlayerUtil = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }


    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
        if(canDelete){
            imgDelete.setVisibility(View.VISIBLE);
        }else{
            imgDelete.setVisibility(View.GONE);
        }
    }
}
