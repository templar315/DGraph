package ua.edu.donntu.service.utils;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.donntu.dto.MessageInDTO;
import ua.edu.donntu.dto.MessageOutDTO;
import ua.edu.donntu.service.MessageService;

import java.io.InputStream;
import java.net.ConnectException;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PropagationThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(PropagationThread.class);

    private String senderHost;
    private String recipientHost;
    private InputStream fileStream;

    @SneakyThrows
    public void run() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://" + recipientHost + "/messages");
        Gson gson = new Gson();

        MessageInDTO messageInDTO = MessageInDTO.builder()
                .sendDate(new Date())
                .build();

        HttpEntity multipart = MultipartEntityBuilder.create()
                .addTextBody("message", gson.toJson(messageInDTO), ContentType.APPLICATION_JSON)
                .addBinaryBody("file", fileStream)
                .build();

        uploadFile.setEntity(multipart);
        try {
            httpClient.execute(uploadFile);
        } catch (ConnectException exception) {
            log.error("Propagation thread error: " + exception);
        }
    }
}
