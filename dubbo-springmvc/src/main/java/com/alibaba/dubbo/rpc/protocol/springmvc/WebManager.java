package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;

public class WebManager {

	private static String profix = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><script src=\"http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js\"></script><link rel = \"stylesheet\" href=\"http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css\"><title>Dubbo-springmvc Manager</title></head><body><div class=\"container\"><table class=\"table\"><thead><tr><th>服务名</th><th>方法</th><th>url</th><th>操作</th></tr>";
	private static String suffix = "<tbody>%s</tbody></table></div></body></html>";
	private static String template = "<tr><td>%s</td><td>%s</td></td><td><a href=\"\">%s</td><td><button class=\"btn btn-default btn-success btn-sm\">调用</button></td></tr>";

	public static String genHtml(Map<Object, HashSet<String>> mappingds) {
		String str = "";
		for (Object handler : mappingds.keySet()) {
			String handlerName = handler.getClass().getSimpleName();
			HashSet<String> paths = mappingds.get(handler);
			Method[] methods = handler.getClass().getMethods();
			for (Method method : methods) {
				for (String path : paths) {
					String[] split = path.split("/");
					String methodName = split[split.length-1];
					if(method.getName().equals(methodName)){
						String tr = String.format(template, handlerName, method.toString(),path);
						str += tr;
					}
				}
			}

		}
		return profix + String.format(suffix, str);
	}
}
