package ua.edu.donntu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import ua.edu.donntu.dto.MessageOutDTO;
import ua.edu.donntu.service.utils.PropagationThread;

import java.io.InputStream;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        PropagationThread.class,
        Thread.class,
        HttpEntity.class,
        HttpClients.class,
        HttpClientBuilder.class,
        RequestConfig.class,
        ObjectMapper.class,
        MessageOutDTO.class,
        InputStream.class
})
@PowerMockIgnore({ "javax.net.ssl.*" })
public class PropagationThreadTest {

    private final String recipientHost = "185.156.14.78";
    private final String recipientPort = "8080";
    private final String fileName = "4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe.txt";
    private final byte[] fileArray = "file1".getBytes();

    private final ObjectMapper objectMapper = PowerMockito.mock(ObjectMapper.class);
    private final RequestConfig requestConfig = PowerMockito.mock(RequestConfig.class);
    private final HttpClientBuilder httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
    private final HttpEntity httpEntity = PowerMockito.mock(HttpEntity.class);
    private final InputStream inputStream = PowerMockito.mock(InputStream.class);
    private final CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
    private final HttpPost httpPost = PowerMockito.mock(HttpPost.class);
    private final CloseableHttpResponse httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
    private final MessageOutDTO message = PowerMockito.mock(MessageOutDTO.class);
    private final PropagationThread propagationThread = PowerMockito.spy(new PropagationThread(
            recipientHost,
            recipientPort,
            fileName,
            fileArray));

    @Test
    public void run() throws Exception {
        PowerMockito.when(propagationThread, "isDataValid").thenReturn(true);
        PowerMockito.whenNew(ObjectMapper.class).withAnyArguments().thenReturn(objectMapper);
        PowerMockito.whenNew(RequestConfig.class).withAnyArguments().thenReturn(requestConfig);
        PowerMockito.whenNew(HttpClientBuilder.class).withAnyArguments().thenReturn(httpClientBuilder);
        PowerMockito.when(httpClientBuilder, "setDefaultRequestConfig", requestConfig).thenReturn(httpClientBuilder);
        PowerMockito.when(httpClientBuilder, "build").thenReturn(httpClient);
        PowerMockito.whenNew(HttpPost.class).withArguments(
                "http://" + recipientHost + ":" + recipientPort + "/messages").thenReturn(httpPost);
        PowerMockito.when(httpResponse, "getEntity").thenReturn(httpEntity);
        PowerMockito.when(httpEntity, "getContent").thenReturn(inputStream);
        PowerMockito.when(httpClient, "execute", httpPost).thenReturn(httpResponse);
        BDDMockito.given(objectMapper.readValue(inputStream, MessageOutDTO.class)).willReturn(message);
        Whitebox.invokeMethod(propagationThread, "call");
        Mockito.verify(httpClient, Mockito.times(1)).execute(httpPost);
    }

}
