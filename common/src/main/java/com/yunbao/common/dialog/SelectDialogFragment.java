package com.yunbao.common.dialog;


import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.yunbao.common.R;
import com.yunbao.common.adapter.radio.IRadioChecker;
import com.yunbao.common.adapter.radio.RadioAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import java.util.List;

public  class SelectDialogFragment<T extends IRadioChecker> extends AbsDialogFragment implements View.OnClickListener{
    private TextView mBtnCancel;
    private TextView mBtnConfirm;
    private RecyclerView mReclyView;
    private RadioAdapter<T> mRadioAdapter;
    private String mSelectId;
    private String mNoSelectTip;


    private OnSelectListner<T> mOnSelectListner;
    private List<T> mData;
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_select;
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
        new LinearSnapHelper().attachToRecyclerView(mReclyView);
        RxRefreshView.ReclyViewSetting.createLinearSetting(mContext,1).
                settingRecyclerView(mReclyView);

        if(mNoSelectTip==null){
            mNoSelectTip = WordUtil.getString(R.string.please_choose_skill);
        }

        mRadioAdapter=new RadioAdapter<T>(null){
            @Override
            public int getLayoutId() {
                return getRecyclerLayouId();
            }
            @Override
            public int checkId() {
                return getRecyclerCheckId();
            }
            @Override
            public int contentViewId() {
                return getRecyclerContentId();
            }
        };

        mReclyView.setAdapter(mRadioAdapter);
        mRadioAdapter.setData(mData);
        int index=mRadioAdapter.setDefaultSelect(mSelectId);
        if(index!=-1){
          mReclyView.scrollToPosition(index);
        }

    }

    public  int getRecyclerContentId(){
        return R.id.check;
    }
    public  int getRecyclerCheckId(){
        return R.id.check;
    }
    public  int getRecyclerLayouId(){
        return R.layout.item_recly_select_default;
    }

    public void setList(List<T> data) {
        mData = data;
    }
  /*  public boolean isNeedDialogListner(){
        return false;
    }*/
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
            ToastUtil.show(mNoSelectTip);
        }
    }


    public void setNoSelectTip(String noSelectTip) {
        mNoSelectTip = noSelectTip;
    }

    public void setSelect(String id){
        this.mSelectId=id;
    }

    public void setOnSelectListner(OnSelectListner<T> onSelectListner) {
        mOnSelectListner = onSelectListner;
    }

    public interface OnSelectListner<T>{
        public void onSelect(T t);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBtnCancel=null;
        mBtnConfirm=null;
        mData=null;
        mOnSelectListner=null;
        if(mRadioAdapter!=null){
          mRadioAdapter.setOnItemChildClickListener(null);
          mRadioAdapter.setData(null);
         mRadioAdapter.setOnItemChildClickListener(null);
         mRadioAdapter=null;
        }
    }

}
