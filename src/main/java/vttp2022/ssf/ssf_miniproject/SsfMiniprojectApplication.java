package vttp2022.ssf.ssf_miniproject;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableCaching
public class SsfMiniprojectApplication {

	@Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig() //
        .prefixCacheNameWith(this.getClass().getPackageName() + ".") //
        .entryTtl(Duration.ofHours(1)) //
        .disableCachingNullValues();

    return RedisCacheManager.builder(connectionFactory) //
        .cacheDefaults(config) //
        .build();
  }

	public static void main(String[] args) {
		SpringApplication.run(SsfMiniprojectApplication.class, args);
	}

}
