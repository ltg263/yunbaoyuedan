package com.yunbao.main.adapter;

import android.text.TextUtils;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.TxLocationPoiBean;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import java.util.List;

public class LocationAdapter  extends BaseRecyclerAdapter<TxLocationPoiBean, BaseReclyViewHolder> {
    public LocationAdapter(List<TxLocationPoiBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_pub_location;
    }
    @Override
    protected void convert(BaseReclyViewHolder helper, TxLocationPoiBean item) {
                helper.setText(R.id.tv_title,item.getTitle());
                helper.setText(R.id.tv_address,item.getAddress());
                if(TextUtils.isEmpty(item.getAddress())){
                    helper.setVisible(R.id.tv_address,false);
                }else{
                    helper.setVisible(R.id.tv_address,true);
                }
    }

    @Override
    public void setData(List<TxLocationPoiBean> data) {
        if(data!=null){
            TxLocationPoiBean txLocationPoiBean=new TxLocationPoiBean();
            txLocationPoiBean.setTitle(WordUtil.getString(R.string.no_show_location));
            data.add(0,txLocationPoiBean);
        }
        super.setData(data);
    }
}
