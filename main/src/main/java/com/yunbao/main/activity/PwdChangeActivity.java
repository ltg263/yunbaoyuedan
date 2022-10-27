package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.SplitEditText;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpUtil;


/**
 * @author apple
 */
public class PwdChangeActivity extends AbsActivity implements View.OnClickListener {

    private SplitEditText mInputView1,mInputView2,mInputView3;
    private boolean input1,input2,input3;
    private TextView mPwdConfirm;
    private String s1,s2,s3;
    private InputMethodManager imm;
    private View wrap1,wrap2,wrap3;
    public static void forward(Context context) {
        Intent intent=new Intent(context, PwdChangeActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_pwd_change;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.pwd_change));
        mInputView1=findViewById(R.id.edit1);
        mInputView2=findViewById(R.id.edit2);
        mInputView3=findViewById(R.id.edit3);
        mPwdConfirm=findViewById(R.id.change_pwd_confirm);
        mPwdConfirm.setOnClickListener(this);
        mPwdConfirm.setEnabled(false);
        wrap1 = findViewById(R.id.wrap1);
        wrap2 = findViewById(R.id.wrap2);
        wrap3 = findViewById(R.id.wrap3);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputView1.requestFocus();
        imm.showSoftInput(mInputView1, InputMethodManager.SHOW_FORCED);
        wrap1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        wrap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputView1.requestFocus();
                imm.showSoftInput(mInputView1, InputMethodManager.SHOW_FORCED);
            }
        });
        wrap2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        wrap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputView2.requestFocus();
                imm.showSoftInput(mInputView2, InputMethodManager.SHOW_FORCED);
            }
        });
        wrap3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        wrap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputView3.requestFocus();
                imm.showSoftInput(mInputView3, InputMethodManager.SHOW_FORCED);
            }
        });
        mInputView1.setOnInputListener(new SplitEditText.OnInputListener() {
            @Override
            public void onInputFinished(String text) {
                input1=true;
                s1=text;
                changeBg();
                mInputView2.requestFocus();
                imm.showSoftInput(mInputView2, InputMethodManager.SHOW_FORCED);
            }

            @Override
            public void onInputChanged(String text) {
                input1=false;
                changeBg();
            }
        });
       mInputView2.setOnInputListener(new SplitEditText.OnInputListener() {
           @Override
           public void onInputFinished(String text) {
               input2=true;
               s2=text;
               changeBg();
               mInputView3.requestFocus();
               imm.showSoftInput(mInputView3, InputMethodManager.SHOW_FORCED);
           }

           @Override
           public void onInputChanged(String text) {
               input2=false;
               changeBg();
           }
       });
       mInputView3.setOnInputListener(new SplitEditText.OnInputListener() {
           @Override
           public void onInputFinished(String text) {
               input3=true;
               s3=text;
               changeBg();
           }

           @Override
           public void onInputChanged(String text) {
               input3=false;
               changeBg();
           }
       });
    }
    /**
     * 改变确认按钮的状态
     */
    private void changeBg(){
        if (input1&&input2&&input3){
            mPwdConfirm.setTextColor(getResources().getColor(R.color.white));
            mPwdConfirm.setBackgroundResource(R.drawable.textview_border_circle7);
            mPwdConfirm.setEnabled(true);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }else{
            mPwdConfirm.setTextColor(getResources().getColor(R.color.gray1));
            mPwdConfirm.setBackgroundResource(R.drawable.textview_border_circle13);
        }

    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.change_pwd_confirm) {
            if (!s2.equals(s3)){
                ToastUtil.show(R.string.pwd_no_different);
                return;
            }
            changePwd();
        }
    }
    /**
     * 修改密码
     */
    private void changePwd(){
        MainHttpUtil.updateTeenagerPassword(s1,s2, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code==0){
                    finish();
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mInputView1.setText("");
                            mInputView2.setText("");
                            mInputView3.setText("");
                            mInputView1.requestFocus();
                        }
                    },500);


                }
                ToastUtil.show(msg);
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
