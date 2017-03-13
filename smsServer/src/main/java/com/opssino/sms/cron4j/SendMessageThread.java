/** 
 * Project Name:SmsServerJfinal
 * File Name:SendMessageThread.java
 * Package Name:com.opssino.sms.cron4j
 * Date:2017年3月1日下午5:47:43
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/
package com.opssino.sms.cron4j;

import java.util.Date;

import org.smslib.GatewayException;

import com.jfinal.kit.PropKit;
import com.opssino.sms.common.config.MainConfig;
import com.opssino.sms.common.model.TSmsLogs;
import com.opssino.sms.util.SendMessageUtil;
import com.opssino.sms.util.SmsMain;

/**
 * ClassName:SendMessageThread Function: TODO ADD FUNCTION. Reason: TODO ADD REASON. Date: 2017年3月1日 下午5:47:43
 * 
 * @author rays2
 * @version
 * @since JDK 1.8
 * @see
 */
public class SendMessageThread implements Runnable {
	boolean sendStatusFlag=false;
	@Override
	public void run() {
		while (true) {
			if(!MainConfig.sendStatus){
				try {
					Thread.sleep(2000);
				} catch (Exception ex) {
				}
				continue;
			}
			TSmsLogs tSmsLogs = (TSmsLogs) MainConfig.workq.poll();
			if (tSmsLogs == null) {
				try {
					Thread.sleep(5000);
				} catch (Exception ex) {
				}
				continue;
			}
			
			if(PropKit.use("sms.properties").get("model_Info").equals("siemens")){
				sendStatusFlag=SmsMain.sendSms(tSmsLogs);
			}else{
				String str=SendMessageUtil.sendMessage(tSmsLogs);
				sendStatusFlag=str.indexOf("OK")>-1;
			}
			if(sendStatusFlag){
				tSmsLogs.setIsOk(1L);
				if(tSmsLogs.getReSendCount()>0){
					tSmsLogs.setLastSendTime(new Date());
					tSmsLogs.setReFlag(1);
				}
			}else{
				tSmsLogs.setReSendCount(tSmsLogs.getReSendCount()+1);
				if(tSmsLogs.getReSendCount()%3==0){
					tSmsLogs.setIsOk(0L);
				}else{
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					boolean sendStatus=PropKit.use("sms.properties").getBoolean("sendStatus");
					if(sendStatus){
						MainConfig.workq.add(tSmsLogs);
					}
				}
				
			}
			tSmsLogs.setSendTime(new Date());
			tSmsLogs.setSmsType(1L);
			
			if(tSmsLogs.getIDS()==null){
				tSmsLogs.save();
			}else{
				tSmsLogs.update();
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

//	public void run() {
//		while (true) {
//			CommonSms port = (CommonSms) MainConfig.workq.poll();
//			if (port == null) {
//				
//				try {
//					Thread.sleep(2000);
//				} catch (Exception ex) {
//				}
//				continue;
//			}
//		}
//
//	}
}
