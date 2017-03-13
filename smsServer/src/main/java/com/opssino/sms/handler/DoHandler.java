/** 
 * Project Name:SmsServerJfinal
 * File Name:DoHandler.java
 * Package Name:com.opssino.sms.handler
 * Date:2017年2月27日上午9:34:29
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/ 
package com.opssino.sms.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

/** 
 * ClassName:DoHandler
 * Function: TODO ADD FUNCTION.
 * Reason:   TODO ADD REASON.
 * Date:     2017年2月27日 上午9:34:29
 * @author   rays2 
 * @version   
 * @since    JDK 1.8
 * @see       
 */
public class DoHandler extends Handler{

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		// TODO Auto-generated method stub
		
		 if (target.endsWith(".do")){
			 System.out.println(target);
			 target = target.substring(0, target.length() - 3);
			 next.handle(target, request, response, isHandled);
		 }else{
			 next.handle(target, request, response, isHandled);
		 }
	}
	
}
