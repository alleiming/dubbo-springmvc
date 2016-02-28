package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory;

public class SpringUtil {

	private static final Set<ApplicationContext> contexts = new ConcurrentHashSet<ApplicationContext>();

	@SuppressWarnings("unchecked")
	public static Set<ApplicationContext> getApplicationContexts() {
		Field contextsFiled = ReflectionUtils.findField(SpringExtensionFactory.class, "contexts");
		contextsFiled.setAccessible(true);
		Set<ApplicationContext> applicationContexts = (Set<ApplicationContext>) ReflectionUtils.getField(contextsFiled,
				new SpringExtensionFactory());
		if (applicationContexts != null) {
			for (ApplicationContext applicationContext : applicationContexts) {
				contexts.add(applicationContext);
			}
		}
		return contexts;
	}

	public static void init() {
		if (contexts.size() == 0) {
			getApplicationContexts();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> type, String name) {
		init();
		Object instance = null;
		for (ApplicationContext context : contexts) {
			if (context.containsBean(name)) {
				Object bean = context.getBean(name);
				if (type.isInstance(bean)) {
					instance = bean;
				}
			}
		}
		return (T) instance;
	}

	public static <T> T getBean(Class<T> type) {
		init();
		for (ApplicationContext context : contexts) {
			return context.getBean(type);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		init();
		for (ApplicationContext context : contexts) {
			return (T) context.getBean(name);
		}
		return null;
	}

	public static RequestMappingHandlerMapping getRequestMapping(DispatcherServlet dispatcherServlet) {
		return dispatcherServlet.getWebApplicationContext().getBean(RequestMappingHandlerMapping.class);
	}

	//TODO
	public static  void unRegisterHandler(DispatcherServlet dispatcherServlet, Object handler) throws Exception {
		RequestMappingHandlerMapping requestMapping = getRequestMapping(dispatcherServlet);
	}

	public static void registerHandler(DispatcherServlet dispatcherServlet, Object handler) throws Exception {
		RequestMappingHandlerMapping requestMapping = getRequestMapping(dispatcherServlet);
		Method registerHandler = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "detectHandlerMethods",
				Object.class);
		registerHandler.setAccessible(true);
		registerHandler.invoke(requestMapping, handler);
	}


}
