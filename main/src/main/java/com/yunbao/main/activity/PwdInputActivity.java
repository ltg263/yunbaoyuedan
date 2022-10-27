package com.yunbao.main.activity;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.SplitEditText;
import com.yunbao.common.event.TeenagerEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * @author apple
 */
public class PwdInputActivity extends AbsActivity {

    private SplitEditText mInputView;
    private int type;
    private InputMethodManager imm;
    private View wrap;
    public static void forward(Context context,int type) {
        Intent intent=new Intent(context, PwdInputActivity.class);
        intent.putExtra(Constants.TYPE,type);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_pwd_input;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.pwd_input));
        Intent intent=getIntent();
        type=intent.getIntExtra(Constants.TYPE,0);//1开启 2关闭
        mInputView=findViewById(R.id.edit);
         wrap = findViewById(R.id.wrap);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        wrap.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputView.requestFocus();
                imm.showSoftInput(mInputView, InputMethodManager.SHOW_FORCED);
            }
        });
        mInputView.setOnInputListener(new SplitEditText.OnInputListener() {
            @Override
                public void onInputFinished(String text) {
                if (type==1) {
                    openTeenager(text);
                }else{
                    closeTeenager(text);
                }
    }

    @Override
    public void onInputChanged(String text) {
            }
        });

    }
    /**
     * 开启青少年模式
     */
    private void openTeenager(String pwd){
        MainHttpUtil.openTeenagers(1, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, final String msg, String[] info) {
                if (code == 0) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        EventBus.getDefault().post(new TeenagerEvent());
                        finish();
                    }
                    ToastUtil.show(msg);
            }
        });
    }

    /**
     * 关闭青少年模式
     */
    private void closeTeenager(String pwd){
        MainHttpUtil.closeTeenagers(pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    EventBus.getDefault().post(new TeenagerEvent());
                    finish();
                }
                ToastUtil.show(msg);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
