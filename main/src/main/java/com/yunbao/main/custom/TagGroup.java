package com.yunbao.main.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbao.main.R;
import com.yunbao.main.bean.TagBean;

/**
 * Created by cxf on 2019/4/12.
 */

public class TagGroup extends LinearLayout {

    private Context mContext;
    private TextView[] mTextViews;
    private GradientDrawable[] mDrawables;
    private int mRadius;
    private int mStrokeWidth;
    private int mTextSize;
    private int mTagHeight;
    private int mMinWidth;
    private int mTagPadding;
    private int mTagMargin;
    private int mTagCount;
    private boolean mHasBorder;


    public TagGroup(Context context) {
        this(context, null);
    }

    public TagGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagGroup);
        mRadius = (int) ta.getDimension(R.styleable.TagGroup_tag_radius, 0);
        mStrokeWidth = (int) ta.getDimension(R.styleable.TagGroup_tag_stroke_width, 0);
        mTextSize = (int) ta.getDimension(R.styleable.TagGroup_tag_text_size, 0);
        mTagHeight = (int) ta.getDimension(R.styleable.TagGroup_tag_height, 0);
        mMinWidth = (int) ta.getDimension(R.styleable.TagGroup_tag_min_width, 0);
        mTagPadding = (int) ta.getDimension(R.styleable.TagGroup_tag_padding, 0);
        mTagMargin = (int) ta.getDimension(R.styleable.TagGroup_tag_margin, 0);
        mTagCount = (int) ta.getInt(R.styleable.TagGroup_tag_count, 0);
        mHasBorder = ta.getBoolean(R.styleable.TagGroup_tag_has_border, true);
        ta.recycle();
    }

    private TextView createTextView() {
        TextView textView = new TextView(mContext);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mTagHeight);
        layoutParams.rightMargin = mTagMargin;
        textView.setLayoutParams(layoutParams);
        textView.setMinWidth(mMinWidth);
        textView.setPadding(mTagPadding, 0, mTagPadding, 0);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }


    public void setTagBeans(TagBean[] tagBeans) {
        if (tagBeans == null || tagBeans.length == 0) {
            return;
        }
        if (mTextViews == null) {
            mTextViews = new TextView[mTagCount];
        }
        if (mDrawables == null) {
            mDrawables = new GradientDrawable[mTagCount];
        }
        int len = Math.min(tagBeans.length, mTagCount);
        for (int i = 0; i < len; i++) {
            TextView textView = mTextViews[i];
            if (textView == null) {
                textView = createTextView();
                addView(textView);
                mTextViews[i] = textView;
            }
            if (textView.getVisibility() == INVISIBLE) {
                textView.setVisibility(VISIBLE);
            }
            TagBean tagBean = tagBeans[i];
            textView.setText(tagBean.getName());
            textView.setTextColor(tagBean.getTextColor());
            GradientDrawable drawable = mDrawables[i];
            if (drawable == null) {
                drawable = new GradientDrawable();
                mDrawables[i] = drawable;
            }
            if (mHasBorder) {
                drawable.setStroke(mStrokeWidth, tagBean.getBorderColor());
            }
            drawable.setCornerRadius(mRadius);
            drawable.setColor(tagBean.getBgColor());
            textView.setBackground(drawable);
        }

        if (len < mTagCount) {
            for (int i = len; i < mTagCount; i++) {
                TextView textView = mTextViews[i];
                if (textView != null && textView.getVisibility() == VISIBLE) {
                    textView.setVisibility(INVISIBLE);
                }
            }
        }
    }


}
