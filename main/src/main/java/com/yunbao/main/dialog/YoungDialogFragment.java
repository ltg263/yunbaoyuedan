package com.yunbao.main.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.YoungActivity;

/**
 *
 * @author cxf
 * @date 2022/5/31
 */

public class YoungDialogFragment extends AbsDialogFragment implements View.OnClickListener {

  private String prompt;
  private TextView mPrompt;
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_young;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(280);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mPrompt=findViewById(R.id.prompt);
        findViewById(R.id.btn_young).setOnClickListener(this);
        findViewById(R.id.btn_know).setOnClickListener(this);
        prompt=bundle.getString(Constants.PROMPT);
        mPrompt.setText(prompt);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i== R.id.btn_young){
            Intent intent=new Intent(mContext, YoungActivity.class);
            mContext.startActivity(intent);
            dismiss();
        }else if (i== R.id.btn_know){
            dismiss();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
