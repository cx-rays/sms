package com.opssino.sms.Interceptor;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.opssino.sms.common.config.MainConfig;

/**
 * Created by rays2 on 3/13 0013.
 */
public class addRecordToQueue implements Interceptor{

    public void intercept(Invocation inv) {
        if(MainConfig.sendStatus){
            inv.invoke();
        }
//        else {
//            inv.
//        }

    }
}
