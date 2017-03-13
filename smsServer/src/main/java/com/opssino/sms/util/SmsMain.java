/*
 * Service.java
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.opssino.sms.util;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.smslib.GatewayException;
import org.smslib.Message.MessageEncodings;
import org.smslib.OutboundMessage;
import org.smslib.OutboundMessage.MessageStatuses;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.opssino.sms.common.config.MainConfig;
import com.opssino.sms.common.model.TSmsLogs;

public class SmsMain {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SmsMain.class);

	private static Service srv = null;
	
	private static int reMaxCount =0;
	
	private static boolean rekg = false;
	
	private static SerialModemGateway gateway=null;
	private  static String csq = "AT+CSQ\r";
	private  static String closeModel = "AT+CFUN=0\r";
	private  static String openModel = "AT+CFUN=1\r";
	private static DecimalFormat df = new DecimalFormat("######0.00");
	public static boolean creatService() {
		if (logger.isDebugEnabled()) {
			logger.debug("creatService() - start");
		}
		srv = Service.getInstance();
		Prop p = PropKit.use("sms.properties");
		gateway = new SerialModemGateway("SMS", p.get("comPort"),
				Integer.parseInt(p.get("baudRate")), p.get("manufacturer"), p.get("model"));
		reMaxCount=p.getInt("reMaxCount");
		rekg=p.getBoolean("reKG");
		gateway.setInbound(true);
		gateway.setOutbound(true);
		try {
			srv.S.SERIAL_POLLING = true;
			srv.addGateway(gateway);
			srv.startService();
			logger.info("Modem connected.");

		} catch (Exception ex) {
			logger.error("creatService()", ex);
			ex.printStackTrace();
			if (logger.isDebugEnabled()) {
				logger.debug("creatService() - end");
			}
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("creatService() - end");
		}
		return true;
	}
	
	public synchronized static boolean restartModel(){
		boolean flag=closeModel();
		if(flag){
			try {
				Thread.sleep(25000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flag=openModel();
			return flag;
			
		}else{
			return false;
		}
	}
	public static boolean searchCsq(){
		String result=sendATCommand(csq);
		if (result.indexOf("OK") != -1 ) {
			String value = StringUtils.substringBetween(result, "SQ:", "OK").trim();
			String val = StringUtils.substringBefore(value, ",");
			String status = StringUtils.substringAfter(value, ",");
			Logger.getLogger("csqfile").info("当前时间是：" + new SimpleDateFormat("yyyy-MM-ss HH:mm:ss").format(new Date()) + ",当前信号误码率为" + status + ",99为不可识别,信号强度为" + val + "," + "最大值是32,百分百为" + df.format(Integer.parseInt(val) * 100.0 / 32) + "%,当信号强度小于10时,短信可能发送失败");
			if (!val.equals("99")) {
				return true;
			}else{
				return false;
			}
		}else{
			Logger.getLogger("csqfile").info("当前时间是：" + new SimpleDateFormat("yyyy-MM-ss HH:mm:ss").format(new Date()) +"ERROR");
			return false;
		}
	}
	
	public synchronized static boolean openModel(){
		String result=sendATCommand(openModel);
		if(result.contains("OK")){
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized static boolean closeModel(){
		String result=sendATCommand(closeModel);
		if(result.contains("OK")){
			return true;
		}else{
			return false;
		}
	}

	public static Service getService() {
		if (logger.isDebugEnabled()) {
			logger.debug("getService() - start");
		}
		if (srv == null) {
			creatService();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getService() - end");
		}
		return srv;
	}

	public synchronized static boolean sendSms(TSmsLogs ts) {
		boolean flag=false;
		reMaxCount=3;
		if (srv == null) {
			if(!creatService()){
				close();
				srv=null;
				if (logger.isDebugEnabled()) {
					logger.debug("sendSms(String, String, OutboundMessage) - end");
				}
				if (ts.getIDS() == null) {
					ts.save();
				} 

			}
		}
		OutboundMessage msg=new OutboundMessage(ts.getPhoneNumber(),ts.getMESSAGE());
		msg.setEncoding(MessageEncodings.ENCUCS2);
		try {
			logger.info("当前信号电平为:"+gateway.getSignalLevel());
			srv.sendMessage(msg);
		//	Thread.sleep(1000*5);
			logger.info(msg);
			if(msg.getMessageStatus()==MessageStatuses.SENT){
				flag=true;
//				ts.setSmsType(1L);
//				ts.setIsOk(1L);
//				ts.setFailReason(msg.getErrorMessage());
//				ts.setClentBody(msg.getMessageStatus().toString());
//				if (ts.getIDS() == null) {
//					ts.save();
//				} else {
//					ts.update();
//				}
			}
//			else{
//				
//				logger.error("当前信号电平为:"+gateway.getSignalLevel());
////				ts.setReSendCount(ts.getReSendCount()+1);
////				ts.setSendTime(msg.getDate());
////				ts.setIsOk(0L);
////				if(ts.getReSendCount()%3==0){
////					ts.update();
////				}else{
////					MainConfig.workq.add(ts);
////				}
//				return false;
//			}
		} catch (Exception ex) {
			logger.error("sendSms(String, String, OutboundMessage)", ex);
			ts.setReSendCount(ts.getReSendCount()+1);
			MainConfig.workq.add(ts);
		}
		return flag;
	}
	public  static String sendATCommand(String command){
		String str="";
		if (srv == null) {
			if(!creatService()){
				close();
				srv=null;
				if (logger.isDebugEnabled()) {
					logger.debug("sendSms(String, String, OutboundMessage) - end");
				}
				return str;
			}
		}
		try {
			str=gateway.sendCustomATCommand(command);
		} catch (GatewayException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return str;
		
	}
	public static boolean sendSms(String mobile, String content,OutboundMessage msg,int reCount) {
		if (logger.isDebugEnabled()) {
			logger.debug("sendSms(String, String, OutboundMessage) - start");
		}
		reMaxCount=3;
		if(reCount>=reMaxCount){
			return false;
		}
		if (srv == null) {
			if(!creatService()){
				close();
				srv=null;
				if (logger.isDebugEnabled()) {
					logger.debug("sendSms(String, String, OutboundMessage) - end");
				}
				return false;
			}
		}
		msg.setEncoding(MessageEncodings.ENCUCS2);
		try {
			logger.info("当前信号电平为:"+gateway.getSignalLevel());
			srv.sendMessage(msg);
			Thread.sleep(1000*5);
			logger.info(msg);
			if(msg.getMessageStatus()==MessageStatuses.SENT){
				return true;
			}else{
				logger.error("当前信号电平为:"+gateway.getSignalLevel());
				return sendSms( mobile,content,msg, reCount++);
			}
		} catch (Exception ex) {
			logger.error("sendSms(String, String, OutboundMessage)", ex);
			try {
				logger.info("发送失败   重新发送   ...");
				srv.sendMessage(msg);
				if(msg.getMessageStatus().name().equals("SENT")){
					return true;
				}else{
					
					return sendSms( mobile,content,msg, reCount++);
				}
				
			} catch (Exception ex2) {
				logger.error("sendSms(String, String, OutboundMessage)", ex2);
				if (logger.isDebugEnabled()) {
					logger.debug("sendSms(String, String, OutboundMessage) - end");
				}
				return false;
			}
		}finally{
			if (logger.isDebugEnabled()) {
				logger.debug("sendSms(String, String, OutboundMessage) - end");
			}
		}

		
		//return true;
	}

	public static void close() {
		if (logger.isDebugEnabled()) {
			logger.debug("close() - start");
		}

		try {
			logger.info("Modem disconnected.");
			
			srv.stopService();
			srv.removeGateway(gateway);
		} catch (Exception ex) {
			logger.error("close()", ex);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("close() - end");
		}
	}
	
	public static void main(String args[]) {
//		TSmsLogs ts=new TSmsLogs();
//		OutboundMessage out=new OutboundMessage();
//		ts.setMESSAGE("发送测试啊测ksljfklsjdfklslkfsd k fhsd fhsdkjhf kdjshf jksd fdsjk fjsdl fjsdjf sdjfdj sfkdsjfkjdfkdjf sd jfljsdfksd f试");
//		ts.setPhoneNumber("15311442765");
//		sendSms(ts);
		searchCsq();
		close();
		
		//sendSms("15311442765", "发送测试啊测ksljfklsjdfklslkfsd k fhsd fhsdkjhf kdjshf jksd fdsjk fjsdl fjsdjf sdjfdj sfkdsjfkjdfkdjf sd jfljsdfksd f试",out,0);
		//sendATCommand("");
	}
	public static int getSmsSignalLevel(){
		try {
			
			return gateway.getSignalLevel();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (GatewayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
//	public static Map<String,String> getProperties(String path) {
//		Properties p = new Properties();
//		InputStream inputStream = SmsMain.class.getResourceAsStream(path);
//		
//		try {
//			p.load(inputStream);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
//		Map<String,String> map = new HashMap<String,String>();
//		Iterator<Object> it =p.keySet().iterator();
//		String key="";
//		while(it.hasNext()){
//			key = it.next().toString();
//			map.put(key, p.getProperty(key).trim());
//		}
//		return map;
//
//	}
}
