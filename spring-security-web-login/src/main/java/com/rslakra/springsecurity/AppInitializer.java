package com.rslakra.springsecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * WebApp Initializer
 *
 * @author Rohtash Lakra
 * @version 1.0.0
 * @since 11/11/2023 6:02 PM
 * *
 */
public class AppInitializer implements WebApplicationInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);

    /**
     * @param servletContext
     */
    @Override
    public void onStartup(ServletContext servletContext) {
        LOGGER.debug("+onStartup({})", servletContext);
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.scan(getClass().getPackageName());
        servletContext.addListener(new ContextLoaderListener(root));

        // add dispatcher-servlet
        ServletRegistration.Dynamic appServlet = servletContext.addServlet("mvc", new DispatcherServlet(new GenericWebApplicationContext()));
        appServlet.setLoadOnStartup(1);
        appServlet.addMapping("/");

        servletContext.addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain"))
                .addMappingForUrlPatterns(null, false, "/*");
        LOGGER.debug("-onStartup()");
    }

}
