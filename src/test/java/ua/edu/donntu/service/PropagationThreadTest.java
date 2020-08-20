package ua.edu.donntu.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
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
import ua.edu.donntu.service.utils.PropagationThread;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        PropagationThread.class,
        Thread.class,
        HttpClients.class})
@PowerMockIgnore({ "javax.net.ssl.*" })
public class PropagationThreadTest {

    private final String senderHost = "127.125.14.22";
    private final String recipientHost = "185.156.14.78";
    private final String recipientPort = "8080";
    private final String fileName = "4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe.txt";
    private final byte[] fileArray = "file1".getBytes();

    private final CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
    private final HttpPost httpPost = PowerMockito.mock(HttpPost.class);
    private final CloseableHttpResponse httpResponse = PowerMockito.mock(CloseableHttpResponse.class);
    private final PropagationThread propagationThread = PowerMockito.spy(new PropagationThread(
            senderHost,
            recipientHost,
            recipientPort,
            fileName,
            fileArray));

    @Test
    public void run() throws Exception {
        PowerMockito.when(propagationThread, "isDataValid").thenReturn(true);
        PowerMockito.mockStatic(HttpClients.class);
        BDDMockito.given(HttpClients.createDefault()).willReturn(httpClient);
        PowerMockito.whenNew(HttpPost.class).withArguments(
                "http://" + recipientHost + ":" + recipientPort + "/messages").thenReturn(httpPost);
        PowerMockito.when(httpClient, "execute", httpPost).thenReturn(httpResponse);
        Whitebox.invokeMethod(propagationThread, "run");
        Mockito.verify(httpClient, Mockito.times(1)).execute(httpPost);
    }

}
