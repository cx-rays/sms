<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<base href="<%=basePath%>"></base>
	<meta http-equiv="content-type" content="text/html; charset=utf-8"> 
	<title>SMS List</title>
	<link rel="stylesheet" type="text/css" href="js/jquery-easyui-1.3.4/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="js/jquery-easyui-1.3.4/themes/icon.css">
<!-- 	<link rel="stylesheet" type="text/css" href="js/jquery-easyui-1.3.4/demo.css"> -->	
	<script type="text/javascript" src="js/jquery-easyui-1.3.4/jquery.min.js"></script>
	<script type="text/javascript" src="js/jquery-easyui-1.3.4/jquery.easyui.min.js"></script>
	<!-- 
	<script type="text/javascript" charset="utf-8" src="js/jquery-easyui-1.3.4/locale/easyui-lang-zh_CN.js"></script>
	 -->
	 <script type="text/javascript" charset="utf-8" src="js/jquery-easyui-1.3.4/jquery_cn_zh.js"></script>
<script type="text/javascript">

var v_all_page=20;
var v_def_pn="1";
var grid_param={};

/*
function cleardata(){
	$('#ff').form('clear');
	$("#ff").form("validate");
}


var editIndex = undefined;
function endEditing(){
	if (editIndex == undefined){return true}
	if ($('#mygrid').datagrid('validateRow', editIndex)){
		$('#mygrid').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}


function onDblClickCell(index, field){
	if (endEditing()){
		$('#mygrid').datagrid('selectRow', index)
				.datagrid('editCell', {index:index,field:field});
		editIndex = index;
	}
}
*/

function querydata(pageNumber, pageSize){

	var phonenumber = $("#phonenumber").val();
	var message = $("#message").val();
	var dsv = $("#ds").datebox("getValue");
	var dev = $("#de").datebox("getValue");
	var isok = $("#isok").combobox("getValue");
	var pageSize1=$('#mygrid').datagrid('getPager').data("pagination").options.pageSize;

	//alert(dsv+"  "+dev);
	//var _rat = $.getUrlParam("rat");
	//var _net = $.getUrlParam("net");
	var errmsg="";

	if(dsv!='' && dev!='' && dsv > dev){
		errmsg += " 开始时间必须大于结束时间!";
	}

	/*
	if(!$("#ff").form("validate")){
		errmsg +="<li>条件错误!</li>";
	}
	*/

	if(errmsg != ""){
		$.messager.alert('错误',errmsg,'warning');

		/*
		$("#err_msg2").empty();
		$("#err_msg2").append("错误信息 : "+errmsg+"");
		$("#err_msg2").show();
		*/
		return ;
	}

	grid_param={};

	/*
	$("#err_msg2").hide();
	*/
	if(dsv!=null &&dsv!=''){
		grid_param["ds"]=dsv;
	}
	if(dev!=null && dev!=''){
		grid_param["de"]=dev;
	}
	if(phonenumber!=null && phonenumber!=''){
		grid_param["phonenumber"]=phonenumber;
	}
	if(message!=null && message!=''){
		grid_param["message"]=encodeURI(message);
	}
	grid_param["isok"]=isok;
	grid_param["pagen"]=pageNumber;
	grid_param["pages"]=pageSize;
	var Time2  = new Date().getTime();
	grid_param["timeidk"]=Time2;

	$('#mygrid').datagrid("reload",grid_param);
	$('#mygrid').datagrid('getPager').pagination({pageNumber:1});

}

function pagedata(pageNumber, pageSize){
	
	grid_param["pageNumber"]=pageNumber;
	grid_param["pageSize"]=pageSize;
	$('#mygrid').datagrid("reload",grid_param);
}

/*
function export_file(file_type){
	var v_loca="";

	$.each(grid_param,function(key,value){

		if(v_loca != ""){
			v_loca += "&";
		}
		v_loca+=key+"="+value;
	});

	if(v_loca == ""){
		$.messager.alert('警告','请先查询才能导出文件!','warning');
		return;
	}

	if( file_type == "txt"){
		v_loca+="&exportType=txt"
	}

	location="/export/wcdma/export.php?"+v_loca;
}
*/


$(function(){
	var lastIndex;
	//$("#err_msg2").hide();

	//$("#cencel").click(cleardata);

	$("#query").click(function(){
		querydata(v_def_pn, v_all_page );
	});

	$('#mygrid').datagrid({
		fit:true,
		url:"queryTabData",	
		method:"get",
		nowrap:false,
		singleSelect:true,
		//onClickCell:onDblClickCell,
		columns:[[
			{field:'SMS_TYPE',title:'消息类别',width:80,align:'center',formatter:f_smstype},
			{field:'PHONE_NUMBER',title:'电话号码',width:110,align:'center'},
			{field:'MESSAGE',title:'消息内容',width:550,align:'left',editor:"text"},
			{field:'SEND_TIME',title:'发送时间',width:150,align:'center'},
			{field:'IS_OK',title:'是否成功',width:60,align:'center',formatter:f_smsisok},
			{field:'re_flag',title:'是否重发成功',width:80,align:'left',formatter:f_re_send_flag},
			{field:'last_send_time',title:'重发发送时间',width:150,align:'center'}
			/*
			,
			{field:'FAIL_REASON',title:'失败原因',width:150,align:'left'}
			*/
		]],
		pagination:true,
		
		rownumbers:true,
		onClickRow:function(rowIndex){
			/*
			if (lastIndex != rowIndex){
				$('#mygrid').datagrid('endEdit', lastIndex);
				$('#mygrid').datagrid('beginEdit', rowIndex);
			}
			lastIndex = rowIndex;
			*/
		}
	});
	
	var p = $('#mygrid').datagrid('getPager');
	$(p).pagination({
		pageSize:v_all_page,
		pageList:[v_all_page,40,60],
		beforePageText: '第',
		afterPageText: '页    共 {pages} 页',
		displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
		onSelectPage:function(pageNumber, pageSize){
			pagedata(pageNumber , pageSize)
		}
	});
});


function f_smsisok(val,row){
	if (val==1){
		return '成功';
	} else if(val==0){
		return '失败';
	}
		return val;
}

function f_re_send_flag(val,row){
	if(val==1){
		return "重发成功";
	}else if(val==0){
		return '';
	}
		return val;
}

function f_smstype(val,row){
	if (val==1){
		return '短信';
	} 
		return val;
}

</script>
</head> 
<body class="easyui-layout">
	
<div region="north" style="height:95px;background:#B3DFDA;padding:5px" title="短信发送记录" >

		<div>手机号码：      <input id="phonenumber" class="easyui-text" type="text" name="phonenumber" data-options="required:true"  style="width:145px"></input>       &nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;短信内容：      <input id="message" class="easyui-text" type="text" name="message" data-options="required:true" style="width:145px"></input>       &nbsp;&nbsp;&nbsp;&nbsp;
		短信状态：			<select id="isok"  class="easyui-combobox" panelHeight="auto" style="width:100px">
				<option value="9" selected="selected">全部</option>
				<option value="1">成功</option>
				<option value="0">失败</option>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;</br>
			&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			从：  <input id="ds" class="easyui-datebox" validType="date"  value="" style="width:110px"></input>&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			到：  <input id="de" class="easyui-datebox" validType="date"  value="" style="width:110px"></input>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<a id="query" href="#" class="easyui-linkbutton" iconCls="icon-search">&nbsp;查询&nbsp; </a> 
			<!-- 
			&nbsp;&nbsp;&nbsp;&nbsp;<label id="err_msg2"  style="color:red">错误:</label>
			 -->
		</div>
</div>
<!-- 
	<div region="west" title="查询条件" split="false" style="width:220px;">
		<div style="background:#fafafa;padding:10px;width:198px;height:100%;">
		    <form id="ff" method="post">
		    	<div style="padding:5px;background:#fafafa;width:196px;">
					<a id="cencel" href="#" class="easyui-linkbutton" plain="true" iconCls="icon-reload">Cancel</a>
					<a id="query" href="#" class="easyui-linkbutton" plain="true" iconCls="icon-search">Query</a>
				</div>
		        <div>
		            <label for="ds">开始时间:</label>
		            <input id="ds" class="easyui-datebox" validType="date" required="true" value=""></input>
		        </div>
		        <div>
		            <label for="de">结束时间:</label>
		            <input id="de" class="easyui-datebox" validType="date" required="true" value=""></input>
		        </div>
		        <div>
		        &nbsp;
		        </div>
		        <div id="err_msg" class="demo-info" >
				</div>
		    </form>
		</div>
	</div>
 -->			
	<div region="center"  style="overflow:hidden;">
		

	<table id="mygrid"></table>
		
	</div>
	
	<!-- 
	<div region="south" split="false" style="height:30px;padding:5px;background:black;">
		<center><span style="color: #FFFFFF"><span></center>
	</div>
 -->



</body>
</html>