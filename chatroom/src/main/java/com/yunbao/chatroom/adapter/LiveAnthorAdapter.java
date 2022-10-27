package com.yunbao.chatroom.adapter;

import android.widget.ImageView;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.chatroom.widet.StateView;
import java.util.List;

public class LiveAnthorAdapter extends BaseLiveAnthorAdapter<LiveAnthorBean> {
    private StateView.State defaultNormalState;
    private StateView.State defaultBossState;

    public LiveAnthorAdapter(List<LiveAnthorBean> data, ValueFrameAnimator valueFrameAnimator) {
        super(data, valueFrameAnimator);
        init();
    }

    private void init(){
        defaultNormalState=new StateView.State();
        defaultNormalState.avator=R.mipmap.icon_live_mike;
        defaultNormalState.name= WordUtil.getString(R.string.wait_wheat);
        defaultNormalState.color=R.color.white;

        defaultBossState=new StateView.State();
        defaultBossState.color=R.color.yellow;
        defaultBossState.avator=R.mipmap.icon_boss;
        defaultBossState.name=WordUtil.getString(R.string.boss_position);
    }


    @Override
    public int getLayoutId() {
        return R.layout.item_recly_live_anthor;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, LiveAnthorBean item) {
        StateView stateView=helper.getView(R.id.state_view);
        ImageView bgImageView=stateView.getBgImageView();
        if(item.isBoss()){
            stateView.setDefaultState(defaultBossState);
        }else{
            stateView.setDefaultState(defaultNormalState);
        }
        StateView.State state=transForm(item.getUserBean(),item.isBoss());
        stateView.setSelectState(state);
        stateView.setState(item.getUserBean()!=null);

        if(item.isCurrentSpeak()&&item.getUserBean()!=null){
            mValueFrameAnimator.addAnim(bgImageView);
        }else {
            mValueFrameAnimator.removeAnim(bgImageView);
            bgImageView.setImageResource(0);
        }
    }


    private StateView.State transForm(UserBean userBean, boolean isBoss) {
        StateView.State state=new StateView.State();
        if(userBean!=null){
            state.name=userBean.getUserNiceName();
            state.avator=userBean.getAvatar();
            state.color=isBoss?R.color.yellow:R.color.white;
            state.frame=isBoss?R.drawable.bound_aval_yellow:R.drawable.bound_aval_white;
        }
        return state;
    }

    @Override
    public void release() {
        if(mValueFrameAnimator!=null){
            mValueFrameAnimator.release();
        }
    }
}
