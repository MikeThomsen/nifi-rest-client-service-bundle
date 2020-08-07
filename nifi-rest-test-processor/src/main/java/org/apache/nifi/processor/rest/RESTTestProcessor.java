package org.apache.nifi.processor.rest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controllers.rest.RESTClientProviderService;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
public class RESTTestProcessor extends AbstractProcessor {
    private RESTClientProviderService restProviderService;

    public static final PropertyDescriptor CLIENT = new PropertyDescriptor.Builder()
        .name("client")
        .displayName("Client")
        .identifiesControllerService(RESTClientProviderService.class)
        .required(true)
        .build();

    public static final Relationship FAIL = new Relationship.Builder().name("failure").build();
    public static final Relationship SUCCESS = new Relationship.Builder().name("success").build();

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return Arrays.asList(CLIENT);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return new HashSet<>(Arrays.asList(
            SUCCESS, FAIL
        ));
    }

    @OnScheduled
    public void onScheduled(ProcessContext context) {
        restProviderService = context.getProperty(CLIENT).asControllerService(RESTClientProviderService.class);
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        FlowFile input = session.get();
        if (input == null) {
            return;
        }

        OkHttpClient client = restProviderService.getClient();

        try {
            String url = input.getAttribute("url");

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() >= 200 && response.code() < 300) {
                session.transfer(input, SUCCESS);
            } else {
                session.transfer(input, FAIL);
            }
        } catch (Exception ex) {
            getLogger().error("", ex);
            session.transfer(input, FAIL);
        }
    }
}
