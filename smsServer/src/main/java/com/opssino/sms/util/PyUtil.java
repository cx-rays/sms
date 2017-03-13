/** 
 * Project Name:SmsServerJfinal
 * File Name:PyUtil.java
 * Package Name:com.opssino.sms.util
 * Date:2017年3月2日下午4:09:14
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/
package com.opssino.sms.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName:PyUtil Function: TODO ADD FUNCTION. Reason: TODO ADD REASON. Date: 2017年3月2日 下午4:09:14
 * 
 * @author rays2
 * @version
 * @since JDK 1.8
 * @see
 */
public class PyUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(PyUtil.class);

//	public void dypy() {
//		Properties props = new Properties();
//		props.put("python.console.encoding", "UTF-8"); // Used to prevent: console: Failed to install '': java.nio.charset.UnsupportedCharsetException: cp0.
//		props.put("python.security.respectJavaAccessibility", "false"); //don't respect java accessibility, so that we can access protected members on subclasses
//		props.put("python.import.site","false");
//		Properties preprops = System.getProperties();
//		PythonInterpreter.initialize(preprops, props, new String[0]);
////		PythonInterpreter interp = new PythonInterpreter();
//		PythonInterpreter interp = new PythonInterpreter();
//		interp.exec("import sys");
//		
//		//interp.exec("sys.path.append('D:/Program Files (x86)/jython2.7.0/Lib')");
//		interp.exec("sys.path.append('C:/Users/rays2/AppData/Local/Programs/Python/Python35-32/Lib')");
//		//interp.exec("sys.path.append('C:/Users/rays2/AppData/Local/Programs/Python/Python35-32/Lib/site-packages/')");
//	//	interp.exec("sys.path.append('C:/Users/rays2/AppData/Local/Programs/Python/Python35-32/Lib/importlib')");
//		
//	
//		interp.execfile("D:\\git\\SmsServerJfinal\\ff.py");
//		PyFunction func = (PyFunction) interp.get("adder", PyFunction.class);
//		PyObject pyobj = func.__call__();
//		System.out.println("anwser = " + pyobj.toString());  
//	}
	public static String restartModel(String openModelPath,String closeModelPath){
		String str="";
		closeModelPath="python D:\\git\\SmsServerJfinal\\closeModel.py";
		str=invokingPython(closeModelPath);
		if (str.indexOf("OK") != -1 ){
			try {
				Thread.sleep(15*1000);
				openModelPath="python D:\\git\\SmsServerJfinal\\openModel.py";
				str=invokingPython(openModelPath);
				if (str.indexOf("OK") != -1 ){
					str="isok";
				}else{
					str="当前时间是：" + new SimpleDateFormat("yyyy-mm-ss HH:MM:SS.yyy").format(new Date()) +"ERROR";
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			str="error";
		}
		
		
		return str;
	}
	
	
	public static String searchCSQ(String pyPath){
		String str="";
		str=invokingPython(pyPath);
		if (str.indexOf("OK") != -1 && str.indexOf("+CSQ") != -1) {
			String value = StringUtils.substringBetween(str, "+CSQ:", "OK").trim();
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
			
		}else{
			str="当前时间是：" + new SimpleDateFormat("yyyy-mm-ss HH:MM:SS.yyy").format(new Date()) +"ERROR";
		}
		
		return str;
	}
	
	public static String invokingPython(String pyPath){
		String result="";
		if (logger.isDebugEnabled()) {
			logger.debug("restartModel(String) - start");
		}
		try {
			Process pr = Runtime.getRuntime().exec(pyPath);
			BufferedReader in = new BufferedReader(new  
			        InputStreamReader(pr.getInputStream()));  
			
			String line;  
			while ((line = in.readLine()) != null) {  
//			    System.out.println(line);  
			    result+=line;
			}  
			in.close();  
			pr.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (logger.isDebugEnabled()) {
			logger.debug("restartModel(String) - end");
		}
		return result;
	}
	public static void main(String[] args) {
		searchCSQ("python D:\\git\\SmsServerJfinal\\csq.py");
//		try {
//			Runtime.getRuntime().exec("python D:\\git\\SmsServerJfinal\\ff.py");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("start");  
//        try {
//			Process pr = Runtime.getRuntime().exec("python D:\\git\\SmsServerJfinal\\csq.py");  
//			  
//			BufferedReader in = new BufferedReader(new  
//			        InputStreamReader(pr.getInputStream()));  
//			String line;  
//			while ((line = in.readLine()) != null) {  
//			    System.out.println(line);  
//			}  
//			in.close();  
//			pr.waitFor();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
//      
//        System.out.println("end");  
//		PyUtil py=new PyUtil();
//		py.dypy();
	}
}
