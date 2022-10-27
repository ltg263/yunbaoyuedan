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
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.ChooseSexDialog;
import com.yunbao.common.event.LoginSuccessEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.upload.FileUploadManager;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/9/29.
 * 我的 编辑资料
 */

public class SetProfileActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String userInfo, boolean fromThird) {
        Intent intent = new Intent(context, SetProfileActivity.class);
        intent.putExtra(Constants.USER_BEAN, userInfo);
        intent.putExtra(Constants.FROM, fromThird);
        context.startActivity(intent);
    }

    private ImageView mAvatar;
    private EditText mNickname;
    private TextView mBirthday;
    private TextView mSex;
    private View mBtnSave;
    private int mSexVal;
    private ProcessImageUtil mImageUtil;
    private File mAvatarFile;
    private String mAvatarFileUrl;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoading;
    private JSONObject mUserJsonObject;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_profile;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.set_profile));
        mAvatar = findViewById(R.id.avatar);
        mNickname = findViewById(R.id.nickname);
        mNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkSubmitEnable(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBirthday = findViewById(R.id.birthday);
        mSex = findViewById(R.id.sex);
        mBtnSave = findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
        findViewById(R.id.btn_avatar).setOnClickListener(this);
        findViewById(R.id.btn_birthday).setOnClickListener(this);
        findViewById(R.id.btn_sex).setOnClickListener(this);
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mAvatar);
                    mAvatarFile = file;
                }
            }

            @Override
            public void onFailure() {
            }
        });

        Intent intent = getIntent();
        String userData = intent.getStringExtra(Constants.USER_BEAN);
        boolean fromThird = intent.getBooleanExtra(Constants.FROM, false);//是否是三方登录
        if (!TextUtils.isEmpty(userData)) {
            JSONObject obj = JSON.parseObject(userData);
            mUserJsonObject = obj;
            mAvatarFileUrl = obj.getString("avatar");
            ImgLoader.display(mContext, mAvatarFileUrl, mAvatar);

            if (fromThird) {
                String nickname = obj.getString("user_nickname");
                mNickname.setText(nickname);
                mNickname.setSelection(nickname.length());
            }
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_save) {
            save();
        } else if (i == R.id.btn_avatar) {
            choooseAvatar();
        } else if (i == R.id.btn_birthday) {
            chooseBirthday();
        } else if (i == R.id.btn_sex) {
            chooseSex();
        }
    }

    /**
     * 保存
     */
    private void save() {
        if (mAvatarFile != null && mAvatarFile.exists()) {
            String name = mNickname.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                ToastUtil.show(R.string.set_profile_nickname);
                return;
            }
            String birthday = mBirthday.getText().toString().trim();
            if (TextUtils.isEmpty(birthday)) {
                ToastUtil.show(R.string.set_profile_birthday);
                return;
            }
            String sex = mSex.getText().toString().trim();
            if (TextUtils.isEmpty(sex) || mSexVal == 0) {
                ToastUtil.show(R.string.set_profile_sex);
                return;
            }
            uploadAvatarImage();
        } else {
            submit();
        }
    }

    /**
     * 选择图片
     */
    private void choooseAvatar() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }


    /**
     * 上传头像
     */
    private void uploadAvatarImage() {
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
                list.add(new UploadBean(mAvatarFile));
                mUploadStrategy.upload(list, true, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        if (success) {
                            if (list != null && list.size() > 0) {
                                mAvatarFileUrl = list.get(0).getRemoteFileName();
                                submit();
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 选择生日
     */
    private void chooseBirthday() {
        DialogUitl.showDatePicker(this, new DialogUitl.DataPickerCallback() {
            @Override
            public void onConfirmClick(String year, String month, String day) {
                if (mBirthday != null) {
                    mBirthday.setText(year + "-" + month + "-" + day);
                    checkSubmitEnable();
                }
            }
        });
    }

    /**
     * 选择性别
     */
    private void chooseSex() {
        ChooseSexDialog chooseSexDialog = new ChooseSexDialog();
        chooseSexDialog.setActionListener(new ChooseSexDialog.ActionListener() {
            @Override
            public void onConfirmClick(String sexString, int sexVal) {
                if (mSex != null) {
                    mSex.setText(sexString);
                }
                mSexVal = sexVal;
                checkSubmitEnable();
            }
        });
        chooseSexDialog.show(getSupportFragmentManager(), "ChooseSexDialog");
    }

    private void checkSubmitEnable() {
        String nickname = mNickname.getText().toString().trim();
        checkSubmitEnable(nickname);
    }

    private void checkSubmitEnable(String nickname) {
        String birthday = mBirthday.getText().toString().trim();
        String sex = mSex.getText().toString().trim();
        mBtnSave.setEnabled(!TextUtils.isEmpty(nickname) && !TextUtils.isEmpty(birthday) && !TextUtils.isEmpty(sex));
    }

    /**
     * 提交信息
     */
    private void submit() {
        if (mUserJsonObject == null) {
            return;
        }
        String name = mNickname.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(R.string.set_profile_nickname);
            return;
        }
        String birthday = mBirthday.getText().toString().trim();
        if (TextUtils.isEmpty(birthday)) {
            ToastUtil.show(R.string.set_profile_birthday);
            return;
        }
        String sex = mSex.getText().toString().trim();
        if (TextUtils.isEmpty(sex) || mSexVal == 0) {
            ToastUtil.show(R.string.set_profile_sex);
            return;
        }
        if (TextUtils.isEmpty(mAvatarFileUrl)) {
            ToastUtil.show(R.string.set_profile_avarar);
            return;
        }
        JSONObject params = new JSONObject();
        params.put("user_nickname", name);
        params.put("birthday", birthday);
        params.put("sex", mSexVal);
        params.put("avatar", mAvatarFileUrl);
        MainHttpUtil.setUserProfile(params.toJSONString(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mUserJsonObject != null) {
                        UserBean u = JSON.toJavaObject(mUserJsonObject, UserBean.class);
                        u.setUserNiceName(obj.getString("user_nickname"));
                        u.setAvatar(obj.getString("avatar"));
                        u.setAvatarThumb(obj.getString("avatar_thumb"));
                        u.setBirthday(obj.getString("birthday"));
                        u.setAge(obj.getString("age"));
                        u.setXingZuo(obj.getString("constellation"));
                        u.setSex(obj.getIntValue("sex"));
                        CommonAppConfig appConfig = CommonAppConfig.getInstance();
                        String uid = mUserJsonObject.getString("id");
                        String token = mUserJsonObject.getString("token");
                        appConfig.setLoginInfo(uid, token, true);
                        appConfig.setUserBean(u);
                        Map<String, String> map = new HashMap<>();
                        map.put(SpUtil.USER_INFO, JSON.toJSONString(u));
                        map.put(SpUtil.TX_IM_USER_SIGN, mUserJsonObject.getString("usersig"));
                        SpUtil.getInstance().setMultiStringValue(map);
                        MainActivity.forward(mContext);
                        EventBus.getDefault().post(new LoginSuccessEvent());
                        CommonAppConfig.getInstance().setIsLogin(1);
                        finish();
                    }
                }
                ToastUtil.show(msg);
            }

            @Override
            public void onFinish() {
                if (mLoading != null) {
                    mLoading.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mLoading != null) {
            mLoading.dismiss();
        }
        MainHttpUtil.cancel(MainHttpConsts.SET_USER_PROFILE);
        super.onDestroy();
    }
}
