package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.iceteck.silicompressorr.SiliCompressor;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.dynamic.ui.activity.GalleryActivity;
import com.yunbao.main.bean.PhotoBean;
import com.yunbao.main.http.MainHttpUtil;
import java.util.ArrayList;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.POSITION;
import static com.yunbao.common.Constants.TYPE;

public class AlbumGalleryActivity extends GalleryActivity {
    private ArrayList<PhotoBean> photoBeanList;
    @Override
    public void readParm() {
        Intent intent=getIntent();
        type=intent.getIntExtra(TYPE,TYPE_WATCH);
        currentPositoin=intent.getIntExtra(POSITION,0);
        photoBeanList=intent.getParcelableArrayListExtra(DATA);
        photoPathArray= ListUtil.transForm(photoBeanList, String.class, new ListUtil.TransFormListner<PhotoBean, String>() {
            @Override
            public String transform(PhotoBean photoBean) {
                return photoBean.getThumb();
            }
        });
    }

    public static void forward(Context context,ArrayList<PhotoBean>list, int position){
         Intent intent=new Intent(context,AlbumGalleryActivity.class);
         intent.putExtra(POSITION,position);
         intent.putExtra(DATA,list);
         intent.putExtra(DATA,list);
         intent.putExtra(TYPE,TYPE_EDIT);
         context.startActivity(intent);
    }
    public static void forwardWatch(Context context,ArrayList<PhotoBean>list, int position){
        Intent intent=new Intent(context,AlbumGalleryActivity.class);
        intent.putExtra(POSITION,position);
        intent.putExtra(DATA,list);
        intent.putExtra(DATA,list);
        intent.putExtra(TYPE,TYPE_WATCH);
        context.startActivity(intent);
    }

    @Override
    public void deletePhoto(View view) {
        PhotoBean photoBean=ListUtil.safeGetData(photoBeanList,currentPositoin);
        if(!ClickUtil.canClick()||photoBean==null){
            return;
        }
        MainHttpUtil.delPhoto(photoBean.getId()).subscribe(new DialogObserver<Boolean>(this) {
            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                    int index=deleteGalleyPhoto();
                    if(index!=-1){
                      photoBeanList.remove(index);
                    }
                }
            }
        });

    }
}
