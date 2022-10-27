package com.yunbao.chatroom.ui.dialog;

import android.widget.TextView;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.ApplyResult;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.ui.view.ApplyManngerViewHolder;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class FriendApplyListFragment extends AbsViewPagerDialogFragment{
    private TextView mTvTite;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_friend_apply_list;
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
    protected AbsMainViewHolder[] createViewHolder() {
       final  ApplyManngerViewHolder applyManngerViewHolder1=  new ApplyManngerViewHolder(mContext,mViewPager) {
            @Override
            public Observable<List<UserBean>> getData(int p) {
                return requestData(p,1);
            }
        };
        applyManngerViewHolder1.setSuccessListner(new SuccessListner() {
            @Override
            public void success() {
                applyManngerViewHolder1.loadData();
            }
        });

        applyManngerViewHolder1.setLifecycleProvider(this);
        final ApplyManngerViewHolder applyManngerViewHolder2=  new ApplyManngerViewHolder(mContext,mViewPager) {
            @Override
            public Observable<List<UserBean>> getData(int p) {
                return requestData(p,2);
            }
        };
        applyManngerViewHolder2.setSuccessListner(new SuccessListner() {
            @Override
            public void success() {
                applyManngerViewHolder2.loadData();
            }
        });

        applyManngerViewHolder2.setLifecycleProvider(this);
        return new AbsMainViewHolder[]{
                applyManngerViewHolder1 ,
                applyManngerViewHolder2
        };
    }

    /*查找Behavior实现类可以去BehaviorFactory查看具体的实现*/
    protected Observable<List<UserBean>>requestData(int p, int sex) {
        GetApplyListBehavior applyListBehavior= CacheBehaviorFactory.getInstance().getApplyListBehavior(getActivity());
        Observable<ApplyResult>observable=applyListBehavior.getApplyList(this,p,sex);
        return observable.map(new Function<ApplyResult, List<UserBean>>() {
            @Override
            public List<UserBean> apply(ApplyResult applyResult) throws Exception {
                setTitle(applyResult.getNums());
                return applyResult.getList();
            }
        });
    }


    @Override
    public String[] getTitles() {
        return new String[]{
                WordUtil.getString(R.string.male_guests),
                WordUtil.getString(R.string.female_guests)
        };
    }

    @Override
    public void init() {
        super.init();
        mTvTite = findViewById(R.id.tv_tite);

    }

    private void setTitle(String size) {
        mTvTite.setText(WordUtil.getString(R.string.wheat_apply_tip,size));
    }



}
