/** 
 * Project Name:SmsServerJfinal
 * File Name:SendErrorMessage.java
 * Package Name:com.opssino.sms.cron4j
 * Date:2017年2月24日下午4:41:31
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/
package com.opssino.sms.cron4j;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.opssino.sms.common.config.MainConfig;
import com.opssino.sms.common.model.TSmsLogs;
import com.opssino.sms.util.SendMessageUtil;

/**
 * ClassName:SendErrorMessage Function: TODO ADD FUNCTION. Reason: TODO ADD REASON. Date: 2017年2月24日 下午4:41:31
 * 
 * @author rays2
 * @version
 * @since JDK 1.8
 * @see
 */
public class SendErrorMessage implements Runnable {
	private static final Logger logger = Logger.getLogger("logfile");
	

	@Override
	public void run() {
		logger.info(new Date() + " SendErrorMessage is start!");
		Prop p = PropKit.use("sms.properties");
		boolean rekg = p.getBoolean("reKG");
		if (rekg) {
			// List<TSmsLogs> errList=TSmsLogs.dao.find("select * from t_sms_logs where send_time>DATE_SUB(CURDATE(),INTERVAL 1 DAY) and is_ok=0 ");
		//	List<TSmsLogs> errList = TSmsLogs.dao.find("select * from t_sms_logs where HOUR( timediff( now(), send_time) ) <12 and is_ok=0 limit 0,100");
			List<TSmsLogs> errList = TSmsLogs.dao.getErrorMessage(p.getInt("SearcherrorMsgHour"));
			for (TSmsLogs t : errList) {
				//OutboundMessage msg = new OutboundMessage(t.getPhoneNumber(), t.getMESSAGE());
				//SendMessageUtil.sendMessage(t);
				MainConfig.workq.add(t);
			}
			logger.info(new Date() + " SendErrorMessage is end!");
			// System.out.println(new Date()+"info info");
		}
	}

}
