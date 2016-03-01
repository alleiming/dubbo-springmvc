"# dubbox-springmvc" 
#基于dubbox 扩展的springmvc插件
#依赖jar
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-validator</artifactId>
			<version>4.3.1.Final</version>
		</dependency>
		
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>dubbo</artifactId>
			<version>2.8.4</version>
		</dependency>
		
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webmvc</artifactId>
			<version>4.1.7.RELEASE</version>
			<scope>compile</scope>
		</dependency>

		<dependency>
			<groupId>org.jboss.resteasy</groupId>
			<artifactId>resteasy-client</artifactId>
			<version>3.0.7.Final</version>
			<scope>compile</scope>
		</dependency>
		
#example
<dubbo:protocol name="springmvc" server="tomcat" port="8080" />