package com.yunbao.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.main.R;
import com.yunbao.main.adapter.RelatedSkillsAdapter;
import com.yunbao.main.bean.AllSkillBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.bean.MySkillBean;
import com.yunbao.main.http.MainHttpUtil;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import static com.yunbao.common.Constants.DATA;

public class RelatedSkillsActivity extends AbsActivity {
    public static final int GET_RELATED_SKILLS=11;
    private RxRefreshView<MySkillBean> rxRefreshView;
    private RelatedSkillsAdapter relatedSkillsAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_related_skills;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.related_skills));
        rxRefreshView=findViewById(R.id.refreshView);
        rxRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,1));
        rxRefreshView.setDataListner(new RxRefreshView.DataListner<MySkillBean>() {
            @Override
            public Observable<List<MySkillBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<MySkillBean> data) {
            }
            @Override
            public void error(Throwable e) {
                e.printStackTrace();
            }
        });
        relatedSkillsAdapter=new RelatedSkillsAdapter(null);
        relatedSkillsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MySkillBean skillBean=relatedSkillsAdapter.getArray().get(position);
                if(!TextUtils.equals(skillBean.getId(),MySkillBean.EMPTY_ID)&&skillBean.getSwitchX()==0){
                    MySkillActivity.forward(mContext);
                }else {
                    returnSelData(skillBean.getSkill());
                }
            }
        });
        rxRefreshView.setAdapter(relatedSkillsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        rxRefreshView.initData();
    }

    private void returnSelData(SkillBean bean) {
        Intent intent=new Intent();
        if(bean!=null&&!TextUtils.isEmpty(bean.getSkillName2())){
            intent.putExtra(DATA,bean);
        }
        setResult(RESULT_OK,intent);
        finish();
    }

    private Observable<List<MySkillBean>> getData(int p) {
        return MainHttpUtil.getMySkill().compose(this.<List<MySkillBean>>bindToLifecycle());
    }
}
