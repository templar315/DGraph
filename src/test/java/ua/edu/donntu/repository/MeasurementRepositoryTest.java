package ua.edu.donntu.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.donntu.domain.Measurement;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MeasurementRepositoryTest extends BaseDomainTest {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Test
    public void add() {
        Measurement measurement = measurementRepository.saveAndFlush(Measurement.builder()
                .startDate(new Date())
                .size(1024 * 1024)
                .build());
        assertThat(measurementRepository.getOne(measurement.getId())).isNotNull();
        assertThat(measurementRepository.count()).isEqualTo(3);
    }

    @Test
    public void update() {
        assertThat(measurementRepository.count()).isEqualTo(2);
        Measurement measurement = measurementRepository.findAll().get(0);
        Date date = new Date();
        measurement.setFinishDate(date);
        measurement.setFinished(true);
        measurement = measurementRepository.saveAndFlush(measurement);
        assertThat(measurement.getFinishDate()).isEqualTo(date);
        assertThat(measurement.isFinished()).isTrue();
        assertThat(measurementRepository.count()).isEqualTo(2);
    }

    @Test
    public void delete() {
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        Measurement measurement = measurementRepository.findAll().get(0);
        measurementRepository.delete(measurement);
        assertThat(measurementRepository.count()).isEqualTo(1);
        assertThat(measurementUnitRepository.count()).isEqualTo(2);
    }

    @Test
    public void deleteById() {
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        Measurement measurement = measurementRepository.findAll().get(0);
        measurementRepository.deleteById(measurement.getId());
        assertThat(measurementRepository.count()).isEqualTo(1);
        assertThat(measurementUnitRepository.count()).isEqualTo(2);
    }

    @Test
    public void deleteAll() {
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        measurementRepository.deleteAll();
        assertThat(measurementRepository.count()).isEqualTo(0);
        assertThat(measurementUnitRepository.count()).isEqualTo(0);
    }

    @Test
    public void findById() {
        Measurement measurement = measurementRepository.findAll().get(0);
        assertThat(measurementRepository.findById(measurement.getId())
                .orElse(null))
                .isEqualTo(measurement);
    }

    @Test
    public void existsById() {
        Measurement measurement = measurementRepository.findAll().get(0);
        assertThat(measurementRepository.existsById(measurement.getId())).isTrue();
    }

    @Test
    public void findAll() {
        assertThat(measurementRepository.findAll()).hasSize(2);
    }

    @Test
    public void getOne() {
        assertThat(measurementRepository.getOne(measurementRepository.findAll().get(0).getId())).isNotNull();
    }

    @Test
    public void count() {
        assertThat(measurementRepository.count()).isEqualTo(2);
    }
}
