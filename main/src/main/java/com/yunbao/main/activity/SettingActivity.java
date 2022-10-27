package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.GlideCatchUtil;
import com.yunbao.common.utils.LanguageUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VersionUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.main.R;

import java.io.File;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingActivity extends AbsActivity {

    private TextView mVersion;
    private TextView mCacheSize;
    private Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.setting));
        mVersion = findViewById(R.id.version);
        mCacheSize = findViewById(R.id.cache_size);
        mVersion.setText(VersionUtil.getVersion());
        mCacheSize.setText(getCacheSize());
        TextView lang = findViewById(R.id.lang);
        lang.setText(LanguageUtil.isEn() ? R.string.lang_en : R.string.lang_zh);
    }


    public void settingClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_about_us) {
            WebViewActivity.forward(mContext, HtmlConfig.ABOUT_US);
        } else if (i == R.id.btn_check_update) {
            checkVersion();
        } else if (i == R.id.btn_clear_cache) {
            clearCache();
        } else if (i == R.id.btn_logout) {
            logout();
        } else if (i == R.id.btn_lang) {
            chooseLanguage();
        } else if (i == R.id.btn_black) {
            startActivity(BlackListActivity.class);
        } else if (i == R.id.btn_destroy) {
            startActivity(LogoutActivity.class);
        } else if (i == R.id.btn_privacy) {
            WebViewActivity.forward(mContext, HtmlConfig.SETTING_PRIVACY_POLICY, false);
        } else if (i == R.id.btn_service_protocol) {
            WebViewActivity.forward(mContext, HtmlConfig.SETTING_SERVICE_PROTOCOL, false);
        }

    }


    /**
     * 切换语言
     */
    public void chooseLanguage() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{R.string.lang_en, R.string.lang_zh}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                String targetLang = null;
                if (tag == R.string.lang_en) {
                    targetLang = Constants.LANG_EN;
                } else if (tag == R.string.lang_zh) {
                    targetLang = Constants.LANG_ZH;
                }
                if (TextUtils.isEmpty(targetLang) || targetLang.equals(LanguageUtil.getInstance().getLanguage())) {
                    return;
                }
                LanguageUtil.getInstance().updateLanguage(targetLang);
                ImMessageUtil.getInstance().refreshMsgTypeString();
                CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
                    @Override
                    public void callback(ConfigBean bean) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

            }
        });
    }


    /**
     * 检查更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (VersionUtil.isLatest(configBean.getVersion())) {
                        ToastUtil.show(R.string.version_latest);
                    } else {
                        VersionUtil.showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                    }
                }
            }
        });

    }

    /**
     * 退出登录
     */
    private void logout() {
        new DialogUitl.Builder(mContext)
                .setContent(WordUtil.getString(R.string.ok_to_log_out))
                .setCancelable(true)
                .setBackgroundDimEnabled(true)
                .setCancelColor(R.color.textColor)
                .setConfirmColor(R.color.global)
                .showTitle(false)
                .setCancelString(WordUtil.getString(R.string.cancel))
                .setConfrimString(WordUtil.getString(R.string.log_out))
                .setClickCallback(new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        CommonAppConfig.getInstance().clearLoginInfo();
                        //退出IM
                        ImMessageUtil.getInstance().logoutImClient();
                        //ImPushUtil.getInstance().logout();
                        //友盟统计登出
                        //UMengUtil.onLogout();
                        LoginActivity.forward();
                    }
                })
                .build()
                .show();
    }


    /**
     * 获取缓存
     */
    private String getCacheSize() {
        return GlideCatchUtil.getInstance().getCacheSize();
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        final Dialog dialog = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.setting_clear_cache_ing));
        dialog.show();
        GlideCatchUtil.getInstance().clearImageAllCache();
        File gifGiftDir = new File(CommonAppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (mCacheSize != null) {
                    mCacheSize.setText(getCacheSize());
                }
                ToastUtil.show(R.string.setting_clear_cache);
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        super.onDestroy();
    }
}
