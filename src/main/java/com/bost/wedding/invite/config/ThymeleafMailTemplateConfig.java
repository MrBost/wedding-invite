package com.bost.wedding.invite.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collections;

@Configuration
public class ThymeleafMailTemplateConfig {

    @Bean
    public ResourceBundleMessageSource emailMessageSource(){
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    private ITemplateResolver emailHtmlTemplateResolver() {
        var staticHtmlTemplateResolver = new ClassLoaderTemplateResolver();
        staticHtmlTemplateResolver.setOrder(1);
        staticHtmlTemplateResolver.setResolvablePatterns(Collections.singleton("*"));
        staticHtmlTemplateResolver.setPrefix("/templates/");
        staticHtmlTemplateResolver.setSuffix(".html");
        staticHtmlTemplateResolver.setTemplateMode(TemplateMode.HTML);
        staticHtmlTemplateResolver.setCharacterEncoding("UTF-8");
        staticHtmlTemplateResolver.setCacheable(false);
        return staticHtmlTemplateResolver;
    }

    @Bean
    @Qualifier("emailTemplateEngine")
    public TemplateEngine emailTemplateEngine(ResourceBundleMessageSource emailMessageSource){
        var templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(emailHtmlTemplateResolver());
        templateEngine.setTemplateEngineMessageSource(emailMessageSource);
        return templateEngine;
    }

}
