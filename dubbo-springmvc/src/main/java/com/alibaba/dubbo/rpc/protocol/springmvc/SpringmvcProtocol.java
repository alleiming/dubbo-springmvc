package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.Source;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;

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

	private static final int DEFAULT_PORT = 8080;

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

	@SuppressWarnings("unchecked")
	@Override
	// 暂时不支持基于springmvc的消费
	protected <T> T doRefer(Class<T> type, URL url) throws RpcException {
		return null;
		// PoolingHttpClientConnectionManager manager = new
		// PoolingHttpClientConnectionManager();
		// CloseableHttpClient httpClient =
		// HttpClientBuilder.create().setConnectionManager(manager).build();
		// HttpComponentsClientHttpRequestFactory factory = new
		// HttpComponentsClientHttpRequestFactory(httpClient);
		// final RestTemplate restTemplate = new RestTemplate(factory);
		// List<HttpMessageConverter<?>> messageConverters =
		// getHttpMessageConverters();
		// restTemplate.setMessageConverters(messageConverters);
		//
		// String addr = "http://" + url.getIp() + ":" + url.getPort();
		// String defaultGroup = url.getParameter("group", "defaultGroup");
		// String version = url.getParameter("version", "1.0.0");
		// String service = getShortName(url.getParameter("interface"));
		// String contextPath = getContextPath(url);
		// String contentType = "json";
		//
		// final String httpUrl = addr + "/" + contextPath + "/" + defaultGroup
		// + "/" + version + "/" + contentType + "/"
		// + service + "/";
		//
		// return (T)
		// Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
		// new Class[] { type },
		// new InvocationHandler() {
		//
		// @Override
		// public Object invoke(Object proxy, Method method, Object[] args)
		// throws Throwable {
		// String fullRequestPath = httpUrl + method.getName();
		// Class<?> returnType = method.getReturnType();
		// HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_JSON);
		// HttpEntity httpEntity = new HttpEntity(JSON.toJSONBytes(args),
		// headers);
		// return restTemplate.postForObject(fullRequestPath, httpEntity,
		// returnType);
		// }
		// });
	}

	public List<HttpMessageConverter<?>> getHttpMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(4);
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false); // see SPR-7316
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringHttpMessageConverter);
		messageConverters.add(new SourceHttpMessageConverter<Source>());
		messageConverters.add(new AllEncompassingFormHttpMessageConverter());
		// messageConverters.add(new FastJsonHttpMessageConverter());
		return messageConverters;
	}

	public String getShortName(String str) {
		return firstLow(str.split("[.]")[str.split("[.]").length - 1]);
	}

	public String firstLow(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	protected String getContextPath(URL url) {
		int pos = url.getPath().lastIndexOf("/");
		return pos > 0 ? url.getPath().substring(0, pos) : "";
	}

	protected int getErrorCode(Throwable e) {
		return super.getErrorCode(e);
	}

	public void destroy() {
		servers.clear();
		super.destroy();
	}

}
