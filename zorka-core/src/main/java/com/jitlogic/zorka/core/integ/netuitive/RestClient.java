package com.jitlogic.zorka.core.integ.netuitive;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.ByteArrayOutputStream;

public class RestClient {

    private final String baseUrl;
    private final String apiKey;
    private final HttpClient httpClient;

    public RestClient(String baseUrl, String apiKey, int socketTimeout, int connectTimeout, int connectionRequestTimeout, String proxyAddress) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;

        RequestConfig.Builder builder = RequestConfig.copy(RequestConfig.DEFAULT)
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout);

        if (proxyAddress != null) {
            HttpHost proxy = HttpHost.create(proxyAddress);
            builder.setProxy(proxy);
        }

        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(builder.build()).build();
    }

    public RestResponse post(String content) {
        HttpPost httpPost = new HttpPost(baseUrl + "/ingest/java/" + apiKey);
        StringEntity httpEntity = null;
        try {
            httpEntity = new StringEntity(content);
            httpEntity.setContentType("application/json");
            httpPost.setEntity(httpEntity);

            HttpResponse response = httpClient.execute(httpPost);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.getEntity().writeTo(outputStream);
            String responseContent = outputStream.toString("UTF-8");
            return new RestResponse(
                    response.getStatusLine().getStatusCode(),
                    response.getEntity().getContentType() != null ? response.getEntity().getContentType().toString() : "",
                    response.getEntity().getContentEncoding() != null ? response.getEntity().getContentEncoding().toString() : "",
                    responseContent);
        } catch (Exception e) {
            throw new RestClientException("A problem occurred attempting post on url: " + baseUrl, e);
        }
    }
}
