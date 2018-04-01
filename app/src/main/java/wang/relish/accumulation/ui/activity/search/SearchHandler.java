package wang.relish.accumulation.ui.activity.search;

import android.os.Handler;
import android.os.Message;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Relish Wang
 * @since 2018/03/31
 */
public class SearchHandler extends Handler {


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case SearchActivity.WHAT:
                String obj = (String) msg.obj;
                EventBus.getDefault().post(new MessageEvent(obj));
                break;
        }
    }
}
