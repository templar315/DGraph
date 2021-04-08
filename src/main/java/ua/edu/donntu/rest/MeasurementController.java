package ua.edu.donntu.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.donntu.dto.MeasurementInDTO;
import ua.edu.donntu.dto.MeasurementOutDTO;
import ua.edu.donntu.service.MeasurementService;
import ua.edu.donntu.service.exceptions.MeasurementNotFinishedException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/measurements")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeasurementController {

    private final MeasurementService measurementService;

    /*@PostMapping("/start/{id}")
    public ResponseEntity<MeasurementOutDTO> startSingleNodeMeasurement(@PathVariable long node,
                                                                        @RequestParam int size,
                                                                        @RequestParam int measurements) throws NodeDoesNotExistException {
        log.debug("REST Request to start single node measurement with arguments: " +
                "node = {}, size = {}, measurements = {}", node, size, measurements);
        return ResponseEntity.ok(measurementService.measureSingleNodeThroughput(node, size, measurements));
    }*/

    @PostMapping("/start")
    public ResponseEntity<MeasurementOutDTO> startNodesThroughputMeasurement(@Validated @RequestBody MeasurementInDTO measurement) throws NodeDoesNotExistException {
        log.debug("REST Request to start throughput measurement: {}", measurement);
        return ResponseEntity.ok(measurementService.measureNodesThroughput(measurement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id) throws MeasurementNotFinishedException {
        log.debug("REST Request to delete Measurement with id: " + id);
        measurementService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity deleteAll() throws MeasurementNotFinishedException {
        log.debug("REST Request to delete all Measurements");
        measurementService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeasurementOutDTO> getOne(@PathVariable long id) {
        log.debug("REST Request to get Message with id: " + id);
        MeasurementOutDTO measurement = measurementService.getOne(id);
        if (measurement == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(measurement);
    }

    @GetMapping
    public ResponseEntity<List<MeasurementOutDTO>> getAll() {
        log.debug("REST Request to get all Measurements");
        return ResponseEntity.ok(measurementService.getAll());
    }
}
