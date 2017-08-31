package com.intramirror.web.junit;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

//使用junit4进行测试  
@RunWith(SpringJUnit4ClassRunner.class) 

//加载配置文件  
@ContextConfiguration
({"/freemarker/spring-servlet.xml","/spring/app-beans.xml"}) 

@ActiveProfiles({"spring.profiles.active","sit"})

//@Transactional(transactionManager = "transactionManager")
public class BaseJunit4Test {

}
 