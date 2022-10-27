package com.yunbao.main.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.liveobsever.LifeVoiceMediaHelper;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.custom.VoiceView;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.business.AnimHelper;
import com.yunbao.main.R;
import com.yunbao.main.bean.GreateManBean;
import java.util.List;

public class GreatManAdapter  extends BaseRecyclerAdapter<GreateManBean, BaseReclyViewHolder> {

    private View.OnClickListener mOnClickListener;
    private VoiceMediaPlayerUtil voiceMediaPlayerUtil;
    private String voicePath;

    public GreatManAdapter(List<GreateManBean> data, Context context) {
        super(data);
        voiceMediaPlayerUtil= LifeVoiceMediaHelper.getByContext((AppCompatActivity) context).getMediaPlayer();
        voiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
            @Override
            public void onPrepared() {
            }
            @Override
            public void onError() {
            }
            @Override
            public void onPlayEnd() {
                for (GreateManBean bean1 : getData()){
                    bean1.setCanPlay(false);
                }
                notifyDataSetChanged();
            }
        });
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GreateManBean bean = (GreateManBean) v.getTag();
                VoiceView voiceView = (VoiceView) v;
                if (bean != null){
                    if (!bean.isCanPlay()){
                        for (GreateManBean bean1 : getData()){
                            if (bean.getId().equals(bean1.getId())){
                                bean1.setCanPlay(true);
                                setPlayPath(bean.getAuthinfo().getSkillVoice());
                            }else {
                                bean1.setCanPlay(false);
                            }
                        }
                    }
//                    else {
//                        if (checkPlayPause()){
//                            resumePlay();
//                            voiceView.startAnimation();
//                        }else if (checkPlayStarted()){
//                            pausePlay();
//                            voiceView.stopAnimation();
//                        }
//                        else {
//                            ToastUtil.show("问题");
//                        }
//                    }
                }
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getLayoutId() {
        return R.layout.item_recly_greate_man;
    }

    private void startPlay() {
        if(voiceMediaPlayerUtil!=null){
            voiceMediaPlayerUtil.startPlay(voicePath);
        }

    }

    private boolean checkPlayStarted(){
        if (voiceMediaPlayerUtil.isStarted()){
            return true;
        }
        return false;
    }

    private boolean checkPlayPause(){
        if (voiceMediaPlayerUtil.isPaused()){
            return true;
        }
        return false;
    }


    private void pausePlay() {
        if(voiceMediaPlayerUtil!=null && voiceMediaPlayerUtil.isStarted()){
            voiceMediaPlayerUtil.pausePlay();
        }
    }

    private void resumePlay() {
        if(voiceMediaPlayerUtil!=null && voiceMediaPlayerUtil.isPaused()){
            voiceMediaPlayerUtil.resumePlay();
        }
    }

    public void setPlayPath(String voicePath){
        this.voicePath=voicePath;
        stopPlay();
    }

    private void stopPlay() {
        if(voiceMediaPlayerUtil!=null&&voiceMediaPlayerUtil.isStarted()){
            voiceMediaPlayerUtil.stopPlay();
        }
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, GreateManBean item) {
        UserBean userBean=item.getUserinfo();
        VoiceView voiceView=helper.getView(R.id.voiceView);
        voiceView.setDefautAnimDrawable(ContextCompat.getDrawable(context, R.mipmap.icon_skill_voice_2));
        TextView tvDes=helper.getView(R.id.tv_des);
        if(userBean!=null){
            helper.setImageUrl(userBean.getAvatar(),R.id.img_avatar);
            helper.setText(R.id.tv_name,userBean.getUserNiceName());
            helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(userBean.getSex()));
            View view=helper.getView(com.yunbao.dynamic.R.id.ll_sex_group);
            view.setBackground(CommonIconUtil.getSexBgDrawable(userBean.getSex()));
            helper.setText(R.id.age,userBean.getAge()+"");
        }
        SkillBean skillBean=item.getAuthinfo();
        if(skillBean!=null){
            helper.setText(R.id.tv_star,skillBean.getStarCount()+"");
            helper.setText(R.id.tv_level,skillBean.getSkillLevel());
            helper.setText(R.id.tv_coin,skillBean.getPirceResult());
            helper.setText(R.id.tv_order_num,WordUtil.getString(R.string.game_order_num_1,skillBean.getOrderNum())+" | ");
            String[]labelArray=skillBean.getLabels();

            if(labelArray==null||labelArray.length==0){
                helper.setVisible(R.id.img_label_icon,false);
            }else{
                helper.setText(R.id.tv_label,getArrayLabelString(labelArray));
                helper.setVisible(R.id.img_label_icon,true);
            }

            tvDes.setText(skillBean.getDes());
            int voiceDucation=skillBean.getSkillVoiceDuration();
            String voicePath=skillBean.getSkillVoice();
            if(!TextUtils.isEmpty(voicePath)){
                voiceView.setVoiceLength(voiceDucation);
                voiceView.setVisibility(View.VISIBLE);
                tvDes.setVisibility(View.INVISIBLE);
                if (item.isCanPlay()){
                    startPlay();
                    voiceView.startAnimation();
                }else {
                    voiceView.stopAnimation();
                }
            }else{
                voiceView.setVisibility(View.INVISIBLE);
                tvDes.setVisibility(View.VISIBLE);
            }
        }else{
            voiceView.setVisibility(View.INVISIBLE);
            tvDes.setVisibility(View.INVISIBLE);
        }
        voiceView.setTag(item);
        voiceView.setOnClickListener(mOnClickListener);
    }


    private String getArrayLabelString(String[] labelArray) {
       StringBuilder builder=new StringBuilder();
       for(String label:labelArray){
           builder.append(label)
           .append("  ");
       }
       return builder.toString();
    }



    public void release(){

    }
}
