package ua.edu.donntu.service.utils;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.donntu.domain.Measurement;
import ua.edu.donntu.domain.MeasurementUnit;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.MeasurementInDTO;
import ua.edu.donntu.dto.MessageOutDTO;
import ua.edu.donntu.repository.MeasurementRepository;
import ua.edu.donntu.repository.MeasurementUnitRepository;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
public class MultiNodeMeasurementThread extends Thread {

    private MeasurementUnitRepository measurementUnitRepository;
    private MeasurementRepository measurementRepository;
    private NodeRepository nodeRepository;

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String TEST_FILE_NAME = "throughput_test_file.txt";
    private static final int TIMEOUT = 10 * 60 * 1000; // in ms

    private long measurementId;
    private List<Long> nodesIds;
    private int size;
    private int measurements;
    private byte[] fileArray;

    public MultiNodeMeasurementThread(Long measurementId,
                                      MeasurementInDTO measurementDTO,
                                      byte[] fileArray,
                                      MeasurementUnitRepository measurementUnitRepository,
                                      MeasurementRepository measurementRepository,
                                      NodeRepository nodeRepository) {
        this.measurementId = measurementId;
        this.nodesIds = measurementDTO.getNodes();
        this.size = measurementDTO.getSize();
        this.measurements = measurementDTO.getMeasurements();
        this.fileArray = fileArray;
        this.measurementUnitRepository = measurementUnitRepository;
        this.measurementRepository = measurementRepository;
        this.nodeRepository = nodeRepository;
    }

    @SneakyThrows
    @Transactional
    public void run() {
        Map<Long, Node> nodesMap = getNodes(nodesIds);
        Measurement measurement = measurementRepository.getOne(measurementId);
        for (int i = 0; i < measurements; i++) {
            ExecutorService executorService = Executors.newCachedThreadPool();
            Map<Long, Future<MessageOutDTO>> futures = new HashMap<>();
            try {
                for (Node node : nodesMap.values()) {
                    futures.put(node.getId(), executorService.submit(PropagationThread.builder()
                            .recipientHost(node.getHost())
                            .recipientPort(node.getPort())
                            .fileName(TEST_FILE_NAME)
                            .fileArray(fileArray).build()));
                }
                executorService.shutdown();
                if (!executorService.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS)) {
                        log.error("Threads pool did not terminate");
                    }
                }
                for (Long nodeId : futures.keySet()) {
                    try {
                        mapResultToUnit(futures.get(nodeId).get(), nodesMap.get(nodeId), measurement);
                    } catch (ExecutionException e) {
                        log.error("Error occurred while map propagation result: " + e.getMessage(), e);
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error occurred while terminate propagation threads: " + e.getMessage(), e);
                executorService.shutdownNow();
            }
        }
        measurementRepository.saveAndFlush(measurement.toBuilder()
                .finishDate(new Date())
                .finished(true)
                .build());
    }

    @Transactional
    private Map<Long, Node> getNodes(List<Long> nodesIds) throws NodeDoesNotExistException {
        List<Node> nodes = new ArrayList<>();
        if (nodesIds.isEmpty()) {
            nodes = nodeRepository.findAll();
        } else {
            nodeRepository.findAllById(nodesIds);
        }

        if (nodes.size() == 0) {
            log.error("Node list is empty");
            throw new NodeDoesNotExistException("Node list is empty");
        }

        Map<Long, Node> nodesMap = new HashMap<>();
        for (Node node : nodes) {
            nodesMap.put(node.getId(), node);
        }
        return nodesMap;
    }

    @Transactional
    private void mapResultToUnit(MessageOutDTO result, Node node, Measurement measurement) {
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
