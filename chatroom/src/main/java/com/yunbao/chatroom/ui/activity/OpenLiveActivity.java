package com.yunbao.chatroom.ui.activity;

import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.SelectDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.LockClickObserver;
import com.yunbao.common.upload.FileUploadManager;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.upload.UploadCallback;
import com.yunbao.common.upload.UploadStrategy;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.im.config.CallConfig;
import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.adapter.LiveBgAdapter;
import com.yunbao.chatroom.bean.LiveTypeBean;
import com.yunbao.chatroom.bean.LiveSetInfo;
import com.yunbao.chatroom.bean.OpenLiveCommitBean;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.DataListner;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.business.LiveType;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.activity.dispatch.LiveDispatchHostActivity;
import com.yunbao.chatroom.ui.activity.friend.LiveFriendHostActivity;
import com.yunbao.chatroom.ui.activity.gossip.LiveGossipHostActivity;
import com.yunbao.chatroom.ui.activity.song.LiveSongHostActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpenLiveActivity extends AbsActivity {
    private ImageView mImgAvator;
    private EditText mEtTitle;
    private EditText mEtNotice;
    private TextView mBtnConfirm;
    private ViewGroup mBtnLiveType;
    private RecyclerView mReclyView;
    private TextView mTvType;

    private LiveBgAdapter mLiveBgAdapter;
    private OpenLiveCommitBean mOpenLiveCommitBean;
    private ProcessImageUtil mImageUtil;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoadingDialog;
    private List<LiveTypeBean>mLiveTypeBeanList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_live;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.open_live));
        mImgAvator = (ImageView) findViewById(R.id.img_avator);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtNotice = (EditText) findViewById(R.id.et_notice);
        mBtnConfirm = (TextView) findViewById(R.id.btn_confirm);
        mBtnLiveType = (ViewGroup) findViewById(R.id.btn_live_type);
        mReclyView = (RecyclerView) findViewById(R.id.reclyView);
        mTvType = (TextView) findViewById(R.id.tv_type);
        mLiveBgAdapter=new LiveBgAdapter(null);

        mReclyView.setAdapter(mLiveBgAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
        mReclyView.setLayoutManager(linearLayoutManager);

        mOpenLiveCommitBean=new OpenLiveCommitBean();
        mOpenLiveCommitBean.getNotice().bind(mEtNotice);
        mOpenLiveCommitBean.getTitle().bind(mEtTitle);
        mOpenLiveCommitBean.setDataListner(new DataListner() {
            @Override
            public void compelete(boolean isCompelete) {
                mBtnConfirm.setEnabled(isCompelete);
            }
        });
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {
            }
            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mImgAvator);
                    uploadConver(file);
                }
            }
            @Override
            public void onFailure() {
            }
        });
        mLiveBgAdapter.setOnSelectListner(new LiveBgAdapter.onSelectListner() {
            @Override
            public void select(LiveSetInfo.LiveBgBean bgBean) {
                if(mOpenLiveCommitBean!=null){
                   mOpenLiveCommitBean.setRoomConverId(bgBean.getId());
                   mOpenLiveCommitBean.setRoomConver(bgBean.getThumb());
                }
            }
        });
        getLiveInfo();
    }
    private void getLiveInfo() {
        ChatRoomHttpUtil.getLiveSetInfo().compose(this.<LiveSetInfo>bindToLifecycle()).subscribe(new DefaultObserver<LiveSetInfo>() {
            @Override
            public void onNext(LiveSetInfo liveSetInfo) {
                setLiveTypeInfo(liveSetInfo);
                setLiveDefaultInfo(liveSetInfo);
            }
        });
    }

    private void setLiveTypeInfo(LiveSetInfo liveSetInfo) {
        mLiveBgAdapter.setData(liveSetInfo.getCoverList());
        mLiveTypeBeanList= liveSetInfo.getLiveTypeBeanList();
    }

   /*设置默认的直播间开播信息*/
    private void setLiveDefaultInfo(LiveSetInfo liveBean) {
        if(mOpenLiveCommitBean!=null){
            String thumb=liveBean.getThumb_p();
            if(!TextUtils.isEmpty(thumb)){
                mOpenLiveCommitBean.setConver(thumb);
                ImgLoader.display(this,thumb,mImgAvator);
            }
            int type=liveBean.getType();
            if(type!=0){
                mOpenLiveCommitBean.setLiveType(type);
                mTvType.setText(LiveType.getTagTitle(type));
            }

            mOpenLiveCommitBean.getNotice().setData(liveBean.getDes());
            mOpenLiveCommitBean.getTitle().setData(liveBean.getTitle());
            if(mLiveBgAdapter!=null){
              int index= mLiveBgAdapter.select(liveBean.getBgid());
              if(index!=-1){
                mReclyView.scrollToPosition(index);
                LiveSetInfo.LiveBgBean bgBean=mLiveBgAdapter.getSelectData();
                mOpenLiveCommitBean.setRoomConverId(bgBean.getId());
                mOpenLiveCommitBean.setRoomConver(bgBean.getThumb());
              }


            }
        }
    }

    public void setCover(View view){
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

    /*上传封面*/
    private void uploadConver(final File file) {
        mLoadingDialog = DialogUitl.loadingDialog(mContext);
        mLoadingDialog.show();
        FileUploadManager.getInstance().createUploadImpl(mContext, new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                if (strategy == null){
                    ToastUtil.show(WordUtil.getString(R.string.upload_type_error));
                    return;
                }
                mUploadStrategy = strategy;
                List<UploadBean> list = new ArrayList<>(1);
                list.add(new UploadBean(file));
                mUploadStrategy.upload(list, true, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        dimissLoadingDialog();
                        if (success&& ListUtil.haveData(list)){
                            String avatarFileName = list.get(0).getRemoteFileName();
                            mOpenLiveCommitBean.setConver(avatarFileName);
                        }
                    }
                });
            }
        });
    }

    private void dimissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void confirm(View view) {
        if(CallConfig.isBusy()){
            ToastUtil.show(getString(R.string.tip_please_close_chat_window));
            return;
        }
        ChatRoomHttpUtil.startLive(mOpenLiveCommitBean).subscribe(new LockClickObserver<LiveBean>(view) {
            @Override
            public void onSucc(LiveBean liveBean) {
                completionData(liveBean);
            }
        });
    }

    /*补全聊天室信息然后跳转主持人界面*/
    private void completionData(LiveBean liveBean) {
        liveBean.setRoomCover(mOpenLiveCommitBean.getRoomConver());
        UserBean userBean= CommonAppConfig.getInstance().getUserBean();
        liveBean.setUid(userBean.getId());
        liveBean.setUserNiceName(userBean.getUserNiceName());
        liveBean.setAvatar(userBean.getAvatar());
        liveBean.setTitle(mOpenLiveCommitBean.getTitle().toString());
        liveBean.setDes(mOpenLiveCommitBean.getNotice().toString());
        liveBean.setThumb(mOpenLiveCommitBean.getConver());
        intentToLiveActivity(liveBean);
    }
    private void intentToLiveActivity(LiveBean liveBean) {
        int type=mOpenLiveCommitBean.getLiveType();
        liveBean.setType(type);
        liveBean.setTypeName(LiveType.getTagTitle(type));
        switch (type){
            case Constants.LIVE_TYPE_DISPATCH:
                LiveDispatchHostActivity.forward(this,liveBean);
                break;
            case Constants.LIVE_TYPE_FRIEND:
                LiveFriendHostActivity.forward(this,liveBean);
                break;
            case Constants.LIVE_TYPE_CHAT:
                LiveGossipHostActivity.forward(this,liveBean);
                break;
            case Constants.LIVE_TYPE_SONG:
                LiveSongHostActivity.forward(this,liveBean);
                break;
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if(mOpenLiveCommitBean!=null){
          mOpenLiveCommitBean.release();
        }
        if(mImageUtil!=null){
          mImageUtil.release();
        }
    }

    /*选择直播类型*/
    public void selectLiveType(View view) {
        SelectDialogFragment<LiveTypeBean> selectDialogFragment=new SelectDialogFragment();
        selectDialogFragment.setList(mLiveTypeBeanList);
        selectDialogFragment.setSelect(Integer.toString(mOpenLiveCommitBean.getLiveType()));
        selectDialogFragment.setOnSelectListner(new SelectDialogFragment.OnSelectListner<LiveTypeBean>() {
            @Override
            public void onSelect(LiveTypeBean liveTypeBean) {
                if(mOpenLiveCommitBean!=null){
                    mOpenLiveCommitBean.setLiveType(Integer.parseInt(liveTypeBean.getId()));
                }
                mTvType.setText(liveTypeBean.getContent());
            }
        });
        selectDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
