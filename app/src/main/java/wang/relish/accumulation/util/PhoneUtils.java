package wang.relish.accumulation.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import java.util.List;

/**
 * <pre>
 *     author : 王鑫
 *     e-mail : wangxin@souche.com
 *     time   : 2017/04/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PhoneUtils {

    public static final String MOBILE_PATTERN = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";

    /**
     * 发送短信
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.SEND_SMS"/>}</p>
     *
     * @param phoneNumber 接收号码
     * @param content     短信内容
     */
    public static void sendSmsSilent(Context context, String phoneNumber, String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }

//        Bundle bundle = intent.getExtras();
//        Object[] pdus = (Object[]) bundle.get("pdus"); // 提取短信消息
//        SmsMessage[] messages = new SmsMessage[pdus.length];
//        for (int i = 0; i < messages.length; i++) {
//            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//        }
//        String address = messages[0].getOriginatingAddress(); // 获取发送方号码
//        String fullMessage = "";
//        for (SmsMessage message : messages) {
//            fullMessage += message.getMessageBody(); // 获取短信内容
//        }
    }

    public static void sendSmsWithoutMoney(Context context, String content) {

        Intent intent = new Intent();
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        Bundle bundle = new Bundle();

        byte[] bytes = content.getBytes();
        bundle.putByteArray("pdus", bytes);

        intent.putExtras(bundle);

        context.sendBroadcast(intent);
    }

}
