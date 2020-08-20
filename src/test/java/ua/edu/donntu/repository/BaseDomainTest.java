package ua.edu.donntu.repository;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.edu.donntu.domain.Message;
import ua.edu.donntu.domain.Node;

import javax.persistence.EntityManager;
import java.util.Date;

@RunWith(SpringRunner.class)
@DataJpaTest
public abstract class BaseDomainTest {

    @Autowired
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        entityManager.persist(Node.builder()
                .host("193.125.15.20")
                .port("8080")
                .nativeNode(true)
                .build());
        entityManager.persist(Node.builder()
                .host("127.125.14.20")
                .port("8080")
                .nativeNode(false)
                .build());
        entityManager.persist(Message.builder()
                .sendDate(new Date())
                .receiveDate(new Date())
                .saveDate(new Date())
                .transmissionTime(100L)
                .processingTime(10L)
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
                .filePath("7c9c0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c922980fe82af.txt")
                .hash("7c9c0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c922980fe82af")
                .sender("127.125.14.20")
                .build());
    }
}
