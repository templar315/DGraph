package ua.edu.donntu.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.NodeInDTO;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.NodeAlreadyExistException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;
import ua.edu.donntu.service.exceptions.ObjectUniquenessException;

import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.net.URL;
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
            .nativeNode(true)
            .build();
    private final Node nodeReturn2 = Node.builder()
            .id(4L)
            .host("127.125.14.55")
            .port("8080")
            .nativeNode(false)
            .build();

    @Test
    public void save() throws Exception {
        PowerMockito.when(nodeRepository.getByHost("127.125.14.22")).thenReturn(null);
        PowerMockito.when(nodeRepository, "saveAndFlush", Mockito.any(Node.class)).thenReturn(nodeReturn);
        PowerMockito.stub(MemberMatcher.method(NodeService.class, "isNativeNode")).toReturn(true);
        PowerMockito.when(nodeRepository.getNodeByNativeNodeIsTrue()).thenReturn(null);
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
    public void saveNativeNodeWithoutSavedNativeNode() throws Exception {
        PowerMockito.when(nodeRepository.getByHost("127.125.14.22")).thenReturn(null);
        PowerMockito.stub(MemberMatcher.method(NodeService.class, "isNativeNode")).toReturn(true);
        PowerMockito.when(nodeRepository.getNodeByNativeNodeIsTrue()).thenReturn(null);
        nodeReturn.setNativeNode(true);
        PowerMockito.when(nodeRepository, "saveAndFlush", Mockito.any(Node.class)).thenReturn(nodeReturn);
        assertThat(nodeService.save(nodeIn).isNativeNode()).isEqualTo(true);
    }

    @Test
    public void update() throws Exception {
        PowerMockito.when(nodeRepository.getOne(3L)).thenReturn(nodeReturn);
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
        PowerMockito.when(nodeRepository.existsById(1L)).thenReturn(true);
        assertThat(nodeService.delete(1L)).isTrue();
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

    @Test
    public void getNativeNode() {
        PowerMockito.when(nodeRepository.getNodeByNativeNodeIsTrue()).thenReturn(nodeReturn);
        assertThat(nodeService.getNativeNode().isNativeNode()).isEqualTo(true);
    }

    @Test
    public void initNativeNode() throws Exception {
        PowerMockito.stub(MemberMatcher.method(NodeService.class, "getNativeNode")).toReturn(null);
        PowerMockito.stub(MemberMatcher.method(NodeService.class, "getHost")).toReturn("127.125.14.22");
        PowerMockito.stub(MemberMatcher.method(NodeService.class, "getPort")).toReturn("8080");
        PowerMockito.when(nodeRepository, "saveAndFlush", Mockito.any(Node.class)).thenReturn(nodeReturn);
        assertThat(nodeService.initNativeNode()).isEqualTo(nodeReturn);
    }

    @Test
    public void isNativeNode() throws Exception {
        PowerMockito.stub(MemberMatcher.method(NodeService.class, "getHost")).toReturn("127.125.14.22");
        assertThat((boolean) Whitebox.invokeMethod(nodeService, "isNativeNode", nodeReturn)).isTrue();
        assertThat((boolean) Whitebox.invokeMethod(nodeService, "isNativeNode", nodeReturn2)).isFalse();
    }

    @Test
    public void getHost() throws Exception {
        URL url = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(Mockito.anyString()).thenReturn(url);
        PowerMockito.when(url.openStream()).thenReturn(new ByteArrayInputStream("127.125.14.22".getBytes()));
        assertThat((String) Whitebox.invokeMethod(nodeService, "getHost")).isEqualTo("127.125.14.22");
    }

    @Test
    public void getPort() throws Exception {
        Socket socket = PowerMockito.mock(Socket.class);
        PowerMockito.whenNew(Socket.class).withNoArguments().thenReturn(socket);
        PowerMockito.when(socket.getLocalPort()).thenReturn(8080);
        assertThat((String) Whitebox.invokeMethod(nodeService, "getPort")).isEqualTo("8080");
    }
}
