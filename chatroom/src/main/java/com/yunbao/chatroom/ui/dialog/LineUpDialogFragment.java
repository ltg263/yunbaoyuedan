package com.yunbao.chatroom.ui.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.LiveUpAdapter;
import com.yunbao.chatroom.bean.ApplyResult;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.SuccessListner;

import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/*当前上麦排队情况*/
public class LineUpDialogFragment extends AbsDialogFragment  implements View.OnClickListener {
    private TextView mTvTite;
    private RxRefreshView<UserBean> mRxRefreshView;
    private LiveUpAdapter mLiveUpAdapter;
    private int mCisPosition=1;
    private SocketProxy mSocketProxy;
    private LiveBean mLiveBean;



    @Override
    protected int getLayoutId() {

        return R.layout.dialog_line_up;
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
        params.height = (int) (SystemUtil.getWindowsPixelHeight(getActivity())*0.5);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);



    }

    @Override
    public void init() {
        super.init();
        mTvTite = (TextView) findViewById(R.id.tv_tite);
        mRxRefreshView = findViewById(R.id.refreshView);
        mLiveUpAdapter=new LiveUpAdapter(null);
        mRxRefreshView.setAdapter(mLiveUpAdapter);
        RxRefreshView.ReclyViewSetting reclyViewSetting=RxRefreshView.ReclyViewSetting.createLinearSetting(mContext,1);
        mRxRefreshView.setReclyViewSetting(reclyViewSetting);
        mRxRefreshView.setDataListner(new RxRefreshView.DataListner<UserBean>() {
            @Override
            public Observable<List<UserBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<UserBean> data) {
            }
            @Override
            public void error(Throwable e) {

            }
        });
        LiveActivityLifeModel holder= LiveActivityLifeModel.getByContext(getActivity(), LiveActivityLifeModel.class);
        if(holder!=null){
           mSocketProxy=holder.getSocketProxy();
           mLiveBean=holder.getLiveBean();
        }
        setPosition(mLiveUpAdapter.containPositoin(CommonAppConfig.getInstance().getUserBean()));
        setOnClickListener(R.id.btn_confirm,this);
        mRxRefreshView.initData();
    }

    private void setPosition(int position) {
        mCisPosition=position;
        if(mCisPosition!=-1){
          mTvTite.setText(WordUtil.getString(R.string.cis_tip,mCisPosition));
        }
    }
    public  Observable<List<UserBean>> getData(int p) {
        int sex=CommonAppConfig.getInstance().getUserBean().getSex();
        GetApplyListBehavior applyListBehavior= CacheBehaviorFactory.getCacheBehaviorFactory().getApplyListBehavior(getActivity());
        return applyListBehavior.getApplyList(this,p,sex).map(new Function<ApplyResult, List<UserBean>>() {
            @Override
            public List<UserBean> apply(ApplyResult applyResult) throws Exception {
                setPosition(applyResult.getRank());
                return applyResult.getList();
            }
        });
    }

    @Override
    public void onClick(View v) {
       cancleQueue(v);
    }

    /*取消排队*/
    private void cancleQueue(View view) {
        CancleQueBehavior cancleQueBehavior= CacheBehaviorFactory.getInstance().getCancleQueBehavior(getActivity());
        if(cancleQueBehavior!=null){
           cancleQueBehavior.cancleApplyQueue(view, this, new SuccessListner() {
               @Override
               public void success() {
                   dismiss();
               }
           });
        }
        CacheBehaviorFactory.setApplying(false,getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocketProxy=null;
    }
}


