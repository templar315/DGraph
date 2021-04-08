package ua.edu.donntu.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.donntu.dto.MessageInDTO;
import ua.edu.donntu.dto.MessageOutDTO;
import ua.edu.donntu.service.MessageService;
import ua.edu.donntu.service.exceptions.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageOutDTO> save(@RequestPart(name = "message") @Validated MessageInDTO messageInDTO,
                                              @RequestPart(name = "file") MultipartFile file,
                                              @Context HttpServletRequest requestContext) throws FileSaveException,
                                                                                                 MessageDigestException,
                                                                                                 FileInputStreamException {
        log.debug("REST Request to save Message: {}", messageInDTO);
        MessageOutDTO message = messageService.save(messageInDTO,
                                                    file,
                                                    requestContext.getRemoteHost(),
                                                    new Date());
        if(message != null) return ResponseEntity.status(HttpStatus.CREATED).body(message);
        else return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        log.debug("REST Request to delete Message with id: " + id);
        if(messageService.delete(id)) return ResponseEntity.ok().build();
        else return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        log.debug("REST Request to delete all Messages");
        messageService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageOutDTO> getOne(@PathVariable long id) {
        log.debug("REST Request to get Message with id: " + id);
        MessageOutDTO message = messageService.getOne(id);
        if(message != null) return ResponseEntity.ok(message);
        else return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<MessageOutDTO>> getAll() {
        log.debug("REST Request to get all Messages");
        return ResponseEntity.ok(messageService.getAll());
    }

    @GetMapping(params = "sender")
    public ResponseEntity<List<MessageOutDTO>> getAllBySender(@RequestParam String sender) {
        log.debug("REST Request to get all Messages with sender host: " + sender);
        List<MessageOutDTO> messages = messageService.getAllMessagesBySender(sender);
        return ResponseEntity.ok(messages);
    }
}
