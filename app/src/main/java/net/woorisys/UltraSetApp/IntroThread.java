package net.woorisys.UltraSetApp;

import android.os.Handler;
import android.os.Message;

public class IntroThread extends Thread{

    private Handler handler;

    public IntroThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        Message msg = new Message();

        msg.what = 1;
        handler.sendEmptyMessage(msg.what);
    }
}
