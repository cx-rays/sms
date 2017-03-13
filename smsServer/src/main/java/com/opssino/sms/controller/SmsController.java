/** 
 * Project Name:SmsServerJfinal
 * File Name:SmsController.java
 * Package Name:com.opssino.sms.controller
 * Date:2017年2月21日上午9:45:19
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/
package com.opssino.sms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.smslib.GatewayException;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;
import com.opssino.sms.Interceptor.TSmsLogsInterceptor;
import com.opssino.sms.common.config.MainConfig;
import com.opssino.sms.common.model.TSmsLogs;
import com.opssino.sms.util.PyUtil;
import com.opssino.sms.util.SendMessage;

/**
 * ClassName:SmsController Function: TODO ADD FUNCTION. Reason: TODO ADD REASON. Date: 2017年2月21日 上午9:45:19
 * 
 * @author rays2
 * @version
 * @since JDK 1.8
 * @see
 */

public class SmsController extends Controller {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SmsController.class);
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-ss HH:MM:SS.yyy");

	public TSmsLogsInterceptor t;

	public void index() {
		if (logger.isDebugEnabled()) {
			logger.debug("index() - start");
		}
		System.out.println("SmsController.index11111");
		render("main.jsp");

		if (logger.isDebugEnabled()) {
			logger.debug("index() - end");
		}
	}
	public void sendSMS() {
		if (logger.isDebugEnabled()) {
			logger.debug("sendMessage() - start");
		}
		
		String phoneNumber = getPara("phonenumber");
		String message = getPara("message");
		TSmsLogs ts=new TSmsLogs();
		ts.setPhoneNumber(phoneNumber);
		ts.setMESSAGE(message);
		boolean sendStatus=PropKit.use("sms.properties").getBoolean("sendStatus");
		if(sendStatus){
			MainConfig.workq.add(ts);
		}
		Logger.getLogger("receivefile").info(sdf.format(new Date()) + "接受到发给["+phoneNumber+"]的消息,消息正文为["+message+"],发送开关为["+sendStatus+"]");
		if (logger.isDebugEnabled()) {
			logger.debug("sendMessage() - end");
		}
		renderNull();
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	@Before({ TSmsLogsInterceptor.class })
	public void queryTabData() {
		if (logger.isDebugEnabled()) {
			logger.debug("queryTabData() - start");
		}
		
		StringBuilder sql = new StringBuilder();
		List<Object> paras = new ArrayList<Object>();
		t.buildSql(sql, paras);
		// System.out.println(sql.toString());
		// List<TSmsLogs> lst=TSmsLogs.findSqlAllS();
		Page<TSmsLogs> tSmsLogsPage = TSmsLogs.dao.paginate(t.getPageNumber(), t.getPageSize(), sql.toString(), paras.toArray());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rows", tSmsLogsPage.getList());
		map.put("total", tSmsLogsPage.getTotalRow());
		map.put("totalPage", tSmsLogsPage.getTotalPage());
		map.put("pageNumber", tSmsLogsPage.getPageNumber());
		map.put("pageSize", tSmsLogsPage.getPageSize());
		renderJson(map);

		if (logger.isDebugEnabled()) {
			logger.debug("queryTabData() - end");
		}
	}
	public void searchCSQ(){
		
		renderJson(PyUtil.searchCSQ("python D:\\git\\SmsServerJfinal\\csq.py"));
	}
	

	public void smslist() {
		if (logger.isDebugEnabled()) {
			logger.debug("smslist() - start");
		}

		render("main.jsp");

		if (logger.isDebugEnabled()) {
			logger.debug("smslist() - end");
		}
	}

	public void saveSms() {
		if (logger.isDebugEnabled()) {
			logger.debug("saveSms() - start");
		}

		TSmsLogs t = new TSmsLogs();
		t.setMESSAGE("ess");
		t.save();
		renderNull();
		// renderNull();

		if (logger.isDebugEnabled()) {
			logger.debug("saveSms() - end");
		}
	}
	public void getSimple(){
		renderJson(PyUtil.restartModel("", ""));
	}

}
