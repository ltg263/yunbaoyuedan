package com.yunbao.main.activity;

import android.content.Intent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.AllSkillAdapter;
import com.yunbao.main.bean.AllSkillBean;
import com.yunbao.main.bean.AllSkillSectionBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.http.MainHttpUtil;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.yunbao.common.Constants.DATA;

@Route(path=RouteUtil.PATH_All_Skill)
public class AllSkillActivity extends AbsActivity {

    public static final int GET_SKILL=10;
    private RxRefreshView<AllSkillSectionBean> refreshView;
    private AllSkillAdapter allSkillAdapter;

    private SkillBean mSelectedSkillBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_all_skill;
    }

    @Override
    protected void main() {
        super.main();
        setTitleById(R.string.all_skill);
        Intent intent = getIntent();
        mSelectedSkillBean = intent.getParcelableExtra(Constants.SELECTED_SKILL);
        refreshView = (RxRefreshView) findViewById(R.id.refreshView);
        allSkillAdapter=new AllSkillAdapter(R.layout.item_all_skill,R.layout.head_all_skill,null);
        allSkillAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SkillBean skillBean=allSkillAdapter.getArray().get(position).t;
                compelete(skillBean);
            }
        });

        refreshView.setDataListner(new RxRefreshView.DataListner<AllSkillSectionBean>() {
            @Override
            public Observable<List<AllSkillSectionBean>> loadData(int p) {
                return getData();
            }
            @Override
            public void compelete(List data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });

        RxRefreshView.ReclyViewSetting setting= RxRefreshView.ReclyViewSetting.createGridSetting(this,4);
        refreshView.setReclyViewSetting(setting);
        refreshView.setAdapter(allSkillAdapter);
        refreshView.initData();
    }

    private void compelete(SkillBean skillBean) {
        if(skillBean!=null){
            Intent intent=getIntent();
            intent.putExtra(DATA,skillBean);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    public void backClick(View v) {
        super.backClick(v);
    }

    /*数据流的转换*/
    private Observable<List<AllSkillSectionBean>> getData() {
       return MainHttpUtil.getAllSkill().map(new Function<List<AllSkillBean>, List<AllSkillSectionBean>>() {
           @Override
           public List<AllSkillSectionBean> apply(List<AllSkillBean> allSkillBeans) throws Exception {
              List<AllSkillSectionBean>skillSectionBeans=new ArrayList<>();
               for(AllSkillBean entity:allSkillBeans){
                   skillSectionBeans.add(new AllSkillSectionBean(true,entity.getName()));
                   List<SkillBean>array=entity.getList();
                   if(array==null){
                       continue;
                   }
                   for(SkillBean bean:array){
                       if (mSelectedSkillBean != null){
                           if (mSelectedSkillBean.getId().equals(bean.getId())){
                               bean.setSelected("1");
                           }else {
                               bean.setSelected("0");
                           }
                       }
                       skillSectionBeans.add(new AllSkillSectionBean(bean));
                   }
               }
               return skillSectionBeans;
           }
           //绑定actvity的生命周期
       }). compose(this.<List<AllSkillSectionBean>>bindToLifecycle());
    }

}
