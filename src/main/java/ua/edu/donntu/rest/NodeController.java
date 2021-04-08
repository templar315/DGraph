package ua.edu.donntu.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.donntu.dto.NodeInDTO;
import ua.edu.donntu.dto.NodeOutDTO;
import ua.edu.donntu.service.NodeService;
import ua.edu.donntu.service.exceptions.NodeAlreadyExistException;
import ua.edu.donntu.service.exceptions.NodeDoesNotExistException;
import ua.edu.donntu.service.exceptions.ObjectUniquenessException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/nodes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NodeController {

    private final NodeService nodeService;

    @PostMapping
    public ResponseEntity<NodeOutDTO> save(@RequestBody @Validated NodeInDTO nodeInDTO) throws
                                                                            NodeAlreadyExistException {
        log.debug("REST Request to save Node: {}", nodeInDTO);
        NodeOutDTO node = nodeService.save(nodeInDTO);
        if (node != null) return ResponseEntity.status(HttpStatus.CREATED).body(node);
        else return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<NodeOutDTO> update(@PathVariable Long id,
                                             @RequestBody @Validated NodeInDTO nodeInDTO) throws
                                                                            NodeDoesNotExistException,
                                                                            ObjectUniquenessException {
        log.debug("REST Request to update Node: {}", nodeInDTO);
        NodeOutDTO node = nodeService.update(id, nodeInDTO);
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
    public ResponseEntity<NodeOutDTO> getOne(@PathVariable long id) {
        log.debug("REST Request to get Node with id: " + id);
        NodeOutDTO node = nodeService.getOne(id);
        if (node != null) return ResponseEntity.ok(node);
        else return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<NodeOutDTO>> getAll() {
        log.debug("REST Request to get all Nodes");
        return ResponseEntity.ok(nodeService.getAll());
    }
}
