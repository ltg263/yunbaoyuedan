package com.yunbao.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.upload.FileUploadManager;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadQnImpl;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.ui.activity.SelectPhotoActivity;
import com.yunbao.main.R;
import com.yunbao.main.bean.PhotoBean;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.views.MyPhotoViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.DYNAMIC_PHOTO;

public class MyPhotoActivity extends AbsActivity implements View.OnClickListener {

    private ProcessResultUtil mProcessResultUtil;
    private UploadStrategy uploadStrategy;

    private MyPhotoViewHolder myPhotoViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_photo;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.my_photo));
        TextView tvRightTitle= setRightTitle(getString(R.string.upload));
        tvRightTitle.setTextColor(getResources().getColor(R.color.global));
        tvRightTitle.setOnClickListener(this);
        mProcessResultUtil=new ProcessResultUtil(this);
        myPhotoViewHolder=new MyPhotoViewHolder(this, (ViewGroup) findViewById(R.id.container));
        myPhotoViewHolder.setNoDataTip(WordUtil.getString(R.string.no_update_photo));
        myPhotoViewHolder.setOnItemClickListner(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                toAlbumGallery(position);
            }
        });
        myPhotoViewHolder.addToParent();
    }
    @Override
    protected void onStart() {
        super.onStart();
        myPhotoViewHolder.loadData();
    }

    private void toAlbumGallery(int position) {
        try {
            AlbumGalleryActivity.forward(this, (ArrayList<PhotoBean>)myPhotoViewHolder.findData(),position);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.tv_right_title){
            selectPhoto();
        }
    }

    private void selectPhoto() {
        mProcessResultUtil.requestPermissions(
                new CommonCallback<Boolean>() {
                    @Override
                    public void callback(Boolean bean) {
                        if(bean) {
                            toSelectPhoto();
                        }
                    }
                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO);
    }

    private void toSelectPhoto() {
        startActivityForResult(SelectPhotoActivity.class,DYNAMIC_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DYNAMIC_PHOTO&&resultCode==RESULT_OK){
            setPhoto(data);
        }else if(requestCode==DYNAMIC_PHOTO){

        }
    }


    private void setPhoto(Intent data) {
        List<String>photoPathArray=data.getStringArrayListExtra(DATA);
        if(ListUtil.haveData(photoPathArray)){
          uploadPhotoToStrategy(photoPathArray);
        }
    }

    private Dialog dialog;
    private void uploadPhotoToStrategy(final List<String>photoPathArray) {
        FileUploadManager.getInstance().createUploadImpl(mContext, new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                if (strategy == null){
                    ToastUtil.show(WordUtil.getString(R.string.upload_type_error));
                    return;
                }
                uploadStrategy = strategy;
                List<UploadBean>list=new ArrayList<>();
                for(String data:photoPathArray){
                    list.add(new UploadBean(new File(data)));
                }
                dialog=DialogUitl.loadingDialog(MyPhotoActivity.this);
                dialog.show();
                uploadStrategy.upload(list,true, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        if(success){
                            uploadPhoto(list);
                        }
                    }
                });
            }
        });
    }

    private void uploadPhoto(List<UploadBean> list) {
        List<String> nameList = new ArrayList<>();
        for (UploadBean bean : list) {
            nameList.add(bean.getRemoteFileName());
        }
        MainHttpUtil.setPhoto(ListUtil.listToSingleString(nameList)).
                compose(this.<Boolean>bindToLifecycle()).
                subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                   myPhotoViewHolder.loadData();
                }
            }
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        if(dialog!=null) {
                            dialog.dismiss();
                        }
                    }
                }
        );

    }
}
