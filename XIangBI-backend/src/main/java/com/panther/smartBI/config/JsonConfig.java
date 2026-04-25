/**
 * XiangBI File: src/main/java/com/panther/smartBI/config/JsonConfig.java
 * Responsibility: Project configuration module.
 */
package com.panther.smartBI.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@JsonComponent
public class JsonConfig {

    private static final String STANDARD_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final TimeZone GMT_8 = TimeZone.getTimeZone("GMT+8");

    /**
     * 解决 Long 转 json 精度丢失，并统一 Date 的序列化 / 反序列化格式
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        module.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                    return;
                }
                gen.writeString(createDateFormat(STANDARD_DATETIME_PATTERN).format(value));
            }
        });
        module.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (value == null || value.trim().isEmpty()) {
                    return null;
                }
                String text = value.trim();
                String[] patterns = {
                        STANDARD_DATETIME_PATTERN,
                        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                        "yyyy-MM-dd'T'HH:mm:ssXXX",
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                        "yyyy-MM-dd'T'HH:mm:ss'Z'"
                };
                for (String pattern : patterns) {
                    try {
                        return createDateFormat(pattern).parse(text);
                    } catch (ParseException ignored) {
                    }
                }
                throw new IOException("Unsupported date format: " + text);
            }
        });
        objectMapper.registerModule(module);
        return objectMapper;
    }

    private SimpleDateFormat createDateFormat(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(GMT_8);
        return sdf;
    }
}

