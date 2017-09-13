package com.intramirror.web.util;

import org.apache.log4j.Logger;

import com.intramirror.web.enums.QueueNameJobEnum;

public class QueueNameEnumUtils {
	
	 private static Logger logger = Logger.getLogger(QueueNameEnumUtils.class);
	
	public static QueueNameJobEnum searchQueue(String mqName){
	        logger.info("QueueNameEnumUtilsQueue:"+mqName);
	        for(QueueNameJobEnum queueNameEnum : QueueNameJobEnum.values()) {
	            if(queueNameEnum.getCode().equals(mqName)) {
	                return queueNameEnum;
	            }
	        }
	        return null;
	    }
}

