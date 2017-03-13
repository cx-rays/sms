/** 
 * Project Name:SmsServerJfinal
 * File Name:SearchCSQ.java
 * Package Name:com.opssino.sms.cron4j
 * Date:2017年3月7日上午12:39:35
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/ 
package com.opssino.sms.cron4j;

import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;
import com.opssino.sms.common.config.MainConfig;
import com.opssino.sms.util.SendMessageUtil;
import com.opssino.sms.util.SmsMain;

/** 
 * ClassName:SearchCSQ
 * Function: 定时扫描短信猫信号强度
 * Reason:   TODO ADD REASON.
 * Date:     2017年3月7日 上午12:39:35
 * @author   rays2 
 * @version   
 * @since    JDK 1.8
 * @see       
 */
public class SearchCSQ implements Runnable {
	boolean flag=false;
	@Override
	public void run() {
		if(MainConfig.sendStatus){
			if(PropKit.use("sms.properties").get("model_Info").equals("siemens")){
				flag=SmsMain.searchCsq();
			}else{
				String str=SendMessageUtil.queryCSQ();
				flag=str.equals("ERROR");
			}
			
			if(flag){
				MainConfig.searchCSQCount++;
				if(MainConfig.searchCSQCount>PropKit.use("sms.properties").getInt("csqMaxCount")){
					MainConfig.sendStatus=false;
					Logger.getLogger("csqfile").info("model is restart");
					String result=SendMessageUtil.restartModel();
					if(result.contains("OK")){
						MainConfig.sendStatus=true;
						Logger.getLogger("csqfile").info("model is restart success");
					}else{
						if(PropKit.use("sms.properties").get("model_Info").equals("siemens")){
							SmsMain.restartModel();
						}else{
							SendMessageUtil.restartModel();
						}
						
						MainConfig.sendStatus=true;
					}
				}
			}else{
				MainConfig.searchCSQCount=0;
			}
		}
		

	}

}
