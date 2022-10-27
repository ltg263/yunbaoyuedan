package com.yunbao.dynamic.ui.dialog;

import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.ui.view.DynamicCommentViewHolder;

public class CommentDialogFragment extends AbsDialogFragment {
    private FrameLayout mFlContainer;
    private DynamicBean mDynamicBean;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_comment;
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
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(350);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.bottomToTopAnim);
    }

    @Override
    public void init() {
        super.init();
        mFlContainer = (FrameLayout) findViewById(R.id.fl_container);
        DynamicCommentViewHolder dynamicCommentViewHolder=new DynamicCommentViewHolder(mContext,mFlContainer,mDynamicBean,false);
        dynamicCommentViewHolder.addToParent();
    }

    public void setDynamicBean(DynamicBean mDynamicBean) {
        this.mDynamicBean = mDynamicBean;
    }
}
