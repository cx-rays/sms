/** 
 * Project Name:SmsServerJfinal
 * File Name:ReadMessages.java
 * Package Name:com.opssino.sms.util
 * Date:2017年2月27日上午10:28:14
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/ 
package com.opssino.sms.util;
import java.util.ArrayList;  
import java.util.List;  
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.smslib.AGateway;  
import org.smslib.ICallNotification;  
import org.smslib.IGatewayStatusNotification;  
import org.smslib.IInboundMessageNotification;  
import org.smslib.IOrphanedMessageNotification;  
import org.smslib.InboundMessage;  
import org.smslib.Library;  
import org.smslib.Service;  
import org.smslib.AGateway.GatewayStatuses;  
import org.smslib.AGateway.Protocols;  
import org.smslib.InboundMessage.MessageClasses;  
import org.smslib.Message.MessageTypes;  
import org.smslib.crypto.AESKey;  
import org.smslib.modem.SerialModemGateway;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit; 
/** 
 * ClassName:ReadMessages
 * Function: TODO ADD FUNCTION.
 * Reason:   TODO ADD REASON.
 * Date:     2017年2月27日 上午10:28:14
 * @author   rays2 
 * @version   
 * @since    JDK 1.8
 * @see       
 */
public class ReadMessages {  
    public static Service srv = Service.getInstance();  
    private static final Logger logger = Logger.getLogger(ReadMessages.class);
    public void doIt() throws Exception {  
        List<InboundMessage> msgList;  
        InboundNotification inboundNotification = new InboundNotification();  
        CallNotification callNotification = new CallNotification();  
        GatewayStatusNotification statusNotification = new GatewayStatusNotification();  
        OrphanedMessageNotification orphanedMessageNotification = new OrphanedMessageNotification();  
        try {  
            Prop p = PropKit.use("sms.properties");
            logger.info("Example: Read messages from a serial gsm modem.");  
            logger.info(Library.getLibraryDescription());  
            logger.info("Version: " + Library.getLibraryVersion());  
            SerialModemGateway gateway = new SerialModemGateway("SMS", p.get("comPort"),
    				Integer.parseInt(p.get("baudRate")), p.get("manufacturer"), p.get("model"));
            gateway.setProtocol(Protocols.PDU);  
            gateway.setInbound(true);  
            gateway.setOutbound(true);  
            srv.setInboundMessageNotification(inboundNotification);  
            srv.setCallNotification(callNotification);  
            srv.setGatewayStatusNotification(statusNotification);  
            srv.setOrphanedMessageNotification(orphanedMessageNotification);  
            srv.addGateway(gateway);  
            srv.startService();  
            logger.info("Modem Information:");  
            logger.info(" Manufacturer: " + gateway.getManufacturer());  
            logger.info(" Model: " + gateway.getModel());  
            logger.info(" Serial No: " + gateway.getSerialNo());  
            logger.info(" SIM IMSI: " + gateway.getImsi());  
            logger.info(" Signal Level: " + gateway.getSignalLevel() + "%");  
            logger.info(" Battery Level: " + gateway.getBatteryLevel() + "%");  
            srv.getKeyManager().registerKey("+8613800100500", new AESKey(new SecretKeySpec("0011223344556677".getBytes(), "AES")));  
            msgList = new ArrayList<InboundMessage>();  
            srv.readMessages(msgList, MessageClasses.ALL);  
            for (InboundMessage msg : msgList) {  
                logger.info(msg);  
//              srv.deleteMessage(msg);     //删除短信  
            }  
            logger.info("Now Sleeping - Hit <enter> to stop service.");  
            System.in.read();  
            System.in.read();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public class InboundNotification implements IInboundMessageNotification {  
        public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {  
            if (msgType == MessageTypes.INBOUND)  
                logger.info(">>> New Inbound message detected from Gateway: " + gateway.getGatewayId());  
            else if (msgType == MessageTypes.STATUSREPORT)  
                logger.info(">>> New Inbound Status Report message detected from Gateway: " + gateway.getGatewayId());  
            logger.info(msg);  
        }  
    }  
  
    public class CallNotification implements ICallNotification {  
        public void process(AGateway gateway, String callerId) {  
            logger.info(">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId);  
        }  
    }  
  
    public class GatewayStatusNotification implements IGatewayStatusNotification {  
        public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus) {  
            logger.info(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus + " -> NEW: " + newStatus);  
        }  
    }  
  
    public class OrphanedMessageNotification implements IOrphanedMessageNotification {  
        public boolean process(AGateway gateway, InboundMessage msg) {  
            logger.info(">>> Orphaned message part detected from " + gateway.getGatewayId());  
            logger.info(msg);  
            return false;  
        }  
    }  
  
    public static void main(String args[]) {  
        ReadMessages app = new ReadMessages();  
        try {  
            app.doIt();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
