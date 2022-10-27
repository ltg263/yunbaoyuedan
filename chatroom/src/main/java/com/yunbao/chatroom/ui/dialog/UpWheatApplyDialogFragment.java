package com.yunbao.chatroom.ui.dialog;

import android.content.DialogInterface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.ApplyResult;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.ui.view.ApplyManngerViewHolder;

import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/*主持人处理申请情况*/
public class UpWheatApplyDialogFragment extends AbsDialogFragment  {

    private TextView mTvTite;
    ApplyManngerViewHolder mManngerViewHolder;
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_uw_apply;
    }
    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }
    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (SystemUtil.getWindowsPixelHeight(getActivity())*0.79);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        super.init();
        mTvTite = (TextView) findViewById(R.id.tv_tite);
        initApplyManngerViewHolder();

    }

    private void initApplyManngerViewHolder() {
        mManngerViewHolder=new ApplyManngerViewHolder(getContext(), (ViewGroup) mRootView) {
            @Override
            public Observable<List<UserBean>> getData(int p) {
                return getObseveData(p);
            }
        };
        mManngerViewHolder.setLifecycleProvider(this);
        mManngerViewHolder.setSuccessListner(new SuccessListner() {
            @Override
            public void success() {
                mManngerViewHolder.loadData();
            }
        });
        mManngerViewHolder.addToParent();
        mManngerViewHolder.loadData();
    }


    /*网络请求*/
    private Observable<List<UserBean>> getObseveData(int p) {
        GetApplyListBehavior applyListBehavior=CacheBehaviorFactory.getInstance().getApplyListBehavior(getActivity());
        Observable<ApplyResult>observable=applyListBehavior.getApplyList(this,p);
        return observable.map(new Function<ApplyResult, List<UserBean>>() {
            @Override
            public List<UserBean> apply(ApplyResult applyResult) throws Exception {
                setTitle(applyResult.getNums());
                return applyResult.getList();
            }
        });
    }

    private void setTitle(String size) {
        mTvTite.setText(WordUtil.getString(R.string.wheat_apply_tip,size));
    }




    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        mManngerViewHolder=null;
    }



}
