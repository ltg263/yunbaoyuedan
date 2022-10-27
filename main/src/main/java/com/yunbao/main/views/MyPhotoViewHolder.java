package com.yunbao.main.views;

import android.content.Context;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.activity.AlbumGalleryActivity;
import com.yunbao.main.adapter.PhotoAdapter;
import com.yunbao.main.bean.PhotoBean;
import com.yunbao.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

public class MyPhotoViewHolder extends AbsMainViewHolder {
    private RxRefreshView<PhotoBean> refreshView;
    private PhotoAdapter photoAdapter;

    public MyPhotoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_my_photo;
    }

    @Override
    public void init() {

        refreshView=findViewById(R.id.refreshView);
        photoAdapter=new PhotoAdapter(null);
        refreshView.setAdapter(photoAdapter);
        RxRefreshView.ReclyViewSetting reclyViewSetting= RxRefreshView.ReclyViewSetting.createGridSetting(mContext,3,5);
        refreshView.setReclyViewSetting(reclyViewSetting);
        refreshView.setDataListner(new RxRefreshView.DataListner<PhotoBean>() {
            @Override
            public Observable<List<PhotoBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<PhotoBean> data) {
            }
            @Override
            public void error(Throwable e) {
                e.printStackTrace();
            }
        });
    }


    public void  setNoDataTip(String noDataTip){
        if(refreshView!=null){
            refreshView.setNoDataTip(noDataTip);
        }
    }



    public void setOnItemClickListner( BaseQuickAdapter.OnItemClickListener onItemClickListner){
        if(photoAdapter!=null) {
            photoAdapter.setOnItemClickListener(onItemClickListner);
        }
    }
    @Override
    public void loadData() {
       refreshView.initData();
    }
    public PhotoBean findDataByPosition(int position){
        return photoAdapter!=null?photoAdapter.getItem(position):null;
    }
    public List<PhotoBean> findData(){
        return photoAdapter!=null? (ArrayList<PhotoBean>) photoAdapter.getData():null;
    }

    public Observable<List<PhotoBean>> getData(int p) {
        RxAppCompatActivity activity= (RxAppCompatActivity) mContext;
        return MainHttpUtil.getMyPhotos(p).compose(activity.<List<PhotoBean>>bindToLifecycle());
    }
}
