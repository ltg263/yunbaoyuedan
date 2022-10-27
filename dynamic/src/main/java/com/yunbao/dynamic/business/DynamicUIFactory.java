package com.yunbao.dynamic.business;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yunbao.common.adapter.ImChatFacePagerAdapter;
import com.yunbao.common.interfaces.OnFaceClickListener;
import com.yunbao.dynamic.R;

public class DynamicUIFactory {


 public static View createFaceView(int[]faceHeight,Context context, View.OnClickListener clickListener, OnFaceClickListener onFaceClickListener){
         LayoutInflater inflater = LayoutInflater.from(context);
         View v = inflater.inflate(R.layout.view_chat_face, null);
         v.measure(0, 0);
         if(faceHeight!=null&faceHeight.length>0){
            faceHeight[0]= v.getMeasuredHeight();
         }
         v.findViewById(R.id.btn_send).setOnClickListener(clickListener);
         final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
         ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
         viewPager.setOffscreenPageLimit(10);
         ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(context, onFaceClickListener);
         viewPager.setAdapter(adapter);
         viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             }
             @Override
             public void onPageSelected(int position) {
                 ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
             }
             @Override
             public void onPageScrollStateChanged(int state) {
             }
         });
         for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
             RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
             radioButton.setId(i + 10000);
             if (i == 0) {
                 radioButton.setChecked(true);
             }
             radioGroup.addView(radioButton);
         }
         return v;
 }

}
