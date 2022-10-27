package com.yunbao.dynamic.widet;

import android.support.design.widget.AppBarLayout;

import com.yunbao.common.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;

public class StartChangeOffectListner implements AppBarLayout.OnOffsetChangedListener {
    private float mCurrentRate;
    private int mCurrentOffect;
    private float mStatChangeOffect;

    private List<OnWatchOffsetListner> mOnWatchOffsetListnerList;

    public StartChangeOffectListner(float statChangeOffect) {
        mStatChangeOffect = statChangeOffect;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offect) {
        if(mCurrentOffect==offect){
            return;
        }
        this.mCurrentOffect=offect;
        float totalRange=appBarLayout.getTotalScrollRange();
        float absOffect=Math.abs(mCurrentOffect);

        if(absOffect>=totalRange-mStatChangeOffect){
            mCurrentRate=(totalRange-absOffect)/mStatChangeOffect;
            watchOffect(mCurrentRate);
        }else if(mCurrentRate!=1){
            mCurrentRate=1;
            watchOffect(mCurrentRate);
        }
    }

    private void watchOffect(float currentRate) {
        if(ListUtil.haveData(mOnWatchOffsetListnerList)){
            for(OnWatchOffsetListner onWatchOffsetListner:mOnWatchOffsetListnerList){
                onWatchOffsetListner.offect(currentRate);
            }
        }
    }

    public void addOnWatchOffectListner(OnWatchOffsetListner watchOffsetListner){
        if( mOnWatchOffsetListnerList==null){
            mOnWatchOffsetListnerList=new ArrayList<>();
        }
            mOnWatchOffsetListnerList.add(watchOffsetListner);
    }



    public void release(){
        if(mOnWatchOffsetListnerList!=null){
            mOnWatchOffsetListnerList.clear();
        }
    }
    public interface  OnWatchOffsetListner{
        public void offect(float rate);
    }

}
