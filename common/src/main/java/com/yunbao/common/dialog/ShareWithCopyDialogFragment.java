package com.yunbao.common.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.R;
import com.yunbao.common.adapter.CommonShareAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.mob.MobShareUtil;
import com.yunbao.common.mob.ShareData;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

public class ShareWithCopyDialogFragment extends CommonShareDialogFragment {

    private MobShareUtil mMobShareUtil;
    private String mLinkUrl;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        mLinkUrl = bundle.getString(Constants.SHARE_WEB_URL);
        setActionListener(new ActionListener() {
            @Override
            public void onItemClick(String type) {
                if(TextUtils.isEmpty(mLinkUrl)){
                    return;
                }
                if (Constants.COPY.equals(type)) {
                    copyLink(mLinkUrl);
                } else {
                    shareLive(type, mLinkUrl);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMobShareUtil!=null){
            mMobShareUtil.release();
        }
    }
}
