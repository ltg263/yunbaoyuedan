package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.SplitEditText;
import com.yunbao.common.event.TeenagerEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * @author apple
 */
public class PwdActivity extends AbsActivity implements View.OnClickListener {

    private SplitEditText mInputView;
    private ImageView mBack;
    private InputMethodManager imm;
    private int type;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_pwd;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.setting_pwd));
        Intent intent=getIntent();
        type=intent.getIntExtra(Constants.TYPE,0);
        mInputView=findViewById(R.id.edit);
        mBack=findViewById(R.id.btn_back);
        mBack.setOnClickListener(this);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputView.setOnInputListener(new SplitEditText.OnInputListener() {
            @Override
                public void onInputFinished(String text) {
                    MainHttpUtil.openTeenagers(type, text, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, final String msg, String[] info) {
                            if (code == 0) {
                                if (type==1) {
                                    new DialogUitl.Builder(mContext)
                                            .setContent(WordUtil.getString(R.string.setting_pwd_success))
                                            .setCancelable(false)
                                            .setCancelString(WordUtil.getString(R.string.setting_pwd_1))
                                            .setConfrimString(WordUtil.getString(R.string.confirm))
                                            .setConfirmColor(R.color.global2)
                                            .setClickCallback(new DialogUitl.SimpleCallback2() {
                                                @Override
                                                public void onCancelClick() {
                                                    finish();
                                                    Intent intent=new Intent(mContext, PwdActivity.class);
                                                    intent.putExtra(Constants.TYPE,0);
                                                    startActivity(intent);

                                                }

                                                @Override
                                                public void onConfirmClick(Dialog dialog, String content) {
                                                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                                    EventBus.getDefault().post(new TeenagerEvent());
                                                    finish();
                                                    ToastUtil.show(msg);
                                                }
                                            })
                                            .build()
                                            .show();
                                } else {
                                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                    EventBus.getDefault().post(new TeenagerEvent());
                                    finish();
                                    ToastUtil.show(msg);
                                }

                            }
                        }

                    });

            }
            @Override
            public void onInputChanged(String text) {

            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i=v.getId();
        if (i== R.id.btn_back){
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            EventBus.getDefault().post(new TeenagerEvent());
            finish();
        }
    }
}
