/** 
 * Project Name:SmsServerJfinal
 * File Name:SendMessage.java
 * Package Name:com.opssino.sms.util
 * Date:2017年2月27日上午10:23:01
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/
package com.opssino.sms.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IOutboundMessageNotification;
import org.smslib.Message.MessageEncodings;
import org.smslib.OutboundMessage.MessageStatuses;
import org.smslib.SMSLibException;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.opssino.sms.common.config.MainConfig;
import com.opssino.sms.common.model.TSmsLogs;

/**
 * ClassName:SendMessage Function: TODO ADD FUNCTION. Reason: TODO ADD REASON. Date: 2017年2月27日 上午10:23:01
 * 
 * @author rays2
 * @version
 * @since JDK 1.8
 * @see
 */
public class SendMessage {
	private static final Logger logger = Logger.getLogger(SendMessage.class);
	private static Service srv;
	private static OutboundMessage msg;
	private SerialModemGateway gateway;
//
//	public class OutboundNotification implements IOutboundMessageNotification {
//		public void process(AGateway agateway, OutboundMessage outboundmessage) {
//			logger.info("Outbound handler called from Gateway: " + agateway);
//			System.out.println(outboundmessage);
//
//		}
//	}
	
	public static void sendAt(String common){
		srv = Service.getInstance();
		common="AT+CSQ\r";
	}

	@SuppressWarnings("deprecation")
	public synchronized static void sendSMS(TSmsLogs tSmsLogs) throws GatewayException {
		
		// TSmsLogs tSmsLogs;
		logger.info("phonenumber is "+tSmsLogs.getPhoneNumber());
		logger.info("message is "+tSmsLogs.getMESSAGE());
	//	OutboundNotification outboundNotification = new OutboundNotification();
		// srv = new Service();
		srv = Service.getInstance();
		
		//srv.S.SERIAL_POLLING=true;
		Prop p = PropKit.use("sms.properties");
		SerialModemGateway gateway = new SerialModemGateway(getrechargeablePassword(), "COM3", 9600, "Wavecom", ""); // 设置端口与波特率
		// SerialModemGateway gateway = new SerialModemGateway("SMS", p.get("comPort"),
		// Integer.parseInt(p.get("baudRate")), p.get("manufacturer"), p.get("model"));
		gateway.setInbound(true);
		gateway.setOutbound(true);
		
		// gateway.setSimPin("1234");
		// gateway.setOutboundNotification(outboundNotification);
		//srv.setOutboundMessageNotification(outboundNotification);
		srv.addGateway(gateway);
		logger.info("初始化成功，准备开启服务");
		try {
			srv.startService();
			logger.info("服务启动成功");
			// tSmsLogs = new TSmsLogs();
			msg = new OutboundMessage(tSmsLogs.getPhoneNumber(), tSmsLogs.getMESSAGE());
			
			msg.setEncoding(MessageEncodings.ENCUCS2); // 中文
			
			srv.sendMessage(msg);
//			System.out.println("PDU"+msg.getPduUserData());
//			System.out.println("PDU_HEAD"+msg.getPduUserDataHeader());
//			System.out.println("PDU_HEAD"+msg.getPdus("15311442765", 1));
			Thread.sleep(3 * 1000);
			if (msg.getMessageStatus() == MessageStatuses.SENT) {
				tSmsLogs.setSendTime(msg.getDispatchDate());
				tSmsLogs.setIsOk(1L);
			} else {
				tSmsLogs.setReSendCount(tSmsLogs.getReSendCount() + 1);
				if (tSmsLogs.getReSendCount() % 3 == 0) {
					tSmsLogs.setSendTime(msg.getDate());
					tSmsLogs.setIsOk(0L);
				} else {
					MainConfig.workq.add(tSmsLogs);
				}
				tSmsLogs.setSendTime(msg.getDate());
				tSmsLogs.setIsOk(0L);
			}
			tSmsLogs.setSmsType(1L);
			tSmsLogs.setFailReason(msg.getErrorMessage());
			tSmsLogs.setClentBody(msg.getMessageStatus().toString());
			if (tSmsLogs.getIDS() == null) {
				tSmsLogs.save();
			} else {
				tSmsLogs.update();
			}
			logger.info("record is save");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			try {
				//gateway.
				//gateway.stopGateway();
				gateway.setInbound(false);
				gateway.setOutbound(false);
			//	gateway.sendCustomATCommand(atCommand);
				srv.stopService();
				srv.removeGateway(gateway);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (SMSLibException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	  // 获取密码  
    public static String getrechargeablePassword() {  
        Random random = new Random();  
        char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',  
                'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'c',  
                'b', 'd', 'f', 'e', 'g', 'h', 'j', 'i', 'l', 'k', 'n', 'm', 'o', 'p', 'q', 'r', 's', 't', 'u', 'w',  
                'v' };  
        String strRand = "";  
        for (int i = 0; i < 18; i++) {  
            strRand = strRand + String.valueOf(codeSequence[random.nextInt(59)]);  
        }  
        
        return strRand;  
    }  

	public static void main(String[] args) throws GatewayException {
		TSmsLogs tSmsLogs = new TSmsLogs();
		tSmsLogs.setPhoneNumber("15311442765");
		tSmsLogs.setMESSAGE("短信内容" + new SimpleDateFormat("yyyy-mm-dd HH:MM:SS").format(new Date()));
		SendMessage sendMessage = new SendMessage();
		sendMessage.sendSMS(tSmsLogs);
	}
}
