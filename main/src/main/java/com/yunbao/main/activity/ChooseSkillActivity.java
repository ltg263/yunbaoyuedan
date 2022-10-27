package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.AuthGameAdapter;
import com.yunbao.main.bean.SkillClassBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/7/19.
 * 选择技能
 */

public class ChooseSkillActivity extends AbsActivity implements OnItemClickListener<SkillClassBean> {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ChooseSkillActivity.class));
    }

    private ViewGroup mGroup;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_skill_choose;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.skill_choose));
        mGroup = findViewById(R.id.group);
        MainHttpUtil.getUserSkill(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && mGroup != null) {
                    JSONArray array = JSON.parseArray(Arrays.toString(info));
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    for (int i = 0, size = array.size(); i < size; i++) {
                        View v = inflater.inflate(R.layout.item_class_group, mGroup, false);
                        TextView titleTextView = v.findViewById(R.id.title);
                        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
                        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
                        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 15, 15);
                        decoration.setOnlySetItemOffsetsButNoDraw(true);
                        recyclerView.addItemDecoration(decoration);
                        JSONObject obj = array.getJSONObject(i);
                        titleTextView.setText(obj.getString("name"));
                        List<SkillClassBean> list = JSON.parseArray(obj.getString("list"), SkillClassBean.class);
                        AuthGameAdapter adapter = new AuthGameAdapter(mContext, list);
                        adapter.setOnItemClickListener(ChooseSkillActivity.this);
                        recyclerView.setAdapter(adapter);
                        mGroup.addView(v);
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(SkillClassBean bean, int position) {
        String url = StringUtil.contact(HtmlConfig.SKILL_AUTH, bean.getId());
        WebViewActivity.forward(mContext, url);
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_USER_SKILL);
        super.onDestroy();
    }
}
