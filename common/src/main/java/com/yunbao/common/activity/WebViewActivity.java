package com.yunbao.common.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.R;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.dialog.CommonShareDialogFragment;
import com.yunbao.common.dialog.ShareWithCopyDialogFragment;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.mob.MobShareUtil;
import com.yunbao.common.mob.ShareData;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.LanguageUtil;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cxf on 2018/9/25.
 */

public class WebViewActivity extends AbsActivity {

    private static final String TAG = "WebViewActivity";
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private final int CHOOSE = 100;//Android 5.0以下的
    private final int CHOOSE_ANDROID_5 = 200;//Android 5.0以上的
    private ValueCallback<Uri> mValueCallback;
    private ValueCallback<Uri[]> mValueCallback2;
    private String mShareCode;
    private MobShareUtil mMobShareUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }




    public static  String getHost(String url){
        Pattern pattern = Pattern.compile("^http[s]?:\\/\\/(.*?)([:\\/]|$)");// 匹配的模式
        Matcher m = pattern.matcher(url);
        while (m.find()) {
            return m.group(1);
        }
        return "";
    }


    @Override
    protected void main() {
        String url = getIntent().getStringExtra(Constants.URL);
        L.e("H5--->" + url);
        LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mWebView = new WebView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = DpUtil.dp2px(1);
        mWebView.setLayoutParams(params);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rootView.addView(mWebView);
       // mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("H5-------->" + url);
                if (url.startsWith(Constants.COPY_PREFIX)) {
                    String content = url.substring(Constants.COPY_PREFIX.length());
                    if (!TextUtils.isEmpty(content)) {
                        copy(content);
                    }
                } else if (url.startsWith(Constants.SHARE_PREFIX)) {
                    String content = url.substring(Constants.SHARE_PREFIX.length());
                    if (!TextUtils.isEmpty(content)) {
                        mShareCode = content;
                        openShareWindow();
                    }
                } else if(url.toLowerCase().startsWith("http:") || url.toLowerCase().startsWith("https:")) {
//                    String baseUrl=CommonAppConfig.HOST;
//                    String host=getHost(url);
//                    if(baseUrl.contains(host)){
//                       view.loadUrl(url);
//                    }
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }
            //以下是在各个Android版本中 WebView调用文件选择器的方法
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                openImageChooserActivity(valueCallback);
            }
            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                openImageChooserActivity(valueCallback);
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback,
                                        String acceptType, String capture) {
                openImageChooserActivity(valueCallback);
            }

            // For Android >= 5.0
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mValueCallback2 = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                startActivityForResult(intent, CHOOSE_ANDROID_5);
                return true;
            }
        });
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setDomStorageEnabled(true);//开启本地DOM存储


        WebSettings  mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebSettings.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
        mWebSettings.setSupportZoom(true);//是否可以缩放，默认true
        mWebSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        mWebSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        mWebSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        mWebSettings.setAppCacheEnabled(true);//是否使用缓存
        mWebSettings.setDomStorageEnabled(true);//开启本地DOM存储
        mWebSettings.setLoadsImagesAutomatically(true); // 加载图片
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.loadUrl(url);
    }


    private void openImageChooserActivity(ValueCallback<Uri> valueCallback) {
        mValueCallback = valueCallback;
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, WordUtil.getString(R.string.choose_flie)), CHOOSE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case CHOOSE://5.0以下选择图片后的回调
                processResult(resultCode, intent);
                break;
            case CHOOSE_ANDROID_5://5.0以上选择图片后的回调
                processResultAndroid5(resultCode, intent);
                break;
        }
    }

    private void processResult(int resultCode, Intent intent) {
        if (mValueCallback == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            Uri result = intent.getData();
            mValueCallback.onReceiveValue(result);
        } else {
            mValueCallback.onReceiveValue(null);
        }
        mValueCallback = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processResultAndroid5(int resultCode, Intent intent) {
        if (mValueCallback2 == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            mValueCallback2.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
        } else {
            mValueCallback2.onReceiveValue(null);
        }
        mValueCallback2 = null;
    }

    protected boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    @Override
    public void onBackPressed() {
        if (isNeedExitActivity()) {
            finish();
        } else {
            if (canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
    }


    private boolean isNeedExitActivity() {
        if (mWebView != null) {
            String url = mWebView.getUrl();
            if (!TextUtils.isEmpty(url)) {
                return url.contains("auth/index")//实名认证成功页面
                        || url.contains("skillauth/apply");//技能申请提交成功页面

            }
        }
        return false;
    }

    public static void forwardH5(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    public static void forward(Context context, String url, boolean addArgs) {
        url = StringUtil.contact(url,"&lang=", LanguageUtil.getInstance().getLanguage());
        if (addArgs) {
            url = StringUtil.contact(url, "&uid=", CommonAppConfig.getInstance().getUid(), "&token=", CommonAppConfig.getInstance().getToken());
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    public static void forwardNoLanguage(Context context, String url, boolean addArgs) {
        if (addArgs) {
            url = StringUtil.contact(url, "&uid=", CommonAppConfig.getInstance().getUid(), "&token=", CommonAppConfig.getInstance().getToken());
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    public static void forward(Context context, String url) {
        forward(context, url, true);
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
        }
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        super.onDestroy();
    }

    /**
     * 复制到剪贴板
     */

    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(getString(R.string.copy_success));
    }

    /**
     * 分享
     */
    private void openShareWindow() {
        ShareWithCopyDialogFragment fragment = new ShareWithCopyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SHARE_WEB_URL,HtmlConfig.MAKE_MONEY + mShareCode);
        fragment.setArguments(bundle);
        fragment.setActionListener(new CommonShareDialogFragment.ActionListener() {
            @Override
            public void onItemClick(String type) {
                ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
                ShareData data = new ShareData();
                data.setTitle(configBean.getAgentShareTitle());
                data.setDes(configBean.getAgentShareDes());
                data.setImgUrl(CommonAppConfig.getInstance().getUserBean().getAvatarThumb());
                String webUrl = HtmlConfig.MAKE_MONEY + mShareCode;
                data.setWebUrl(webUrl);
                if (mMobShareUtil == null) {
                    mMobShareUtil = new MobShareUtil();
                }
                mMobShareUtil.execute(type, data, null);
            }
        });
                fragment.show(getSupportFragmentManager(), "ShareWithCopyDialogFragment");
    }


    /**
     * 检查权限
     */
    private boolean mPermissionEnable;
    private ProcessResultUtil mProcessResultUtil;
    private void checkPermissions() {
        if (mProcessResultUtil == null){
            mProcessResultUtil = new ProcessResultUtil(this);
        }
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
        }, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    mPermissionEnable = true;
                }
            }
        });
    }

    /**
     * 长按图片后下载
     */
    private void longTouchImgDownload(){
        checkPermissions();
        if (!mPermissionEnable){
            return;
        }
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;
                if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
                    //let TextViewhandles context menu return true;
                }
                if (type == WebView.HitTestResult.IMAGE_TYPE){
                    L.e(TAG,"正在长按图片");
                    String imgurl = result.getExtra();
                    String fileName =  imgurl.substring(imgurl.lastIndexOf("/"));
                    File dir = new File(CommonAppConfig.CAMERA_IMAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir,fileName);
                    if (file.exists()){
                        return true;
                    }
                    DownloadUtil downloadUtil = new DownloadUtil();
                    downloadUtil.download("webViewImg", dir, fileName, imgurl, new DownloadUtil.Callback() {
                        @Override
                        public void onSuccess(File file) {
                            if (file == null){
                                return;
                            }
                            L.e(TAG,"下载成功"+file.getAbsolutePath());
                            ToastUtil.show("图片已下载");
                        }

                        @Override
                        public void onProgress(int progress) {

                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

}
