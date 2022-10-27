package com.yunbao.dynamic.ui.activity;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VideoChooseBean;
import com.yunbao.common.utils.VideoLocalUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.adapter.SelectVideoAdapter;
import com.yunbao.im.config.CallConfig;

import java.io.File;
import java.util.List;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.DYNAMIC_VIDEO;
import static com.yunbao.common.utils.VideoLocalUtil.REQUEST_RECORD;

public class SelectVideoActivity extends AbsActivity implements View.OnClickListener {


    private RecyclerView reclyView;
    private SelectVideoAdapter selectVideoAdapter;
    private VideoLocalUtil videoLocalUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_video;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.all_video));
        setBackImageResoure(R.mipmap.icon_close_x);
        reclyView =findViewById(R.id.reclyView);

        RxRefreshView.ReclyViewSetting reclyViewSetting= RxRefreshView.ReclyViewSetting.createGridSetting(this,4,3);
        reclyViewSetting.settingRecyclerView(reclyView);

        selectVideoAdapter=new SelectVideoAdapter(null);
        selectVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectVideo(selectVideoAdapter.getData().get(position).getVideoPath());
            }
        });
        reclyView.setAdapter(selectVideoAdapter);
        videoLocalUtil=new VideoLocalUtil();
        videoLocalUtil.getLocalVideoList(new CommonCallback<List<VideoChooseBean>>() {
            @Override
            public void callback(List<VideoChooseBean> bean) {
                selectVideoAdapter.setData(bean);
            }
        });
        View headerView= LayoutInflater.from(this).inflate(R.layout.head_single_imageview,reclyView,false);
        DrawableTextView imageView=headerView.
                findViewById(R.id.draw_text_view);
        imageView.setTopDrawable(ContextCompat.getDrawable(this,R.mipmap.icon_take_video));
        imageView.setText(R.string.im_input_camera);
        selectVideoAdapter.setHeaderViewAsFlow(true);
        selectVideoAdapter.addHeaderView(headerView);
        headerView.setOnClickListener(this);
    }

    private void selectVideo(String videoPath) {
      if(!TextUtils.isEmpty(videoPath)){
          DynamicVideoActivity.forwordForResult(this,DynamicVideoActivity.TYPE_USE,videoPath,"");
      }
    }
    private File file;
    @Override
    public void onClick(View v) {
        if(CallConfig.isBusy()){
            ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.tip_please_close_chat_window));
            return;
        }
        file=videoLocalUtil.createMediaFile();
        videoLocalUtil.startOpenRecord(this,file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_RECORD&&resultCode==RESULT_OK){
            selectVideo(file.getAbsolutePath());
        }else if(requestCode==DYNAMIC_VIDEO&&resultCode==RESULT_OK){
            String url=data.getStringExtra(DATA);
            compelete(url);
        }
    }


    private void compelete(String url) {
        Intent intent=getIntent();
        if(!TextUtils.isEmpty(url)){
            intent.putExtra(DATA,url);
        }
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoLocalUtil.release();
    }
}
