package com.yunbao.chatroom.ui.view.seat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.chatroom.adapter.BaseLiveAnthorAdapter;
import com.yunbao.chatroom.adapter.LiveAnthorAdapter;
import java.util.List;

public class LiveSpatchViewHolder extends AbsLiveSeatViewHolder  {

    public LiveSpatchViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public BaseLiveAnthorAdapter initAdapter() {
        return new LiveAnthorAdapter(null,valueFrameAnimator);
    }


    @Override
    public void setData(List userBeanList) {
        if(userBeanList!=null&&userBeanList.size()==8){
           LiveAnthorBean liveAnthorBean= (LiveAnthorBean) userBeanList.get(7);
          liveAnthorBean.setBoss(true);
        }
        super.setData(userBeanList);
    }




    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        openItemOpenUserDialog(position);
    }

}
