package com.yunbao.main.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbao.main.R;

/**
 * Created by cxf on 2019/4/12.
 */

public class TagGroup2 extends LinearLayout {

    private Context mContext;
    private TextView[] mTextViews;
    private int mTextSize;
    private int mTagHeight;
    private int mMinWidth;
    private int mTagPadding;
    private int mTagMargin;
    private int mTagCount;
    private int mColor;
    private Drawable mBgDrawable;


    public TagGroup2(Context context) {
        this(context, null);
    }

    public TagGroup2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroup2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagGroup);
        int radius = (int) ta.getDimension(R.styleable.TagGroup_tag_radius, 0);
        int strokeWidth = (int) ta.getDimension(R.styleable.TagGroup_tag_stroke_width, 0);
        mTextSize = (int) ta.getDimension(R.styleable.TagGroup_tag_text_size, 0);
        mTagHeight = (int) ta.getDimension(R.styleable.TagGroup_tag_height, 0);
        mMinWidth = (int) ta.getDimension(R.styleable.TagGroup_tag_min_width, 0);
        mTagPadding = (int) ta.getDimension(R.styleable.TagGroup_tag_padding, 0);
        mTagMargin = (int) ta.getDimension(R.styleable.TagGroup_tag_margin, 0);
        mTagCount = ta.getInt(R.styleable.TagGroup_tag_count, 0);
        mColor = ta.getColor(R.styleable.TagGroup_tag_color, 0);
        ta.recycle();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(strokeWidth, mColor);
        drawable.setCornerRadius(radius);
        mBgDrawable = drawable;
    }

    private TextView createTextView() {
        TextView textView = new TextView(mContext);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mTagHeight);
        layoutParams.rightMargin = mTagMargin;
        textView.setLayoutParams(layoutParams);
        textView.setMinWidth(mMinWidth);
        textView.setPadding(mTagPadding, 0, mTagPadding, 0);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(mColor);
        textView.setBackground(mBgDrawable);
        return textView;
    }


    public void setTags(String[] tags) {
        if (tags == null || tags.length == 0) {
            if (mTextViews == null) {
                mTextViews = new TextView[mTagCount];
            }
            for (int i = 0; i < mTextViews.length; i++) {
                TextView textView = mTextViews[i];
                if (textView != null && textView.getVisibility() == VISIBLE) {
                    textView.setVisibility(INVISIBLE);
                }
            }
            return;
        }
        if (mTextViews == null) {
            mTextViews = new TextView[mTagCount];
        }
        int len = Math.min(tags.length, mTagCount);
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
            textView.setText(tags[i]);
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
