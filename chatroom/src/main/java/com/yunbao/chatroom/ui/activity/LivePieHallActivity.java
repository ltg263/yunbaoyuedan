package com.yunbao.chatroom.ui.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.view.LiveTabulationViewHolder;
import java.util.List;
import io.reactivex.Observable;

public class LivePieHallActivity extends AbsActivity {
    private ViewGroup mRootView;
    LiveTabulationViewHolder mLiveTabulationViewHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_pie_hall;
    }
    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.pie_hall));
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mLiveTabulationViewHolder= new LiveTabulationViewHolder(this, mRootView) {
            @Override
            public Observable<List<LiveBean>> getData(int p) {
                return ChatRoomHttpUtil.getLiveList(p, Constants.LIVE_TYPE_DISPATCH);
            }
        };
        mLiveTabulationViewHolder.addToParent();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mLiveTabulationViewHolder.loadData();
    }
}
