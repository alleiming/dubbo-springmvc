package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.http.HttpBinder;
import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.AbstractProxyProtocol;

/**
 * 
 * @author wuyu DATA:2016-2-10
 */
public class SpringmvcProtocol extends AbstractProxyProtocol {

	private static final int DEFAULT_PORT = 80;

	private final Map<String, SpringmvcHttpServer> servers = new ConcurrentHashMap<String, SpringmvcHttpServer>();

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
	protected <T> Runnable doExport(T impl, final Class<T> type, URL url) throws RpcException {
		final String addr = url.getIp() + ":" + url.getPort();
		SpringmvcHttpServer server = servers.get(addr);
		if (server == null) {
			if (server == null) {
				server = serverFactory.createServer(url.getParameter(Constants.SERVER_KEY, "jetty9"));
				server.start(url);
				servers.put(addr, server);
			}
		}

		server.deploy(type, url);

		return new Runnable() {
			public void run() {
				servers.get(addr).undeploy(type);
			}
		};
	}

	@Override
	// 暂时不支持基于springmvc的消费
	protected <T> T doRefer(Class<T> type, URL url) throws RpcException {
		return null;
		// PoolingHttpClientConnectionManager manager=new
		// PoolingHttpClientConnectionManager();
		// CloseableHttpClient httpClient =
		// HttpClientBuilder.create().setConnectionManager(manager).build();
		// HttpComponentsClientHttpRequestFactory factory=new
		// HttpComponentsClientHttpRequestFactory(httpClient);
		// final RestTemplate restTemplate=new RestTemplate(factory);
		// List<HttpMessageConverter<?>> messageConverters;
		// //restTemplate.setMessageConverters(messageConverters);
		//
		// final String httpUrl =
		// url.setProtocol("http").toJavaURL().toString();
		//
		// return (T)
		// Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
		// new Class[]{type}, new InvocationHandler() {
		//
		// @Override
		// public Object invoke(Object proxy, Method method, Object[] args)
		// throws Throwable {
		// String methodName = method.getName();
		// Class<?> returnType = method.getReturnType();
		// Object request = null;
		// restTemplate.postForEntity(httpUrl, request, returnType);
		// return null;
		// }
		// });
	}

	protected int getErrorCode(Throwable e) {
		return super.getErrorCode(e);
	}

	public void destroy() {
		servers.clear();
		super.destroy();
	}

}
