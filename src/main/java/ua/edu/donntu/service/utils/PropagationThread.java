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
import ua.edu.donntu.dto.MessageInDTO;
import ua.edu.donntu.dto.MessageOutDTO;

import java.io.InputStream;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PropagationThread extends Thread {

    private String senderHost;
    private String recipientHost;
    private InputStream fileStream;

    @SneakyThrows
    public void run() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://" + recipientHost);
        Gson gson = new Gson();

        MessageInDTO messageInDTO = MessageInDTO.builder()
                .sendDate(new Date())
                .senderHost(senderHost)
                .recipientHost(recipientHost)
                .build();

        HttpEntity multipart = MultipartEntityBuilder.create()
                .addTextBody("message", gson.toJson(messageInDTO), ContentType.APPLICATION_JSON)
                .addBinaryBody("file", fileStream)
                .build();

        uploadFile.setEntity(multipart);
        httpClient.execute(uploadFile);
    }
}
