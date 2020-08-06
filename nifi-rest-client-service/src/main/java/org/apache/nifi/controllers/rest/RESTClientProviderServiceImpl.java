package org.apache.nifi.controllers.rest;

import okhttp3.OkHttpClient;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.security.util.OkHttpClientUtils;
import org.apache.nifi.security.util.TlsConfiguration;
import org.apache.nifi.ssl.SSLContextService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CapabilityDescription("Providers a builder client service for generating OkHttp clients.")
@Tags({ "http", "rest", "client", "builder" })
public class RESTClientProviderServiceImpl extends AbstractControllerService implements RESTClientProviderService {
    private volatile OkHttpClient.Builder builder;

    @OnEnabled
    public void onEnabled(ConfigurationContext context) {
        OkHttpClient.Builder temp = new OkHttpClient.Builder();
        if (context.getProperty(SSL_CONTEXT_SERVICE).isSet()) {
            SSLContextService sslContextService = context.getProperty(SSL_CONTEXT_SERVICE).asControllerService(SSLContextService.class);
            final TlsConfiguration tlsConfiguration = sslContextService.createTlsConfiguration();
            OkHttpClientUtils.applyTlsToOkHttpClientBuilder(tlsConfiguration, temp);
        }

        long connectTimeout = context.getProperty(CONNECT_TIMEOUT).asLong();
        long readTimeout = context.getProperty(READ_TIMEOUT).asLong();
        long writeTimeout = context.getProperty(WRITE_TIMEOUT).asLong();

        temp
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);

        builder = temp;
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public OkHttpClient getClient() {
        return builder.build();
    }
}
