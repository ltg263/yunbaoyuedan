package com.yunbao.chatroom.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yunbao.common.custom.viewanimator.AnimationBuilder;
import com.yunbao.common.custom.viewanimator.AnimationListener;
import com.yunbao.common.custom.viewanimator.ViewAnimator;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.chatroom.R;

/**
 * Created by Sky.L on 2020-12-03
 */
public class MakePairFailureDialogFragment extends AbsDialogFragment {
    private static final String TAG = "MakePairFailureDialogFr";
    private LinearLayout mContainer;
    private FrameLayout mGroupLeft;
    private ImageView mImgTip;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_make_pair_failure;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height =  WindowManager.LayoutParams.MATCH_PARENT;
//        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContainer = findViewById(R.id.container);
        mGroupLeft = findViewById(R.id.group_left);
        mImgTip = findViewById(R.id.img_failure_tip);
        startPlayAnim();
    }

    private AnimationBuilder mAnimationBuilder;
    private void startPlayAnim(){
        mAnimationBuilder= ViewAnimator.
                animate(mContainer)
                .alpha(0,1).duration(100)
                .thenAnimate(mGroupLeft).slideLeftIn().
                        duration(1000).
                        thenAnimate(mImgTip)
                .duration(500).zoomIn().
                        thenAnimate(mContainer,mImgTip).alpha(1,0).duration(500).
                        onStop(new AnimationListener.Stop() {
                            @Override
                            public void onStop() {
                                mGroupLeft.setTranslationX(-DpUtil.dp2px(300));
                                L.e(TAG,"结束动画了");
                                dismiss();
                            }
                        });
        mAnimationBuilder.startDelay(3000).start();
        L.e("mAnimationBuilder 执行");
    }

}
