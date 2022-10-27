package com.yunbao.chatroom.ui.view.bottom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.ui.dialog.LiveOrderDialog;

public class LiveDisPatchHostViewHolder extends LiveHostBottomViewHolder {
    public LiveDisPatchHostViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_dispatch_bottom_host;
    }

    @Override
    public void init() {
        super.init();
        setOnClickListner(R.id.btn_order,this);
        //setOnClickListner(R.id.btn_msg,this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id=v.getId();
         if(id==R.id.btn_order){
            openOrderDialog();
        }
//         else if (id == R.id.btn_msg){
//             openChatListWindow();
//         }
    }

    /*打开推送订单dialog*/
    private void openOrderDialog() {
        if(mFragmentActivity!=null){
            LiveOrderDialog liveOrderDialog=new LiveOrderDialog();
            liveOrderDialog.show(mFragmentActivity.getSupportFragmentManager());
        }
    }

}
