package ua.edu.donntu.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.donntu.domain.MeasurementUnit;
import ua.edu.donntu.domain.Node;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NodeRepositoryTest extends BaseDomainTest {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Test
    public void add() {
        nodeRepository.saveAndFlush(Node.builder()
                .host("185.150.21.47")
                .port("8080")
                .build());
        assertThat(nodeRepository.getByHost("185.150.21.47")).isNotNull();
        assertThat(nodeRepository.count()).isEqualTo(3);
    }

    @Test
    public void update() {
        assertThat(nodeRepository.count()).isEqualTo(2);
        Node node = nodeRepository.getByHost("127.125.14.20");
        node.setHost("127.125.14.21");
        nodeRepository.saveAndFlush(node);
        assertThat(nodeRepository.getByHost("127.125.14.21")).isNotNull();
        assertThat(nodeRepository.count()).isEqualTo(2);
    }

    @Test
    public void delete() {
        assertThat(nodeRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        Node node = nodeRepository.getByHost("127.125.14.20");
        node.getMeasurementUnits().clear();
        nodeRepository.delete(node);
        assertThat(nodeRepository.count()).isEqualTo(1);
        assertThat(measurementUnitRepository.count()).isEqualTo(2);
    }

    @Test
    public void deleteById() {
        assertThat(nodeRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        Node node = nodeRepository.getByHost("127.125.14.20");
        node.getMeasurementUnits().clear();
        nodeRepository.deleteById(node.getId());
        assertThat(nodeRepository.count()).isEqualTo(1);
        assertThat(measurementUnitRepository.count()).isEqualTo(2);
    }

    @Test
    public void deleteAll() {
        assertThat(nodeRepository.count()).isEqualTo(2);
        assertThat(measurementUnitRepository.count()).isEqualTo(4);
        nodeRepository.findAll().forEach(node -> node.getMeasurementUnits().clear());
        nodeRepository.deleteAll();
        assertThat(nodeRepository.count()).isEqualTo(0);
        assertThat(measurementUnitRepository.count()).isEqualTo(0);
    }

    @Test
    public void addAll() {
        assertThat(nodeRepository.count()).isEqualTo(2);
        List<Node> nodes = new ArrayList<>();
        nodes.add(Node.builder()
                .host("185.150.21.47")
                .port("8080")
                .build());
        nodes.add(Node.builder()
                .host("185.150.21.50")
                .port("8080")
                .build());
        nodeRepository.saveAll(nodes);
        assertThat(nodeRepository.count()).isEqualTo(4);
        assertThat(nodeRepository.getByHost("185.150.21.47")).isNotNull();
        assertThat(nodeRepository.getByHost("185.150.21.50")).isNotNull();
    }

    @Test
    public void findById() {
        Node node = nodeRepository.getByHost("127.125.14.20");
        assertThat(nodeRepository.findById(node.getId())
                .orElse(null))
                .isEqualTo(node);
    }

    @Test
    public void existsById() {
        Node node = nodeRepository.getByHost("127.125.14.20");
        assertThat(nodeRepository.existsById(node.getId())).isTrue();
        assertThat(nodeRepository.existsById(nodeRepository.count() + 1)).isFalse();
    }

    @Test
    public void findAll() {
        assertThat(nodeRepository.findAll()).hasSize(2);
    }

    @Test
    public void count() {
        assertThat(nodeRepository.count()).isEqualTo(2);
    }

    @Test
    public void getOne() {
        assertThat(nodeRepository.getOne(
                nodeRepository.getByHost("193.125.15.20").getId())
                .getHost())
                .isEqualTo("193.125.15.20");
    }

    @Test
    public void getByHost() {
        Node node = nodeRepository.getByHost("193.125.15.20");
        assertThat(node).isNotNull();
        node = nodeRepository.getByHost("193.125.15.35");
        assertThat(node).isNull();
    }
}
