package com.yunbao.dynamic.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.widet.VoicePlayView;

public class VoiceDynamiceViewHolderScroll extends NormalScrollDynamicViewHolder {
    public VoiceDynamiceViewHolderScroll(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    public void setData(DynamicBean dynamicBean) {
        super.setData(dynamicBean);
        if(dynamicBean!=null){
          String voiceUrl= dynamicBean.getVoice();
          if(!TextUtils.isEmpty(voiceUrl)){
            VoicePlayView voicePlayView= (VoicePlayView) LayoutInflater.from(mContext).inflate(R.layout.item_recly_pub_voice,null);
            mFlOntherContainer.addView(voicePlayView);
            voicePlayView.setVoiceInfo(dynamicBean.getVoice_l(),voiceUrl);
          }
        }
    }
}
