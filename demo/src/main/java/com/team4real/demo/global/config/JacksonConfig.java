package com.team4real.demo.global.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.utils.StringUtils;

import java.io.IOException;
import java.time.Instant;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // Serializer: Long → String
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(long.class, ToStringSerializer.instance);

            // Deserializer: Numeric String → Long
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Long.class, new StdDeserializer<Long>(Long.class) {
                @Override
                public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                    String text = p.getText();
                    if (StringUtils.isNotBlank(text) && text.matches("\\d+")) {
                        return Long.parseLong(text);
                    }
                    return null;
                }
            });
            module.addDeserializer(long.class, new StdDeserializer<Long>(Long.TYPE) {
                @Override
                public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                    String text = p.getText();
                    if (StringUtils.isNotBlank(text) && text.matches("\\d+")) {
                        return Long.parseLong(text);
                    }
                    return 0L;
                }
            });

            // Java Time support: Instant, LocalDateTime 등
            module.addSerializer(Instant.class, ToStringSerializer.instance);
            module.addDeserializer(Instant.class, new StdDeserializer<Instant>(Instant.class) {
                @Override
                public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                    return Instant.parse(p.getText());
                }
            });

            builder.modules(module, new JavaTimeModule());

            //타임스탬프(숫자) 대신 ISO-8601 문자열 사용
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}