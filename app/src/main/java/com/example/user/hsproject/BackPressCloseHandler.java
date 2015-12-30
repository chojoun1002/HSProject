package com.example.user.hsproject;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by 20151047 on 2015-12-28.
 *
 * [온라인팀 손동현]
 *
 * Back버튼 2번 눌렀을 경우 발생하는 이벤트 정의. 2번 입력시 종료되게 처리함.
 *
 */
public class BackPressCloseHandler {

    private long backBtnPressTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }

    public void onBackBtnPressed() {
        if (System.currentTimeMillis() > backBtnPressTime + 2000) {
            backBtnPressTime = System.currentTimeMillis();
            showToast();
            return;
        }
        if (System.currentTimeMillis() <= backBtnPressTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showToast(){
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
