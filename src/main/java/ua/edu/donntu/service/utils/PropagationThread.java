package ua.edu.donntu.service.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.donntu.dto.MessageInDTO;

import java.net.ConnectException;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PropagationThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(PropagationThread.class);

    private String senderHost;
    private String recipientHost;
    private String recipientPort;
    private String fileName;
    private byte[] fileArray;

    @SneakyThrows
    public void run() {
        if (isDataValid()) {
            CloseableHttpClient httpClient = HttpClients.createDefault();
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
            } catch (ConnectException exception) {
                log.error("Propagation thread error: ", exception);
            }
        }
    }

    private boolean isDataValid() {
        return senderHost != null
                && !senderHost.isEmpty()
                && recipientHost != null
                && !recipientHost.isEmpty()
                && recipientPort != null
                && !recipientPort.isEmpty()
                && fileArray != null
                && fileArray.length > 0
                && fileName != null
                && !fileName.isEmpty();
    }
}