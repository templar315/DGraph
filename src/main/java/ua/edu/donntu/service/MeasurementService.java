package ua.edu.donntu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.donntu.domain.Measurement;
import ua.edu.donntu.domain.MeasurementUnit;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.MeasurementInDTO;
import ua.edu.donntu.dto.MeasurementOutDTO;
import ua.edu.donntu.repository.MeasurementRepository;
import ua.edu.donntu.repository.MeasurementUnitRepository;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.MeasurementNotFinishedException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;
import ua.edu.donntu.service.utils.MultiNodeMeasurementThread;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeasurementService {

    private final NodeRepository nodeRepository;

    private final MeasurementRepository measurementRepository;

    private final MeasurementUnitRepository measurementUnitRepository;

    private final FileService fileService;

    protected MeasurementOutDTO toMeasurementDTO(Measurement measurement) {
        if (measurement == null) {
            return null;
        }
        return MeasurementOutDTO.builder()
                .id(measurement.getId())
                .startDate(measurement.getStartDate())
                .finishDate(measurement.getFinishDate())
                .size(measurement.getSize())
                .finished(measurement.isFinished())
                .transmissionMean(getTransmissionMean(measurement))
                .transmissionDeviation(getTransmissionDeviation(measurement))
                .processingMean(getProcessingMean(measurement))
                .processingDeviation(getProcessingDeviation(measurement))
                .units(measurement.getMeasurementUnits() == null
                        ? new ArrayList<>()
                        : measurement.getMeasurementUnits().stream()
                        .map(MeasurementUnit::getId)
                        .collect(Collectors.toList()))
                .build();
    }

    public MeasurementOutDTO getOne(long id) {
        log.debug("Request to get Measurement by id: {}", id);
        return toMeasurementDTO(measurementRepository.getOne(id));
    }

    public List<MeasurementOutDTO> getAll() {
        log.debug("Request to get all Measurements");
        return measurementRepository.findAll()
                .stream()
                .map(this::toMeasurementDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(long id) throws MeasurementNotFinishedException {
        log.debug("Request to delete measurement: {}", id);

        if (!measurementRepository.existsById(id)) {
            return;
        }

        Measurement measurement = measurementRepository.getOne(id);

        if (!measurement.isFinished()) {
            log.warn("Measurement with id {} not finished", id);
            throw new MeasurementNotFinishedException("Measurement not finished");
        }

        measurementRepository.delete(measurement);
    }

    @Transactional
    public void deleteAll() throws MeasurementNotFinishedException {
        log.debug("Request to delete all measurements");

        List<Measurement> measurements = measurementRepository.getByFinished(false);

        if (!measurements.isEmpty()) {
            log.warn("Measurement not finished");
            throw new MeasurementNotFinishedException("Measurement not finished");
        }

        measurementRepository.deleteAllInBatch();
    }

    public MeasurementOutDTO measureNodesThroughput(MeasurementInDTO measurementDTO) throws NodeDoesNotExistException {
        log.debug("Request to measure throughput with parameters: {}", measurementDTO);
        Measurement measurement = save(measurementDTO);
        new MultiNodeMeasurementThread(
                measurement.getId(),
                measurementDTO,
                fileService.generateFileByteArray(measurementDTO.getSize()),
                measurementUnitRepository,
                measurementRepository,
                nodeRepository)
                .start();
        return toMeasurementDTO(measurement);
    }

    @Transactional
    private Measurement save(MeasurementInDTO measurementDTO) {
        return measurementRepository.saveAndFlush(Measurement.builder()
                .startDate(new Date())
                .size(measurementDTO.getSize())
                .measurementUnits(new ArrayList<>())
                .build());
    }

    private long getTransmissionMean(Measurement measurement) {
        //log.debug("Request to get measurement transmission mean: {}", measurement);
        List<MeasurementUnit> units = measurement.getMeasurementUnits();
        if (units.size() == 0) {
            return 0L;
        }
        return units.stream()
                .map(MeasurementUnit::getTransmissionTime)
                .mapToLong(Long::longValue)
                .sum() / units.size();
    }

    private long getTransmissionDeviation(Measurement measurement) {
        //log.debug("Request to get measurement transmission mean deviation: {}", measurement);
        List<MeasurementUnit> units = measurement.getMeasurementUnits();
        if (units.size() == 0) {
            return 0L;
        }
        long mean = getTransmissionMean(measurement);
        return units.stream()
                .map(unit -> Math.pow((double) (unit.getTransmissionTime() - mean), 2))
                .mapToLong(Double::longValue)
                .sum() / units.size();
    }

    private long getProcessingMean(Measurement measurement) {
        //log.debug("Request to get measurement processing mean: {}", measurement);
        List<MeasurementUnit> units = measurement.getMeasurementUnits();
        if (units.size() == 0) {
            return 0L;
        }
        return units.stream()
                .map(MeasurementUnit::getProcessingTime)
                .mapToLong(Long::longValue)
                .sum() / units.size();
    }

    private long getProcessingDeviation(Measurement measurement) {
        //log.debug("Request to get measurement processing mean deviation: {}", measurement);
        List<MeasurementUnit> units = measurement.getMeasurementUnits();
        if (units.size() == 0) {
            return 0L;
        }
        long mean = getTransmissionMean(measurement);
        return units.stream()
                .map(unit -> Math.pow((double) (unit.getProcessingTime() - mean), 2))
                .mapToLong(Double::longValue)
                .sum() / units.size();
    }

    /*public MeasurementOutDTO measureSingleNodeThroughput(long nodeId, int size, int measurements) throws NodeDoesNotExistException {
        log.debug("Request to measure throughput for node {} with message size {}", nodeId, size);

        if (nodeId <= 0L) {
            log.error("Node id are less or equals 0");
            throw new NodeDoesNotExistException("Node id are less or equals 0");
        }

        if (!nodeRepository.existsById(nodeId)) {
            log.error("Node does not exist");
            throw new NodeDoesNotExistException("Node does not exist");
        }

        Node node = nodeRepository.getOne(nodeId);
        Measurement measurement = measurementRepository.saveAndFlush(Measurement.builder()
                .startDate(new Date())
                .size(size)
                .build());
        SingleNodeMeasurementThread.builder()
                .measurement(measurement)
                .node(node)
                .size(size)
                .measurements(measurements)
                .fileArray(fileService.generateFileByteArray(size))
                .measurementUnitRepository(measurementUnitRepository)
                .measurementRepository(measurementRepository)
                .build()
                .start();

        return toMeasurementDTO(measurement);
    }*/

        /*public MeasurementOutDTO measureAllNodesThroughput(int size, int measurements) throws NodeDoesNotExistException {
        log.debug("Request to measure throughput for all nodes with message size {}", size);
        List<Node> nodes = nodeRepository.findAll();
        return measureThroughput(nodes, size, measurements);
    }*/

    /*private MeasurementOutDTO measureThroughput(List<Node> nodes, int size, int measurements) throws NodeDoesNotExistException {
        log.debug("Request to measure throughput for nodes: {}", nodes);

        if (nodes.size() == 0) {
            log.error("Node list is empty");
            throw new NodeDoesNotExistException("Node list is empty");
        }

        Measurement measurement = Measurement.builder()
                .startDate(new Date())
                .size(size)
                .build();
        MultiNodeMeasurementThread.builder()
                .nodes(nodes)
                .size(size)
                .measurements(measurements)
                .fileArray(fileService.generateFileByteArray(size))
                .measurementUnitRepository(measurementUnitRepository)
                .measurementRepository(measurementRepository)
                .build()
                .start();

        return toMeasurementDTO(measurement);
    }*/
}
