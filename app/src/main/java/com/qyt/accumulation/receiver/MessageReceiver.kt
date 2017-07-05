package com.qyt.accumulation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

class MessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val messages = getMessagesFromIntent(intent)
            for (message in messages) {
                if (message != null) {
                    Log.i("MessageReceiver", message.originatingAddress + " : " +
                            message.displayOriginatingAddress + " : " +
                            message.displayMessageBody + " : " +
                            message.timestampMillis)
                }
            }
        }
    }

    fun getMessagesFromIntent(intent: Intent): Array<SmsMessage?> {
        val messages = intent.getSerializableExtra("pdus") as Array<Any>
        val pduObjs = arrayOfNulls<ByteArray>(messages.size)

        for (i in messages.indices) {
            pduObjs[i] = messages[i] as ByteArray
        }
        val pdus = arrayOfNulls<ByteArray>(pduObjs.size)
        val pduCount = pdus.size
        val msgs = arrayOfNulls<SmsMessage>(pduCount)
        for (i in 0..pduCount - 1) {
            pdus[i] = pduObjs[i]
            msgs[i] = SmsMessage.createFromPdu(pdus[i])
        }
        return msgs
    }
}
