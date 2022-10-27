package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.event.UpdateFieldEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.ActivityResultCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.upload.FileUploadManager;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadQnImpl;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.bean.SkillLabelBean;
import com.yunbao.main.bean.SkillLevelBean;
import com.yunbao.main.bean.SkillPriceBean;
import com.yunbao.main.dialog.SkillLabelDialogFragment;
import com.yunbao.main.dialog.SkillPriceDialogFragment;
import com.yunbao.main.event.UpdateSkillEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by cxf on 2019/7/26.
 * 编辑技能资料
 */

public class EditSkillActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, SkillBean bean) {
        Intent intent = new Intent(context, EditSkillActivity.class);
        intent.putExtra(Constants.SKILL_BEAN, bean);
        context.startActivity(intent);
    }

    private TextView mPrice;
    private TextView mLevel;
    private ImageView mThumb;
    private EditText mDes;
    private TextView mDesCount;
    private View mSkillChooseTip;
    private TextView[] mSkillLabels;
    private List<SkillLabelBean> mLabelList;
    private SkillBean mSkillBean;
    private String mCoinName;
    private HttpCallback mUpdateCallback;
    private HttpCallback mLevelCallback;
    private ProcessImageUtil mImageUtil;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoading;
    private List<SkillLevelBean> mSkillLevelList;
    private ActivityResultCallback mVoiceCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_skill_edit;
    }

    @Override
    protected void main() {
        SkillBean skillBean = getIntent().getParcelableExtra(Constants.SKILL_BEAN);
        mSkillBean = skillBean;
        setTitle(skillBean.getSkillName());
        mPrice = findViewById(R.id.price);
        mLevel = findViewById(R.id.level);
        mThumb = findViewById(R.id.thumb);
        mDes = findViewById(R.id.des);
        mDesCount = findViewById(R.id.count);
        mDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mDesCount != null) {
                    mDesCount.setText(StringUtil.contact(String.valueOf(s.length()), "/30"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSkillChooseTip = findViewById(R.id.choose_tip);
        mSkillLabels = new TextView[3];
        mSkillLabels[0] = findViewById(R.id.skill_label_0);
        mSkillLabels[1] = findViewById(R.id.skill_label_1);
        mSkillLabels[2] = findViewById(R.id.skill_label_2);
        findViewById(R.id.btn_skill_label).setOnClickListener(this);
        findViewById(R.id.btn_price).setOnClickListener(this);
        findViewById(R.id.btn_level).setOnClickListener(this);
        findViewById(R.id.btn_choose_img).setOnClickListener(this);
        findViewById(R.id.btn_voice).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mThumb);
                    uploadThumb(file);
                }
            }

            @Override
            public void onFailure() {
            }
        });


        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mPrice.setText(skillBean.getPirceResult(mCoinName));
        String[] labels = skillBean.getLabels();
        if (labels != null && labels.length > 0) {
            List<SkillLabelBean> labelList = new ArrayList<>();
            for (String label : labels) {
                labelList.add(new SkillLabelBean(label));
            }
            showSkillLabels(labelList);
        }
        mLevel.setText(skillBean.getSkillLevel());
        ImgLoader.display(mContext, skillBean.getSkillThumb(), mThumb);
        String des = skillBean.getDes();
        mDes.setText(des);
        if (des.length() > 0) {
            mDes.setSelection(des.length());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_price) {
            openPriceWindow();
        } else if (i == R.id.btn_skill_label) {
            openSkillWindow();
        } else if (i == R.id.btn_save) {
            updateDes();
        } else if (i == R.id.btn_choose_img) {
            chooseImage();
        } else if (i == R.id.btn_level) {
            chooseLevel();
        } else if (i == R.id.btn_voice) {
            editVoice();
        }
    }

    /**
     * 选择价格
     */
    private void openPriceWindow() {
        if (mSkillBean == null) {
            return;
        }
        SkillPriceDialogFragment fragment = new SkillPriceDialogFragment();
        fragment.setSkillBean(mCoinName, mSkillBean);
        fragment.setActionListener(new SkillPriceDialogFragment.ActionListener() {
            @Override
            public void onPriceSelected(SkillPriceBean bean) {
                if (mPrice != null && mSkillBean != null) {
                    mPrice.setText(StringUtil.contact(bean.getCoin(), mCoinName, "/", mSkillBean.getUnit()));
                }
                mSkillBean.setSkillPrice(bean.getCoin());
                updateSkillInfo(StringUtil.contact("{\"coin\":\"", bean.getId(), "\"}"));
            }
        });
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "MainPriceDialogFragment");
    }


    /**
     * 技能标签
     */
    private void openSkillWindow() {
        if (mSkillBean == null) {
            return;
        }
        SkillLabelDialogFragment fragment = new SkillLabelDialogFragment();
        fragment.setActionListener(new SkillLabelDialogFragment.ActionListener() {
            @Override
            public void onConfrim(List<SkillLabelBean> list) {
                if(list==null){
                    ToastUtil.show(R.string.chose_skill_tag);
                    return;
                }
                showSkillLabels(list);
                StringBuilder sb = new StringBuilder();
                for (SkillLabelBean bean : list) {
                    sb.append(bean.getId());
                    sb.append(",");
                }
                updateSkillInfo(StringUtil.contact("{\"label\":\"", sb.toString(), "\"}"));
            }
        });
        fragment.setSkillId(mSkillBean.getSkillId());
        fragment.setLabelList(mLabelList);
        fragment.show(getSupportFragmentManager(), "SkillLabelDialogFragment");
    }

    /**
     * 技能标签
     */
    private void showSkillLabels(List<SkillLabelBean> list) {
        mLabelList = list;
        if (list == null || list.size() == 0) {
            if (mSkillChooseTip != null && mSkillChooseTip.getVisibility() != View.VISIBLE) {
                mSkillChooseTip.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < 3; i++) {
                if (mSkillLabels[i].getVisibility() == View.VISIBLE) {
                    mSkillLabels[i].setVisibility(View.INVISIBLE);
                }
            }
            return;
        }
        if (mSkillChooseTip != null && mSkillChooseTip.getVisibility() == View.VISIBLE) {
            mSkillChooseTip.setVisibility(View.INVISIBLE);
        }
        int diff = 3 - list.size();
        for (int i = 2; i >= 0; i--) {
            TextView v = mSkillLabels[i];
            if (i >= diff) {
                if (v.getVisibility() != View.VISIBLE) {
                    v.setVisibility(View.VISIBLE);
                }
                SkillLabelBean bean = list.get(i - diff);
                v.setText(bean.getName());
            } else {
                if (v.getVisibility() == View.VISIBLE) {
                    v.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 选择段位
     */
    private void chooseLevel() {
        if (mSkillBean == null) {
            return;
        }
        if (mLevelCallback == null) {
            mLevelCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        mSkillLevelList = JSON.parseArray(Arrays.toString(info), SkillLevelBean.class);
                        if (mSkillLevelList == null) {
                            return;
                        }
                        int size = mSkillLevelList.size();
                        String[] arr = new String[size];
                        for (int i = 0; i < size; i++) {
                            arr[i] = mSkillLevelList.get(i).getName();
                        }
                        DialogUitl.showXinZuoDialog(EditSkillActivity.this, arr, new OptionPicker.OnOptionPickListener() {
                            @Override
                            public void onOptionPicked(int i, String s) {
                                if (mSkillLevelList != null && i >= 0 && i <= mSkillLevelList.size()) {
                                    SkillLevelBean bean = mSkillLevelList.get(i);
                                    if (bean == null) {
                                        return;
                                    }
                                    String level = bean.getName();
                                    if (TextUtils.isEmpty(level)) {
                                        return;
                                    }
                                    if (mSkillBean == null) {
                                        return;
                                    }
                                    if (level.equals(mSkillBean.getSkillLevel())) {
                                        return;
                                    }
                                    mSkillBean.setSkillLevel(level);
                                    if (mLevel != null) {
                                        mLevel.setText(level);
                                    }
                                    updateSkillInfo(StringUtil.contact("{\"level\":\"", bean.getId(), "\"}"));
                                }
                            }
                        });

                    }
                }
            };
        }
        MainHttpUtil.getSkillLevel(mSkillBean.getSkillId(), mLevelCallback);
    }

    /**
     * 选择图片
     */
    private void chooseImage() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera(false);
                } else {
                    mImageUtil.getImageByAlumb(false);
                }
            }
        });
    }
    /**
     * 上传技能图片
     */

    private void uploadThumb(final File file) {
        mLoading = DialogUitl.loadingDialog(mContext);
        mLoading.show();
        FileUploadManager.getInstance().createUploadImpl(mContext, new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                if (strategy == null){
                    ToastUtil.show(WordUtil.getString(R.string.upload_type_error));
                    return;
                }
                mUploadStrategy = strategy;
                List<UploadBean> list = new ArrayList<>();
                list.add(new UploadBean(file));
                mUploadStrategy.upload(list, true, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        if (mLoading != null && mLoading.isShowing()) {
                            mLoading.dismiss();
                        }
                        if (success) {
                            if (list != null && list.size() > 0) {
                                String fileName = list.get(0).getRemoteFileName();
                                updateSkillInfo(StringUtil.contact("{\"thumb\":\"", fileName, "\"}"));
                            }
                        }
                    }
                });
            }
        });
    }


    private void updateSkillInfo(String fields) {
        if (mSkillBean == null) {
            return;
        }
        if (mUpdateCallback == null) {
            mUpdateCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        EventBus.getDefault().post(new UpdateSkillEvent());
                    }
                    ToastUtil.show(msg);
                }
            };
        }
        MainHttpUtil.updateSkillInfo(mSkillBean.getSkillId(), fields, mUpdateCallback);
    }

    /**
     * 设置语音
     */
    private void editVoice() {
        if (mSkillBean == null) {
            return;
        }
        if (mVoiceCallback == null) {
            mVoiceCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        if (mSkillBean != null) {
                            mSkillBean.setSkillVoice(intent.getStringExtra(Constants.VOICE));
                            mSkillBean.setSkillVoiceDuration(intent.getIntExtra(Constants.VOICE_DURATION, 0));
                        }
                    }
                }
            };
        }
        Intent intent = new Intent(mContext, EditVoiceActivity.class);
        intent.putExtra(Constants.VOICE, mSkillBean.getSkillVoice());
        intent.putExtra(Constants.VOICE_DURATION, mSkillBean.getSkillVoiceDuration());
        intent.putExtra(Constants.VOICE_FROM, Constants.VOICE_FROM_SKILL);
        intent.putExtra(Constants.SKILL_ID, mSkillBean.getSkillId());
        mImageUtil.startActivityForResult(intent, mVoiceCallback);
    }

    /**
     * 更新介绍
     */
    private void updateDes() {
        if (mDes == null) {
            return;
        }
        String des = mDes.getText().toString().trim();
        MainHttpUtil.updateSkillInfo(mSkillBean.getSkillId(), StringUtil.contact("{\"des\":\"", des, "\"}"), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new UpdateSkillEvent());
                    EventBus.getDefault().post(new UpdateFieldEvent());
                    finish();
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_SKILL_INFO);
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
        mLoading = null;
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        mImageUtil = null;
        super.onDestroy();
    }



}
