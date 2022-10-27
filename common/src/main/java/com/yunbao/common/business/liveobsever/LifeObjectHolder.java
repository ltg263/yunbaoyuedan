package com.yunbao.common.business.liveobsever;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import com.yunbao.common.utils.L;

/**以activity作为全局,持有一个唯一句柄对象,并在界面回收的情况下自动进行对象释放*/
public abstract class LifeObjectHolder {
    private static ArrayMap<String,LifeObjectHolder> holderArrayMap;

    public   LifeObjectHolder(){

    }

    public static <T extends LifeObjectHolder > T getByContext(@Nullable LifecycleOwner owner, @Nullable Class<T>cs) {
        if(holderArrayMap==null){
            synchronized (LifeObjectHolder.class){
                holderArrayMap=new ArrayMap<>();
            }
        }
        L.e("holderArrayMap size=="+holderArrayMap.size());
        int hashCode=owner.hashCode();
        final String key=hashCode+cs.getName();

        LifeObjectHolder lifeObjectHolder=holderArrayMap.get(key);
        if(lifeObjectHolder!=null&&lifeObjectHolder.getClass()!=cs){
            return null;
        }
        T holder= (T) holderArrayMap.get(key);
        if(holder==null){
            try {
              holder=cs.newInstance();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        holderArrayMap.put(key,holder);
        owner.getLifecycle().addObserver(new LifeObserver(hashCode) {
            @Override
            public void onDestory() {
                super.onDestory();

                LifeObjectHolder lifeObjectHolder=holderArrayMap.get(key);
                if(lifeObjectHolder!=null){
                   lifeObjectHolder.release();
                   holderArrayMap.remove(key);
                }
            }
        });
        return holder;
    }

    public abstract void release();


}
