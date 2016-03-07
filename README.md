#基于dubbo 扩展的springmvc插件,自动发布相关rest接口
	
	默认发布地址
		默认每个方法暴露出 两个url
		http://ip:port:8080/组/service版本/响应格式/接口名/方法名
		json地址:http://localhost:8090/defaultGroup/1.0.0/json/userService/getById?id=1
		json地址:http://localhost:8090/defaultGroup/1.0.0/xml/userService/getById?id=1

	
		
#安装
mvn install -Dmaven.test.skip=true

		<!-- dubbo-springmvc插件 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>dubbo-springmvc</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>

#example
	<!-- 如果要使用tomcat server -->
	<dubbo:protocol name="springmvc" server="tomcat" port="8080" />
	
	<!-- 如果要使用jetty9 server -->
	<dubbo:protocol name="springmvc" server="jetty9" port="8080" />
	

#增加异常处理SpringmvcExceptionHandler
	1.自动将异常以json/xml方式响应给浏览器或调用的客户端
	2.可以打上@ErrorMsg注解,自定义要返回异常信息.
		@ErrorMsg(msg = "错误信息",status=500,responseType="application/json;charset=utf-8")
		
#自定义token拦截器
	只需要把jar里的dubbo-springmvc.xml文件拿出来,配置基于springmvc的拦截器即可.
#

#依赖jar
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-validator</artifactId>
			<version>4.3.1.Final</version>
		</dependency>
		
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>dubbo</artifactId>
			<version>2.5.3</version>
		</dependency>
		
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webmvc</artifactId>
			<version>4.1.7.RELEASE</version>
			<scope>compile</scope>
		</dependency>
		
		<!-- 如果要使用tomcat server -->
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-core</artifactId>
			<version>8.0.11</version>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-logging-juli</artifactId>
			<version>8.0.11</version>
			<scope>compile</scope>
		</dependency>
		
		<!-- 如果要使用jett9 server -->
		<dependency>
			<groupId>org.eclipse.jetty.aggregate</groupId>
			<artifactId>jetty-all</artifactId>
			<version>9.2.15.v20160210</version>
		</dependency>
		
		<!-- 如果要使用jett9 server -->
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-core</artifactId>
			<version>2.3.3</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.3.3</version>
		</dependency>