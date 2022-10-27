package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
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
import com.yunbao.common.utils.BirthdayUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.utils.CityUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Created by cxf on 2018/9/29.
 * 我的 编辑资料
 */

public class EditProfileActivity extends AbsActivity implements View.OnClickListener {


    public static void forward(Context context) {
        context.startActivity(new Intent(context, EditProfileActivity.class));
    }

    private ImageView mAvatar;
    private TextView mNickname;
    private TextView mAge;
    private TextView mCity;
    private TextView mSign;
    private TextView mInterest;
    private TextView mJob;
    private TextView mSchool;
    private ProcessImageUtil mImageUtil;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoading;
    private UserBean mUserBean;
    private String mProvinceVal;
    private String mCityVal;
    private String mZoneVal;
    private ActivityResultCallback mInterestCallback;
    private ActivityResultCallback mNicknameCallback;
    private ActivityResultCallback mAddrCallback;
    private ActivityResultCallback mSignCallback;
    private ActivityResultCallback mJobCallback;
    private ActivityResultCallback mSchoolCallback;
    private ActivityResultCallback mVoiceCallback;
    private HttpCallback mUpdateAvatarCallback;
    private HttpCallback mUpdateBirthdayCallback;
    private HttpCallback mUpdateCityCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void main() {
        mAvatar = findViewById(R.id.avatar);
        mNickname = findViewById(R.id.nickname);
        mAge = findViewById(R.id.age);
        mCity = findViewById(R.id.city);
        mSign = findViewById(R.id.sign);
        mInterest = findViewById(R.id.interest);
        mJob = findViewById(R.id.job);
        mSchool = findViewById(R.id.school);
        findViewById(R.id.avatar).setOnClickListener(this);
        findViewById(R.id.btn_voice).setOnClickListener(this);
        findViewById(R.id.btn_nickname).setOnClickListener(this);
        findViewById(R.id.btn_age).setOnClickListener(this);
        findViewById(R.id.btn_city).setOnClickListener(this);
        findViewById(R.id.btn_sign).setOnClickListener(this);
        findViewById(R.id.btn_interest).setOnClickListener(this);
        findViewById(R.id.btn_job).setOnClickListener(this);
        findViewById(R.id.btn_school).setOnClickListener(this);
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {
            }
            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mAvatar);
                    getUploadStrategy(file);
                }
            }

            @Override
            public void onFailure() {
            }
        });
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean u) {
                showData(u);
            }
        });
    }

    private void showData(UserBean u) {
        mUserBean = u;
        if (mAvatar != null) {
            ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        }
        if (mNickname != null) {
            mNickname.setText(u.getUserNiceName());
        }
        if (mAge != null) {
            mAge.setText(StringUtil.contact(u.getAge(), "/", u.getXingZuo()));
        }
        if (mCity != null) {
            mCity.setText(u.getCity());
        }
        if (mSign != null) {
            mSign.setText(u.getSignature());
        }
        if (mInterest != null) {
            mInterest.setText(u.getInteret());
        }
        if (mJob != null) {
            mJob.setText(u.getProfession());
        }
        if (mSchool != null) {
            mSchool.setText(u.getSchool());
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.avatar) {
            editAvatar();
        } else if (i == R.id.btn_voice) {
            editVoice();
        } else if (i == R.id.btn_nickname) {
            setNickname();
        } else if (i == R.id.btn_age) {
            chooseBirthday();
        } else if (i == R.id.btn_city) {
            setAddr();
        } else if (i == R.id.btn_sign) {
            setSign();
        } else if (i == R.id.btn_interest) {
            chooseInterest();
        } else if (i == R.id.btn_job) {
            setJob();
        } else if (i == R.id.btn_school) {
            setSchool();
        }
    }

    private void editAvatar() {
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
     * 设置语音
     */
    private void editVoice() {
        if (mUserBean == null) {
            return;
        }
        if (mVoiceCallback == null) {
            mVoiceCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        String voice = intent.getStringExtra(Constants.VOICE);
                        int voiceLength = intent.getIntExtra(Constants.VOICE_DURATION,0);
                        if (mUserBean != null) {
                            mUserBean.setVoice(voice);
                            mUserBean.setVoiceDuration(voiceLength);
                        }
                    }
                }
            };
        }
        Intent intent = new Intent(mContext, EditVoiceActivity.class);
        intent.putExtra(Constants.VOICE, mUserBean.getVoice());
        intent.putExtra(Constants.VOICE_DURATION, mUserBean.getVoiceDuration());
        intent.putExtra(Constants.VOICE_FROM, Constants.VOICE_FROM_USER);
        mImageUtil.startActivityForResult(intent, mVoiceCallback);
    }

    /**
     * 设置昵称
     */
    private void setNickname() {
        if (mNicknameCallback == null) {
            mNicknameCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        String nickname = intent.getStringExtra(Constants.NICKNAME);
                        if (!TextUtils.isEmpty(nickname) && mNickname != null) {
                            mNickname.setText(nickname);
                        }
                    }
                }
            };
        }
        Intent intent = new Intent(mContext, EditNameActivity.class);
        intent.putExtra(Constants.NICKNAME, mNickname.getText().toString());
        mImageUtil.startActivityForResult(intent, mNicknameCallback);
    }

    /**
     * 设置地址
     */
    private void setAddr() {
        if (mAddrCallback == null) {
            mAddrCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        String addr = intent.getStringExtra(Constants.ADDR);
                        if (addr != null && mCity != null) {
                            mCity.setText(addr);
                        }
                    }
                }
            };
        }
        Intent intent = new Intent(mContext, EditAddrActivity.class);
        intent.putExtra(Constants.ADDR, mCity.getText().toString());
        mImageUtil.startActivityForResult(intent, mAddrCallback);
    }



    /**
     * 选择城市
     */
    private void chooseCity() {
        ArrayList<Province> list = CityUtil.getInstance().getCityList();
        if (list == null || list.size() == 0) {
            final Dialog loading = DialogUitl.loadingDialog(mContext);
            loading.show();
            CityUtil.getInstance().getCityListFromAssets(new CommonCallback<ArrayList<Province>>() {
                @Override
                public void callback(ArrayList<Province> newList) {
                    loading.dismiss();
                    if (newList != null) {
                        showChooseCityDialog(newList);
                    }
                }
            });
        } else {
            showChooseCityDialog(list);
        }
    }

    /**
     * 选择城市
     */
    private void showChooseCityDialog(ArrayList<Province> list) {
        String province = mProvinceVal;
        String city = mCityVal;
        String district = mZoneVal;
        if (TextUtils.isEmpty(province)) {
            province = CommonAppConfig.getInstance().getProvince();
        }
        if (TextUtils.isEmpty(city)) {
            city = CommonAppConfig.getInstance().getCity();
        }
        if (TextUtils.isEmpty(district)) {
            district = CommonAppConfig.getInstance().getDistrict();
        }
        DialogUitl.showCityChooseDialog(this, list, province, city, district, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Province province, final City city, County county) {
                String provinceName = province.getAreaName();
                String cityName = city.getAreaName();
                String zoneName = county.getAreaName();
                mProvinceVal = provinceName;
                mCityVal = cityName;
                mZoneVal = zoneName;
                String addr = StringUtil.contact(provinceName, cityName);
                if (mCity != null) {
                    mCity.setText(addr);
                }
                if (mUpdateCityCallback == null) {
                    mUpdateCityCallback = new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                EventBus.getDefault().post(new UpdateFieldEvent());
                                if (info.length > 0) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    UserBean userBean = CommonAppConfig.getInstance().getUserBean();
                                    userBean.setCity(obj.getString("addr"));
                                }
                            }
                            ToastUtil.show(msg);
                        }
                    };
                }
                MainHttpUtil.updateUserInfo(StringUtil.contact("{\"addr\":\"", addr, "\"}"), mUpdateCityCallback);
            }
        });
    }

    /**
     * 设置签名
     */
    private void setSign() {
        if (mSignCallback == null) {
            mSignCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        String sign = intent.getStringExtra(Constants.SIGN);
                        if (sign != null && mSign != null) {
                            mSign.setText(sign);
                        }
                    }
                }
            };
        }
        Intent intent = new Intent(mContext, EditSignActivity.class);
        intent.putExtra(Constants.SIGN, mSign.getText().toString());
        mImageUtil.startActivityForResult(intent, mSignCallback);
    }

    /**
     * 选择生日
     */
    private void chooseBirthday() {
        DialogUitl.showDatePicker(this, new DialogUitl.DataPickerCallback() {
            @Override
            public void onConfirmClick(String year, String month, String day) {
                String xingZuoName = BirthdayUtil.getXinZuoName(Integer.parseInt(month), Integer.parseInt(day));
                int age = BirthdayUtil.getAge(Integer.parseInt(year));
                if (mAge != null) {
                    mAge.setText(StringUtil.contact(String.valueOf(age), "/", xingZuoName));
                }
                if (mUpdateBirthdayCallback == null) {
                    mUpdateBirthdayCallback = new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                EventBus.getDefault().post(new UpdateFieldEvent());
                                if (info.length > 0) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    UserBean userBean = CommonAppConfig.getInstance().getUserBean();
                                    userBean.setAge(obj.getString("age"));
                                    userBean.setBirthday(obj.getString("birthday"));
                                    userBean.setXingZuo(obj.getString("constellation"));
                                }
                            }
                            ToastUtil.show(msg);
                        }
                    };
                }
                String birthday = StringUtil.contact(year, "-", month, "-", day);
                MainHttpUtil.updateUserInfo(StringUtil.contact("{\"birthday\":\"", birthday, "\"}"), mUpdateBirthdayCallback);
            }
        });
    }

    /**
     * 选择兴趣
     */
    private void chooseInterest() {
        if (mInterestCallback == null) {
            mInterestCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        String interest = intent.getStringExtra(Constants.INTEREST);
                        if (!TextUtils.isEmpty(interest) && mInterest != null) {
                            mInterest.setText(interest);
                        }
                    }
                }
            };
        }
        Intent intent = new Intent(mContext, EditInterestActivity.class);
        intent.putExtra(Constants.INTEREST, mInterest.getText().toString());
        mImageUtil.startActivityForResult(intent, mInterestCallback);
    }


    /**
     * 设置职业
     */
    private void setJob() {
        if (mJobCallback == null) {
            mJobCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        String job = intent.getStringExtra(Constants.JOB);
                        if (mJob != null && job != null) {
                            mJob.setText(job);
                        }
                    }
                }
            };
        }
        Intent intent = new Intent(mContext, EditJobActivity.class);
        intent.putExtra(Constants.JOB, mJob.getText().toString());
        mImageUtil.startActivityForResult(intent, mJobCallback);
    }

    /**
     * 设置学校
     */
    private void setSchool() {
        if (mSchoolCallback == null) {
            mSchoolCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        String school = intent.getStringExtra(Constants.SCHOOL);
                        if (school != null && mSchool != null) {
                            mSchool.setText(school);
                        }
                    }
                }
            };
        }
        Intent intent = new Intent(mContext, EditSchoolActivity.class);
        intent.putExtra(Constants.SCHOOL, mSchool.getText().toString());
        mImageUtil.startActivityForResult(intent, mSchoolCallback);
    }


    private void getUploadStrategy(final File file){
        FileUploadManager.getInstance().createUploadImpl(mContext, new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                if (strategy == null){
                    ToastUtil.show(WordUtil.getString(R.string.upload_type_error));
                    return;
                }
                mUploadStrategy = strategy;
                uploadAvatarImage(file);
            }
        });
    }

    /**
     * 上传头像
     */
    private void uploadAvatarImage(File file) {
        mLoading = DialogUitl.loadingDialog(mContext);
        mLoading.show();
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
                        String avatarFileName = list.get(0).getRemoteFileName();
                        if (mUpdateAvatarCallback == null) {
                            mUpdateAvatarCallback = new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 0) {
                                        EventBus.getDefault().post(new UpdateFieldEvent());
                                        if (info.length > 0) {
                                            JSONObject obj = JSON.parseObject(info[0]);
                                            UserBean userBean = CommonAppConfig.getInstance().getUserBean();
                                            userBean.setAvatar(obj.getString("avatar"));
                                            userBean.setAvatarThumb(obj.getString("avatar_thumb"));
                                        }
                                    }
                                    ToastUtil.show(msg);
                                }
                            };
                        }
                        MainHttpUtil.updateUserInfo(StringUtil.contact("{\"avatar\":\"", avatarFileName, "\"}"), mUpdateAvatarCallback);
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_USER_INFO);
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

