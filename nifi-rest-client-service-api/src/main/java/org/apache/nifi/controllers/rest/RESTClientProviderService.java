package org.apache.nifi.controllers.rest;

import okhttp3.OkHttpClient;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.ssl.SSLContextService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface RESTClientProviderService extends ControllerService {
    PropertyDescriptor SSL_CONTEXT_SERVICE = new PropertyDescriptor.Builder()
        .name("rest-provider-ssl-service")
        .displayName("SSL Context Service")
        .description("The SSL Context Service to use for configuring SSL.")
        .required(false)
        .addValidator(Validator.VALID)
        .identifiesControllerService(SSLContextService.class)
        .build();

    PropertyDescriptor CONNECT_TIMEOUT = new PropertyDescriptor.Builder()
        .name("rest-provider-connect-timeout")
        .displayName("Connect Timeout")
        .description("The amount of time in milliseconds that will have to elapse before a connect timeout occurs.")
        .addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
        .defaultValue("15000")
        .required(true)
        .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
        .build();

    PropertyDescriptor READ_TIMEOUT = new PropertyDescriptor.Builder()
        .name("rest-provider-read-timeout")
        .displayName("Read Timeout")
        .description("The amount of time in milliseconds that will have to elapse before a read timeout occurs.")
        .addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
        .defaultValue("15000")
        .required(true)
        .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
        .build();

    PropertyDescriptor WRITE_TIMEOUT = new PropertyDescriptor.Builder()
        .name("rest-provider-write-timeout")
        .displayName("Write Timeout")
        .description("The amount of time in milliseconds that will have to elapse before a write timeout occurs.")
        .addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
        .defaultValue("15000")
        .required(true)
        .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
        .build();

    List<PropertyDescriptor> DESCRIPTORS = Collections.unmodifiableList(Arrays.asList(
        SSL_CONTEXT_SERVICE, CONNECT_TIMEOUT, READ_TIMEOUT, WRITE_TIMEOUT
    ));

    OkHttpClient getClient();
}
