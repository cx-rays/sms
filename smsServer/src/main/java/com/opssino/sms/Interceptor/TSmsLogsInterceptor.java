/** 
 * Project Name:SmsServerJfinal
 * File Name:TSmsLogs.java
 * Package Name:com.opssino.sms.util
 * Date:2017年2月21日下午1:14:12
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/ 
package com.opssino.sms.Interceptor;

import java.util.List;

import com.jfinal.aop.Invocation;
import com.jfinal.aop.PrototypeInterceptor;
import com.mysql.jdbc.StringUtils;
import com.opssino.sms.controller.SmsController;

/** 
 * ClassName:TSmsLogs
 * Function: TODO ADD FUNCTION.
 * Reason:   TODO ADD REASON.
 * Date:     2017年2月21日 下午1:14:12
 * @author   rays2 
 * @version   
 * @since    JDK 1.8
 * @see       
 */
public class TSmsLogsInterceptor extends PrototypeInterceptor {
	private String order ="order by ids desc";
	private String phonenumber;
	private Integer isok;
	private String ds;
	private String de;
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	private Integer pageNumber;
	private Integer pageSize;
	private String message;
	
	private SmsController s;
	@Override
	public void doIntercept(Invocation inv) {
		init(inv);
		s.setAttr("tsmsLogsCot", this);
		s.t=this;
		inv.invoke();
		
	}
	private void init(Invocation ai) {
		s=(SmsController)ai.getController();
		isok=s.getParaToInt("isok");
		phonenumber=s.getPara("phonenumber");
		ds=s.getPara("ds");
		de=s.getPara("de");
		pageNumber=s.getParaToInt("pageNumber",1);
		pageSize=s.getParaToInt("pageSize",20);
		message=s.getPara("message");
		
	}
	
	public void buildSql(StringBuilder sql, List<Object> paras) {
		 sql.append("from t_sms_logs where 1 = 1  ");
		if(phonenumber!=null){
			sql.append(" AND phone_number=?");
			paras.add(phonenumber);
		}
		if(isok!=null&&isok!=9){
			sql.append(" AND is_ok=? ");
			paras.add(isok);
		}
		if(ds!=null){
			sql.append("And send_time >= '"+ds+"'" );
		}
		
		if(de!=null){
			sql.append("And send_time <= '"+de+"'" );
		}
		if(message!=null){
			sql.append(" AND message like '%"+message+"%'" );
		}
		sql.append(order);
	}

}
