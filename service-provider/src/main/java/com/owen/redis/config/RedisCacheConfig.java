package com.owen.redis.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

/**
 * Created by huang_b on 2017/12/5.
 * 这个只是将 spring 的缓存 搞到了 redis 里面去了
 */
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    @Autowired
    private RedisTemplate redisTemplate;

    /*
    * 将redistemplate 的 key 序列化换成 string  value序列化换成 fastjson
    * */
    @Bean
    public RedisTemplate getRedisTemplate() {
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssX");
        fastJsonRedisSerializer.setFastJsonConfig(fastJsonConfig);

        //设置序列化Key的实例化对象
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置序列化Value的实例化对象
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);

//        redisTemplate.getConnectionFactory().getClusterConnection().setConfig();
        return redisTemplate;
    }

    /* redis key 生成策略  */

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        // 设置缓存过期时间，秒
        rcm.setDefaultExpiration(600);
        return rcm;
    }

    /*redis 操作出错后不能影响 业务处理  后续还要查数据库*/
    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                System.out.println(key);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                System.out.println(value);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {

            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {

            }
        };
        return cacheErrorHandler;
    }
}



