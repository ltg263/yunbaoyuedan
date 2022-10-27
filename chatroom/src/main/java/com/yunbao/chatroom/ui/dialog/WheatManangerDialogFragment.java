package com.yunbao.chatroom.ui.dialog;

import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yunbao.common.Constants;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.ui.view.SpeakTurnViewHolder;
import com.yunbao.chatroom.ui.view.WheatManangerViewHolder;

/*控麦管理*/
public class WheatManangerDialogFragment extends AbsDialogFragment implements SpeakTurnViewHolder.WheelSpeakOpenListner {
    private WheatManangerViewHolder mWheatManangerViewHolder;
    private SpeakTurnViewHolder mSpeakTurnViewHolder;
    private ViewGroup mContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wheat_mannger;
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
        params.height = (int) (SystemUtil.getWindowsPixelHeight(getActivity())*0.79);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        super.init();
        mContainer = (ViewGroup) findViewById(R.id.container);
        mWheatManangerViewHolder=new WheatManangerViewHolder(getContext(),mContainer);
        mWheatManangerViewHolder.addToParent();

        LiveActivityLifeModel liveActivityLifeModel=LifeObjectHolder.getByContext(getActivity(),LiveActivityLifeModel.class);
        if(liveActivityLifeModel.getLiveType()== Constants.LIVE_TYPE_DISPATCH){
            mSpeakTurnViewHolder=new SpeakTurnViewHolder(getContext(), (ViewGroup) mRootView,this);
            mSpeakTurnViewHolder.addToParent();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWheatManangerViewHolder!=null){
           mWheatManangerViewHolder.onDestroy();
        }
        if(mSpeakTurnViewHolder!=null){
           mSpeakTurnViewHolder.onDestroy();
        }
    }

    @Override
    public void open(boolean isOpen) {
        if(mWheatManangerViewHolder!=null){
           mWheatManangerViewHolder.setNotCanSwitchWheat(isOpen);
        }
    }
}
