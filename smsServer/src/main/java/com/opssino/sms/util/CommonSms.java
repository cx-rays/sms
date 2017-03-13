/** 
 * Project Name:SmsServerJfinal
 * File Name:CommonSms.java
 * Package Name:com.opssino.sms.util
 * Date:2017年3月1日下午1:35:21
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/ 
package com.opssino.sms.util;
/** 
 * ClassName:CommonSms
 * Function: TODO ADD FUNCTION.
 * Reason:   TODO ADD REASON.
 * Date:     2017年3月1日 下午1:35:21
 * @author   rays2 
 * @version   
 * @since    JDK 1.8
 * @see       
 */
/** 
 * 短信实体类 
 * @author Anlw 
 * 
 */  
public class CommonSms{  
  
    //内容  
    private String smstext;  
    //发送者  
    private String sender;  
    //接收者  
    private String recver;  
    //状态  
    private String state;  
  
    public String getSmstext() {  
        return smstext;  
    }  
    public void setSmstext(String smstext) {  
        this.smstext = smstext;  
    }  
    public String getSender() {  
        return sender;  
    }  
    public void setSender(String sender) {  
        this.sender = sender;  
    }  
    public String getRecver() {  
        return recver;  
    }  
    public void setRecver(String recver) {  
        this.recver = recver;  
    }  
    public String getState() {  
        return state;  
    }  
    public void setState(String state) {  
        this.state = state;  
    }  
      
  
      
}  
