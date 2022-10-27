package com.yunbao.common.custom;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.yunbao.common.R;

public class UIFactory {
  public static RadioButton createOrderLabelRadioButton(LayoutInflater inflater, String title,ViewGroup viewGroup, ViewGroup.LayoutParams layoutParams){
      if(inflater==null){
          return null;
      }
      RadioButton radioButton= (RadioButton) inflater.inflate(R.layout.item_skill,viewGroup,false);
      radioButton.setText(title);
      radioButton.setLayoutParams(layoutParams);
      return radioButton;
  }

    public static RadioButton createOrderLabelRadioButton2(LayoutInflater inflater, String title,ViewGroup viewGroup, ViewGroup.LayoutParams layoutParams){
        if(inflater==null){
            return null;
        }
        RadioButton radioButton= (RadioButton) inflater.inflate(R.layout.item_skill_2,viewGroup,false);
        radioButton.setText(title);
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }

}
