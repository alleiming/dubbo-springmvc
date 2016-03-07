package com.vcg.example;

import com.alibaba.dubbo.common.utils.ConfigUtils;

/**
 * Hello world!
 *
 */
public class Main {
	public static void main(String[] args) {
		ConfigUtils.getProperties().put("dubbo.spring.config", "classpath:dubbo-service.xml");
		com.alibaba.dubbo.container.Main.main(new String[] { "spring" });
	}
}
