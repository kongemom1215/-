package com.unity.potato.config;

import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CachingConfig {
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put("hibernate.cache.region.factory_class", JCacheRegionFactory.class.getName());
    }
    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache("worldcupCache",
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_DAY))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true));
            cm.createCache("worldcupInfoCache",
                new MutableConfiguration<>()
                        .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.TEN_MINUTES))
                        .setStoreByValue(false)
                        .setStatisticsEnabled(true));
            cm.createCache("worldcupDetailCache",
                    new MutableConfiguration<>()
                    .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.TEN_MINUTES))
                    .setStoreByValue(false)
                    .setStatisticsEnabled(true));
            cm.createCache("hotBoardDate",
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.TEN_MINUTES))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true));
            cm.createCache("hotBoardList",
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.TEN_MINUTES))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true));
        };
    }

    @Bean
    public CacheManager cacheManager() {
        javax.cache.CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

        MutableConfiguration<Object, Object> cacheConfig = new MutableConfiguration<>()
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 30)))
                .setStoreByValue(false)
                .setStatisticsEnabled(true);

        cacheManager.createCache("myCache", cacheConfig);

        return cacheManager;
    }
}
