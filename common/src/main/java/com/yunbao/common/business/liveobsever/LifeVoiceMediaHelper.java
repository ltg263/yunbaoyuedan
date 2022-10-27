package com.yunbao.common.business.liveobsever;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;
import java.util.ArrayList;
import java.util.List;

/*基于context去单例,每一个context只能有一个LifeVoiceMediaHelper实例*/
public class LifeVoiceMediaHelper   {
   public    static final int ALL_CANCLE_CODE=-1;
   private   static ArrayMap<Integer,LifeVoiceMediaHelper> map;

   private   VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
   private   List<SingleSoundListner> mSingleSoundListnerArray;
   private   int mLastPlayObjectHashCode;

   private   LifeVoiceMediaHelper() {
       mVoiceMediaPlayerUtil=new VoiceMediaPlayerUtil();
   }
    public static LifeVoiceMediaHelper getByContext(@NonNull  LifecycleOwner owner) {
        if(map==null){
            synchronized (LifeVoiceMediaHelper.class){
                map=new ArrayMap<>();
            }
        }
        int hashCode=owner.hashCode();
        LifeVoiceMediaHelper voiceMediaHelper=map.get(hashCode);
        if(voiceMediaHelper==null){
            voiceMediaHelper=new LifeVoiceMediaHelper();
        }
        map.put(hashCode,voiceMediaHelper);
        owner.getLifecycle().addObserver(new LifeObserver(hashCode) {
            @Override
            public void onStop() {
                super.onStop();
                LifeVoiceMediaHelper lifeVoiceMediaHelper=map.get(ownerHashCode);
                lifeVoiceMediaHelper.watchPlay(ALL_CANCLE_CODE);
            }
            @Override
            public void onDestory() {
                super.onDestory();
                L.e("LifeVoiceMediaHelper== OnDestory()");
                LifeVoiceMediaHelper lifeVoiceMediaHelper=map.get(ownerHashCode);
                if(lifeVoiceMediaHelper!=null){
                    lifeVoiceMediaHelper.release();
                    map.remove(ownerHashCode);
                }
            }
        });
        return voiceMediaHelper;
    }

    public void addSingleSoundLisnter(SingleSoundListner singleSoundListner){
       if(mSingleSoundListnerArray==null){
           synchronized (LifeVoiceMediaHelper.class){
            mSingleSoundListnerArray=new ArrayList<>();
           }
       }
        mSingleSoundListnerArray.add(singleSoundListner);
    }

    public void watchPlay(int hashCode){
       this.mLastPlayObjectHashCode=hashCode;
        if(mSingleSoundListnerArray!=null){
           for(SingleSoundListner singleSoundListner:mSingleSoundListnerArray){
               singleSoundListner.single(hashCode);
           }
        }
    }

    public VoiceMediaPlayerUtil getMediaPlayer(){
       return mVoiceMediaPlayerUtil;
    }
    public void release(){
       if(mVoiceMediaPlayerUtil!=null){
          mVoiceMediaPlayerUtil.destroy();
       }
       if(mSingleSoundListnerArray!=null){
          mSingleSoundListnerArray.clear();
       }
    }

    public interface SingleSoundListner{
       public void single(int hashCode);
    }

    public int getLastPlayObjectHashCode() {
        return mLastPlayObjectHashCode;
    }
}
