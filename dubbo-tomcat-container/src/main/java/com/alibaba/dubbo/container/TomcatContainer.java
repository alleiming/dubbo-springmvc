package com.alibaba.dubbo.container;

import java.io.File;

import javax.servlet.ServletContext;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoaderListener;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;
import com.alibaba.dubbo.remoting.http.servlet.ServletManager;
import com.alibaba.dubbo.remoting.http.tomcat.TomcatHttpServer;

public class TomcatContainer implements Container {
	private static final Logger logger = LoggerFactory.getLogger(TomcatHttpServer.class);

	public static final String TOMCAT_PORT = "dubbo.tomcat.port";

	public static final String TOMCAT_DIRECTORY = "dubbo.tomcat.directory";

	public static final String TOMCAT_PAGES = "dubbo.tomcat.page";

	public static final String TOMCAT_THREADS = "dubbo.tomcat.threads";

	public static final String DUBBO_SPRING_CONFIG = "dubbo.spring.config";

	public final int DEFAULT_JETTY_PORT = 8080;

	public static int PORT = 8080;

	private Tomcat tomcat;

	public void start() {
		String baseDir = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
		String serverPort = ConfigUtils.getProperty(TOMCAT_PORT);
		if (serverPort == null || serverPort.length() == 0) {
			PORT = DEFAULT_JETTY_PORT;
		} else {
			PORT = Integer.parseInt(serverPort);
		}
		tomcat = new Tomcat();
		tomcat.setBaseDir(baseDir);
		tomcat.setPort(PORT);
		String threads = ConfigUtils.getProperty(TOMCAT_THREADS);
		tomcat.getConnector().setProperty("maxThreads", threads == null ? threads : "200");
		tomcat.getConnector().setProperty("maxConnections", String.valueOf(-1));
		tomcat.getConnector().setProperty("URIEncoding", "UTF-8");
		tomcat.getConnector().setProperty("connectionTimeout", "60000");
		tomcat.getConnector().setProperty("maxKeepAliveRequests", "-1");
		tomcat.getConnector().setProtocol("org.apache.coyote.http11.Http11NioProtocol");
		Context context = tomcat.addContext("/", baseDir);
		ServletManager.getInstance().addServletContext(PORT, context.getServletContext());
		ContextLoaderListener listener = new ContextLoaderListener() {

			@Override
			protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac,
					ServletContext sc) {
				if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
					// The application context id is still set to its original
					// default value
					// -> assign a more useful id based on available information
					String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
					if (idParam != null) {
						wac.setId(idParam);
					} else {
						// Generate default id...
						wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX
								+ ObjectUtils.getDisplayString(sc.getContextPath()));
					}
				}

				wac.setServletContext(sc);
				String configLocationParam = ConfigUtils.getProperty(DUBBO_SPRING_CONFIG);
				if (configLocationParam != null) {
					wac.setConfigLocation(configLocationParam);
				}

				// The wac environment's #initPropertySources will be called in
				// any case when the context
				// is refreshed; do it eagerly here to ensure servlet property
				// sources are in place for
				// use in any post-processing or initialization that occurs
				// below prior to #refresh
				ConfigurableEnvironment env = wac.getEnvironment();
				if (env instanceof ConfigurableWebEnvironment) {
					((ConfigurableWebEnvironment) env).initPropertySources(sc, null);
				}

				customizeContext(sc, wac);
				wac.refresh();
			}

		};
		listener.initWebApplicationContext(context.getServletContext());
		Tomcat.addServlet(context, "dispatcher", new DispatcherServlet());
		context.addServletMapping("/*", "dispatcher");

		try {
			tomcat.start();
		} catch (LifecycleException e) {
			throw new IllegalStateException("Failed to start tomcat server at " + NetUtils.getLocalHost() + ":" + PORT,
					e);
		}
	}

	public void stop() {
		ServletManager.getInstance().removeServletContext(PORT);

		try {
			tomcat.stop();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}

}
