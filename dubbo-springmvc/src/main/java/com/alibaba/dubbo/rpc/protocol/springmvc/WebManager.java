package com.alibaba.dubbo.rpc.protocol.springmvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WebManager {

	private static String profix = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><script src=\"http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js\"></script><link rel = \"stylesheet\" href=\"http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css\"><script src=\"http://cdn.bootcss.com/bootstrap/2.3.1/js/bootstrap-transition.js\"></script><script src=\"http://cdn.bootcss.com/bootstrap/2.3.1/js/bootstrap-modal.js\"></script>%s<title>Dubbo-springmvc Manager</title></head><body><div class=\"container\"><table class=\"table table-striped  table-hover\"><thead><tr><th>服务名</th><th>方法</th><th>url</th><th>操作</th></tr>";
	private static String suffix = "<tbody>%s</tbody></table></div>%s</body></html>";
	private static String template = "<tr class=\"%s\"><td>%s</td><td>%s</td></td><td><a href=\"%s\">%s</td><td><button onclick='invokerPop(\"%s\")' class=\"btn btn-default btn-primary btn-sm\">调用</button></td></tr>";
	private static List<String> cssTrClass = Arrays.asList("", "info");

	public static String genHtml(Map<Object, HashSet<String>> mappingds, String addr) {
		String str = "";
		int i = 1;
		for (Object handler : mappingds.keySet()) {
			i++;
			String handlerName = handler.getClass().getSimpleName();
			ArrayList<String> paths = new ArrayList<String>(mappingds.get(handler));
			Collections.sort(paths);
			Method[] methods = handler.getClass().getMethods();
			for (Method method : methods) {
				for (String path : paths) {
					String[] split = path.split("/");
					String methodName = split[split.length - 1];
					if (method.getName().equals(methodName)) {
						String url = addr + path;
						String tr = String.format(template, cssTrClass.get(i % cssTrClass.size()), handlerName,
								method.toString(), url, url, url);
						str += tr;
					}
				}
			}

		}
		return String.format(profix, script) + String.format(suffix, str, invokerPop);
	}

	private static String invokerPop = "<div class='modal' id='mymodal'><div class='modal-dialog'><div class='modal-content'><div class='modal-header'><button type='button' class='close' data-dismiss='modal'><span aria-hidden='true'>×</span><span class='sr-only'>Close</span></button><h4 class='modal-title'>url注意跨域!地址会有缓存,请在隐私模式下使用!</h4></div><div class='modal-body'><form role='form'><div class='form-group'><label for='invokerUrl'>地址：</label> <input type='url'class='form-control' id='invokerUrl' value='' placeholder='调用地址'></div><div class='form-group'><label for='invokerMethod'>参数:</label> <input type='text'class='form-control' value='' id='invokerArgs'placeholder='参数以json格式传递:{\"name\":\"wuyu\"},如果没有参数:{}'></div><div class='form-group'><label for='invokerMethod'>次数:</label> <input type='number'class='form-control' value='1' id='invokerCount'placeholder='調用次数'></div><div class='form-group'><label for='invokerMethod'> 请求头:</label></br> <label class='radio-inline'> <input type='radio' name='reqeustHeader' checked='checked' id='inlineRadio1' value='application/x-www-form-urlencoded; charset=UTF-8'> application/x-www-form-urlencoded</label> <label class='radio-inline'> <input type='radio' name='reqeustHeader' id='inlineRadio2' value='application/json;charset=UTF-8'>application/json</div><div class='form-group'><label for='invokerMethod'>result:</label><div id='result'></div></div><div class='modal-footer'><button type='button' class='btn btn-default' data-dismiss='modal'>关闭</button><button type='button' id='execute' onclick='invoker()' class='btn btn-primary'>执行</button></div></form></div></div></div></div>";

	private static String script = "<script>" + "function invokerPop(url) {" + "$('#result').html('');"
			+ "$('#invokerUrl').attr('value',url);" + "setTimeout(function(){$('#mymodal').modal('toggle')}, 100)" + "}"
			+ "function invoker(){" + "var invokerUrl=$('#invokerUrl').val().trim();"
			+ "var invokerArgs=$('#invokerArgs').val().trim();" + "var data;"
			+ "var invokerCount=$('#invokerCount').val().trim();"
			+ "var header=$('input:radio[name=\"reqeustHeader\"]:checked').val();" + "var headers = {header};"
			+ "if(header=='application/json;charset=UTF-8'){" + "data=invokerArgs;" + "}else{"
			+ "data=JSON.parse(invokerArgs)" + "}var result='';" + "if(invokerCount==1){"
			+ "var resultJson=requestJson(invokerUrl,data,headers);" + "result=JSON.stringify(resultJson);"
			+ "$('#result').html(result);" + "}else{" + "for (var i = 0; i < invokerCount; i++) {"
			+ "var resultJson=requestJson(invokerUrl,data,headers);"
			+ "result+=i+1+':'+JSON.stringify(resultJson)+'</br>';" + "$('#result').html(result);}" + "}" + "}"
			+ "function requestJson(url, data, headers) {" + "var result;" + "$.ajax({" + "type : 'post',"
			+ "async : false," + "dataType : 'json'," + "url : url," + "data : data," + "headers : headers,"
			+ "success : function(data) {" + "result = data;" + "}"
			// + ",error : function(data) {" + "result = data;" + "}"
			+ "});" + "return result;" + "}" + "</script>";
}