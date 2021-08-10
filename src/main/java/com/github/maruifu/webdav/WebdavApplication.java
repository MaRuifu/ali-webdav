package com.github.maruifu.webdav;

import com.github.maruifu.webdav.filter.ErrorFilter;
import com.github.maruifu.webdav.store.AliYunDriverFileSystemStore;
import net.sf.webdav.WebdavServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
public class WebdavApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebdavApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<WebdavServlet> myServlet(){
        ServletRegistrationBean<WebdavServlet> servletRegistrationBean = new ServletRegistrationBean<>(new WebdavServlet(), "/*");
        Map<String, String> inits = new LinkedHashMap<>();
        inits.put("ResourceHandlerImplementation", AliYunDriverFileSystemStore.class.getName());
        inits.put("rootpath", "./");
        inits.put("storeDebug", "1");
        servletRegistrationBean.setInitParameters(inits);
        return servletRegistrationBean;
    }


    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new ErrorFilter());
        filterRegistrationBean.setEnabled(true);
        return filterRegistrationBean;
    }


}
