package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory;
import com.alibaba.dubbo.remoting.http.HttpBinder;
import com.alibaba.dubbo.remoting.http.HttpHandler;
import com.alibaba.dubbo.remoting.http.HttpServer;
import com.alibaba.dubbo.remoting.http.servlet.BootstrapListener;
import com.alibaba.dubbo.remoting.http.servlet.ServletManager;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.rest.RestServer;

/**
 * 
 * @author wuyu DATA:2016-2-27
 */

public class SpringmvcHttpServer implements RestServer {

	private DispatcherServlet dispatcher = new DispatcherServlet();
	private HttpBinder httpBinder;
	private HttpServer httpServer;
	private WebApplicationContext webApplicationContext;

	public SpringmvcHttpServer(HttpBinder httpBinder) {
		this.httpBinder = httpBinder;
	}

	public void stop() {
		dispatcher.destroy();
		httpServer.close();
	}

	protected void doStart(URL url) {
		httpServer = httpBinder.bind(url, new RestHandler());

		ServletContext servletContext = ServletManager.getInstance().getServletContext(url.getPort());
		if (servletContext == null) {
			servletContext = ServletManager.getInstance().getServletContext(ServletManager.EXTERNAL_SERVER_PORT);
		}
		if (servletContext == null) {
			throw new RpcException("No servlet context found. If you are using server='servlet', "
					+ "make sure that you've configured " + BootstrapListener.class.getName() + " in web.xml");
		}
		webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		dispatcher.setContextConfigLocation("classpath:dubbo-springmvc.xml");
		try {
			dispatcher.init(new SimpleServletConfig(servletContext));
		} catch (ServletException e) {
			throw new RpcException(e);
		}
	}

	public DispatcherServlet getDispacherServlet() {
		return dispatcher;
	}

	private class RestHandler implements HttpHandler {
		public void handle(HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			RpcContext.getContext().setRemoteAddress(request.getRemoteAddr(), request.getRemotePort());
			dispatcher.service(request, response);
		}
	}

	@Override
	public void start(URL url) {
		doStart(url);
	}

	@Override
	public void deploy(Class resourceDef, Object resourceInstance, String contextPath) {

		try {
			
			//由于dubbox提供的ServiceClassHolder不是一个实例化的对象，而是一个class 无法动态注册bean的url handler
			//故 反射SpringExtensionFactory 拿到所有的ApplicatonContext 通过class类型获取bean
			//但是有一个问题，如果存在多个ApplicatonContext有可能会获取到错误的bean
			Object bean = null;
			if (webApplicationContext != null && webApplicationContext.getParent() != null) {
				bean = webApplicationContext.getParent().getBean((Class) resourceInstance);
			} else {
				bean = SpringUtil.getBean((Class) resourceInstance, firstLow(resourceDef));
				if (bean == null) {
					bean = SpringUtil.getBean(resourceDef, firstLow((Class) resourceInstance));
				}
			}

			SpringUtil.registerHandler(dispatcher, bean);
		} catch (Exception e) {
			throw new RpcException(e);
		}

	}

	@Override
	public void undeploy(Class resourceDef) {
		try {
			SpringUtil.unRegisterHandler(dispatcher, resourceDef);
		} catch (Exception e) {

		}
	}

	public static String firstLow(Class clazz) {
		String clazzName = clazz.getSimpleName();
		String upperCase = clazzName.substring(0, 1).toLowerCase();
		String substring = clazzName.substring(1);
		return upperCase + substring;
	}

	private static class SimpleServletConfig implements ServletConfig {

		private final ServletContext servletContext;

		public SimpleServletConfig(ServletContext servletContext) {
			this.servletContext = servletContext;
		}

		public String getServletName() {
			return "DispatcherServlet";
		}

		public ServletContext getServletContext() {
			return servletContext;
		}

		public String getInitParameter(String s) {
			return null;
		}

		public Enumeration getInitParameterNames() {
			return new Enumeration() {
				public boolean hasMoreElements() {
					return false;
				}

				public Object nextElement() {
					return null;
				}
			};
		}
	}
}
