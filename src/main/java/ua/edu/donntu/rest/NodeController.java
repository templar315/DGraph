package ua.edu.donntu.rest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.donntu.dto.NodeDTO;
import ua.edu.donntu.service.NodeService;
import ua.edu.donntu.service.exceptions.EmptyNullableFieldException;
import ua.edu.donntu.service.exceptions.NodeAlreadyExistException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;
import ua.edu.donntu.service.exceptions.ObjectUniquenessException;

import java.util.List;

@RestController
@RequestMapping("/nodes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NodeController {

    private final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final NodeService nodeService;

    @PostMapping
    public ResponseEntity<NodeDTO> save(@RequestBody NodeDTO nodeDTO) throws EmptyNullableFieldException,
                                                                             NodeAlreadyExistException {
        log.debug("REST Request to save Node: {}", nodeDTO);
        NodeDTO node = nodeService.save(nodeDTO);
        if (node != null) return ResponseEntity.status(HttpStatus.CREATED).body(node);
        else return ResponseEntity.badRequest().build();
    }

    @PutMapping
    public ResponseEntity<NodeDTO> update(@RequestBody NodeDTO nodeDTO) throws EmptyNullableFieldException,
                                                                               NodeDoesNotExistException,
                                                                               ObjectUniquenessException {
        log.debug("REST Request to update Node: {}", nodeDTO);
        NodeDTO node = nodeService.update(nodeDTO);
        if(node != null) return ResponseEntity.ok(node);
        else return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        log.debug("REST Request to delete Node with id: " + id);
        if (nodeService.delete(id)) return ResponseEntity.ok().build();
        else return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeDTO> getOne(@PathVariable long id) {
        log.debug("REST Request to get Node with id: " + id);
        NodeDTO node = nodeService.getOne(id);
        if (node != null) return ResponseEntity.ok(node);
        else return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<NodeDTO>> getAll() {
        log.debug("REST Request to get all Nodes");
        return ResponseEntity.ok(nodeService.getAll());
    }

    @GetMapping("/native")
    public ResponseEntity<NodeDTO> getNative() {
        log.debug("REST Request to get Node data");
        NodeDTO node = nodeService.getNativeNode();
        if(node != null) return ResponseEntity.ok(node);
        else return ResponseEntity.notFound().build();
    }
}
