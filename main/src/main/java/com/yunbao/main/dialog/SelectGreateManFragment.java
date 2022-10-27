package com.yunbao.main.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.MoneyHelper;
import com.yunbao.common.custom.ItemLinearLayout;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.pay.PayCallback;
import com.yunbao.main.R;
import com.yunbao.main.bean.GreateManBean;
import com.yunbao.main.bean.SnapOrderBean;

public class SelectGreateManFragment extends AbsDialogFragment implements View.OnClickListener {
    private RoundedImageView imgAvator;
    private TextView tvName;
    private TextView tvLevel;
    private ItemLinearLayout vpSkill;
    private ItemLinearLayout vpTime;
    private ItemLinearLayout vpPrice;
    private GreateManBean greateManBean;

    private SnapOrderBean snapOrderBean;
    private int totalCoin;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_sel_greateman;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(com.yunbao.common.R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        super.init();
        if(snapOrderBean==null||greateManBean==null){
            dismiss();
        }
        imgAvator = (RoundedImageView) findViewById(R.id.img_avator);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvLevel = (TextView) findViewById(R.id.tv_level);
        vpSkill = (ItemLinearLayout) findViewById(R.id.vp_skill);
        vpTime = (ItemLinearLayout) findViewById(R.id.vp_time);
        vpPrice = (ItemLinearLayout) findViewById(R.id.vp_price);
        setOnClickListener(R.id.btn_order,this);
        setData();
    }

    private void setData() {
        if(greateManBean!=null&&snapOrderBean!=null){

            UserBean userBean= greateManBean.getUserinfo();
            if(userBean!=null){
                ImgLoader.display(mContext,userBean.getAvatar(),imgAvator);
                tvName.setText(userBean.getUserNiceName());
            }
            SkillBean skillBean=greateManBean.getAuthinfo();
            totalCoin=skillBean.getPriceVal()*snapOrderBean.getOrderNum();
            if(skillBean!=null){
                tvLevel.setText(skillBean.getSkillLevel());
                vpSkill.setContent(skillBean.getSkillName());
                vpTime.setContent(snapOrderBean.getServiceTimeFormat() +"  "+ snapOrderBean.getTotalUnit());
                vpPrice.setContent(MoneyHelper.moneySymbol(totalCoin,MoneyHelper.TYPE_PLATFORM));
            }
        }
    }
    public void setGreateManBean(GreateManBean greateManBean) {
        this.greateManBean = greateManBean;
    }
    @Override
    public void onClick(View v) {
    int id=v.getId();
    if(id==R.id.btn_order){
        openPayDialog();
     }
    }

    private void openPayDialog() {
        if(greateManBean==null){
            return;
        }
        PayDialogFragment payDialogFragment=new PayDialogFragment();
        payDialogFragment.setDripid(greateManBean.getDripid())
        .setLiveuid(greateManBean.getLiveuid());
        payDialogFragment.setmPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                ((Activity)getContext()).finish();
            }
            @Override
            public void onFailed() {
            }
        });
        SkillBean skillBean=greateManBean.getAuthinfo();
        if(skillBean!=null){
            payDialogFragment.setTotalPrice(totalCoin);
        }
        payDialogFragment.show(getFragmentManager());
    }


    public void setSnapOrderBean(SnapOrderBean snapOrderBean) {
        this.snapOrderBean = snapOrderBean;
    }
}
