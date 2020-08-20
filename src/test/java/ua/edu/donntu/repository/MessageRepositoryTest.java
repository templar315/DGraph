package ua.edu.donntu.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.donntu.domain.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MessageRepositoryTest extends BaseDomainTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void add() {
        messageRepository.saveAndFlush(Message.builder()
                .sendDate(new Date())
                .receiveDate(new Date())
                .saveDate(new Date())
                .transmissionTime(78L)
                .processingTime(12L)
                .filePath("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe.txt")
                .hash("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe")
                .sender("127.125.14.20")
                .build());
        assertThat(messageRepository.getByHash("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe"))
                .isNotNull();
        assertThat(messageRepository.count()).isEqualTo(3);
    }

    @Test
    public void update() {
        assertThat(messageRepository.count()).isEqualTo(2);
        Message message = messageRepository.getByHash("7c9c0ef1cba060e24088ff31fd2a4b8ede9890102d37130dbc8c922980fe82af");
        message.setHash("b7a623376aa76e50e0e8cb0e1c2e06151668187c28b620747df1f358e796309f");
        message.setFilePath("b7a623376aa76e50e0e8cb0e1c2e06151668187c28b620747df1f358e796309f.txt");
        message.setSender("127.125.14.21");
        messageRepository.saveAndFlush(message);
        assertThat(messageRepository
                .getByHash("b7a623376aa76e50e0e8cb0e1c2e06151668187c28b620747df1f358e796309f"))
                .isNotNull();
        assertThat(messageRepository.count()).isEqualTo(2);
    }

    @Test
    public void delete() {
        assertThat(messageRepository.count()).isEqualTo(2);
        messageRepository.delete(messageRepository
                .getByHash("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4cf825ca6b21744194f4caeb8"));
        assertThat(messageRepository.count()).isEqualTo(1);
    }

    @Test
    public void deleteById() {
        assertThat(messageRepository.count()).isEqualTo(2);
        messageRepository.deleteById(messageRepository
                .getByHash("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4cf825ca6b21744194f4caeb8")
                .getId());
        assertThat(messageRepository.count()).isEqualTo(1);
    }

    @Test
    public void deleteAll() {
        assertThat(messageRepository.count()).isEqualTo(2);
        messageRepository.deleteAll();
        assertThat(messageRepository.count()).isEqualTo(0);
    }

    @Test
    public void addAll() {
        assertThat(messageRepository.count()).isEqualTo(2);
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .sendDate(new Date())
                .receiveDate(new Date())
                .saveDate(new Date())
                .transmissionTime(78L)
                .processingTime(12L)
                .filePath("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe.txt")
                .hash("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe")
                .sender("127.125.14.20")
                .build());
        messages.add(Message.builder()
                .sendDate(new Date())
                .receiveDate(new Date())
                .saveDate(new Date())
                .transmissionTime(92L)
                .processingTime(15L)
                .filePath("707b33aa868149d6f9763b2bb2114e48158ebe93c0185945c27cf9df16cf2754.txt")
                .hash("707b33aa868149d6f9763b2bb2114e48158ebe93c0185945c27cf9df16cf2754")
                .sender("127.125.14.32")
                .build());
        messageRepository.saveAll(messages);
        assertThat(messageRepository.count()).isEqualTo(4);
        assertThat(messageRepository
                .getByHash("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe"))
                .isNotNull();
        assertThat(messageRepository
                .getByHash("707b33aa868149d6f9763b2bb2114e48158ebe93c0185945c27cf9df16cf2754"))
                .isNotNull();
    }

    @Test
    public void findById() {
        Message message = messageRepository.getByHash("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4cf825ca6b21744194f4caeb8");
        assertThat(messageRepository.findById(message.getId())
                .orElse(null))
                .isEqualTo(message);
    }

    @Test
    public void existsById() {
        Message message = messageRepository.getByHash("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4cf825ca6b21744194f4caeb8");
        assertThat(messageRepository.existsById(message.getId())).isTrue();
        assertThat(messageRepository.existsById(messageRepository.count() + 1)).isFalse();
    }

    @Test
    public void findAll() {
        assertThat(messageRepository.findAll()).hasSize(2);
    }

    @Test
    public void count() {
        assertThat(messageRepository.count()).isEqualTo(2);
    }

    @Test
    public void getOne() {
        assertThat(messageRepository.getOne(messageRepository
                        .getByHash("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4cf825ca6b21744194f4caeb8")
                        .getId())
                .getTransmissionTime())
                .isEqualTo(100L);
    }

    @Test
    public void getByHash() {
        Message message = messageRepository.getByHash("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4cf825ca6b21744194f4caeb8");
        assertThat(message).isNotNull();
        assertThat(message.getProcessingTime()).isEqualTo(10L);
        message = messageRepository.getByHash("9b19d0d2bb21a3aaa44ed660234b4af35e21f3b4c8225ca6b21744194f4caeb8");
        assertThat(message).isNull();
    }

    @Test
    public void getAllBySender() {
        List<Message> messages = messageRepository.getAllBySender("127.125.14.20");
        assertThat(messages).isNotEmpty();
        assertThat(messages.size()).isEqualTo(2);
        messages = messageRepository.getAllBySender("127.125.14.25");
        assertThat(messages).isEmpty();
    }
}
