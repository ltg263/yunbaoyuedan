package com.yunbao.dynamic.ui.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.adapter.SelectPhotoAdapter;
import com.yunbao.im.bean.ChatChooseImageBean;
import com.yunbao.im.utils.ImageUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.yunbao.common.Constants.DATA;

public class SelectPhotoActivity extends AbsActivity implements View.OnClickListener {

    private RecyclerView reclyView;
    private TextView tvRightTitle;
    private SelectPhotoAdapter adapter;
    private ImageUtil mImageUtil;
    private ProcessImageUtil processImageUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_photo;
    }
    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.all_photo));
        setBackImageResoure(R.mipmap.icon_close_x);

        tvRightTitle = findViewById(R.id.tv_right_title);
        reclyView =findViewById(R.id.reclyView);
        tvRightTitle.setOnClickListener(this);

        RxRefreshView.ReclyViewSetting reclyViewSetting= RxRefreshView.ReclyViewSetting.createGridSetting(this,4,3);
        reclyViewSetting.settingRecyclerView(reclyView);

        ArrayList<String>selectList=getIntent().getStringArrayListExtra(DATA);
        adapter=new SelectPhotoAdapter(null,selectList,this);
        adapter.setDataChangeLisnter(new SelectPhotoAdapter.DataChangeLisnter() {
            @Override
            public void change() {
               setPhotoNum();
            }
        });
        setPhotoNum();
        reclyView.setAdapter(adapter);
        mImageUtil=new ImageUtil();
        mImageUtil.getLocalImageList(new CommonCallback<List<ChatChooseImageBean>>() {
            @Override
            public void callback(List<ChatChooseImageBean> list) {
                adapter.setData(list);
            }
        });

        View headerView=LayoutInflater.from(this).inflate(R.layout.head_single_imageview,reclyView,false);
        DrawableTextView imageView=headerView.
                findViewById(R.id.draw_text_view);
        imageView.setTopDrawable(ContextCompat.getDrawable(this,R.mipmap.icon_take_photo));
        imageView.setText(R.string.im_input_camera);
        adapter.setHeaderViewAsFlow(true);
        adapter.addHeaderView(headerView);
        headerView.setOnClickListener(this);
        processImageUtil=new ProcessImageUtil(this);
        processImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {
            }
            @Override
            public void onSuccess(File file) {
                ChatChooseImageBean chatChooseImageBean= new ChatChooseImageBean(file);
                chatChooseImageBean.setChecked(true);
                adapter.addData(chatChooseImageBean);
                compelete();
            }
            @Override
            public void onFailure() {
            }
        });
    }

    private void setPhotoNum() {
       int size= adapter.getSelectImagePathList()==null?0:adapter.getSelectImagePathList().size();
        if(size==0){
          tvRightTitle.setText(WordUtil.getString(R.string.compelete));
          tvRightTitle.setEnabled(false);
        }else{
           tvRightTitle.setText(WordUtil.getString(R.string.photo_compelete,size, Constants.MAX_PHOTO_LENGTH));
           tvRightTitle.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.tv_right_title){
           compelete();
        }else{
            openCameRa();
        }
    }

    private void openCameRa() {
        if(!adapter.isLimit()){
          processImageUtil.getImageByCamera();
        }
    }


    private void compelete() {
      ArrayList<String> list=adapter.getSelectImagePathList();
      Intent intent=getIntent();
      intent.putExtra(DATA,list);
      setResult(RESULT_OK,intent);
      finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageUtil.release();
    }

}
