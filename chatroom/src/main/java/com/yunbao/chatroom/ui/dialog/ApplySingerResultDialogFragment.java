package com.yunbao.chatroom.ui.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.song.SongSocketProxy;
import com.yunbao.chatroom.business.socket.song.mannger.SongWheatMannger;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/*主持人处理申请上歌手麦的弹框*/
public class ApplySingerResultDialogFragment extends AbsDialogFragment implements View.OnClickListener {
   private static final int MAX_SECOND=10;

    private RoundedImageView mAvatar;
    private TextView mTvName;
    private LinearLayout mLlSexGroup;
    private ImageView mSex;
    private TextView mAge;
    private TextView mTvContent;
    private TextView mBtnCancel;
    private TextView mBtnConfirm;

    private UserBean mSingerBean;
    private String mSitId;

    private LiveActivityLifeModel<SongSocketProxy> mLiveActivityLifeModel;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_to_apply_singer_result;
    }
    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }
    @Override
    protected boolean canCancel() {
        return false;
    }
    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(250);
        params.height = DpUtil.dp2px(190);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        super.init();
        mLiveActivityLifeModel= LifeObjectHolder.getByContext(getActivity(),LiveActivityLifeModel.class);
        mAvatar = (RoundedImageView) findViewById(R.id.avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mLlSexGroup = (LinearLayout) findViewById(R.id.ll_sex_group);
        mSex = (ImageView) findViewById(R.id.sex);
        mAge = (TextView) findViewById(R.id.age);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
        mBtnConfirm = (TextView) findViewById(R.id.btn_confirm);
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        if(mSingerBean!=null){
            ImgLoader.display(mContext,mSingerBean.getAvatar(),mAvatar);
            mSex.setImageDrawable(CommonIconUtil.getSexDrawable(mSingerBean.getSex()));
            mLlSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(mSingerBean.getSex()));
            mTvName.setText(mSingerBean.getUserNiceName());
            mAge.setText(mSingerBean.getAge());
        }
        startLimitTimeToSelect();
    }
    private Disposable mDisposable;
    private void startLimitTimeToSelect() {
        mDisposable= Observable.interval(1, TimeUnit.SECONDS).take(MAX_SECOND).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                setTip(MAX_SECOND-aLong);
                if(MAX_SECOND-aLong==1){
                    agreeResult(false);
                }
            }
        });
    }

    private void setTip(long l) {
       String tip= getString(R.string.argee_wheat_tip,mSitId,Long.toString(l));
        mTvContent.setText(tip);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_confirm){
            agreeResult(true);
        }else if(id==R.id.btn_cancel){
            agreeResult(false);
        }
    }
    private void agreeResult(boolean b) {
        if(mLiveActivityLifeModel==null){
            return;
        }
        SongWheatMannger wheatMannger= mLiveActivityLifeModel.getSocketProxy().getWheatMannger();
        String stream=mLiveActivityLifeModel.getLiveBean()==null?null:mLiveActivityLifeModel.getLiveBean().getStream();
        wheatMannger.agreeSingerApply(this, mSingerBean, mSitId, stream, b, new SuccessListner() {
            @Override
            public void success() {
                dismiss();
            }
        });
    }

    public void setSingerBean(UserBean singerBean,String sitId) {
        mSingerBean = singerBean;
        this.mSitId = sitId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDisposable!=null&&!mDisposable.isDisposed()){
          mDisposable.dispose();
            mDisposable=null;
        }
    }

}
