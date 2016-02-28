package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory;

public class SpringUtil {

	@SuppressWarnings("unchecked")
	public static Set<ApplicationContext> getApplicationContexts() {
		Field contextsFiled = ReflectionUtils.findField(SpringExtensionFactory.class, "contexts");
		contextsFiled.setAccessible(true);
		return (Set<ApplicationContext>) ReflectionUtils.getField(contextsFiled, new SpringExtensionFactory());
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> type, String name) {
		Set<ApplicationContext> contexts = getApplicationContexts();
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

	public static RequestMappingHandlerMapping getRequestMapping(DispatcherServlet dispatcherServlet) {
		return dispatcherServlet.getWebApplicationContext().getBean(RequestMappingHandlerMapping.class);
	}

	// TODO
	public static void unRegisterHandler(DispatcherServlet dispatcherServlet, Object handler) throws Exception {
		RequestMappingHandlerMapping requestMapping = getRequestMapping(dispatcherServlet);
		Field urlMapFiled = ReflectionUtils.findField(RequestMappingHandlerMapping.class, "urlMap");
		urlMapFiled.setAccessible(true);
		Map<String, Object> urlMap = (Map<String, Object>) ReflectionUtils.getField(urlMapFiled, requestMapping);
		Method[] methods = handler.getClass().getMethods();
		for (Method method : methods) {
			RequestMapping annotation = method.getAnnotation(RequestMapping.class);
			if (annotation != null && annotation.value() != null) {
				urlMap.remove(annotation.value());
			}
		}

	}

	public static void registerHandler(DispatcherServlet dispatcherServlet, Object handler) throws Exception {
		RequestMappingHandlerMapping requestMapping = getRequestMapping(dispatcherServlet);
		Method registerHandler = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "detectHandlerMethods",
				Object.class);
		registerHandler.setAccessible(true);
		registerHandler.invoke(requestMapping, handler);
	}

}
