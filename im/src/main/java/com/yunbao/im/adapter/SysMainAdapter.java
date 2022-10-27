package com.yunbao.im.adapter;

import com.yunbao.common.adapter.base.BaseMutiRecyclerAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.im.R;
import com.yunbao.im.bean.IMLiveBean;

import java.util.List;

public class SysMainAdapter extends BaseMutiRecyclerAdapter<IMLiveBean, BaseReclyViewHolder> {
    public SysMainAdapter(List<IMLiveBean> data) {
        super(data);
        addItemType(IMLiveBean.TYPE_LIVE,R.layout.item_recly_sys_main);
        addItemType(IMLiveBean.TYPE_AUTH_NOTICE,R.layout.item_recly_main_auth_notice);
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, IMLiveBean item) {
        switch (helper.getItemViewType()){
            case IMLiveBean.TYPE_LIVE:
                convertLive(helper,item);
                break;
            case IMLiveBean.TYPE_AUTH_NOTICE:
                convertAuthNotice(helper,item);
                break;
            default:
                break;
        }
    }


    private void convertLive(BaseReclyViewHolder helper, IMLiveBean item) {
        helper.setText(R.id.tv_skill_name, StringUtil.contact("技能：",item.getSkillname()));
        helper.setText(R.id.tv_host, StringUtil.contact("主持人：",item.getUserNiceName()));
        helper.setText(R.id.time,item.getTime() );
        helper.addOnClickListener(R.id.btn_confirm);
    }

    private void convertAuthNotice(BaseReclyViewHolder helper, IMLiveBean item) {
        helper.setText(R.id.time,item.getTime() );
        helper.setText(R.id.tv_content, item.getTip_des());
    }

}
