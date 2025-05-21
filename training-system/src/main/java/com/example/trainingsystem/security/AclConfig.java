package com.example.trainingsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableMethodSecurity
public class AclConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AclCache aclCache() {
        ConcurrentHashMap<ObjectIdentity, Acl> cacheMap = new ConcurrentHashMap<>();

        return new AclCache() {
            @Override
            public void evictFromCache(ObjectIdentity objectIdentity) {
                cacheMap.remove(objectIdentity);
            }

            @Override
            public void evictFromCache(Serializable primaryKey) {
                cacheMap.entrySet().removeIf(entry -> primaryKey.equals(entry.getKey().getIdentifier()));
            }

            @Override
            public MutableAcl getFromCache(ObjectIdentity objectIdentity) {
                return (MutableAcl) cacheMap.get(objectIdentity);
            }

            @Override
            public MutableAcl getFromCache(Serializable primaryKey) {
                return (MutableAcl) cacheMap.values().stream()
                        .filter(acl -> acl.getObjectIdentity().equals(primaryKey))
                        .findFirst()
                        .orElse(null);
            }

            @Override
            public void putInCache(MutableAcl acl) {
                cacheMap.put(acl.getObjectIdentity(), acl);
            }

            @Override
            public void clearCache() {
                cacheMap.clear();
            }
        };
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }


    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }


    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
        return expressionHandler;
    }


    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(
                dataSource,
                aclCache(),
                aclAuthorizationStrategy(),
                new ConsoleAuditLogger()
        );
    }


    @Bean
    public JdbcMutableAclService aclService() {
        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    }
}