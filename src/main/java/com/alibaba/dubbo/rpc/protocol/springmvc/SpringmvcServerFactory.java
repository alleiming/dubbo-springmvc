package com.alibaba.dubbo.rpc.protocol.springmvc;

import com.alibaba.dubbo.remoting.http.HttpBinder;
import com.alibaba.dubbo.rpc.protocol.rest.RestServer;
import com.alibaba.dubbo.rpc.protocol.rest.RestServerFactory;

/**
 * 
 * @author wuyu
 * DATA:2016-2-27
 */
public class SpringmvcServerFactory extends RestServerFactory {

	private HttpBinder httpBinder;

	public void setHttpBinder(HttpBinder httpBinder) {
		this.httpBinder = httpBinder;
	}

	@Override
	public RestServer createServer(String name) {
		if ("servlet".equalsIgnoreCase(name) || "jetty".equalsIgnoreCase(name) || "tomcat".equalsIgnoreCase(name)) {
			return new SpringmvcHttpServer(httpBinder);
		}

		throw new IllegalArgumentException("Unrecognized server name: " + name);
	}

}
