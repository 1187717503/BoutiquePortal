package com.intramirror.web.controller.order;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//使用junit4进行测试  
@RunWith(SpringJUnit4ClassRunner.class) 

//加载配置文件  
@ContextConfiguration
({"*:/freemarker/spring-servlet.xml","/spring/app-beans.xml"}) 

@ActiveProfiles({"spring.profiles.active", "test"})
@Ignore
//@Transactional(transactionManager = "transactionManager")
public class BaseJunit4Test {

}
 