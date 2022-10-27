package com.yunbao.dynamic.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.ViewPagerSnapHelper;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.ui.view.GalleryViewHolder;
import java.util.ArrayList;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.POSITION;
import static com.yunbao.common.Constants.TYPE;

public class GalleryActivity extends AbsActivity  {
    public static final int TYPE_WATCH=0;
    public static final int TYPE_EDIT=2;

    protected ArrayList<String>photoPathArray;
    protected int currentPositoin;
    protected int type;
    protected GalleryViewHolder galleryViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gallery;
    }

    @Override
    protected void main() {
        super.main();
        readParm();
        if(type==TYPE_EDIT){
            setTitle(getString(R.string.preview));
        }else{
            findViewById(R.id.tv_delete).setVisibility(View.GONE);
        }
        galleryViewHolder=new GalleryViewHolder(this, (ViewGroup)
                findViewById(R.id.container),photoPathArray,currentPositoin);
        galleryViewHolder.setPageSelectListner(new ViewPagerSnapHelper.OnPageSelectListner() {
            @Override
            public void onPageSelect(int position) {
               // L.e("GalleryActivity---","onPageSelect---"+position);
                setCurrentPosition(position);
            }
        });
        galleryViewHolder.addToParent();
        galleryViewHolder.subscribeActivityLifeCycle();
        setCurrentPosition(currentPositoin);
    }
    private void setCurrentPosition(int position) {
        this.currentPositoin=position;
        setRightTitle(position+1+"/"+galleryViewHolder.size());
    }

    public static void forward(Context context, ArrayList<String>list,int position,int type){
        if(list==null) {
            return;
        }
        Intent intent=new Intent(context, GalleryActivity.class);
        intent.putExtra(DATA,list);
        intent.putExtra(POSITION,position);
        intent.putExtra(TYPE,type);
        context.startActivity(intent);
    }

    public static void forward(Activity activity, View view,ArrayList<String>list,int position,int type){
        if(list==null) {
            return;
        }
        Intent intent=new Intent(activity, GalleryActivity.class);
        intent.putExtra(DATA,list);
        intent.putExtra(POSITION,position);
        intent.putExtra(TYPE,type);
        Bundle bundle= ActivityOptions.makeSceneTransitionAnimation((Activity) activity,view, WordUtil.getString(R.string.transition_image)+position).toBundle();
        activity.startActivity(intent,bundle);
    }










    public void readParm(){
        Intent intent=getIntent();
        photoPathArray=intent.getStringArrayListExtra(DATA);
        currentPositoin=intent.getIntExtra(POSITION,0);
        type=intent.getIntExtra(TYPE,0);
    }

    @Override
    public void onBackPressed() {
        Intent intent=getIntent();
        intent.putExtra(DATA,photoPathArray);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void deletePhoto(View view) {
        if(!ClickUtil.canClick()) {
            return;
        }
        deleteGalleyPhoto();
    }


    protected int deleteGalleyPhoto() {
        if(galleryViewHolder.size()==1){
            photoPathArray=null;
            onBackPressed();
            return -1;
        }
        return galleryViewHolder.remove(currentPositoin);
    }
}
