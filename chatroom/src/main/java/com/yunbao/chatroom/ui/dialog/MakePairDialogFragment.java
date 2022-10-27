package com.yunbao.chatroom.ui.dialog;

import android.support.v7.widget.AppCompatTextView;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.custom.viewanimator.AnimationBuilder;
import com.yunbao.common.custom.viewanimator.AnimationListener;
import com.yunbao.common.custom.viewanimator.ViewAnimator;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.MakePairBean;

import java.util.Collection;
import java.util.LinkedList;


/*展示配对动画*/
public class MakePairDialogFragment extends AbsDialogFragment {
    private ImageView mImgBeckoningTip;
    private ImageView mImgBgMan;
    private RoundedImageView mImgAvatorMan;
    private ImageView mImgBgWoman;
    private RoundedImageView mImgAvatorWoman;
    private AppCompatTextView mTvManName;
    private AppCompatTextView mTvWomanName;
    private FrameLayout mGroupMan;
    private FrameLayout mGroupWoman;
    private FrameLayout mGroupBottom;
    private LinearLayout mContainer;

    private LinkedList<MakePairBean>mMakePairQueue;
    private MakePairBean mMakePairBean;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_make_pair;
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

    }

    @Override
    public void init() {
        super.init();
        mImgBeckoningTip = (ImageView) findViewById(R.id.img_beckoning_tip);
        mImgBgMan = (ImageView) findViewById(R.id.img_bg_man);
        mImgAvatorMan = (RoundedImageView) findViewById(R.id.img_avator_man);
        mImgBgWoman = (ImageView) findViewById(R.id.img_bg_woman);
        mImgAvatorWoman = (RoundedImageView) findViewById(R.id.img_avator_woman);
        mTvManName = (AppCompatTextView) findViewById(R.id.tv_man_name);
        mTvWomanName = (AppCompatTextView) findViewById(R.id.tv_woman_name);
        mGroupMan = (FrameLayout) findViewById(R.id.group_man);
        mGroupWoman = (FrameLayout) findViewById(R.id.group_woman);
        mGroupBottom = (FrameLayout) findViewById(R.id.group_bottom);
        mContainer = (LinearLayout) findViewById(R.id.container);
        readQueque();
    }

    private AnimationBuilder mAnimationBuilder;
    private void startPlayAnim(){
        mAnimationBuilder=ViewAnimator.
                animate(mContainer)
                .alpha(0,1).duration(100)
                .thenAnimate(mGroupMan).slideLeftIn().
                duration(1000).
                andAnimate(mGroupWoman).slideRightIn()
                .duration(1000).
                thenAnimate(mImgBeckoningTip,mGroupBottom)
                 .duration(500).zoomIn().
                 thenAnimate(mContainer,mImgBeckoningTip,mGroupBottom).alpha(1,0).duration(500).
                        onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        mGroupMan.setTranslationX(-DpUtil.dp2px(300));
                        mGroupWoman.setTranslationX(DpUtil.dp2px(300));
                        L.e("结束动画了");
                        mMakePairBean=null;
                        readQueque();
                    }
                });
        mAnimationBuilder.startDelay(3000).start();
        L.e("mAnimationBuilder 执行");
    }


    private void readQueque() {
        if(mMakePairBean!=null){
            return;
        }
        if(ListUtil.haveData(mMakePairQueue)){
           MakePairBean makePairBean=mMakePairQueue.removeLast();
           mMakePairBean=makePairBean;
           putData(makePairBean);
           startPlayAnim();
        }else{
            dismiss();
        }
    }


    public void makePair(MakePairBean data){
        if(mMakePairQueue==null){
            mMakePairQueue=new LinkedList<>();
        }
         mMakePairQueue.add(data);
    }

    public void makePair(Collection<MakePairBean> list){
        if(mMakePairQueue==null){
            mMakePairQueue=new LinkedList<>();
        }
        mMakePairQueue.addAll(list);
    }

    private void putData(MakePairBean data) {
        ImgLoader.display(getContext(),data.getManAvatar(),mImgAvatorMan);
        mTvManName.setText(data.getManUserNickname());

        ImgLoader.display(getContext(),data.getWomanAvatar(),mImgAvatorWoman);
        mTvWomanName.setText(data.getWomanUserNickname());
    }


}
