package com.ikea.imc.pam.budget.service.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/*TODO: We might want to discuss solving this issue in another way
This seems to be what most search results recommend when looking for
"How to reach instance of Beans outside spring-managed classes" */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextUtil.applicationContext = applicationContext;
    }
    
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext != null ? applicationContext.getBean(clazz) : null;
    }
}
