package ua.edu.donntu.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.NodeInDTO;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.NodeAlreadyExistException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;
import ua.edu.donntu.service.exceptions.ObjectUniquenessException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NodeRepository.class, NodeService.class})
public class NodeServiceTest {

    private final NodeRepository nodeRepository = PowerMockito.mock(NodeRepository.class);
    private final NodeService nodeService = PowerMockito.spy(new NodeService(nodeRepository));

    private final NodeInDTO nodeIn = NodeInDTO.builder()
            .host("127.125.14.22")
            .port("8080")
            .build();
    private final NodeInDTO nodeIn2 = NodeInDTO.builder()
            .host("127.125.14.55")
            .port("8080")
            .build();
    private final Node nodeReturn = Node.builder()
            .id(3L)
            .host("127.125.14.22")
            .port("8080")
            .measurementUnits(new ArrayList<>())
            .build();
    private final Node nodeReturn2 = Node.builder()
            .id(4L)
            .host("127.125.14.55")
            .port("8080")
            .measurementUnits(new ArrayList<>())
            .build();

    @Test
    public void save() throws Exception {
        PowerMockito.when(nodeRepository.getByHost("127.125.14.22")).thenReturn(null);
        PowerMockito.when(nodeRepository, "saveAndFlush", Mockito.any(Node.class)).thenReturn(nodeReturn);
        assertThat(nodeService.save(nodeIn).getHost()).isEqualTo("127.125.14.22");
    }

    @Test
    public void saveWithAlreadyExistNode() {
        PowerMockito.when(nodeRepository.getByHost("127.125.14.22")).thenReturn(nodeReturn);
        try {
            nodeService.save(nodeIn);
            assert false;
        } catch (NodeAlreadyExistException exception) {
            assert true;
        }
    }

    @Test
    public void update() throws Exception {
        PowerMockito.when(nodeRepository.getOne(3L)).thenReturn(nodeReturn);
        PowerMockito.when(nodeRepository.existsById(3L)).thenReturn(true);
        PowerMockito.when(nodeRepository.getByHost("127.125.14.55")).thenReturn(null);
        nodeReturn.setHost("127.125.14.55");
        PowerMockito.when(nodeRepository, "saveAndFlush", Mockito.any(Node.class)).thenReturn(nodeReturn);
        assertThat(nodeService.update(3L, nodeIn2).getHost()).isEqualTo("127.125.14.55");
    }

    @Test
    public void updateNodeWhichNotExist() throws ObjectUniquenessException {
        PowerMockito.when(nodeRepository.getOne(4L)).thenReturn(null);
        try {
            nodeService.update(4L, nodeIn2);
            assert false;
        } catch (NodeDoesNotExistException exception) {
            assert true;
        }
    }

    @Test
    public void updateWithNotUniqueNode() throws NodeDoesNotExistException {
        PowerMockito.when(nodeRepository.getOne(3L)).thenReturn(nodeReturn);
        PowerMockito.when(nodeRepository.existsById(3L)).thenReturn(true);
        PowerMockito.when(nodeRepository.getByHost("127.125.14.55")).thenReturn(nodeReturn2);
        try {
            nodeService.update(3L, nodeIn2);
            assert false;
        } catch (ObjectUniquenessException exception) {
            assert true;
        }
    }

    @Test
    public void delete() {
        PowerMockito.when(nodeRepository.existsById(3L)).thenReturn(true);
        PowerMockito.when(nodeRepository.getOne(3L)).thenReturn(nodeReturn);
        assertThat(nodeService.delete(3L)).isTrue();
    }

    @Test
    public void getOne() {
        PowerMockito.when(nodeRepository.getOne(1L)).thenReturn(nodeReturn);
        assertThat(nodeService.getOne(1L).getHost()).isEqualTo("127.125.14.22");
    }

    @Test
    public void getAll() {
        PowerMockito.when(nodeRepository.findAll()).thenReturn(Arrays.asList(nodeReturn, nodeReturn2));
        assertThat(nodeService.getAll().size()).isEqualTo(2);
    }
}
