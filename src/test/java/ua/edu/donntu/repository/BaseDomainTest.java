package ua.edu.donntu.repository;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.donntu.domain.Measurement;
import ua.edu.donntu.domain.MeasurementUnit;
import ua.edu.donntu.domain.Message;
import ua.edu.donntu.domain.Node;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public abstract class BaseDomainTest {

    @Autowired
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        Node node = Node.builder()
                .host("193.125.15.20")
                .port("8080")
                .build();
        Node node2 = Node.builder()
                .host("127.125.14.20")
                .port("8080")
                .build();

        Measurement measurement = Measurement.builder()
                .startDate(new Date())
                .size(1024 * 1024)
                .build();
        Measurement measurement2 = Measurement.builder()
                .startDate(new Date())
                .finishDate(new Date())
                .finished(true)
                .size(1024 * 1024)
                .build();

        MeasurementUnit measurementUnit = MeasurementUnit.builder()
                .node(node)
                .measurement(measurement)
                .hash("6ccc0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c815480fe82af")
                .transmissionTime(154)
                .processingTime(16)
                .build();
        MeasurementUnit measurementUnit2 = MeasurementUnit.builder()
                .node(node)
                .measurement(measurement)
                .hash("6ccc0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c815480fe82af")
                .transmissionTime(160)
                .processingTime(20)
                .build();
        MeasurementUnit measurementUnit3 = MeasurementUnit.builder()
                .node(node2)
                .measurement(measurement2)
                .hash("8k5k0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c815480fe82af")
                .transmissionTime(220)
                .processingTime(12)
                .build();
        MeasurementUnit measurementUnit4 = MeasurementUnit.builder()
                .node(node2)
                .measurement(measurement2)
                .hash("8k5k0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c815480fe82af")
                .transmissionTime(201)
                .processingTime(13)
                .build();

        List<MeasurementUnit> units = new ArrayList<>();
        List<MeasurementUnit> units2 = new ArrayList<>();
        units.add(measurementUnit);
        units.add(measurementUnit2);
        units2.add(measurementUnit3);
        units2.add(measurementUnit4);

        node.setMeasurementUnits(units);
        node2.setMeasurementUnits(units2);
        measurement.setMeasurementUnits(units);
        measurement2.setMeasurementUnits(units2);

        entityManager.persist(measurementUnit);
        entityManager.persist(measurementUnit2);
        entityManager.persist(measurementUnit3);
        entityManager.persist(measurementUnit4);

        entityManager.persist(Message.builder()
                .sendDate(new Date())
                .receiveDate(new Date())
                .saveDate(new Date())
                .transmissionTime(100L)
                .processingTime(10L)
                .size(1024 * 1024)
                .filePath("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4cf825ca6b21744194f4caeb8.txt")
                .hash("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4cf825ca6b21744194f4caeb8")
                .sender("127.125.14.20")
                .build());
        entityManager.persist(Message.builder()
                .sendDate(new Date())
                .receiveDate(new Date())
                .saveDate(new Date())
                .transmissionTime(89L)
                .processingTime(11L)
                .size(1024 * 1024)
                .filePath("7c9c0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c922980fe82af.txt")
                .hash("7c9c0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c922980fe82af")
                .sender("127.125.14.20")
                .build());
        entityManager.close();
    }
}
