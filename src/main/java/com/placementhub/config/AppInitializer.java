package com.placementhub.config;

import jakarta.servlet.Filter;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Replaces web.xml. Boots the Spring root context (persistence) and the
 * DispatcherServlet (web) context, and maps the DispatcherServlet to "/".
 */
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{PersistenceConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * Keeps the Hibernate Session open through view rendering so JSPs can read
     * lazy associations (e.g. student.user, job.company) without a
     * LazyInitializationException.
     */
    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{new OpenSessionInViewFilter()};
    }
}
