/**
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.module.mongo.automation.testdata;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDataBuilder {

	private static Map<String, Object> getSpringBean(String beanName) {
		ApplicationContext context = new ClassPathXmlApplicationContext("AutomationSpringBeans.xml");
		@SuppressWarnings("unchecked")
		Map<String, Object> testData = (Map<String, Object>) context.getBean(beanName);
		((ConfigurableApplicationContext)context).close();
		return testData;
	}

	public static Map<String, Object> addUserTestData(){
		return getSpringBean("addUser");
	}

}