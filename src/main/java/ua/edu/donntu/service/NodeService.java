package ua.edu.donntu.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ua.edu.donntu.domain.Message;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.NodeDTO;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.EmptyNullableFieldException;
import ua.edu.donntu.service.exceptions.ObjectUniquenessException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;
import ua.edu.donntu.service.exceptions.NodeAlreadyExistException;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NodeService {

    private final Logger log = LoggerFactory.getLogger(NodeService.class);

    private final NodeRepository nodeRepository;

    private Node fromDTO(NodeDTO nodeDTO) {
        if (nodeDTO == null) {
            return null;
        } else {
            return Node.builder()
                    .id(nodeDTO.getId())
                    .host(nodeDTO.getHost())
                    .nativeNode(nodeDTO.isNativeNode())
                    .build();
        }
    }

    private NodeDTO toDTO(Node node) {
        if (node == null) {
            return null;
        } else {
            return NodeDTO.builder()
                    .id(node.getId())
                    .host(node.getHost())
                    .nativeNode(node.isNativeNode())
                    .sentMessages(node.getSentMessages() == null
                            ? new ArrayList<>()
                            : node
                            .getSentMessages()
                            .stream()
                            .map(Message::getId)
                            .collect(Collectors.toList()))
                    .receivedMessages(node.getReceivedMessages() == null
                            ? new ArrayList<>()
                            : node
                            .getReceivedMessages()
                            .stream()
                            .map(Message::getId)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Transactional
    public NodeDTO save(NodeDTO nodeDTO) throws NodeAlreadyExistException, EmptyNullableFieldException {
        log.debug("Request to save Node: {}", nodeDTO);

        if (!checkNonNullableFields(nodeDTO)) {
            log.error("Non nullable field is empty");
            throw new EmptyNullableFieldException("Non nullable field is empty");
        }

        if (nodeRepository.existsById(nodeDTO.getId())
                || nodeRepository.getByHost(nodeDTO.getHost()) != null) {
            log.error("Node already exist");
            throw new NodeAlreadyExistException("Node already exist");
        }

        if (isNativeNode(nodeDTO)) {
            nodeDTO.setNativeNode(nodeRepository.getNodeByNativeNodeIsTrue() == null);
        }

        return toDTO(nodeRepository.saveAndFlush(fromDTO(nodeDTO)));
    }

    @Transactional
    public NodeDTO update(NodeDTO nodeDTO) throws NodeDoesNotExistException, EmptyNullableFieldException, ObjectUniquenessException {
        log.debug("Request to update Node: {}", nodeDTO);

        if (!checkNonNullableFields(nodeDTO)) {
            log.error("Non nullable field is empty");
            throw new EmptyNullableFieldException("Non nullable field is empty");
        }

        Node checkNode = nodeRepository.getOne(nodeDTO.getId());

        if (checkNode == null) {
            log.error("Node does not exist");
            throw new NodeDoesNotExistException("Node does not exist");
        }

        Node checkByHostNode = nodeRepository.getByHost(nodeDTO.getHost());

        if ((checkByHostNode != null && checkNode != checkByHostNode)) {
            log.error("Node is not unique");
            throw new ObjectUniquenessException("Node is not unique");
        }

        return toDTO(nodeRepository.saveAndFlush(
                checkNode.toBuilder()
                    .host(nodeDTO.getHost())
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

    public NodeDTO getOne(long id) {
        log.debug("Request to get Node by id: " + id);
        return toDTO(nodeRepository.getOne(id));
    }

    public List<NodeDTO> getAll() {
        log.debug("Request to get all Nodes");
        return nodeRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public NodeDTO getNativeNode() {
        log.debug("Request to get server Node data");
        return toDTO(nodeRepository.getNodeByNativeNodeIsTrue());
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initNativeNode() {
        log.debug("Request to init native Node");
        if (getNativeNode() == null) {
            nodeRepository.saveAndFlush(Node.builder()
                    .host(getHost())
                    .build());
        }
    }

    private boolean checkNonNullableFields(NodeDTO nodeDTO) {
        return nodeDTO.getHost() != null
                && !nodeDTO.getHost().isEmpty();
    }

    private boolean isNativeNode(NodeDTO nodeDTO) {
        return nodeDTO.getHost().equals(getHost());
    }

    private String getHost() {
        try {
            URL nativeIp = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(nativeIp.openStream()));
            return in.readLine();
        } catch (IOException ioException) {
            log.error(ioException.getMessage());
        }
        return null;
    }
}
