/** 
 * Project Name:SmsServerJfinal
 * File Name:Port.java
 * Package Name:com.opssino.sms.util
 * Date:2017年3月1日下午1:32:21
 * Copyright (c) 2017, chenxi41898095@126.com All Rights Reserved. 
 * 
*/ 
package com.opssino.sms.util;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

import org.apache.log4j.Logger;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;  
  
//import javax.comm.CommPortIdentifier;  
//import javax.comm.NoSuchPortException;  
//import javax.comm.PortInUseException;  
//import javax.comm.SerialPort;  
//import javax.comm.UnsupportedCommOperationException;  
/** 
 * 串口操作实现类 
 * @author Anlw 
 * 
 */  
public class Port {  
	private static final Logger logger = Logger.getLogger(Port.class);
    private CommPortIdentifier portId;  
    private SerialPort serialPort;  
    private OutputStreamWriter out;  
    private InputStreamReader in;  
    private String COMname;  
  
    public String getCOMname() {  
        return COMname;  
    }  
  
    public void setCOMname(String mname) {  
        COMname = mname;  
    }  
  
    public CommPortIdentifier getPortId() {  
        return portId;  
    }  
  
    public void setPortId(CommPortIdentifier portId) {  
        this.portId = portId;  
    }  
  
    public SerialPort getSerialPort() {  
        return serialPort;  
    }  
  
    public void setSerialPort(SerialPort serialPort) {  
        this.serialPort = serialPort;  
    }  
  
    public OutputStreamWriter getOut() {  
        return out;  
    }  
  
    public void setOut(OutputStreamWriter out) {  
        this.out = out;  
    }  
  
    public InputStreamReader getIn() {  
        return in;  
    }  
  
    public void setIn(InputStreamReader in) {  
        this.in = in;  
    }  
  
    public boolean isused = true;  
  
    public boolean isIsused() {  
        return isused;  
    }  
  
    public void setIsused(boolean isused) {  
        this.isused = isused;  
    }  
  
    /** 
     * 构造器打开com口  
     * @param portName 
     * @return 
     */   
    public Port(String portName) {  
        try {  
            portId = CommPortIdentifier.getPortIdentifier(portName);  
            if (portId == null) {  
                logger.info("port is null");  
            }  
            try {  
                serialPort = (SerialPort) portId.open(getrechargeablePassword(), 20000);  
            } catch (PortInUseException e) {  
                System.gc();  
                logger.info("COM端口被占用，请选择正确的COM端口！！");  
            }  
            // 下面是得到用于和COM口通讯的输入、输出流。  
            try {  
                in = new InputStreamReader(serialPort.getInputStream());  
                out = new OutputStreamWriter(serialPort.getOutputStream());  
            } catch (Exception e) {  
                System.gc();  
            }  
            // 下面是初始化COM口的传输参数，如传输速率：9600等  
            try {  
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,  
                        SerialPort.PARITY_NONE);  
                setCOMname(portId.getName());  
                setIsused(true);  
            } catch (Exception e) {  
                System.gc();  
            }  
  
        } catch (NoSuchPortException e) {  
            System.gc();  
            logger.info("无此COM端口，请选择正确的COM端口！！");  
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
  
    /** 
     * 关闭COM口 
     * @return boolean 
     */  
    public void close() {  
        try {  
            in.close();  
            out.close();  
            serialPort.close();  
            System.gc();  
            setIsused(false);  
        } catch (Exception e) {  
        }  
          
    }  
  
    /** 
     * 向串口中写入字符串命令 
     * @param s 字符串命令 
     * @throws Exception 异常 
     */  
    public void writeln(String s) throws Exception {  
        out.write(s);  
        out.write('\r'); 
        out.flush();  
    }  
  
    /** 
     * 读取COM命令的返回字符串 
     * @return 结果字符串 
     * @throws Exception 
     */  
    public String read() throws Exception {  
        int n, i;  
        char c;  
        String answer = "";  
        for (i = 0; i < 100; i++) {  
            while (in.ready()) {  
                n = in.read();  
                if (n != -1) {  
                    c = (char) n;  
                    answer = answer + c;  
                    Thread.sleep(1);  
                } else  
                    break;  
            }  
            if (answer.indexOf("OK") != -1) {  
                break;  
            }  
            Thread.sleep(100);  
        }  
        return answer;  
    }  
  
    /** 
     * 向串口发送AT指令 
     * @param atcommand 指令内容 
     * @return 指令返回结果 
     * @throws java.rmi.RemoteException 
     */  
    public String sendAT(String atcommand) throws java.rmi.RemoteException {  
        String returnStr = "";  
        try {  
            Thread.sleep(100);  
            writeln(atcommand);  
            Thread.sleep(800);  
            returnStr = read();  
            Thread.sleep(150);  
        } catch (Exception e) {  
            System.gc();  
        }  
        return returnStr;  
    }  
}  
