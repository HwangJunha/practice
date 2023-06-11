package com.around.practice.config;

import com.around.practice.dto.HttpTraceWrapper;
import com.around.practice.repository.HttpTraceWrapperRepository;
import com.around.practice.repository.SpringDataHttpTraceRepository;
import org.bson.Document;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Configuration
public class ActuatorConfig {

    @Bean
    public HttpTraceRepository springDataHttpTraceRepository(HttpTraceWrapperRepository httpTraceWrapperRepository){
        return new SpringDataHttpTraceRepository(httpTraceWrapperRepository);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context) {

        MappingMongoConverter mappingConverter = //
                new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context); // <1>

        mappingConverter.setCustomConversions( // <2>
                new MongoCustomConversions(Collections.singletonList(CONVERTER))); // <3>

        return mappingConverter;
    }

    static Converter<Document, HttpTraceWrapper> CONVERTER = //
            new Converter<Document, HttpTraceWrapper>() { //
                @Override
                public HttpTraceWrapper convert(Document document) {
                    Document httpTrace = document.get("httpTrace", Document.class);
                    Document request = httpTrace.get("request", Document.class);
                    Document response = httpTrace.get("response", Document.class);

                    return new HttpTraceWrapper(new HttpTrace( //
                            new HttpTrace.Request( //
                                    request.getString("method"), //
                                    URI.create(request.getString("uri")), //
                                    request.get("headers", Map.class), //
                                    null),
                            new HttpTrace.Response( //
                                    response.getInteger("status"), //
                                    response.get("headers", Map.class)),
                            httpTrace.getDate("timestamp").toInstant(), //
                            null, //
                            null, //
                            httpTrace.getLong("timeTaken")));
                }
            };
}
