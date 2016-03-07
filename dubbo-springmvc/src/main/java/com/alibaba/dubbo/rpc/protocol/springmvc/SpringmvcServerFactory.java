package com.alibaba.dubbo.rpc.protocol.springmvc;

import com.alibaba.dubbo.remoting.http.HttpBinder;

/**
 * 
 * @author wuyu DATA:2016-2-27
 */
public class SpringmvcServerFactory {

	private HttpBinder httpBinder;

	public void setHttpBinder(HttpBinder httpBinder) {
		this.httpBinder = httpBinder;
	}

	public SpringmvcHttpServer createServer(String name) {
		if ("servlet".equalsIgnoreCase(name) || "jetty9".equalsIgnoreCase(name) || "tomcat".equalsIgnoreCase(name)) {
			return new SpringmvcHttpServer(httpBinder);
		}

		throw new IllegalArgumentException(
				"Unrecognized server name: " + name + ",If you are using jetty, please use jetty9!");
	}

}
