package ua.edu.donntu.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import ua.edu.donntu.domain.Measurement;
import ua.edu.donntu.domain.MeasurementUnit;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.MessageInDTO;
import ua.edu.donntu.dto.MessageOutDTO;
import ua.edu.donntu.repository.MeasurementRepository;
import ua.edu.donntu.repository.MeasurementUnitRepository;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
public class SingleNodeMeasurementThread extends Thread {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String TEST_FILE_NAME = "throughput_test_file.txt";
    private static final int TIMEOUT = 10 * 60 * 1000; // in ms

    private Measurement measurement;
    private Node node;
    private int size;
    private int measurements;
    private byte[] fileArray;

    private MeasurementUnitRepository measurementUnitRepository;
    private MeasurementRepository measurementRepository;

    public void run() {
        for (int i = 0; i < measurements; i++) {
            mapResultToUnit(getFileUploadResult(node, size), node, measurement, size);
        }
        measurementRepository.saveAndFlush(measurement.toBuilder()
                .finishDate(new Date())
                .finished(true)
                .build());
    }

    private MessageOutDTO getFileUploadResult(Node node, int size) {
        log.debug("Request to get file upload result for node {} and file size - {}", node, size);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpPost uploadFile = new HttpPost("http://" + node.getHost() + ":" + node.getPort() + "/messages");
        Gson gson = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
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
                        TEST_FILE_NAME)
                .build();

        uploadFile.setEntity(entity);

        try {
            HttpResponse response = httpClient.execute(uploadFile);
            log.debug("Propagation thread response: " + response.toString());
            return objectMapper.readValue(response.getEntity().getContent(), MessageOutDTO.class);
        } catch (IOException exception) {
            log.error("Propagation thread error: " + exception.getMessage(), exception);
        }
        return null;
    }

    private void mapResultToUnit(MessageOutDTO result, Node node, Measurement measurement, int size) {
        log.debug("Request for map result to measurement unit: " + result);
        if (result == null) {
            return;
        }
        measurementUnitRepository.saveAndFlush(MeasurementUnit.builder()
                .node(node)
                .measurement(measurement)
                .transmissionTime(result.getTransmissionTime())
                .processingTime(result.getProcessingTime())
                .hash(result.getHash())
                .build());
    }
}
