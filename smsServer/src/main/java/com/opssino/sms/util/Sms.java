/** 
 * Project Name:SmsServerJfinal
 * File Name:Sms.java
 * Package Name:com.opssino.sms.util
 * Date:2017年3月1日下午1:36:03
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/
package com.opssino.sms.util;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

//import javax.comm.CommPortIdentifier;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import gnu.io.CommPortIdentifier;

//import gnu.io.CommPortIdentifier;

/** 
 * ClassName:Sms
 * Function: TODO ADD FUNCTION.
 * Reason:   TODO ADD REASON.
 * Date:     2017年3月1日 下午1:36:03
 * @author   rays2 
 * @version   
 * @since    JDK 1.8
 * @see       
 */
/**
 * 短信息发送
 * 
 * @author Anlw
 * 
 */
public class Sms {
	private static final Logger logger = Logger.getLogger(Sms.class);
	private CommonSms commonsms;
	private static char symbol1 = 13;
	private static String strReturn = "", atCommand = "";
	
	private static Port port;
	
	
	private static boolean isCCID(String port){
		
		return false;
	}
	public static  void init(){
		logger.debug("init() is start");
		List<String> clist= getComPort();
		if (clist.size()>0){
			port=new Port(clist.get(0));
		}
		logger.debug("init() is end");
		querySMSSignal();
		//System.out.println(clist.get(0));
	}
	
	private static String readMessage(String port) {
		String strReturn = "";
		Port myport = new Port(port);
		try {
			String atCommand = "AT+CMGL=\"ALL\"" + String.valueOf(symbol1);
			strReturn = myport.sendAT(atCommand);
			logger.debug("strReturn is [" + strReturn + "]");
		} catch (RemoteException e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			myport.close();
		}
		return strReturn;
	}

	/**
	 * restartDXM:(重启短信猫!). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * TODO(这里描述这个方法的使用方法 – 可选).<br/>
	 * TODO(这里描述这个方法的注意事项 – 可选).<br/>
	 * 
	 * @author rays2
	 * @param port
	 * @return
	 * @since JDK 1.8
	 */
	private static String restartDXM(String port) {
		String strReturn = "";
		Port myport = new Port(port);
		try {
			
			String atCommand = "AT+CFUN=0" + String.valueOf(symbol1);
			strReturn = myport.sendAT(atCommand);
			logger.debug("strReturn is [" + strReturn + "]");
			Thread.sleep(30000);
			atCommand = "AT+CFUN=1" + String.valueOf(symbol1);
			strReturn = myport.sendAT(atCommand);
			logger.debug("strReturn is [" + strReturn + "]");
			// if (strReturn.indexOf("OK") != -1 && strReturn.indexOf("+CSQ") != -1) {
			//
			// }
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			myport.close();
		}
		return null;
	}

	/**
	 * querySMSStatus:(查询当前信号强度状态). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * TODO(这里描述这个方法的使用方法 – 可选).<br/>
	 * TODO(这里描述这个方法的注意事项 – 可选).<br/>
	 * 
	 * @author rays2
	 * @param myport
	 * @return
	 * @since JDK 1.8
	 */
	private static String querySMSSignal(String port) {
		String strReturn = "";
		Port myport = new Port(port);
		try {

			String atCommand = "AT+CSQ" + String.valueOf(symbol1);
			strReturn = myport.sendAT(atCommand);
			if (strReturn.indexOf("OK") != -1 && strReturn.indexOf("+CSQ") != -1) {
				String value = StringUtils.substringBetween(strReturn, "+CSQ:", "OK").trim();
				String val = StringUtils.substringBefore(value, ",");
				String status = StringUtils.substringAfter(value, ",");
				// String
				if (status.equals("0")) {
					status = "网络正常";
				} else if (status.equals("99")) {
					status = "无网络";
				}
				DecimalFormat df = new DecimalFormat("######0.00");
				logger.info("当前时间是：" + new SimpleDateFormat("yyyy-mm-ss HH:MM:SS.yyy").format(new Date()) + ",当前信号状态为" + status + "信号强度为" + val + "," + "最大值是32,百分百为" + df.format(Integer.parseInt(val) * 100.0 / 32) + "%,当信号强度小于10时,短信可能发送失败");
				myport.close();
			}else{
				logger.info("error ");
			}

			// logger.info("csq is ["+strReturn+"]");

			//
			// return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			// return false;
			// return strReturn;
		} finally {
			myport.close();
		}
		return strReturn;
	}
	
	
	private static String querySMSSignal() {
		logger.debug("querySMSSignal() is start");
		String strReturn = "";
		//Port myport = new Port(port);
		try {

			String atCommand = "AT+CSQ" + String.valueOf(symbol1);
			strReturn = port.sendAT(atCommand);
			if (strReturn.indexOf("OK") != -1 && strReturn.indexOf("+CSQ") != -1) {
				String value = StringUtils.substringBetween(strReturn, "+CSQ:", "OK").trim();
				String val = StringUtils.substringBefore(value, ",");
				String status = StringUtils.substringAfter(value, ",");
				// String
				if (status.equals("0")) {
					status = "网络正常";
				} else if (status.equals("99")) {
					status = "无网络";
				}
				DecimalFormat df = new DecimalFormat("######0.00");
				logger.info("当前时间是：" + new SimpleDateFormat("yyyy-mm-ss HH:MM:SS.yyy").format(new Date()) + ",当前信号状态为" + status + "信号强度为" + val + "," + "最大值是32,百分百为" + df.format(Integer.parseInt(val) * 100.0 / 32) + "%,当信号强度小于10时,短信可能发送失败");
			//	myport.close();
			}else{
				logger.error("error");
			}

			// logger.info("csq is ["+strReturn+"]");

			//
			// return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			// return false;
			// return strReturn;
		} 
//		finally {
//			//myport.close();
//		}
		return strReturn;
	}

	private boolean SendSms(Port myport) {
		if (!myport.isIsused()) {
			logger.info("COM通讯端口未正常打开!");
			return false;
		}
		boolean falg = setMessageMode(myport, 1);
		// 空格
		char symbol2 = 34;
		// ctrl~z 发送指令
		char symbol3 = 26;
		try {
			// 设置
			atCommand = "AT+CSMP=17,169,0,08" + String.valueOf(symbol1);
			strReturn = myport.sendAT(atCommand);
			if (strReturn.indexOf("OK", 0) != -1) {
				atCommand = "AT+CMGS=" + commonsms.getRecver() + String.valueOf(symbol1);
				strReturn = myport.sendAT(atCommand);
				atCommand = StringUtil.encodeHex(commonsms.getSmstext().trim()) + String.valueOf(symbol3) + String.valueOf(symbol1);
				strReturn = myport.sendAT(atCommand);
				if (strReturn.indexOf("OK") != -1 && strReturn.indexOf("+CMGS") != -1) {
					logger.info("短信发送成功！！");
					return true;
				}
			}
		} catch (Exception ex) {
			logger.info("短信发送失败！！");
			return false;
		}
		logger.info("COM端口被占用，请选择正确的COM端口！！");
		logger.info("短信发送失败！！");
		return false;
	}
	
	
	private boolean SendSms() {
		if (!port.isIsused()) {
			logger.info("COM通讯端口未正常打开!");
			return false;
		}
		boolean falg = setMessageMode(port, 1);
		// 空格
		char symbol2 = 34;
		// ctrl~z 发送指令
		char symbol3 = 26;
		try {
			// 设置
			atCommand = "AT+CSMP=17,169,0,08" + String.valueOf(symbol1);
			strReturn = port.sendAT(atCommand);
			if (strReturn.indexOf("OK", 0) != -1) {
				atCommand = "AT+CMGS=" + commonsms.getRecver() + String.valueOf(symbol1);
				strReturn = port.sendAT(atCommand);
				atCommand = StringUtil.encodeHex(commonsms.getSmstext().trim()) + String.valueOf(symbol3) + String.valueOf(symbol1);
				strReturn = port.sendAT(atCommand);
				if (strReturn.indexOf("OK") != -1 && strReturn.indexOf("+CMGS") != -1) {
					logger.info("短信发送成功！！");
					return true;
				}
			}
		} catch (Exception ex) {
			logger.info("短信发送失败！！");
			return false;
		}
		logger.info("COM端口被占用，请选择正确的COM端口！！");
		logger.info("短信发送失败！！");
		return false;
	}

	/**
	 * 设置消息模式
	 * 
	 * @param op
	 *            0-pdu 1-text(默认1 文本方式 )
	 * @return
	 */
	private boolean setMessageMode(Port myport, int op) {
		try {
			String atCommand = "AT+CMGF=" + String.valueOf(op) + String.valueOf(symbol1);
			String strReturn = myport.sendAT(atCommand);
			if (strReturn.indexOf("OK", 0) != -1) {
				return true;
			}
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 号码，内容，发送短信息
	 * 
	 * @param phone
	 * @param countstring
	 * @throws Exception
	 */
	private static void send(String phone, String countstring, String com) {
		Sms s = new Sms();
		CommonSms cs = new CommonSms();
		cs.setRecver(phone);
		cs.setSmstext(countstring);
		s.setCommonsms(cs);
		Port myort = new Port(com);
		s.SendSms(myort);
		myort.close();
	}

	// 短信的字符限制为70，加锁，因为每次只能发送一条，其他的要等待
	public static synchronized void sendSms(String phone, String str, String com) {
		if (str.length() > 70) {
			String done = str.substring(0, 70);
			Sms.send(phone, done, com);
			String todo = str.substring(70, str.length());
			Sms.sendSms(phone, todo, com);
		} else {
			Sms.send(phone, str, com);
		}
	}

	private void setCommonsms(CommonSms commonsms) {
		this.commonsms = commonsms;
	}

	// 获取计算机所有可用端口
	public static List<String> getComPort() {

		Enumeration enumeration = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId;
		List<String> list = new ArrayList<String>();
		while (enumeration.hasMoreElements()) {
			portId = (CommPortIdentifier) enumeration.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				list.add(portId.getName());
			}
		}
		return list;
	}
	
	public static boolean testAT(){
		Port port=new Port("COM3");
		try {
			String strReturn =port.sendAT("AT"+ String.valueOf(symbol1)) ;
			System.out.println("testAT()"+strReturn);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		port.close();
		
		return false;
	}

	public static void main(String[] args) {
		final String str1 = "嘿嘿";
		final String str2 = "嘻嘻";
//		Sms.testAT();
		Sms.sendSms("15311442765", "str1heihei你好房东", "COM3");
	//	Sms.init();
		// Sms.restartDXM("COM3");
		// Port myort = new Port("COM3");
		// Sms.querySMSSignal("COM3");
		// myort.close();
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Sms.sendSms("13311220311", str1, "COM6");
		// }
		// }).start();
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Sms.sendSms("13311220311", str2, "COM6");
		// }
		// }).start();
	}

}
