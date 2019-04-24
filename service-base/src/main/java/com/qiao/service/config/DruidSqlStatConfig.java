package com.qiao.service.config;

import com.alibaba.druid.support.http.StatViewServlet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @Description:
 * @Created by ql on 2019/4/24/024 22:04
 * @Version: v1.0
 */
public class DruidSqlStatConfig {
    @Autowired
    private Environment env;

    public DruidSqlStatConfig() {
    }

    @Bean
    public ServletRegistrationBean getDemoServlet() {
        StatViewServlet servlet = new StatViewServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(servlet);
        List<String> urlMappings = new ArrayList();
        urlMappings.add("/druid/*");
        registrationBean.setUrlMappings(urlMappings);
        return registrationBean;
    }
}
