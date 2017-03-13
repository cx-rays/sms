package com.opssino.sms.common.config;

import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.opssino.sms.common.model.TSmsLogs;
import com.opssino.sms.common.model._MappingKit;
import com.opssino.sms.controller.SmsController;
import com.opssino.sms.cron4j.SendMessageThread;
import com.opssino.sms.handler.DoHandler;


public class MainConfig extends JFinalConfig {
	/**
	 * 配置JFinal常量
	 */
	public  static LinkedBlockingQueue<TSmsLogs> workq=new LinkedBlockingQueue<TSmsLogs>(1000);
	public static boolean sendStatus=true;
	public static int searchCSQCount=0;
	@Override
	public void configConstant(Constants me) {
		//读取数据库配置文件
		PropKit.use("config.properties");
		//设置当前是否为开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		//设置默认上传文件保存路径 getFile等使用
		me.setBaseUploadPath("upload/temp/");
		//设置上传最大限制尺寸
		//me.setMaxPostSize(1024*1024*10);
		//设置默认下载文件路径 renderFile使用
		//me.setBaseDownloadPath("");
		//设置默认视图类型
		me.setViewType(ViewType.JSP);
		//设置404渲染视图
		//me.setError404View("404.html");
		
	}
	/**
	 * 配置JFinal路由映射
	 */
	@Override
	public void configRoute(Routes me) {
		me.add("/",SmsController.class,"/WEB-INF/smslist/");
	}
	/**
	 * 配置JFinal插件
	 * 数据库连接池
	 * ORM
	 * 缓存等插件
	 * 自定义插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		//配置数据库连接池插件
		DruidPlugin druidPlugin=new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
	//	C3p0Plugin c3p0Plugin=new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		//orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp=new ActiveRecordPlugin(druidPlugin);
		arp.setBaseSqlTemplatePath(PathKit.getRootClassPath());
		arp.addSqlTemplate("sms.sql");
		arp.setShowSql(PropKit.getBoolean("devMode"));
		arp.setDialect(new MysqlDialect());
		/********在此添加数据库 表-Model 映射*********/
	//	me.add(new EhCachePlugin());
		Cron4jPlugin cp=new Cron4jPlugin(PropKit.use("Cron4jConfig.txt"));
		//添加到插件列表中
		me.add(druidPlugin);
		me.add(arp);
		me.add(cp);
		_MappingKit.mapping(arp);
	}
	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
	}
	/**
	 * 配置全局处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		me.add(new DoHandler());
	}
	public static void main(String[] args) {
	//	System.out.println(System.getProperty("java.io.tmpdir"));
		JFinal.start("WebRoot", 80, "/");
		
	}
	@Override
	public void configEngine(Engine me) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void afterJFinalStart() {
		//SmsMain.creatService();
		new Thread(new SendMessageThread()).start();
		super.afterJFinalStart();
	}
	
	@Override
	public void beforeJFinalStop() {
	//	SmsMain.close();
	//	System.out.printf("%d",100);
		super.beforeJFinalStop();
	}
	public static void addWorkQueue(TSmsLogs tSmsLogs){
		if(sendStatus){
			workq.add(tSmsLogs);
		}
	}

}
