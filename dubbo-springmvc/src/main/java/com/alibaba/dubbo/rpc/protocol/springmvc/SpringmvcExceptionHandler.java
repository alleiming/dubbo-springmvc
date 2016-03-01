package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author wuyu DATA:2016-3-1
 */
public class SpringmvcExceptionHandler implements HandlerExceptionResolver {

	public static final String JSON_TYPE = "application/json;charset=utf-8";
	public static final String XML_TYPE = "text/xml;charset=utf-8";

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod methodHandler = (HandlerMethod) handler;
			ErrorMsg errorMsg = methodHandler.getMethodAnnotation(ErrorMsg.class);
			try {
				handlerExcption(response, errorMsg, ex);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}

	public void handlerExcption(HttpServletResponse response, ErrorMsg errorMsg, Exception ex) throws IOException {
		if (errorMsg != null && errorMsg.responseType().equals(XML_TYPE)) {
			handlerXml(response, errorMsg, ex);
		} else {
			handlerJson(response, errorMsg, ex);
		}
	}

	public void handlerXml(HttpServletResponse response, ErrorMsg errorMsg, Exception ex) throws IOException {
		String msg = "<server><msg>%s</msg><status>%d</status></server>";
		if (errorMsg != null) {
			String message = String.format(msg, errorMsg.msg(), errorMsg.status());
			writerMsg(response, message, XML_TYPE);
		} else {
			String message = String.format(msg, ex.getMessage(), 500);
			writerMsg(response, message, XML_TYPE);
		}
	}

	public void handlerJson(HttpServletResponse response, ErrorMsg errorMsg, Exception ex) throws IOException {
		String msg = "{\"msg\":\"%s\",\"status\":%d}";
		if (errorMsg != null) {
			String message = String.format(msg, errorMsg.msg(), errorMsg.status());
			writerMsg(response, message, JSON_TYPE);
		} else {
			String message = String.format(msg, ex.getMessage(), 500);
			writerMsg(response, message, JSON_TYPE);
		}
	}

	public void writerMsg(HttpServletResponse response, String msg, String responseType) throws IOException {
		response.setContentType(responseType);
		response.getWriter().write(msg);
	}

}
