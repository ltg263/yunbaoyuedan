package com.yunbao.main.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.activity.SkillHomeActivity;
import com.yunbao.main.adapter.SkillUserAdapter;
import com.yunbao.main.bean.SkillClassBean;
import com.yunbao.main.bean.SkillLevelBean;
import com.yunbao.main.bean.SkillUserBean;
import com.yunbao.main.http.MainHttpUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sky.L on 2021-07-16
 */
public class MainHomeGameChildViewHolder extends AbsMainHomeChildViewHolder implements View.OnClickListener, OnItemClickListener<SkillUserBean> {
    private  SkillClassBean mGameBean;
    private LayoutInflater mInflater;
    private View mSortGroup;
    private TextView mSortText1;
    private TextView mSortText2;
    private ImageView mSortImg1;
    private ImageView mSortImg2;
    private ImageView mSortImg3;
    private View mMask;
    private PopupWindow mPop1;
    private PopupWindow mPop2;
    private PopupWindow mPop3;
    private TextView mBtnLevel;
    private TextView mBtnVoice;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private CommonRefreshView mRefreshView;
    private SkillUserAdapter mAdapter;
    private String mGameId;
    private int mOrderKey;    //排序，0智能1最新
    private int mSexKey;    //	性别，0不限1男2女
    private SkillLevelBean mLevelKey;    //段位
    private int mVoiceKey;    //语音，0不限1有
    private SparseArray<String> mOrderTextMap;
    private SparseArray<String> mSexTextMap;
    private List<SkillLevelBean> mSkillLevelList;
    private Drawable mTagCheckedDrawable;
    private Drawable mTagUnCheckedDrawable;
    private int mTagCheckedColor;
    private int mTagUnCheckedColor;

    public MainHomeGameChildViewHolder(Context context, ViewGroup parentView, SkillClassBean skillClassBean) {
        super(context, parentView,skillClassBean);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mGameBean = (SkillClassBean) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_game_child;
    }

    @Override
    public void init() {
        if (mGameBean != null) {
            mGameId = mGameBean.getId();
        }else {
            return;
        }
        mInflater = LayoutInflater.from(mContext);
        mSortGroup = findViewById(R.id.sort_group);
        mSortText1 = findViewById(R.id.text_sort_1);
        mSortText2 = findViewById(R.id.text_sort_2);
        mSortImg1 = findViewById(R.id.img_sort_1);
        mSortImg2 = findViewById(R.id.img_sort_2);
        mSortImg3 = findViewById(R.id.img_sort_3);
        mMask = findViewById(R.id.mask);
        findViewById(R.id.btn_sort_1).setOnClickListener(this);
        findViewById(R.id.btn_sort_2).setOnClickListener(this);
        findViewById(R.id.btn_sort_3).setOnClickListener(this);
        mOrderTextMap = new SparseArray<>();
        mOrderTextMap.put(0, WordUtil.getString(R.string.game_sort_1));
        mOrderTextMap.put(1, WordUtil.getString(R.string.game_sort_4));
        mSexTextMap = new SparseArray<>();
        mSexTextMap.put(0, WordUtil.getString(R.string.game_sort_2));
        mSexTextMap.put(1, WordUtil.getString(R.string.game_sort_5));
        mSexTextMap.put(2, WordUtil.getString(R.string.game_sort_6));
        mTagCheckedDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_game_sort_tag_1);
        mTagUnCheckedDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_game_sort_tag_0);
        mTagCheckedColor = ContextCompat.getColor(mContext, R.color.global);
        mTagUnCheckedColor = ContextCompat.getColor(mContext, R.color.textColor);
        mOnDismissListener = new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mMask != null && mMask.getVisibility() == View.VISIBLE) {
                    mMask.setVisibility(View.INVISIBLE);
                }
                if (mSortImg1 != null) {
                    mSortImg1.setRotation(0);
                }
                if (mSortImg2 != null) {
                    mSortImg2.setRotation(0);
                }
                if (mSortImg3 != null) {
                    mSortImg3.setRotation(0);
                }
            }
        };
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyTips(WordUtil.getString(R.string.no_more_data_skill));
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SkillUserBean>() {
            @Override
            public RefreshAdapter<SkillUserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SkillUserAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeGameChildViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getGameUserList(mOrderKey, mSexKey, mLevelKey == null ? null : mLevelKey.getId(),
                        mVoiceKey, mGameId, p, callback);
            }

            @Override
            public List<SkillUserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), SkillUserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<SkillUserBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<SkillUserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_sort_1) {
            chooseOrder();
        } else if (i == R.id.btn_sort_2) {
            chooseSex();
        } else if (i == R.id.btn_sort_3) {
            getLevelList();
        }
    }

    /**
     * 选择排序
     */
    private void chooseOrder() {
        if (mPop1 == null) {
            View view = mInflater.inflate(R.layout.pop_game_sort_1, null);
            RadioButton btn1 = view.findViewById(R.id.btn_1);
            RadioButton btn2 = view.findViewById(R.id.btn_2);
            btn1.setText(mOrderTextMap.get(0));
            btn2.setText(mOrderTextMap.get(1));
            if (mOrderKey == 0) {
                btn1.setChecked(true);
            } else {
                btn2.setChecked(true);
            }
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    if (i == R.id.btn_1) {
                        orderChanged(0);
                    } else if (i == R.id.btn_2) {
                        orderChanged(1);
                    }
                }
            };
            btn1.setOnClickListener(onClickListener);
            btn2.setOnClickListener(onClickListener);
            mPop1 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPop1.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            mPop1.setOutsideTouchable(true);
            mPop1.setOnDismissListener(mOnDismissListener);
        }
        mPop1.showAsDropDown(mSortGroup);
        if (mMask != null && mMask.getVisibility() != View.VISIBLE) {
            mMask.setVisibility(View.VISIBLE);
        }
        if (mSortImg1 != null) {
            mSortImg1.setRotation(180);
        }
    }

    /**
     * 选择排序
     */
    private void orderChanged(int key) {
        if (mPop1 != null) {
            mPop1.dismiss();
        }
        if (mOrderKey == key) {
            return;
        }
        mOrderKey = key;
        if (mSortText1 != null) {
            mSortText1.setText(mOrderTextMap.get(key));
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    /**
     * 选择性别
     */
    private void chooseSex() {
        if (mPop2 == null) {
            View view = mInflater.inflate(R.layout.pop_game_sort_2, null);
            RadioButton btn1 = view.findViewById(R.id.btn_1);
            RadioButton btn2 = view.findViewById(R.id.btn_2);
            RadioButton btn3 = view.findViewById(R.id.btn_3);
            btn1.setText(mSexTextMap.get(0));
            btn2.setText(mSexTextMap.get(1));
            btn3.setText(mSexTextMap.get(2));
            if (mSexKey == 0) {
                btn1.setChecked(true);
            } else if (mSexKey == 1) {
                btn2.setChecked(true);
            } else {
                btn3.setChecked(true);
            }
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    if (i == R.id.btn_1) {
                        sexChanged(0);
                    } else if (i == R.id.btn_2) {
                        sexChanged(1);
                    } else if (i == R.id.btn_3) {
                        sexChanged(2);
                    }
                }
            };
            btn1.setOnClickListener(onClickListener);
            btn2.setOnClickListener(onClickListener);
            btn3.setOnClickListener(onClickListener);
            mPop2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPop2.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            mPop2.setOutsideTouchable(true);
            mPop2.setOnDismissListener(mOnDismissListener);
        }
        mPop2.showAsDropDown(mSortGroup);
        if (mMask != null && mMask.getVisibility() != View.VISIBLE) {
            mMask.setVisibility(View.VISIBLE);
        }
        if (mSortImg2 != null) {
            mSortImg2.setRotation(180);
        }
    }

    /**
     * 选择性别
     */
    private void sexChanged(int key) {
        if (mPop2 != null) {
            mPop2.dismiss();
        }
        if (mSexKey == key) {
            return;
        }
        mSexKey = key;
        if (mSortText2 != null) {
            mSortText2.setText(mSexTextMap.get(key));
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    /**
     * 段位
     */
    private void getLevelList() {
        if (mSkillLevelList == null) {
            MainHttpUtil.getSkillLevel(mGameId, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        mSkillLevelList = JSON.parseArray(Arrays.toString(info), SkillLevelBean.class);
                        chooseLevel();
                    }
                }
            });
        } else {
            chooseLevel();
        }
    }


    /**
     * 选择段位和语音
     */
    private void chooseLevel() {
        if (mPop3 == null) {
            final View v = mInflater.inflate(R.layout.pop_game_sort_3, null);
            TagFlowLayout flow1 = v.findViewById(R.id.flow_layout_1);
            flow1.setAdapter(new TagAdapter<SkillLevelBean>(mSkillLevelList) {

                @Override
                public View getView(FlowLayout parent, int position, SkillLevelBean bean) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.item_game_sort_tag, parent, false);
                    tv.setText(bean.getName());
                    tv.setTag(bean);
                    return tv;
                }

                @Override
                public void onSelected(int position, View view) {
                    mBtnLevel = (TextView) view;
                    mBtnLevel.setBackground(mTagCheckedDrawable);
                    mBtnLevel.setTextColor(mTagCheckedColor);
                }

                @Override
                public void unSelected(int position, View view) {
                    if (mBtnLevel != null) {
                        mBtnLevel.setBackground(mTagUnCheckedDrawable);
                        mBtnLevel.setTextColor(mTagUnCheckedColor);
                    }
                    mBtnLevel = null;
                }
            });
            TagFlowLayout flow2 = v.findViewById(R.id.flow_layout_2);
            List<String> featureList = new ArrayList<>();
            featureList.add(WordUtil.getString(R.string.game_voice));
            flow2.setAdapter(new TagAdapter<String>(featureList) {

                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.item_game_sort_tag, parent, false);
                    tv.setText(s);
                    return tv;
                }

                @Override
                public void onSelected(int position, View view) {
                    mBtnVoice = (TextView) view;
                    mBtnVoice.setBackground(mTagCheckedDrawable);
                    mBtnVoice.setTextColor(mTagCheckedColor);
                }

                @Override
                public void unSelected(int position, View view) {
                    if (mBtnVoice != null) {
                        mBtnVoice.setBackground(mTagUnCheckedDrawable);
                        mBtnVoice.setTextColor(mTagUnCheckedColor);
                    }
                    mBtnVoice = null;
                }
            });
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    if (i == R.id.btn_reset) {
                        if (mBtnLevel != null) {
                            ViewParent viewParent = mBtnLevel.getParent();
                            if (viewParent != null && viewParent instanceof TagView) {
                                TagView tagView = (TagView) viewParent;
                                tagView.setChecked(false);
                            }
                            mBtnLevel.setBackground(mTagUnCheckedDrawable);
                            mBtnLevel.setTextColor(mTagUnCheckedColor);
                        }
                        mBtnLevel = null;
                        if (mBtnVoice != null) {
                            ViewParent viewParent = mBtnVoice.getParent();
                            if (viewParent != null && viewParent instanceof TagView) {
                                TagView tagView = (TagView) viewParent;
                                tagView.setChecked(false);
                            }
                            mBtnVoice.setBackground(mTagUnCheckedDrawable);
                            mBtnVoice.setTextColor(mTagUnCheckedColor);
                        }
                        mBtnVoice = null;
                        levelChanged(null, 0);
                    } else if (i == R.id.btn_confirm) {
                        levelChanged(mBtnLevel != null ? (SkillLevelBean) mBtnLevel.getTag() : null, mBtnVoice == null ? 0 : 1);
                    }
                }
            };
            v.findViewById(R.id.btn_reset).setOnClickListener(onClickListener);
            v.findViewById(R.id.btn_confirm).setOnClickListener(onClickListener);

            mPop3 = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPop3.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            mPop3.setOutsideTouchable(true);
            mPop3.setOnDismissListener(mOnDismissListener);
        }
        mPop3.showAsDropDown(mSortGroup);
        if (mMask != null && mMask.getVisibility() != View.VISIBLE) {
            mMask.setVisibility(View.VISIBLE);
        }
        if (mSortImg3 != null) {
            mSortImg3.setRotation(180);
        }
    }

    /**
     * 段位
     */
    public void levelChanged(SkillLevelBean levelKey, int voiceKey) {
        if (mPop3 != null) {
            mPop3.dismiss();
        }
        if (mLevelKey == levelKey && mVoiceKey == voiceKey) {
            return;
        }
        mLevelKey = levelKey;
        mVoiceKey = voiceKey;
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    @Override
    public void onItemClick(SkillUserBean bean, int position) {
        SkillHomeActivity.forward(mContext, bean.getUid(), bean.getSkillId());
    }
}
