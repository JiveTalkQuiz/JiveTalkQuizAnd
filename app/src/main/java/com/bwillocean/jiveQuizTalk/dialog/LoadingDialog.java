package com.bwillocean.jiveQuizTalk.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.bwillocean.jiveQuizTalk.MyApplication;
import com.bwillocean.jiveQuizTalk.R;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingDialog extends Dialog {
    final static String TAG = "LoadingDialog";

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_loading);
    }

    static LoadingDialog dialog;
    static Timer dismissTimer;

    static public void showLoading(Context a) {
        showLoading(a, 2 * 60 * 1000);
    }

    static public void showLoading(final Context a, final long timeout) {
        if(Looper.getMainLooper().equals(Looper.myLooper())) {
            _showLoading(a, timeout);
        } else {
            MyApplication.Companion.async(() -> _showLoading(a, timeout));
        }
    }

    static private void _showLoading(Context a, long timeout) {
        if (dialog != null && a instanceof Activity) {
            hideLoading();
        }

        if (a instanceof Activity && !((Activity)a).isFinishing() && !((Activity)a).isDestroyed()) {
            if (dialog == null || !dialog.isShowing()) {

                dialog = new LoadingDialog(a);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnDismissListener(dialog -> {
                    if (dismissTimer != null) {
                        dismissTimer.cancel();
                        dismissTimer = null;
                    }
                });

                if (dismissTimer != null) {
                    dismissTimer.cancel();
                }

                try {
                    dialog.show();

                    dismissTimer = new Timer();
                    dismissTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (hasLoading()) {
                                try {
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static public void hideLoading() {
        if(Looper.getMainLooper().equals(Looper.myLooper())) {
            _hideLoading();
        } else {
            MyApplication.Companion.async(LoadingDialog::_hideLoading);
        }
    }

    static private void _hideLoading() {
        if(hasLoading()) {
            try {
                if(dismissTimer != null) {
                    dismissTimer.cancel();
                    dismissTimer = null;
                }

                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog = null;
        }
    }

    static public boolean hasLoading() {
        return (dialog != null && dialog.isShowing());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }
}
