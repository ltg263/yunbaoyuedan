package com.yunbao.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.yunbao.common.R;

public class CheckImageView extends android.support.v7.widget.AppCompatImageView implements Checkable {
    public static final int MODE_HTTP_STATE=1;
    public static final int MODE_LOCAL_STATE=2;
    private int state=MODE_LOCAL_STATE;

    private Drawable[]imageResource=new Drawable[2];
    private boolean isChecked;
    private onCheckClickListner checkClickListner;

    private boolean enableClick;

    public CheckImageView(Context context) {
        super(context);
        init(context);
    }

    public CheckImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrDrawable(context,attrs);
        init(context);
    }



    private void getAttrDrawable(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CheckImageView);
        Drawable drawable=ta.getDrawable(R.styleable.CheckImageView_deault_image);
        if(drawable!=null) {
            imageResource[0]=drawable;
        }

        drawable= ta.getDrawable(R.styleable.CheckImageView_select_image);
        enableClick=ta.getBoolean(R.styleable.CheckImageView_enable_click,true);
        if(drawable!=null) {
            imageResource[1]=drawable;
        }

    }

    public CheckImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrDrawable(context,attrs);
        init(context);
    }

    private void init(Context context) {
        refeshUI();
        if(state==MODE_LOCAL_STATE&&enableClick){
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkClickListner!=null){
                        checkClickListner.onCheckClick(v,isChecked);
                        change();
                    }
                }
            });
        }else if(state==MODE_HTTP_STATE&&enableClick){
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkClickListner!=null){
                        checkClickListner.onCheckClick(v,isChecked);
                    }
                }
            });
        }
    }

    public void refeshUI() {
        if(isChecked){
            setImageDrawable(imageResource[1]);
        }else{
            setImageDrawable(imageResource[0]);
        }
    }

    public void change(){
        isChecked=!isChecked;
        refeshUI();
     }

    public void addImageResouce(int defaultImg,int selectImg){
        imageResource[0]= ContextCompat.getDrawable(getContext(),defaultImg);
        imageResource[1]= ContextCompat.getDrawable(getContext(),selectImg);
        refeshUI();
    }

    public void addImageResouce(Drawable[] image){
        if(image==null||(imageResource!=null&&imageResource==image)){
            return;
        }
        imageResource=image;
        refeshUI();
    }

    public void setCheckClickListner(onCheckClickListner checkClickListner){
        this.checkClickListner=checkClickListner;
    }



    /**
     * @return The current checked state of the view
     */
    @Override
    public boolean isChecked() {
        return false;
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    @Override
    public void toggle() {
        isChecked=!isChecked;
        refeshUI();
    }

    public interface onCheckClickListner{
        public void onCheckClick(View view, boolean isChecked);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public void setChecked(boolean checked) {
        isChecked = checked;
        refeshUI();
    }





}
