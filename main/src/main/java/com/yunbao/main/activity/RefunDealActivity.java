package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.common.event.OrderStatusEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.RefundinfoBean;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

@Route(path = RouteUtil.PATH_ORDER_REFUND_DEAL)
public class RefunDealActivity extends AbsActivity implements View.OnClickListener {

    private TextView mTvTimeTip;
    private TextView mBtnRefuse;
    private TextView mBtnAgree;
    private RoundedImageView mImgAvatar;
    private TextView mTvSkillName;
    private TextView mTvSkillPrice;
    private TextView mTvReason;
    private TextView mTvRefundPrice;
    private String mCoinName;
    private RefundinfoBean mRefundinfoBean;

    @Override
    protected void main() {
        super.main();
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mTvTimeTip = (TextView) findViewById(R.id.tv_time_tip);
        mBtnRefuse = (TextView) findViewById(R.id.btn_refuse);
        mBtnAgree = (TextView) findViewById(R.id.btn_agree);
        mImgAvatar = (RoundedImageView) findViewById(R.id.img_avatar);
        mTvSkillName = (TextView) findViewById(R.id.tv_skill_name);
        mTvSkillPrice = (TextView) findViewById(R.id.tv_skill_price);
        mTvReason = (TextView) findViewById(R.id.tv_reason);
        mTvRefundPrice = (TextView) findViewById(R.id.tv_refund_price);

        mBtnAgree.setOnClickListener(this);
        mBtnRefuse.setOnClickListener(this);
        String orderId=getIntent().getStringExtra(Constants.ORDER_ID);
        getRefundInfo(orderId);
    }

    private void getRefundInfo(String orderId) {
        MainHttpUtil.getRefundinfo(orderId).compose(this.<List<RefundinfoBean>>bindToLifecycle()).subscribe(new DefaultObserver<List<RefundinfoBean>>() {
            @Override
            public void onNext(List<RefundinfoBean> list) {
                if(!ListUtil.haveData(list)){
                    finish();
                    return;
                }
                mRefundinfoBean=list.get(0);
                setUIData(mRefundinfoBean);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                finish();
            }


        });
    }


    private void setUIData(RefundinfoBean refundinfoBean) {
        String time=refundinfoBean.getDifftime_str();
        mTvTimeTip.setText(time);
        SkillBean skillBean=refundinfoBean.getSkill();

        if(skillBean!=null){
            ImgLoader.display(this,skillBean.getAuthThumb(),mImgAvatar);
            mTvSkillName.setText(skillBean.getSkillName());
            mTvSkillPrice.setText(skillBean.getSkillPrice()+mCoinName);
        }
        mTvReason.setText(getString(R.string.reason_for_refund_tip,refundinfoBean.getContent()));
        String totalMoney=refundinfoBean.getTotal()+mCoinName;
        mTvRefundPrice.setText(getString(R.string.money_for_refund_tip,totalMoney));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refun_deal;
    }

    public static void forward(Context context,String orderId){
        Intent intent=new Intent(context,RefunDealActivity.class);
        intent.putExtra(Constants.ORDER_ID,orderId);
        context.startActivity(intent);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(!ClickUtil.canClick()||mRefundinfoBean==null){
            return;
        }
        if(id==R.id.btn_refuse){
            setAgree(false);
        }else if(id==R.id.btn_agree){
            setAgree(true);
        }
    }


    private void setAgree(boolean isAgree) {
     final int status=isAgree? OrderBean.STATUS_AGREE_REFUND:OrderBean.STATUS_REFUSE_REFUND;
     MainHttpUtil.setRefundStatus(mRefundinfoBean.getOrderid(),status).compose(this.<Boolean>bindToLifecycle()).subscribe(new DialogObserver<Boolean>(this) {
         @Override
         public void onNext(Boolean aBoolean) {
             if(aBoolean){
                 OrderChangedEvent orderChangedEvent=  new OrderChangedEvent(mRefundinfoBean.getOrderid());
                 orderChangedEvent.setStatus(status);
                 EventBus.getDefault().post(orderChangedEvent);
                finish();
             }
         }
     });
    }
}
