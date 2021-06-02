package com.spring;

import org.springframework.context.annotation.*;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@ComponentScan("com")
@PropertySources({ //
		@PropertySource(value = "file:./iris.properties", ignoreResourceNotFound = true) //
})
public class SpringConfig {

//	@Bean(name = "multipartResolver")
//	public MultipartResolver multipartResolver() {
//		return new StandardServletMultipartResolver();
//	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
//		resolver.setDefaultEncoding("utf-8");
//		resolver.setMaxUploadSize(524288000); // 500 MB
//		resolver.setMaxUploadSizePerFile(524288000); // 500 MB
//		resolver.setMaxInMemorySize(524288000);
		return resolver;
	}
}

