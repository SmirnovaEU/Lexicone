package com.example.trainingsystem.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Конфигурация Spring MVC с поддержкой локализации (i18n).
 * <p>
 * Настраивает механизм смены языка по параметру запроса {@code lang},
 * и хранение выбранной локали в HTTP-сессии.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Регистрирует {@link LocaleChangeInterceptor} в цепочке перехватчиков Spring MVC.
     * <p>
     * Этот перехватчик отслеживает параметр {@code lang} в URL,
     * например: {@code /home?lang=ru}, и изменяет текущую локаль приложения.
     *
     * @param registry реестр интерцепторов
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor lci = localeChangeInterceptor();
        registry.addInterceptor(lci);
    }

    /**
     * Создаёт перехватчик, который позволяет менять язык приложения по параметру {@code lang}.
     * <p>
     * Например, при запросе {@code ?lang=ru} локаль переключается на русский.
     *
     * @return бин перехватчика локали
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Определяет, где будет храниться текущая локаль пользователя.
     * <p>
     * В данном случае используется {@link SessionLocaleResolver},
     * который сохраняет локаль в HTTP-сессии.
     * По умолчанию устанавливается английская локаль ({@link Locale#ENGLISH}).
     *
     * @return бин LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
