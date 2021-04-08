package ua.edu.donntu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.NodeInDTO;
import ua.edu.donntu.dto.NodeOutDTO;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.ObjectUniquenessException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;
import ua.edu.donntu.service.exceptions.NodeAlreadyExistException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NodeService {

    private final NodeRepository nodeRepository;

    private Node fromInDTO(NodeInDTO nodeInDTO) {
        if (nodeInDTO == null) {
            return null;
        }
        return Node.builder()
                .host(nodeInDTO.getHost())
                .port(nodeInDTO.getPort())
                .build();
    }

    private NodeOutDTO toOutDTO(Node node) {
        if (node == null) {
            return null;
        }
        return NodeOutDTO.builder()
                .id(node.getId())
                .host(node.getHost())
                .port(node.getPort())
                .build();
    }

    @Transactional
    public NodeOutDTO save(NodeInDTO nodeInDTO) throws NodeAlreadyExistException {
        log.debug("Request to save Node: {}", nodeInDTO);

        if (nodeRepository.getByHost(nodeInDTO.getHost()) != null) {
            log.error("Node already exist");
            throw new NodeAlreadyExistException("Node already exist");
        }

        return toOutDTO(nodeRepository.saveAndFlush(fromInDTO(nodeInDTO)));
    }

    @Transactional
    public NodeOutDTO update(Long id, NodeInDTO nodeInDTO) throws NodeDoesNotExistException, ObjectUniquenessException {
        log.debug("Request to update Node: {}", nodeInDTO);

        if (!nodeRepository.existsById(id)) {
            log.error("Node does not exist");
            throw new NodeDoesNotExistException("Node does not exist");
        }

        Node checkNode = nodeRepository.getOne(id);
        Node checkByHostNode = nodeRepository.getByHost(nodeInDTO.getHost());

        if ((checkByHostNode != null && checkNode != checkByHostNode)) {
            log.error("Node is not unique");
            throw new ObjectUniquenessException("Node is not unique");
        }

        return toOutDTO(nodeRepository.saveAndFlush(
                checkNode.toBuilder()
                        .host(nodeInDTO.getHost())
                        .port(nodeInDTO.getPort())
                        .build()));
    }

    @Transactional
    public boolean delete(long id) {
        log.debug("Request to delete Node with id: " + id);
        if (nodeRepository.existsById(id)) {
            Node node = nodeRepository.getOne(id);
            node.getMeasurementUnits().clear();
            nodeRepository.delete(node);
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
}
