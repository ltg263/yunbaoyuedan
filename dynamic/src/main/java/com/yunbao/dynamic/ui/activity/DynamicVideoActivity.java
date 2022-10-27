package com.yunbao.dynamic.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.video.ui.activity.VideoPlayActivity;
import com.yunbao.common.Constants;
import com.yunbao.dynamic.R;

import static com.yunbao.common.Constants.COVER;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.DYNAMIC_PHOTO;
import static com.yunbao.common.Constants.DYNAMIC_VIDEO;
import static com.yunbao.common.Constants.TYPE;
import static com.yunbao.common.Constants.URL;

public class DynamicVideoActivity extends VideoPlayActivity implements View.OnClickListener {
    public static final int TYPE_USE=1;
    public static final int TYPE_EDIT=2;
    public static final int TYPE_WATCH=3;
    private int type;
    private String url;
    private String cover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_dynamic_video;
    }
    @Override
    public ViewGroup getVideoContainerView() {
        return findViewById(R.id.video_container);
    }

    @Override
    protected void main() {
        super.main();
        setTabBackGroudColor("#cc000000");
        type=getIntent().getIntExtra(TYPE,TYPE_USE);
        if(type==TYPE_USE){
            TextView tvRight= setRightTitle(getString(R.string.use_video));
            tvRight .setOnClickListener(this);
            tvRight.setTextColor(Color.WHITE);
        }else if(type==TYPE_EDIT){
            setTitle(R.string.preview);
            View tvDelete= findViewById(R.id.tv_delete);
            tvDelete.setVisibility(View.VISIBLE);
            tvDelete.setOnClickListener(this);
        }else{

        }
        url=getIntent().getStringExtra(URL);
        cover=getIntent().getStringExtra(COVER);
        startPlay(url,cover);
    }


    public static void forword(Context context, int type, String url,String cover){
        Intent intent=new Intent(context,DynamicVideoActivity.class);
        intent.putExtra(TYPE,type);
        intent.putExtra(URL,url);
        intent.putExtra(COVER,cover);

        context.startActivity(intent);
    }


    public static void forwordForResult(Activity context, int type, String url,String cover){
        Intent intent=new Intent(context,DynamicVideoActivity.class);
        intent.putExtra(TYPE,type);
        intent.putExtra(URL,url);
        intent.putExtra(COVER,cover);
        context.startActivityForResult(intent,DYNAMIC_VIDEO);
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.tv_delete){
            url=null;
            compelete();
        }else if(id==R.id.tv_right_title){
            compelete();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void compelete() {
        Intent intent=getIntent();
        if(!TextUtils.isEmpty(url)){
           intent.putExtra(DATA,url);
        }
        setResult(RESULT_OK,intent);
        finish();
    }
}
