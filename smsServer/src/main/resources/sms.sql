#sql("findErrorMessage")
	select * from t_sms_logs where HOUR( timediff( now(), send_time) ) <? and is_ok=0 limit 0,100
#end

