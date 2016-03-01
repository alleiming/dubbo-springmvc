package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.http.HttpBinder;
import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.ServiceClassHolder;
import com.alibaba.dubbo.rpc.protocol.AbstractProxyProtocol;
import com.alibaba.dubbo.rpc.protocol.rest.RestServer;

/**
 * 
 * @author wuyu DATA:2016-2-10
 */
public class SpringmvcProtocol extends AbstractProxyProtocol {

	private static final int DEFAULT_PORT = 80;

	private final Map<String, RestServer> servers = new ConcurrentHashMap<String, RestServer>();

	private final Map<String, DispatcherServlet> dispatcherServlets = new ConcurrentHashMap<String, DispatcherServlet>();

	private final SpringmvcServerFactory serverFactory = new SpringmvcServerFactory();

	@Override
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}

	public void setHttpBinder(HttpBinder httpBinder) {
		serverFactory.setHttpBinder(httpBinder);
	}

	@Override
	protected <T> Runnable doExport(T impl, Class<T> type, URL url) throws RpcException {
		final String addr = url.getIp() + ":" + url.getPort();
		RestServer server = servers.get(addr);
		if (server == null) {
			if (server == null) {
				server = serverFactory.createServer(url.getParameter(Constants.SERVER_KEY, "jetty"));
				server.start(url);
				servers.put(addr, server);
			}
		}

		// ServiceClassHolder获取的实例是個class 而不是实例化的对象。
		final Class implClass = ServiceClassHolder.getInstance().popServiceClass();

		server.deploy(type, implClass, null);

		return new Runnable() {
			@Override
			public void run() {
				servers.get(addr).undeploy(implClass);
			}
		};
	}

	@Override
	// 暂时不支持基于springmvc的消费
	protected <T> T doRefer(Class<T> type, URL url) throws RpcException {
		return null;
	}

	protected String getContextPath(URL url) {
		int pos = url.getPath().lastIndexOf("/");
		return pos > 0 ? url.getPath().substring(0, pos) : "";
	}

	protected int getErrorCode(Throwable e) {
		// TODO
		return super.getErrorCode(e);
	}

	public void destroy() {
		super.destroy();
	}


}
