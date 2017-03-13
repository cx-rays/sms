/** 
 * Project Name:SmsServerJfinal
 * File Name:SendMessageUtil.java
 * Package Name:com.opssino.sms.util
 * Date:2017年3月6日下午9:15:47
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/
package com.opssino.sms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.opssino.sms.common.config.MainConfig;
import com.opssino.sms.common.model.TSmsLogs;

/**
 * ClassName:SendMessageUtil Function: TODO ADD FUNCTION. Reason: TODO ADD REASON. Date: 2017年3月6日 下午9:15:47
 * 
 * @author rays2
 * @version
 * @since JDK 1.8
 * @see
 */
public class SendMessageUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SendMessageUtil.class);
	//private static 
	private static DecimalFormat df = new DecimalFormat("######0.00");
	private static Prop p = PropKit.use("sms.properties");
	public static String restartModel(){
		String str="";
		String[] param={p.get("smsPath"),"RESTART"};
		//String[] param={"d:/Release/smsproject.exe","RESTART"};
		
		
		str=invokingC(param);
		if (str.indexOf("OK") != -1 ) {
			str="OK";
			logger.info("Restart Model is OK"+"当前时间是：" + new SimpleDateFormat("yyyy-mm-ss HH:MM:SS.yyy").format(new Date()));
		}else{
			str="ERROR";
		}
		return str;
	}
	
	
	public static String queryCSQ() {
		String str="";
		String[] param={p.get("smsPath"),"CSQ"};
		str=invokingC(param);
		if (str.indexOf("OK") != -1 ) {
			String value = StringUtils.substringBetween(str, "SQ:", "OK").trim();
			String val = StringUtils.substringBefore(value, ",");
			String status = StringUtils.substringAfter(value, ",");
			if (!val.equals("99")) {
				str = "OK";
			}else{
				str="ERROR";
			}
			Logger.getLogger("csqfile").info("当前时间是：" + new SimpleDateFormat("yyyy-MM-ss HH:mm:ss").format(new Date()) + ",当前信号误码率为" + status + ",99为不可识别,信号强度为" + val + "," + "最大值是32,百分百为" + df.format(Integer.parseInt(val) * 100.0 / 32) + "%,当信号强度小于10时,短信可能发送失败");
			
		}else{
			Logger.getLogger("csqfile").info("当前时间是：" + new SimpleDateFormat("yyyy-MM-ss HH:mm:ss").format(new Date()) +"ERROR");
			str = "ERROR";
		}
		
		return str;
	}
	public static String sendMessage(String phoneNumber,String msg){
		String str="";
		String[] param={p.get("smsPath"),phoneNumber,msg};
		str=invokingC(param);
		return str;
	}
	
	public synchronized static String sendMessage(TSmsLogs ts){
		Prop p = PropKit.use("sms.properties");
		String str="";
		String[] param={p.get("smsPath"),ts.getPhoneNumber(),ts.getMESSAGE()};//
		str=invokingC(param);
		
		return str;
	}
	public synchronized static String invokingC(String[] param) {
		if (logger.isDebugEnabled()) {
			logger.debug("invokingPython(String) - start");
		}
		String result = "";
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Runtime runtime = Runtime.getRuntime();
			Process pr = runtime.exec(param);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream(),"utf8"));
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				
				result += line;
			}
			in.close();
			pr.waitFor();

		} catch (IOException e) {
			logger.error("invokingPython(String)", e);
		} catch (InterruptedException e) {
			logger.error("invokingPython(String)", e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("restartModel(String) - end");
		}
		return result;
	}

	public static void main(String[] args) {
		String[] param = { "d:/Release/smsproject.exe", "15311442765", "怡宝怡宝好宝宝" };
		queryCSQ();
	//	restartModel();
	//	invokingC(param);
		//sendMessage("15311442765", "5我爱北京");
	}

}
