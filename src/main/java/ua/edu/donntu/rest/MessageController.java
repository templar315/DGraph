package ua.edu.donntu.rest;

import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageController {

    private final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageOutDTO> save(@RequestPart(name = "message") @Validated MessageInDTO messageInDTO,
                                              @RequestPart(name = "file") MultipartFile file) throws FileSaveException,
                                                                                                     FileDownloadException {
        log.debug("REST Request to save Message: {}", messageInDTO);
        MessageOutDTO message = messageService.save(messageInDTO, file);
        if(message != null) return ResponseEntity.status(HttpStatus.CREATED).body(message);
        else return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id) throws FileDeleteException {
        log.debug("REST Request to delete Message with id" + id);
        if(messageService.delete(id)) return ResponseEntity.ok().build();
        else return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageOutDTO> getOne(@PathVariable long id) {
        log.debug("REST Request to get Message with id" + id);
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
    public ResponseEntity<List<MessageOutDTO>> getAllBySender(@RequestParam long sender) {
        log.debug("REST Request to get all Messages with sender id: " + sender);
        List<MessageOutDTO> messages = messageService.getAllMessagesBySender(sender);
        return ResponseEntity.ok(messages);
    }

    @GetMapping(params = "recipient")
    public ResponseEntity<List<MessageOutDTO>> getAllByRecipient(@RequestParam long recipient) {
        log.debug("REST Request to get all Messages with recipient id: " + recipient);
        List<MessageOutDTO> messages = messageService.getAllMessagesByRecipient(recipient);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/init")
    public ResponseEntity<Date> init() {
        log.debug("REST Request to create initial Messages data");
        return ResponseEntity.ok(new Date());
    }
}
