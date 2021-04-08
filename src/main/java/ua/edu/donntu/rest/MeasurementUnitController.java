package ua.edu.donntu.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.donntu.dto.MeasurementInDTO;
import ua.edu.donntu.dto.MeasurementOutDTO;
import ua.edu.donntu.dto.MeasurementUnitOutDTO;
import ua.edu.donntu.service.MeasurementService;
import ua.edu.donntu.service.MeasurementUnitService;
import ua.edu.donntu.service.exceptions.MeasurementNotFinishedException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/units")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeasurementUnitController {

    private final MeasurementUnitService measurementUnitService;

    @GetMapping("/{id}")
    public ResponseEntity<MeasurementUnitOutDTO> getOne(@PathVariable long id) {
        log.debug("REST Request to get Measurement Unit with id: " + id);
        MeasurementUnitOutDTO measurement = measurementUnitService.getOne(id);
        if (measurement == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(measurement);
    }

    @GetMapping
    public ResponseEntity<List<MeasurementUnitOutDTO>> getAll() {
        log.debug("REST Request to get all Measurements Units");
        return ResponseEntity.ok(measurementUnitService.getAll());
    }

    @GetMapping(params = "node")
    public ResponseEntity<List<MeasurementUnitOutDTO>> getAllByNode(@RequestParam long node) {
        log.debug("REST Request to get all Measurements Units by Node id: {}", node);
        return ResponseEntity.ok(measurementUnitService.findAllByNode(node));
    }

    @GetMapping(params = "measurement")
    public ResponseEntity<List<MeasurementUnitOutDTO>> getAllByMeasurement(@RequestParam long measurement) {
        log.debug("REST Request to get all Measurements Units by Measurement id: {}", measurement);
        return ResponseEntity.ok(measurementUnitService.findAllByMeasurement(measurement));
    }
}
