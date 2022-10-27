package com.yunbao.chatroom.widet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.chatroom.R;

public class StateView extends LinearLayout  {
    private RoundedImageView mImgAvatar;
    private TextView mTvName;
    private ViewGroup mVpAvatorBg;

    private State mDefaultState;
    private State mSelectState;
    private OnClickListener mSelectOnClickLisnter;
    private OnClickListener mDefaultOnClickListner;
    private boolean mStated;

    private ImageView mbgImageView;

    public StateView(Context context) {
        super(context);
        init(context);
    }
    public StateView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public StateView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public StateView setDefaultState(State defaultState) {
        mDefaultState = defaultState;
        return this;
    }
    public StateView setSelectState(State selectState) {
        mSelectState = selectState;
        return this;
    }
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widet_state_view,this,true);
        mImgAvatar = (RoundedImageView) findViewById(R.id.img_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mVpAvatorBg =  findViewById(R.id.vp_avator_bg);
        mbgImageView=findViewById(R.id.bg_imageView);
    }
    public StateView setSelectOnClickLisnter(OnClickListener selectOnClickLisnter) {
        mSelectOnClickLisnter = selectOnClickLisnter;
        return this;
    }

    public StateView setDefaultOnClickListner(OnClickListener defaultOnClickListner) {
        mDefaultOnClickListner = defaultOnClickListner;
        return this;
    }

    public void setState(boolean checked) {
        mStated=checked;
        if(mStated){
          setStateData(mSelectState);
        }else{
          setStateData(mDefaultState);
        }
    }

    private void setStateData(State state) {
       if(state!=null){
           if(state.avator!=null){
               if(state.avator instanceof Integer){
                 ImgLoader.display(getContext(),(Integer) state.avator,mImgAvatar);
               }else if(state.avator instanceof String){
                 ImgLoader.display(getContext(),(String) state.avator,mImgAvatar);
               }
           }

           int color=getResources().getColor(state.color);
           mTvName.setText(state.name);
           mTvName.setTextColor(color);

           if(state.frame!=-1){
               mVpAvatorBg.setBackgroundResource(state.frame);
           }else{
               mVpAvatorBg.setBackgroundResource(0);
           }
       }
    }

    public ImageView getBgImageView() {
        return mbgImageView;
    }

    public boolean isChecked() {
        return mStated;
    }

    public void toggle() {
        mStated=!mStated;
        setState(mStated);
    }

    public static class State{
        public String name;
        public Object avator;
        public int  color;
        public int  frame=-1;
    }
}
