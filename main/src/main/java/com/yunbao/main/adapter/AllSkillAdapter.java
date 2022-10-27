package com.yunbao.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.main.R;
import com.yunbao.main.bean.AllSkillSectionBean;
import com.yunbao.common.bean.SkillBean;
import java.util.List;

public class AllSkillAdapter extends BaseSectionQuickAdapter<AllSkillSectionBean, BaseViewHolder> implements RxRefreshView.DataAdapter<AllSkillSectionBean> {
    private CompoundButton mSelCheckBox;

    public AllSkillAdapter(int layoutResId, int sectionHeadResId, List<AllSkillSectionBean> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, AllSkillSectionBean item) {
        helper.setText(R.id.tv_tile,item.header);
    }
    @Override
    protected void convert(BaseViewHolder helper, AllSkillSectionBean item) {
        SkillBean entity= item.t;
        CheckBox checkBox=helper.getView(R.id.checkbox);
        checkBox.setText(entity.getSkillName2());
        checkBox.setTag(item);
        if (entity.isSelected()){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void setData(List<AllSkillSectionBean> data) {
        setNewData(data);
    }

    @Override
    public void appendData(List<AllSkillSectionBean> data) {
        if(mData!=null&&data!=null){
          mData.addAll(data);
        }
    }

    @Override
    public List<AllSkillSectionBean> getArray() {
        return mData;
    }

    @Override
    public RecyclerView.Adapter returnRecyclerAdapter() {
        return this;
    }

    @Override
    public void notifyReclyDataChange() {
        notifyDataSetChanged();
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if(isChecked){
                  if(mSelCheckBox!=null&&mSelCheckBox!=buttonView){
                      mSelCheckBox.setChecked(false);
                  }
                      mSelCheckBox=buttonView;
              }
        }
    };


    private SkillBean getSelData() {
        if(mSelCheckBox!=null){
            SkillBean data= (SkillBean) mSelCheckBox.getTag();
           if(data!=null){
               return data;
           }
        }
        return null;
    }

}
