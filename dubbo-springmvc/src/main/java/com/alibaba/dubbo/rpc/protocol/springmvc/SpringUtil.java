package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory;

public class SpringUtil {

	@SuppressWarnings("unchecked")
	public static Set<ApplicationContext> getApplicationContexts() {
		Field contextsFiled = ReflectionUtils.findField(SpringExtensionFactory.class, "contexts");
		contextsFiled.setAccessible(true);
		return (Set<ApplicationContext>) ReflectionUtils.getField(contextsFiled, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> type, String name) {
		Set<ApplicationContext> contexts = getApplicationContexts();
		for (ApplicationContext context : contexts) {
			if (context.containsBean(name)) {
				T bean = (T) context.getBean(name);
				if (type.isInstance(bean)) {
					return bean;
				}
			}
		}
		return null;
	}

	public static <T> T getBean(Class<T> type) {
		Set<ApplicationContext> contexts = getApplicationContexts();
		for (ApplicationContext context : contexts) {
			return context.getBean(type);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		Set<ApplicationContext> contexts = getApplicationContexts();
		for (ApplicationContext context : contexts) {
			return (T) context.getBean(name);
		}
		return null;
	}

	public static <T> List<String> getBeanNamesForType(Class<T> type) {
		Set<ApplicationContext> contexts = getApplicationContexts();
		List<String> beanNames = new ArrayList<String>();
		for (ApplicationContext context : contexts) {
			String[] beanName = context.getBeanNamesForType(type);
			beanNames.addAll(Arrays.asList(beanName));
		}
		return beanNames;
	}

	public static <T> List<T> getBeans(Class<T> type) {
		List<T> beans = new ArrayList<T>();
		for (String beanName : getBeanNamesForType(type)) {
			T bean = getBean(type, beanName);
			beans.add(bean);
		}
		return beans;
	}

}
