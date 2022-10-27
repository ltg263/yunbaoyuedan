package com.yunbao.chatroom.ui.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.adapter.CommonShareAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.dialog.CommonShareDialogFragment;
import com.yunbao.common.mob.MobShareUtil;
import com.yunbao.common.mob.ShareData;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;

public class LiveShareDialogFragment extends CommonShareDialogFragment {

    private MobShareUtil mMobShareUtil;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionListener(new ActionListener() {
            @Override
            public void onItemClick(String type) {
                String link=getLink();
                if(TextUtils.isEmpty(link)){
                    return;
                }
                if (Constants.COPY.equals(type)) {
                    copyLink(link);
                } else {
                    shareLive(type, link);
                }
            }
        });
    }

    @Override
    protected CommonShareAdapter initAdapter() {
        CommonShareAdapter adapter= super.initAdapter();
        adapter.addLink();
        return adapter;
    }

    private void shareLive(String type, String  link) {
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        ShareData data = new ShareData();
        data.setTitle(configBean.getAgentShareTitle());
        data.setDes(configBean.getAgentShareDes());
        data.setImgUrl(CommonAppConfig.getInstance().getUserBean().getAvatarThumb());
        data.setWebUrl(link);
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        mMobShareUtil.execute(type, data, null);
    }

    private void copyLink(String link) {
        if(TextUtils.isEmpty(link)){
            return;
        }
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", link);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }
   private String linkUrl;
    private String getLink() {
        if(linkUrl==null){
            LiveActivityLifeModel liveActivityLifeModel= LifeObjectHolder.getByContext(getActivity(),LiveActivityLifeModel.class);
            if(liveActivityLifeModel==null||liveActivityLifeModel.getLiveBean()==null){
                return null;
            }
            linkUrl= HtmlConfig.MAKE_MONEY+liveActivityLifeModel.getLiveBean().getInviteCode();
        }
        return linkUrl;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMobShareUtil!=null){
            mMobShareUtil.release();
        }
    }
}
