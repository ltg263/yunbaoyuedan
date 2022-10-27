package com.yunbao.common.dialog;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.mob.MobBean;
import com.yunbao.common.R;
import com.yunbao.common.adapter.CommonShareAdapter;

/**
 * Created by cxf on 2018/10/19.
 * 直播分享弹窗
 */

public class CommonShareDialogFragment extends AbsDialogFragment implements OnItemClickListener<MobBean> {

    private RecyclerView mRecyclerView;
    private ActionListener mActionListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_share;
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
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        CommonShareAdapter adapter = initAdapter();
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    protected CommonShareAdapter initAdapter() {
        return new CommonShareAdapter(getActivity());
    }

    @Override
    public void onItemClick(MobBean bean, int position) {
        if(!canClick()){
            return;
        }
        dismiss();
        if(mActionListener!=null){
            mActionListener.onItemClick(bean.getType());
        }
    }

    public interface ActionListener{
        void onItemClick(String type);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext=null;
        mActionListener=null;
    }
}
