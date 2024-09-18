package com.coolers.aogp.aogp.config;

import com.coolers.aogp.aogp.constant.FormatConst;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneId.SHORT_IDS;

@Configuration
public class RedisConfig {
    /**
     * redis 连接工厂
     */
    @Autowired
    private RedisConnectionFactory connectionFactory;

    /**
     * 实例化RedisTemplate，使用Json序列化，并对日期/时间进行格式化，统一使用字符串key
     *
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        ObjectMapper objectMapper = new ObjectMapper();
        // 日期格式支持
        JavaTimeModule timeModule = new JavaTimeModule();
        final ZoneId DEFAULT_ZONE_ID = ZoneId.of(FormatConst.CTT, SHORT_IDS);
        final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FormatConst.yyyy_MM_dd).withZone(DEFAULT_ZONE_ID);
        final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FormatConst.yyyy_MM_dd_space_HH_mm_ss).withZone(DEFAULT_ZONE_ID);
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER)).addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER)).addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER)).addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        objectMapper.registerModule(timeModule);
        // 反序列化时忽略对象中不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 反序列化时可以转为对象
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        // key统一使用字符串
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // value统一使用json
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
