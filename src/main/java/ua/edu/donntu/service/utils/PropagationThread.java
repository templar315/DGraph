package ua.edu.donntu.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import ua.edu.donntu.dto.MessageInDTO;
import ua.edu.donntu.dto.MessageOutDTO;

import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.Callable;

@Slf4j
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
public class PropagationThread implements Callable<MessageOutDTO> {

    private static final int TIMEOUT = 10 * 60 * 1000; // in ms

    private String recipientHost;
    private String recipientPort;
    private String fileName;
    private byte[] fileArray;

    private MessageOutDTO result;
    private boolean done;

    public PropagationThread(String recipientHost, String recipientPort, String fileName, byte[] fileArray) {
        this.recipientHost = recipientHost;
        this.recipientPort = recipientPort;
        this.fileName = fileName;
        this.fileArray = fileArray;
    }

    @SneakyThrows
    public MessageOutDTO call() {
        if (!isDataValid()) {
            return null;
        }
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpPost uploadFile = new HttpPost("http://" + recipientHost + ":" + recipientPort + "/messages");
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();

        MessageInDTO messageInDTO = MessageInDTO.builder()
                .sendDate(new Date())
                .build();

        HttpEntity entity = MultipartEntityBuilder.create()
                .addTextBody(
                        "message",
                        gson.toJson(messageInDTO),
                        ContentType.APPLICATION_JSON)
                .addBinaryBody(
                        "file",
                        fileArray,
                        ContentType.MULTIPART_FORM_DATA,
                        fileName)
                .build();

        uploadFile.setEntity(entity);

        try {
            HttpResponse response = httpClient.execute(uploadFile);
            log.debug("Propagation thread response: " + response.toString());
            return objectMapper.readValue(response.getEntity().getContent(), MessageOutDTO.class);
        } catch (ConnectException exception) {
            log.error("Propagation thread error: ", exception);
        }
        return null;
    }

    private boolean isDataValid() {
        return recipientHost != null
                && !recipientHost.isEmpty()
                && recipientPort != null
                && !recipientPort.isEmpty()
                && fileArray != null
                && fileArray.length > 0
                && fileName != null
                && !fileName.isEmpty();
    }
}