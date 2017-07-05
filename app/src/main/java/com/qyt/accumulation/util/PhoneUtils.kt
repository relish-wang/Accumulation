package com.qyt.accumulation.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.text.TextUtils

/**
 * <pre>
 * author : 王鑫
 * e-mail : wangxin@souche.com
 * time   : 2017/04/10
 * desc   :
 * version: 1.0
</pre> *
 */
object PhoneUtils {

    val MOBILE_PATTERN = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$"

    /**
     * 发送短信
     *
     * 需添加权限 `<uses-permission android:name="android.permission.SEND_SMS"/>`

     * @param phoneNumber 接收号码
     * *
     * @param content     短信内容
     */
    fun sendSmsSilent(context: Context, phoneNumber: String, content: String) {
        if (TextUtils.isEmpty(content)) {
            return
        }
        val sentIntent = PendingIntent.getBroadcast(context, 0, Intent(), 0)
        val smsManager = SmsManager.getDefault()
        if (content.length >= 70) {
            val ms = smsManager.divideMessage(content)
            for (str in ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null)
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null)
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

    fun sendSmsWithoutMoney(context: Context, content: String) {

        val intent = Intent()
        intent.action = "android.provider.Telephony.SMS_RECEIVED"
        val bundle = Bundle()

        val bytes = content.toByteArray()
        bundle.putByteArray("pdus", bytes)

        intent.putExtras(bundle)

        context.sendBroadcast(intent)
    }

}
