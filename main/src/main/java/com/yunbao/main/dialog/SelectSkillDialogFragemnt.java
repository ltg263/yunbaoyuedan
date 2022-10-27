package com.yunbao.main.dialog;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.common.adapter.radio.RadioAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.MySkillBean;

import java.util.List;

public class SelectSkillDialogFragemnt extends AbsDialogFragment implements View.OnClickListener {
    private TextView mBtnCancel;
    private TextView mBtnConfirm;
    private RecyclerView mReclyView;
    private RadioAdapter<MySkillBean> mRadioAdapter;

    private OnSelectListner mOnSelectListner;

    private List<MySkillBean>mMySkillBeanList;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_choose_skill;
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
        window.setWindowAnimations(com.yunbao.common.R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(200);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        super.init();
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
        mBtnConfirm = (TextView) findViewById(R.id.btn_confirm);
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);

        mReclyView = (RecyclerView) findViewById(R.id.reclyView);
        RxRefreshView.ReclyViewSetting.createLinearSetting(mContext,1).
        settingRecyclerView(mReclyView);
        mRadioAdapter=new RadioAdapter<MySkillBean>(mMySkillBeanList){
            @Override
            public int getLayoutId() {
                return R.layout.item_recly_sel_skill;
            }
            @Override
            public int checkId() {
                return R.id.check;
            }
            @Override
            public int contentViewId() {
                return R.id.check;
            }
        };
        mReclyView.setAdapter(mRadioAdapter);
    }

    public void setMySkillBeanList(List<MySkillBean> mySkillBeanList) {
        mMySkillBeanList = mySkillBeanList;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_cancel){
            dismiss();
        }else if(id==R.id.btn_confirm){
            confrim();
        }
    }


    private void confrim() {
        if(mOnSelectListner!=null&&mRadioAdapter.getSelectData()!=null){
            mOnSelectListner.onSelect(mRadioAdapter.getSelectData());
            dismiss();
        }else{
            ToastUtil.show(R.string.please_choose_skill);
        }
    }

    public void setOnSelectListner(OnSelectListner onSelectListner) {
        mOnSelectListner = onSelectListner;
    }

    public interface OnSelectListner{
        public void onSelect(MySkillBean mySkillBean);
    }
}
