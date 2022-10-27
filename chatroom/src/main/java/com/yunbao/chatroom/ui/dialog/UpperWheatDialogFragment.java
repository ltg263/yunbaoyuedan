package com.yunbao.chatroom.ui.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.socket.SuccessListner;

/*用户申请上麦*/
public class UpperWheatDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RoundedImageView mImgAvator;
    private TextView mTvName;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_upper_wheat;
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
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (SystemUtil.getWindowsPixelHeight(getActivity())*0.5);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        super.init();
        mImgAvator = (RoundedImageView) findViewById(R.id.img_avator);
        mTvName = (TextView) findViewById(R.id.tv_name);
        setOnClickListener(R.id.btn_confirm,this);

        setData();
    }

    private void setData() {
           UserBean userBean=CommonAppConfig.getInstance().getUserBean();
           ImgLoader.display(getContext(),userBean.getAvatar(),mImgAvator);
           mTvName.setText(userBean.getUserNiceName());
    }

    @Override
    public void onClick(View v) {
       ApplyQuequeBehavior applyQuequeBehavior= CacheBehaviorFactory.getInstance().getApplyQueBehavior(getActivity());
        if( applyQuequeBehavior==null){
            return;
        }
        applyQuequeBehavior.applyUpWheat(this, new SuccessListner() {
            @Override
            public void success() {
                CacheBehaviorFactory.setApplying(true,getActivity());
                openLineUpDialog();
                dismiss();
            }
        });
    }
    private void openLineUpDialog() {
        LineUpDialogFragment lineUpDialogFragment=new LineUpDialogFragment();
        lineUpDialogFragment.show(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImgAvator=null;
        mTvName=null;
    }
}
