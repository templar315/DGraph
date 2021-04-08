package ua.edu.donntu.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.donntu.domain.Measurement;
import ua.edu.donntu.domain.MeasurementUnit;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MeasurementUnitRepositoryTest extends BaseDomainTest {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Test
    public void add() {
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        MeasurementUnit measurementUnit = measurementUnitRepository.saveAndFlush(MeasurementUnit.builder()
                .node(nodeRepository.findAll().get(0))
                .measurement(measurementRepository.findAll().get(0))
                .hash("6ccc0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c815480fe82af")
                .transmissionTime(172)
                .processingTime(18)
                .build());
        assertThat(measurementUnitRepository.getOne(measurementUnit.getId())).isNotNull();
        assertThat(measurementUnitRepository.count()).isEqualTo(5);
    }

    @Test
    public void update() {
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        MeasurementUnit measurementUnit = measurementUnitRepository.findAll().get(0);
        measurementUnit.setHash("66660ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c815480fe82af");
        measurementUnit = measurementUnitRepository.saveAndFlush(measurementUnit);
        assertThat(measurementUnit.getHash()).isEqualTo("66660ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c815480fe82af");
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
    }

    @Test
    public void delete() {
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(nodeRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        MeasurementUnit measurementUnit = measurementUnitRepository.findAll().get(0);
        measurementUnitRepository.delete(measurementUnit);
        assertThat(measurementUnitRepository.count()).isEqualTo(3);
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(nodeRepository.count()).isEqualTo(2);
    }

    @Test
    public void deleteById() {
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(nodeRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        MeasurementUnit measurementUnit = measurementUnitRepository.findAll().get(0);
        measurementUnitRepository.deleteById(measurementUnit.getId());
        assertThat(measurementUnitRepository.count()).isEqualTo(3);
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(nodeRepository.count()).isEqualTo(2);
    }

    @Test
    public void deleteAll() {
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(nodeRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        measurementUnitRepository.deleteAll();
        assertThat(measurementUnitRepository.count()).isEqualTo(0);
        assertThat(measurementRepository.count()).isEqualTo(2);
        assertThat(nodeRepository.count()).isEqualTo(2);
    }

    @Test
    public void findById() {
        MeasurementUnit measurementUnit = measurementUnitRepository.findAll().get(0);
        assertThat(measurementUnitRepository.findById(measurementUnit.getId())
                .orElse(null))
                .isEqualTo(measurementUnit);
    }

    @Test
    public void existsById() {
        MeasurementUnit measurementUnit = measurementUnitRepository.findAll().get(0);
        assertThat(measurementUnitRepository.existsById(measurementUnit.getId())).isTrue();
    }

    @Test
    public void findAll() {
        assertThat(measurementUnitRepository.findAll()).hasSize(4);
    }

    @Test
    public void getOne() {
        assertThat(measurementUnitRepository.getOne(measurementUnitRepository.findAll().get(0).getId())).isNotNull();
    }

    @Test
    public void count() {
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
    }
}
