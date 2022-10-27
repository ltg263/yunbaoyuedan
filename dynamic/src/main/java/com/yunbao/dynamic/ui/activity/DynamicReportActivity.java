package com.yunbao.dynamic.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.radio.CheckEntity;
import com.yunbao.common.adapter.radio.IRadioChecker;
import com.yunbao.common.adapter.radio.RadioAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.interfaces.ActivityResultCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.upload.FileUploadManager;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadQnImpl;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.im.activity.ChatChooseImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.os.Build.ID;

public class DynamicReportActivity extends AbsActivity implements View.OnClickListener {
    private RecyclerView reclyView;
    private TextView tvDes;
    private RadioAdapter<CheckEntity>radioAdapter;
    private String did;
    private static final String TAG = "UserReportActivity";
    private ImageView iv_btn_img_1;
    private ImageView iv_btn_img_2;
    private ImageView iv_btn_img_3;
    private ProcessImageUtil mImageUtil;
    private UploadStrategy uploadStrategy;
    private String mImgFilePath1;
    private String mImgFilePath2;
    private String mImgFilePath3;
    private int mSelectedImgPos = 0;
    private Dialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dynamic_report;
    }
    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.report));
        did=getIntent().getStringExtra(ID);
        if(TextUtils.isEmpty(did)){
            return;
        }
        reclyView = (RecyclerView) findViewById(R.id.reclyView);
        tvDes = (TextView) findViewById(R.id.tv_des);
        iv_btn_img_1 = findViewById(R.id.iv_btn_img_1);
        iv_btn_img_2 = findViewById(R.id.iv_btn_img_2);
        iv_btn_img_3 = findViewById(R.id.iv_btn_img_3);
        iv_btn_img_1.setOnClickListener(this);
        iv_btn_img_2.setOnClickListener(this);
        iv_btn_img_3.setOnClickListener(this);
        setOnClickListener(R.id.btn_confirm,this);
        RxRefreshView.ReclyViewSetting.createLinearSetting(this,1).settingRecyclerView(reclyView);
        radioAdapter=new RadioAdapter<CheckEntity>(null){
            @Override
            public int getLayoutId() {
                return R.layout.item_dynamic_report;
            }
        };
        reclyView.setAdapter(radioAdapter);
        getData();
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                L.e(TAG,file.getAbsolutePath());
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void getData() {
        DynamicHttpUtil.getDynamicReport().compose(this.<List<CheckEntity>>bindToLifecycle()).subscribe(new DefaultObserver<List<CheckEntity>>() {
            @Override
            public void onNext(List<CheckEntity> data) {
                radioAdapter.setData(data);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_confirm){
            checkExitImgs();
        }else if (id == R.id.iv_btn_img_1){
            mSelectedImgPos = 1;
            checkReadWritePermissions();
        }else if (id == R.id.iv_btn_img_2){
            mSelectedImgPos = 2;
            checkReadWritePermissions();
        }else if (id == R.id.iv_btn_img_3){
            mSelectedImgPos = 3;
            checkReadWritePermissions();
        }
    }

    public static void forward(Context context,String did){
        Intent intent=new Intent(context,DynamicReportActivity.class);
        intent.putExtra(ID,did);
        context.startActivity(intent);
    }

    private void checkExitImgs(){
        if(radioAdapter==null||TextUtils.isEmpty(radioAdapter.getId())){
            ToastUtil.show(R.string.please_sel_report_reason);
            return;
        }
        List<UploadBean> uploadBeanList=new ArrayList<>();
        if (mImgFilePath1 != null){
            uploadBeanList.add(new UploadBean(new File(mImgFilePath1)));
        }
        if (mImgFilePath2 != null){
            uploadBeanList.add(new UploadBean(new File(mImgFilePath2)));
        }
        if (mImgFilePath3 != null){
            uploadBeanList.add(new UploadBean(new File(mImgFilePath3)));
        }
        if (uploadBeanList.isEmpty()){
            commit("");
        }else {
            showLoadingDialog();
            upLoadResource(false,uploadBeanList);
        }
    }



    private void commit(String thumbs) {
        String des=tvDes.getText().toString();
        String content=radioAdapter.getSelectData().getContent();
        if(!TextUtils.isEmpty(des)){
            content=content+"\t"+des;
        }
        DynamicHttpUtil.setDynamicReport(did,content,thumbs).compose(this.<Boolean>bindToLifecycle()).subscribe(new DialogObserver<Boolean>(this) {
         @Override
         public void onNext(Boolean aBoolean) {
             if(aBoolean){
                 finish();
             }
         }
     });
    }


    private void checkReadWritePermissions() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new CommonCallback<Boolean>() {
                    @Override
                    public void callback(Boolean result) {
                        if (result) {
                            forwardChooseImage();
                        }
                    }
                });
    }

    /**
     * 前往选择图片页面
     */
    private void forwardChooseImage() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.startActivityForResult(new Intent(mContext, ChatChooseImageActivity.class), new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String imagePath = intent.getStringExtra(Constants.SELECT_IMAGE_PATH);
                    if (mSelectedImgPos == 1){
                        mImgFilePath1 = imagePath;
                        iv_btn_img_1.setImageURI(Uri.fromFile(new File(imagePath)));
                        iv_btn_img_1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }else if (mSelectedImgPos==2){
                        mImgFilePath2 = imagePath;
                        iv_btn_img_2.setImageURI(Uri.fromFile(new File(imagePath)));
                        iv_btn_img_2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }else if (mSelectedImgPos == 3){
                        mImgFilePath3 = imagePath;
                        iv_btn_img_3.setImageURI(Uri.fromFile(new File(imagePath)));
                        iv_btn_img_3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    L.e(TAG,imagePath);
                }
            }
        });
    }

    //上传网络资源
    private void upLoadResource(final boolean isNeedCompass, final List<UploadBean> uploadBeanList) {
        FileUploadManager.getInstance().createUploadImpl(mContext, new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                if (strategy == null){
                    ToastUtil.show(WordUtil.getString(R.string.upload_type_error));
                    return;
                }
                uploadStrategy = strategy;
                uploadStrategy.upload(uploadBeanList, isNeedCompass, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        if (success) {
                            StringBuilder builder=new StringBuilder();
                            for(UploadBean bean:list){
                                builder.append(bean.getRemoteFileName())
                                        .append(",") ;
                            }
                            int length=builder.length();
                            if(length>0){
                                builder.deleteCharAt(length-1);
                            }
                            commit(builder.toString());
                            disMissLoadingDialog();
                        }else {
                            disMissLoadingDialog();
                        }
                    }
                });
            }
        });
    }

    //设置等待框
    private void showLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }
        mDialog = DialogUitl.loadingDialog(this);
        mDialog.show();
    }

    //关闭等待框
    public void disMissLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }


}
