package com.yunbao.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.commit.CommitEntity;
import com.yunbao.common.bean.DataListner;
import com.yunbao.common.custom.CheckImageView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.upload.FileUploadManager;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadQnImpl;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.ResourceUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.bean.CommitPubDynamicBean;
import com.yunbao.dynamic.bean.ResourseBean;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.ui.activity.SelectPhotoActivity;
import com.yunbao.dynamic.ui.activity.SelectVideoActivity;
import com.yunbao.dynamic.ui.dialog.VoiceRecordDialogFragment;
import com.yunbao.im.config.CallConfig;
import com.yunbao.main.R;
import com.yunbao.main.adapter.PubDynAdapter2;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.bean.MySkillBean;
import com.yunbao.main.http.MainHttpUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Function;

import static com.yunbao.common.Constants.ADDRESS;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.DYNAMIC_VOICE;
import static com.yunbao.common.Constants.EMPTY_TYPE;
import static com.yunbao.common.Constants.DYNAMIC_PHOTO;
import static com.yunbao.common.Constants.DYNAMIC_VIDEO;
import static com.yunbao.main.activity.LocationActivity.GET_LOCATION;
import static com.yunbao.main.activity.RelatedSkillsActivity.GET_RELATED_SKILLS;

@Route(path = RouteUtil.PUB_DYNAMIC)
public class PublishDynamicsActivity extends AbsActivity implements View.OnClickListener {
    public static final int TEXT_MAX_LENGTH = 200;
    private TextView tvNum;
    private LinearLayout llTool;
    private ImageView btnPhoto;
    private ImageView btnVideo;
    private ImageView btnSound;
    private FrameLayout flLocation;
    private TextView tvLocation;
    private FrameLayout flRelatedSkills;
    private TextView tvRelatedSkills;
    private EditText etInput;
    private CheckImageView ciLocation;
    private CheckImageView ciRelatedSkills;
    private TextView tvRightTitle;
    private RecyclerView rvSourceContainer;
    private PubDynAdapter2 pubDynAdapter;
    private Dialog mDialog;

    private ProcessResultUtil mProcessResultUtil;
    private CommitPubDynamicBean commitPubDynamicBean; //提交数据的包装类，职责也包含控制字段的逻辑
    private UploadStrategy uploadStrategy;

    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.pub_dynamics));
        tvRightTitle = setRightTitle(getString(R.string.publish));
        tvRightTitle.setTextColor(ResourceUtil.getColorList(this, R.color.fg_btn_login_code));
        tvRightTitle.setOnClickListener(this);
        tvRightTitle.setEnabled(false);

        mProcessResultUtil = new ProcessResultUtil(this);
        etInput = (EditText) findViewById(R.id.et_input);
        rvSourceContainer = findViewById(R.id.recly_source_container);

        tvNum = (TextView) findViewById(R.id.tv_num);
        llTool = (LinearLayout) findViewById(R.id.ll_tool);
        btnPhoto = (ImageView) findViewById(R.id.btn_photo);
        btnVideo = (ImageView) findViewById(R.id.btn_video);
        btnSound = (ImageView) findViewById(R.id.btn_sound);
        flLocation = (FrameLayout) findViewById(R.id.fl_location);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        flRelatedSkills = (FrameLayout) findViewById(R.id.fl_related_skills);
        tvRelatedSkills = (TextView) findViewById(R.id.tv_related_skills);
        ciLocation = (CheckImageView) findViewById(R.id.ci_location);
        ciRelatedSkills = (CheckImageView) findViewById(R.id.ci_related_skills);

        flLocation.setOnClickListener(this);
        flRelatedSkills.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnSound.setOnClickListener(this);
        // etInput.setMaxWidth(TEXT_MAX_LENGTH);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                commitPubDynamicBean.setContent(s.toString());
                if (s.length() == 200) {
                    ToastUtil.show(WordUtil.getString(R.string.text_length_too_large));
                }
                setCurrentTextLength(s.length());
            }
        });
        pubDynAdapter = new PubDynAdapter2(null, this);
        pubDynAdapter.setDataLisnter(new PubDynAdapter2.DataLisnter() {
            @Override
            public void dataChange(List<ResourseBean> data) {
                if (ListUtil.haveData(data)) {
                    llTool.setVisibility(View.GONE);
                } else {
                    llTool.setVisibility(View.VISIBLE);
                }
            }
        });

        ItemDecoration decoration = new ItemDecoration(this, 0xffdd00, 5, 5);
        rvSourceContainer.setLayoutManager(pubDynAdapter.createDefaultGridMannger());
        rvSourceContainer.addItemDecoration(decoration);
        rvSourceContainer.setAdapter(pubDynAdapter);
        commitPubDynamicBean = new CommitPubDynamicBean();
        commitPubDynamicBean.setDataListner(new DataListner() {
            @Override
            public void compelete(boolean isCompelete) {
                tvRightTitle.setEnabled(isCompelete);
            }
        });
        judgeHaveSkill();
        setCurrentTextLength(0);
    }

    private void judgeHaveSkill() {
        MainHttpUtil.getMySkill().compose(this.<List<MySkillBean>>bindToLifecycle()).map(new Function<List<MySkillBean>, Boolean>() {
            @Override
            public Boolean apply(List<MySkillBean> mySkillBeans) throws Exception {
                return ListUtil.haveData(mySkillBeans);
            }
        }).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean&&CommonAppConfig.getInstance().getIsState()!=1) {
                    flRelatedSkills.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void setCurrentTextLength(int length) {
        tvNum.setText(length + "/" + TEXT_MAX_LENGTH);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_dynamics;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fl_location) {
            startActivityForResult(LocationActivity.class, GET_LOCATION);
        } else if (id == R.id.fl_related_skills) {
            startActivityForResult(RelatedSkillsActivity.class, GET_RELATED_SKILLS);
        } else if (id == R.id.btn_photo) {
            if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
                ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.can_not_do_this_in_opening_live_room));
                return;
            }
            toPhoto();
        } else if (id == R.id.btn_video) {
            if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
                ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.can_not_do_this_in_opening_live_room));
                return;
            }
            toVideo();
        } else if (id == R.id.btn_sound) {
            if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
                ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.can_not_do_this_in_opening_live_room));
                return;
            }
            toSound();
        } else if (id == R.id.tv_right_title) {
            publish(v);
        }
    }


    private void publish(final View view) {
        if (commitPubDynamicBean == null || !ClickUtil.canClick()) {
            return;
        }

        final List<UploadBean> uploadBeanList = commitPubDynamicBean.createUploadBean();
        if (!ListUtil.haveData(uploadBeanList)) {
            if (etInput != null && TextUtils.isEmpty(etInput.getText().toString().trim())) {
                ToastUtil.show(WordUtil.getString(R.string.dynamic_content_can_not_be_empty));
                return;
            }
            requsetPublish(view);//没有资源上传的时候直接发布，有的话先上传资源
            return;
        }
        showLoadingDialog();
        view.setEnabled(false);
        final boolean isNeedCompass = commitPubDynamicBean.getResourceType() == CommitPubDynamicBean.PHOTO_RESOURCE;
        FileUploadManager.getInstance().createUploadImpl(mContext, new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                if (strategy == null){
                    ToastUtil.show(WordUtil.getString(R.string.upload_type_error));
                    return;
                }
                uploadStrategy = strategy;
                upLoadResource(isNeedCompass, uploadBeanList, view);
            }
        });
        /*如果是视频先压缩后上传*/
      /*  if(commitPubDynamicBean.getResourceType()==CommitPubDynamicBean.VIDEO_RESOURCE){
            FileUtil.comPressorVideoFile(this,uploadBeanList.get(0)).doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    disMissLoadingDialog();
                    view.setEnabled(true);
                }
            }).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    upLoadResource(uploadBeanList,view);
                }
            });
        }else{
           // upLoadResource(uploadBeanList,view);
        }*/
    }

    //上传网络资源
    private void upLoadResource(boolean isNeedCompass, List<UploadBean> uploadBeanList, final View view) {

        uploadStrategy.upload(uploadBeanList, isNeedCompass, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (success) {
                    commitPubDynamicBean.setResouce(list);
                    requsetPublish(view);
                } else {
                    disMissLoadingDialog();
                    view.setEnabled(true);
                }

            }
        });


    }

    //设置等待框
    private void showLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }
        mDialog = DialogUitl.loadingDialog(this);
        mDialog.show();
    }

    //关闭等待框
    public void disMissLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /*实际的上传*/
    private void requsetPublish(final View view) {
        DynamicHttpUtil.setDynamic(commitPubDynamicBean).compose(this.<Boolean>bindToLifecycle())
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {

                            finish();
                        } else {
                            view.setEnabled(true);
                            disMissLoadingDialog();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.setEnabled(true);
                        disMissLoadingDialog();
                    }
                });
    }

    private void toSound() {
        if (CallConfig.isBusy()) {
            ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.tip_please_close_chat_window));
            return;
        }
        judgePermisson(new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean bean) {
                if (bean) {
                    VoiceRecordDialogFragment voiceRecordDialogFragment = new VoiceRecordDialogFragment();
                    voiceRecordDialogFragment.setOnDataListnter(new VoiceRecordDialogFragment.OnDataListnter() {
                        @Override
                        public void data(File file, int cutTime) {
                            ResourseBean resourseBean = new ResourseBean(DYNAMIC_VOICE, file.getAbsolutePath(), cutTime);
                            if (commitPubDynamicBean != null) {
                                commitPubDynamicBean.setVoice(resourseBean.getResouce());
                                commitPubDynamicBean.setVoice_l(cutTime);
                            }
                            setVoice(resourseBean, DYNAMIC_VOICE);
                        }
                    });
                    voiceRecordDialogFragment.show(getSupportFragmentManager(), "VoiceRecordDialogFragment");
                }
            }
        });
    }

    private void toVideo() {
        judgePermisson(new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean bean) {
                if (bean) {
                    startActivityForResult(SelectVideoActivity.class, DYNAMIC_VIDEO);
                }
            }
        });
    }

    private void toPhoto() {
        judgePermisson(new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean bean) {
                if (bean) {
                    startActivityForResult(SelectPhotoActivity.class, DYNAMIC_PHOTO);
                }
            }
        });
    }

    /*先验证权限*/
    private void judgePermisson(CommonCallback<Boolean> commonCallback) {
        mProcessResultUtil.requestPermissions(
                commonCallback,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        L.e("----onActivityResult----" + requestCode);
        if (requestCode == GET_LOCATION) {
            setLocation(data, requestCode);
        } else if (requestCode == GET_RELATED_SKILLS) {
            setSkill(data, requestCode);
        } else if (requestCode == DYNAMIC_PHOTO) {
            setPhoto(data, requestCode);
        } else if (requestCode == DYNAMIC_VIDEO) {
            setVideo(data, requestCode);
        }
    }

    private void setVoice(ResourseBean resourseBean, int requestCode) {
        setRecouceReclyViewData(Arrays.asList(resourseBean), requestCode);
    }

    private void setVideo(Intent data, int requestCode) {
        String videoPath = data.getStringExtra(DATA);
        if (!TextUtils.isEmpty(videoPath)) {
            setRecouceReclyViewData(Arrays.asList(new ResourseBean(requestCode, videoPath)), requestCode);
        } else {
            setRecouceReclyViewData(null, requestCode);
        }
        L.e("----setVideo----" + videoPath);
        if (commitPubDynamicBean != null) {
            commitPubDynamicBean.setVideo(videoPath);
        }
    }

    private void setPhoto(Intent data, int requestCode) {
        List<String> photoPathArray = data.getStringArrayListExtra(DATA);
        if (commitPubDynamicBean != null) {
            commitPubDynamicBean.setThumbs(photoPathArray);
        }
        setRecouceReclyViewData(ResourseBean.transForm(requestCode, photoPathArray), requestCode);
    }

    private void setSkill(Intent data, int requestCode) {
        SkillBean skillBean = data.getParcelableExtra(DATA);
        if (skillBean != null) {
            tvRelatedSkills.setText(skillBean.getSkillName2());
            if (commitPubDynamicBean != null) {
                commitPubDynamicBean.setSkillid(skillBean.getId());
            }
        } else {
            tvRelatedSkills.setText(null);
            if (commitPubDynamicBean != null) {
                commitPubDynamicBean.setSkillid(CommitEntity.DEFAUlT_VALUE);
            }
        }
        ciRelatedSkills.setChecked(skillBean != null);
    }

    private void setLocation(Intent data, int requestCode) {
        String address = data.getStringExtra(ADDRESS);
        tvLocation.setText(address);
        ciLocation.setChecked(!TextUtils.isEmpty(address));
        commitPubDynamicBean.setLocation(address);
    }

    /*根据不同类型资源的返回,设置进adapter*/
    private void setRecouceReclyViewData(List<ResourseBean> array, int type) {
        if (array != null) {
            pubDynAdapter.setDataRequestType(type);
        } else {
            pubDynAdapter.setDataRequestType(EMPTY_TYPE);
        }
        pubDynAdapter.setData(array);
    }

    @Override
    public void onBackPressed() {
        if (commitPubDynamicBean != null && commitPubDynamicBean.observerCondition()) {
            showCloseDialog();
            return;
        }
        super.onBackPressed();
    }

    private void showCloseDialog() {
        DialogUitl.showSimpleDialog(this, getString(R.string.forgive_edit_tip), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProcessResultUtil.release();
        if (uploadStrategy != null) {
            uploadStrategy.cancelUpload();
        }
        disMissLoadingDialog();
    }

}
