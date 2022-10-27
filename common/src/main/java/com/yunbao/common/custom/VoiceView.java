package com.yunbao.common.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.yunbao.common.R;
import com.yunbao.common.business.liveobsever.LifeVoiceMediaHelper;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;

public class VoiceView extends FrameLayout {
    private ImageView voiceImg;
    private TextView voiceTime;
    private Drawable[] mVoiceDrawables;
    private Drawable mVoiceEndDrawable;
    private ValueAnimator mAnimator;



    private Drawable defaultDrawable;
    public VoiceView( Context context) {
        super(context);
    }

    public VoiceView( Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public VoiceView( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.widet_voice,this,true);
        voiceImg = (ImageView) findViewById(R.id.voice_img);
        voiceTime = (TextView) findViewById(R.id.voice_time);
        AppCompatActivity appCompatActivity= (AppCompatActivity) getContext();
        mVoiceEndDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_skill_voice_2);
        mVoiceDrawables = new Drawable[3];
        mVoiceDrawables[0] = ContextCompat.getDrawable(context, R.mipmap.icon_skill_voice_0);
        mVoiceDrawables[1] = ContextCompat.getDrawable(context, R.mipmap.icon_skill_voice_1);
        mVoiceDrawables[2] = mVoiceEndDrawable;
        mAnimator = ValueAnimator.ofFloat(0, 900);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(700);
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (voiceImg != null) {
                    int index = (int) (v / 300);
                    if (index > 2) {
                        index = 2;
                    }
                    voiceImg.setImageDrawable(mVoiceDrawables[index]);
                }
            }
        });
    }

    private void cancleAnimPlay() {
        voiceImg.setImageDrawable(defaultDrawable);
        requestLayout();
    }


    public VoiceView setDefautAnimDrawable(Drawable  animDrawable) {
        this.defaultDrawable=animDrawable;
        voiceImg.setImageDrawable(animDrawable);
        return this;
    }


    public VoiceView setVoiceLength(int length) {
        voiceTime.setText(length+"\"");
        return this;
    }

    public void startAnimation(){
        if(mAnimator!=null){
            mAnimator.start();
        }
    }

    public void stopAnimation(){
        if (mAnimator != null && mAnimator.isRunning()){
            mAnimator.end();
        }
        voiceImg.setImageDrawable(defaultDrawable);
    }

    public void release(){
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = null;
    }


}
