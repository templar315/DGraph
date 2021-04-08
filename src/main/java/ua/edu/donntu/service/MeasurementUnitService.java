package ua.edu.donntu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.donntu.domain.MeasurementUnit;
import ua.edu.donntu.dto.MeasurementUnitOutDTO;
import ua.edu.donntu.repository.MeasurementUnitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeasurementUnitService {

    private final MeasurementUnitRepository measurementUnitRepository;

    protected MeasurementUnitOutDTO toMeasurementUnitDTO(MeasurementUnit measurementUnit) {
        if (measurementUnit == null) {
            return null;
        }
        return MeasurementUnitOutDTO.builder()
                .id(measurementUnit.getId())
                .transmissionTime(measurementUnit.getTransmissionTime())
                .processingTime(measurementUnit.getProcessingTime())
                .hash(measurementUnit.getHash())
                .measurement(measurementUnit.getMeasurement() == null
                        ? 0L
                        : measurementUnit.getMeasurement().getId())
                .node(measurementUnit.getNode() == null
                        ? 0L
                        : measurementUnit.getNode().getId())
                .build();
    }

    public MeasurementUnitOutDTO getOne(long id) {
        log.debug("Request to get Measurement Unit by id: {}", id);
        return toMeasurementUnitDTO(measurementUnitRepository.getOne(id));
    }

    public List<MeasurementUnitOutDTO> getAll() {
        log.debug("Request to get all Measurements Units");
        return measurementUnitRepository.findAll()
                .stream()
                .map(this::toMeasurementUnitDTO)
                .collect(Collectors.toList());
    }

    public List<MeasurementUnitOutDTO> findAllByNode(long id) {
        log.debug("Request to get Measurement Unit by Node id: {}", id);
        return measurementUnitRepository.findAllByNodeId(id)
                .stream()
                .map(this::toMeasurementUnitDTO)
                .collect(Collectors.toList());
    }

    public List<MeasurementUnitOutDTO> findAllByMeasurement(long id) {
        log.debug("Request to get Measurement Unit by Measurement id: {}", id);
        return measurementUnitRepository.findAllByMeasurementId(id)
                .stream()
                .map(this::toMeasurementUnitDTO)
                .collect(Collectors.toList());
    }
}
