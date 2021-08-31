package com.dahuoji.smstransfer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransmitReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            StringBuilder messageBuilder = new StringBuilder();
            String number = null;
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                String receiveTime = format.format(date);
                number = msg.getOriginatingAddress();
                String message = msg.getDisplayMessageBody();
                messageBuilder.append(message);
            }
            //过滤并转发消息
            List<CaseEntity> caseList = ListActivity.getCaseList(context);
            for (int i = 0; i < caseList.size(); i++) {
                CaseEntity caseEntity = caseList.get(i);
                if ((TextUtils.isEmpty(caseEntity.getFiltersPhoneNumber()) || caseEntity.getFiltersPhoneNumber().equals(number)) &&
                        (TextUtils.isEmpty(caseEntity.getFiltersKeyword1()) || messageBuilder.toString().contains(caseEntity.getFiltersKeyword1())) &&
                        (TextUtils.isEmpty(caseEntity.getFiltersKeyword2()) || messageBuilder.toString().contains(caseEntity.getFiltersKeyword2()))
                ) {
                    if (caseEntity.getContact1() != null) {
                        transmitMessageTo(caseEntity.getContact1().getPhoneNumber(), messageBuilder.toString());
                    }
                    if (caseEntity.getContact2() != null) {
                        transmitMessageTo(caseEntity.getContact2().getPhoneNumber(), messageBuilder.toString());
                    }
                }
            }
        }
    }

    public void transmitMessageTo(String phoneNumber, String message) {
        //使用sendMultipartTextMessage()方法发送超长短信，这种方式也是发送多条短信，不过用户收到的短信会连在一起显示一整条。
        SmsManager sms = SmsManager.getDefault();
        if (message.length() > 70) {
            ArrayList<String> msgs = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phoneNumber, null, msgs, null, null);
        } else {
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }
}

