package ua.edu.donntu.service;

import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.donntu.domain.Message;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.MessageInDTO;
import ua.edu.donntu.dto.MessageOutDTO;
import ua.edu.donntu.repository.MessageRepository;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.*;
import ua.edu.donntu.service.utils.PropagationThread;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.donntu.service.NodeService.NATIVE_HOST;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageService {

    private final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final NodeRepository nodeRepository;

    private final MessageRepository messageRepository;

    private final FileService fileService;

    private final NodeService nodeService;

    private final DropboxService dropboxService;

    protected Message fromDTO(MessageInDTO messageInDTO) {
        if (messageInDTO == null) {
            return null;
        } else {
            return Message.builder()
                    .sendDate(messageInDTO.getSendDate())
                    .build();
        }
    }

    protected MessageOutDTO toDTO(Message message) {
        if (message == null) {
            return null;
        } else {

            return MessageOutDTO.builder()
                    .id(message.getId())
                    .sendDate(message.getSendDate())
                    .receiveDate(message.getReceiveDate())
                    .saveDate(message.getSaveDate())
                    .hash(message.getHash())
                    .senderHost(message.getSender())
                    .recipientHost(NATIVE_HOST)
                    .build();
        }
    }

    @Transactional
    public MessageOutDTO save(MessageInDTO messageInDTO, MultipartFile messageFile, String senderHost) throws
                                                                                            FileSaveException,
                                                                                            FileDownloadException {
        log.debug("Request to save Message: {}", messageInDTO);

        Message message = fromDTO(messageInDTO);
        message.setReceiveDate(new Date());
        message.setSender(senderHost);
        StringBuilder filePath = new StringBuilder();
        String hash = "";

        try {
            String name = messageFile.getOriginalFilename();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = fileService.getHash(messageFile.getInputStream(), digest);
            Message messageByHash = messageRepository.getByHash(hash);
            if (messageByHash == null) {
                filePath.append("/")
                        .append(nodeService.getNativeNode().getHost())
                        .append("/")
                        .append(hash)
                        .append(name.substring(name.lastIndexOf(".")));
                dropboxService.upload(messageFile.getInputStream(), filePath.toString());


                message.setFilePath(filePath.toString());
                message.setSaveDate(new Date());
                message.setHash(hash);

                Message savedMessage = messageRepository.saveAndFlush(message);
                startPropagation(dropboxService.download(savedMessage.getFilePath()));

                return toDTO(savedMessage);
            } else {
                startPropagation(dropboxService.download(messageByHash.getFilePath()));
                return toDTO(messageByHash);
            }
        } catch (NoSuchAlgorithmException | IOException | NullPointerException exception) {
            log.error("Message file save error: ");
            exception.printStackTrace();
            throw new FileSaveException("Error while saving file");
        }
    }

    @Transactional
    public boolean delete(long id) throws FileDeleteException {
        log.debug("Request to delete Message with id: " + id);
        if (messageRepository.existsById(id)) {
            Message message = messageRepository.getOne(id);
            dropboxService.delete(message.getFilePath());
            messageRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public MessageOutDTO getOne(long id) {
        log.debug("Request to get Message with id: " + id);
        return toDTO(messageRepository.getOne(id));
    }

    public List<MessageOutDTO> getAll() {
        log.debug("Request to get all Messages");
        return messageRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MessageOutDTO> getAllMessagesBySender(String senderHost) {
        log.debug("Request to get all Messages with Sender host: " + senderHost);
        return messageRepository.getAllBySender(senderHost)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private void startPropagation(ByteArrayOutputStream fileStream) {
        Node nativeNode = nodeRepository.getNodeByNativeNodeIsTrue();
        for (Node node : nodeRepository.findAll()) {
            if (!node.isNativeNode()) {
                new PropagationThread(nativeNode.getHost(),
                                      node.getHost(),
                                      new ByteArrayInputStream(fileStream.toByteArray())).start();
            }
        }
    }
}
