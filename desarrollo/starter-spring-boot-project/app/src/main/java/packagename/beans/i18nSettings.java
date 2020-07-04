package packagename.beans;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;


@Configuration
@ComponentScan(basePackages="packagename.controllers")
public class i18nSettings implements WebMvcConfigurer
{
    static String PARAM_LOCALE   = "lang";
    static String DEFAULT_LOCALE = "es";

   
    @Bean
    public MessageSource messageSource() 
    {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // messageSource.setBasename("classpath:i18n/messages");
        messageSource.setBasenames(
            "classpath:i18n/globals/messages",
            "classpath:i18n/fragments/auth/login/messages",
            "classpath:i18n/fragments/auth/logout/messages",
            "classpath:i18n/fragments/error/messages",
            "classpath:i18n/fragments/main/messages",
            "classpath:i18n/layouts/main/messages"
        );
        
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() 
    {
        ValidCookieLocaleResolver resolver = new ValidCookieLocaleResolver();
        resolver.setRejectInvalidCookies(true);
        resolver.setDefaultLocale(new Locale(DEFAULT_LOCALE));
        resolver.setCookieName(PARAM_LOCALE);
        return resolver;
    }

    /**
     * Forms field validation message source
     * @return
     */
    @Bean
    public LocalValidatorFactoryBean validator()
    {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(this.messageSource());
        return bean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) 
    {
        registry.addInterceptor(localeChangeInterceptor());
    }
 
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() 
    {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(PARAM_LOCALE);
        return interceptor;
    }
}
