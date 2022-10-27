package com.yunbao.dynamic.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.dialog.BottomDealFragment;
import com.yunbao.common.interfaces.OnFaceClickListener;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.utils.CleanLeakUtils;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.bean.DynamicCommentBean;
import com.yunbao.dynamic.business.DynamicUIFactory;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.ui.dialog.DynamicInputDialogFragment;
import com.yunbao.dynamic.ui.view.AbsDynamicDetailViewHolder;
import com.yunbao.dynamic.ui.view.NormalScrollDynamicViewHolder;
import com.yunbao.dynamic.ui.view.PhotoDynamiceViewHolder;
import com.yunbao.dynamic.ui.view.VideoDynamiceViewHolder;
import com.yunbao.dynamic.ui.view.VoiceDynamiceViewHolderScroll;
import com.yunbao.dynamic.widet.StartChangeOffectListner;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.DYNAMIC_PHOTO;
import static com.yunbao.common.Constants.DYNAMIC_VIDEO;
import static com.yunbao.common.Constants.DYNAMIC_VOICE;

public class DynamicDetailActivity extends AbsActivity implements  OnFaceClickListener, View.OnClickListener {
    private FrameLayout mFlContainer;
    private DynamicBean mDynamicBean;
    private View mFaceView;
    private ImageView mBtnBack;
    private ImageView mImgTitleRight;

    private boolean mIsSelfDynamic;
    private int[] mWhiteColorArgb;
    private int[] mBlackColorArgb;
    private int[] mFaceHeightArray;
    private AbsDynamicDetailViewHolder mAbsDynamicDetailViewHolder;

    private DynamicInputDialogFragment mDynamicInputDialogFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dynamic_detail;
    }

    @Override
    protected void main() {
        super.main();
        mDynamicBean=getIntent().getParcelableExtra(DATA);
        if(mDynamicBean==null){
            finish();
        }
        setTabBackGroudColor("#00000000");
        mFlContainer = (FrameLayout) findViewById(R.id.fl_container);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mImgTitleRight = (ImageView) findViewById(R.id.img_title_right);
        mIsSelfDynamic=StringUtil.equals(CommonAppConfig.getInstance().getUid(),mDynamicBean.getUid());
        mWhiteColorArgb = getColorArgb(0xffffffff);
        mBlackColorArgb = getColorArgb(0xff000000);
        mFaceHeightArray=new int[1];
        setData(mDynamicBean);
    }

    /**
     * 获取颜色的argb
     */
    private int[] getColorArgb(int color) {
        return new int[]{Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color)};
    }
    /*根据不同的动态信息，来确定不同的view 并铺设数据*/
    private void setData(DynamicBean dynamicBean) {
        if(dynamicBean==null){
            finish();
        }
        mAbsDynamicDetailViewHolder=null;
        int type=dynamicBean.getType();
        if(type==DYNAMIC_VIDEO){
            mAbsDynamicDetailViewHolder=new VideoDynamiceViewHolder(this,mFlContainer);
        } else if(type==DYNAMIC_PHOTO)   {
            mAbsDynamicDetailViewHolder=new PhotoDynamiceViewHolder(this,mFlContainer)
            .addOnWatchOffectListner(new StartChangeOffectListner.OnWatchOffsetListner() {
                @Override
                public void offect(float rate) {
                  changeTabColor(rate);
              }
            });
        } else if(type==DYNAMIC_VOICE){
            mAbsDynamicDetailViewHolder=new VoiceDynamiceViewHolderScroll(this,mFlContainer);
        } else{
            mAbsDynamicDetailViewHolder=new NormalScrollDynamicViewHolder(this,mFlContainer);
        }
        mAbsDynamicDetailViewHolder.addToParent(0);
        mAbsDynamicDetailViewHolder.subscribeActivityLifeCycle();
        mAbsDynamicDetailViewHolder.setData(dynamicBean);

        float rate=mAbsDynamicDetailViewHolder.defaultColorRate();
        changeTabColor(rate);

    }

    private void changeTabColor(float rate) {
        int a = (int) (mWhiteColorArgb[0] * ( rate) + mBlackColorArgb[0] * rate);
        int r = (int) (mWhiteColorArgb[1] * ( rate) + mBlackColorArgb[1] * rate);
        int g = (int) (mWhiteColorArgb[2] * ( rate) + mBlackColorArgb[2] * rate);
        int b = (int) (mWhiteColorArgb[3] * ( rate) + mBlackColorArgb[3] * rate);
        int color = Color.argb(a, r, g, b);
        mBtnBack.setColorFilter(color);
        mImgTitleRight.setColorFilter(color);
    }


    public  void rightClick(View view){
       showBottomDialog();
    }

    private void showBottomDialog() {
        String btnString=mIsSelfDynamic?WordUtil.getString(R.string.delete):WordUtil.getString(R.string.report);
        BottomDealFragment bottomDealFragment=new BottomDealFragment();
        bottomDealFragment.setDialogButtonArray(new BottomDealFragment.DialogButton(btnString, new BottomDealFragment.ClickListnter() {
            @Override
            public void click(View view) {
                if(mIsSelfDynamic){
                    deleteDynamic();
                }else{
                    report();
                }
            }
        }));
        bottomDealFragment.show(getSupportFragmentManager());
    }

    /*删除动态*/
    private void deleteDynamic() {
        if(mDynamicBean!=null){
          DynamicHttpUtil.delDynamic(mDynamicBean.getId()).compose(this.<Boolean>bindToLifecycle()).subscribe(new DialogObserver<Boolean>(this) {
              @Override
              public void onNext(Boolean aBoolean) {
                    if(aBoolean){
                       finish();
                    }
              }
          });
        }
    }
    public static void forward(Context context,DynamicBean dynamicBean){
        Intent intent=new Intent(context,DynamicDetailActivity.class);
        intent.putExtra(DATA,dynamicBean);
        context.startActivity(intent);
    }
    public View getFaceView() {
        if (mFaceView == null) {
            mFaceView = DynamicUIFactory.createFaceView(mFaceHeightArray,this,this,this);
        }
        return mFaceView;
    }


    public void openCommentInputWindow(boolean openFace, String dynamicId, String dynamicUid, DynamicCommentBean bean) {
        getFaceView();
        DynamicInputDialogFragment fragment = new DynamicInputDialogFragment();
        fragment.setDynamicInfo(dynamicId, dynamicUid);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.VIDEO_FACE_OPEN, openFace);
        bundle.putInt(Constants.VIDEO_FACE_HEIGHT, mFaceHeightArray[0]);
        bundle.putParcelable(Constants.VIDEO_COMMENT_BEAN, bean);
        fragment.setArguments(bundle);
        mDynamicInputDialogFragment = fragment;
        fragment.show(getSupportFragmentManager());
    }


    private void report() {
        if(mDynamicBean!=null){
          DynamicReportActivity.forward(this,mDynamicBean.getId());
        }
    }

    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mDynamicInputDialogFragment != null) {
            mDynamicInputDialogFragment.onFaceClick(str, faceImageRes);
        }
    }

    @Override
    public void onFaceDeleteClick() {
        if (mDynamicInputDialogFragment != null) {
            mDynamicInputDialogFragment.onFaceDeleteClick();
        }
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_send){
            sendFace();
        }
    }

    private void sendFace() {
        if (mDynamicInputDialogFragment != null) {
            mDynamicInputDialogFragment.sendComment();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mDynamicInputDialogFragment != null) {
            mDynamicInputDialogFragment.dismiss();
        }
        CleanLeakUtils.fixInputMethodManagerLeak(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e("onDestroy=="+this.hashCode());
    }
}
