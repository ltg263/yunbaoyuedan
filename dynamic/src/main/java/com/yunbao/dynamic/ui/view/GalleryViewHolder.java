package com.yunbao.dynamic.ui.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.custom.ViewPagerSnapHelper;
import com.yunbao.common.utils.L;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.adapter.GalleryAdapter;
import com.yunbao.dynamic.ui.activity.GalleryActivity;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewHolder extends AbsViewHolder2 {
    private RecyclerView reclyView;
    private GalleryAdapter galleryAdapter;
    private List<String> photoPathArray;
    private int mPosition;
    private ViewPagerSnapHelper.OnPageSelectListner pageSelectListner;
    ViewPagerSnapHelper snapHelper;

    public GalleryViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }


    public GalleryViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        this.photoPathArray= (List<String>) args[0];
        this.mPosition= (int) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_gallery;
    }
    @Override
    public void init() {
        reclyView = (RecyclerView) findViewById(R.id.reclyView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        reclyView.setLayoutManager(linearLayoutManager);

        snapHelper= new ViewPagerSnapHelper();
        snapHelper.attachToRecyclerView(reclyView);
        galleryAdapter=new GalleryAdapter(photoPathArray);
        reclyView.setAdapter(galleryAdapter);
        reclyView.scrollToPosition(mPosition);
        reclyView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                pageSelectListner.onPageSelect(linearLayoutManager.findFirstVisibleItemPosition());
                L.e("onScrolled---"+linearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        });
    }


    public void setData(final List<String>photoList, int position){
        mPosition=position;
        if(galleryAdapter!=null){
            galleryAdapter.setData(photoList);
        }
        reclyView.scrollToPosition(position);
        if (!(mContext instanceof GalleryActivity)){
            galleryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ArrayList<String> stringList=new ArrayList<>();
                    stringList.addAll(photoList);
                    GalleryActivity.forward((Activity) mContext,view, stringList,position,GalleryActivity.TYPE_WATCH);

                }
            });
        }
    }



    public void setPageSelectListner(ViewPagerSnapHelper.OnPageSelectListner pageSelectListner){
        this.pageSelectListner=pageSelectListner;
       // snapHelper.setPageSelectListner(pageSelectListner);
    }

   public int size(){
        return galleryAdapter.size();
   }

   public void setCanScroll(boolean isCanScroll){
        reclyView.setNestedScrollingEnabled(isCanScroll);
   }

    /*设置画廊的图片类型*/
   public void setScaleType(ImageView.ScaleType scaleType){
        if(galleryAdapter!=null){
           galleryAdapter.setScaleType(scaleType);
        }
   }


    /*是否开启放大功能*/
    public void enableZoom(boolean isEnable){
        if(galleryAdapter!=null){
            galleryAdapter.setCanZoom(isEnable);
        }
    }


   public int remove(int position){
     int removePosition=galleryAdapter.removeItem(position)? position:-1;
       if(position==galleryAdapter.size()){  //删除的时候无法走改写的监听,需要手动判断一下
           position=position-1;
       }
    if(pageSelectListner!=null){
        pageSelectListner.onPageSelect(position);
     }
    return removePosition;
   }

}
