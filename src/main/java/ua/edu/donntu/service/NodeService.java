package ua.edu.donntu.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.NodeInDTO;
import ua.edu.donntu.dto.NodeOutDTO;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.ObjectUniquenessException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;
import ua.edu.donntu.service.exceptions.NodeAlreadyExistException;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NodeService {

    private final Logger log = LoggerFactory.getLogger(NodeService.class);

    private final NodeRepository nodeRepository;

    public static String NATIVE_HOST;

    public static String NATIVE_PORT;

    private static final String IP_CHECK_HOST = "checkip.amazonaws.com";

    private Node fromInDTO(NodeInDTO nodeInDTO) {
        if (nodeInDTO == null) {
            return null;
        } else {
            return Node.builder()
                    .host(nodeInDTO.getHost())
                    .port(nodeInDTO.getPort())
                    .build();
        }
    }

    private NodeOutDTO toOutDTO(Node node) {
        if (node == null) {
            return null;
        } else {
            return NodeOutDTO.builder()
                    .id(node.getId())
                    .host(node.getHost())
                    .port(node.getPort())
                    .nativeNode(node.isNativeNode())
                    .build();
        }
    }

    @Transactional
    public NodeOutDTO save(NodeInDTO nodeInDTO) throws NodeAlreadyExistException {
        log.debug("Request to save Node: {}", nodeInDTO);

        if (nodeRepository.getByHost(nodeInDTO.getHost()) != null) {
            log.error("Node already exist");
            throw new NodeAlreadyExistException("Node already exist");
        }

        Node node = fromInDTO(nodeInDTO);

        if (isNativeNode(node)) {
            node.setNativeNode(nodeRepository.getNodeByNativeNodeIsTrue() == null);
        }

        return toOutDTO(nodeRepository.saveAndFlush(node));
    }

    @Transactional
    public NodeOutDTO update(Long id, NodeInDTO nodeInDTO) throws NodeDoesNotExistException, ObjectUniquenessException {
        log.debug("Request to update Node: {}", nodeInDTO);

        Node checkNode = nodeRepository.getOne(id);

        if (checkNode == null) {
            log.error("Node does not exist");
            throw new NodeDoesNotExistException("Node does not exist");
        }

        Node checkByHostNode = nodeRepository.getByHost(nodeInDTO.getHost());

        if ((checkByHostNode != null && checkNode != checkByHostNode)) {
            log.error("Node is not unique");
            throw new ObjectUniquenessException("Node is not unique");
        }

        return toOutDTO(nodeRepository.saveAndFlush(
                checkNode.toBuilder()
                    .host(nodeInDTO.getHost())
                    .build()));
    }

    @Transactional
    public boolean delete(long id) {
        log.debug("Request to delete Node with id: " + id);
        if (nodeRepository.existsById(id)) {
            nodeRepository.deleteById(id);
        }
        return true;
    }

    public NodeOutDTO getOne(long id) {
        log.debug("Request to get Node by id: " + id);
        return toOutDTO(nodeRepository.getOne(id));
    }

    public List<NodeOutDTO> getAll() {
        log.debug("Request to get all Nodes");
        return nodeRepository
                .findAll()
                .stream()
                .map(this::toOutDTO)
                .collect(Collectors.toList());
    }

    public NodeOutDTO getNativeNode() {
        log.debug("Request to get server Node data");
        return toOutDTO(nodeRepository.getNodeByNativeNodeIsTrue());
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initNativeNode() {
        log.debug("Request to init native Node");
        if (getNativeNode() == null) {
            nodeRepository.saveAndFlush(Node.builder()
                    .host(getHost())
                    .port(getPort())
                    .nativeNode(true)
                    .build());
        }
    }

    private boolean isNativeNode(Node node) {
        return node.getHost().equals(getHost());
    }

    private String getHost() {
        try {
            if (NATIVE_HOST == null) {
                URL nativeIp = new URL("http://" + IP_CHECK_HOST);
                BufferedReader in = new BufferedReader(new InputStreamReader(nativeIp.openStream()));
                NATIVE_HOST = in.readLine();
            }
        } catch (IOException exception) {
            log.error("Native host identification error: ", exception);
        }
        return NATIVE_HOST;
    }

    private String getPort() {
        try {
            if (NATIVE_PORT == null) {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(IP_CHECK_HOST, 80));
                NATIVE_PORT = String.valueOf(socket.getPort());
            }
        } catch (IOException exception) {
            log.error("Native port identification error: ", exception);
        }
        return NATIVE_PORT;
    }
}
