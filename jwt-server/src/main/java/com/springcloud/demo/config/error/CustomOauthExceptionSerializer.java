package com.springcloud.demo.config.error;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * 异常处理
 * @author admin
 */
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        //可以不用下面的代码，只用 jsonGenerator.writeRawValue(myJsonString)  输出自定义字符串
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("code4444", value.getHttpErrorCode());
        jsonGenerator.writeBooleanField("status", false);
        jsonGenerator.writeObjectField("data", null);
        jsonGenerator.writeObjectField("errors", Arrays.asList(value.getOAuth2ErrorCode(),value.getMessage()));
        if (value.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                jsonGenerator.writeStringField(key, add);
            }
        }
        jsonGenerator.writeEndObject();
    }
}