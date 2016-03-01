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

#example
	<dubbo:protocol name="springmvc" server="tomcat" port="8080" />
	
#新增tomcat容器

	public static void main(String[] args) {

		ConfigUtils.getProperties().put("dubbo.spring.config", "classpath:applicationContext.xml");
		ConfigUtils.getProperties().put("dubbo.tomcat.port", "8080");
		com.alibaba.dubbo.container.Main.main(new String[] { "tomcat" });

	}
	
	<dubbo:protocol name="springmvc" server="servlet" port="8080" />