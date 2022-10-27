package com.yunbao.common.utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;

/**
 * Created by cxf on 2017/8/3.
 */

public class ToastUtil {

    private static Toast sToast;
    private static long sLastTime;
    private static String sLastString;
    private static View view;
    private static TextView toast_message;

    static {
        sToast = makeToast();
    }

    private static Toast makeToast() {
        Toast toast = new Toast(CommonAppContext.sInstance);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        view = LayoutInflater.from(CommonAppContext.sInstance).inflate(R.layout.view_toast_message, null);
        toast_message=view.findViewById(R.id.toast_message);
        toast.setView(view);
        return toast;
    }


    public static void show(int res) {
        show(WordUtil.getString(res));
    }

    public static void show(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - sLastTime > 2000) {
            sLastTime = curTime;
            sLastString = s;
            toast_message.setText(s);
            sToast.show();
        } else {
            if (!s.equals(sLastString)) {
                sLastTime = curTime;
                sLastString = s;
                sToast = makeToast();
                toast_message.setText(s);
                sToast.show();
            }
        }

    }

}
