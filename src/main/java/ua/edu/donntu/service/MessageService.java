package ua.edu.donntu.service;

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
import java.io.IOException;
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

    private static final String DIGEST = "SHA-256";

    protected Message fromDTO(MessageInDTO messageInDTO) {
        if (messageInDTO != null) {
            return Message.builder()
                    .sendDate(messageInDTO.getSendDate())
                    .build();
        }
        return null;
    }

    protected MessageOutDTO toDTO(Message message) {
        if (message != null) {
            return MessageOutDTO.builder()
                    .id(message.getId())
                    .sendDate(message.getSendDate())
                    .receiveDate(message.getReceiveDate())
                    .saveDate(message.getSaveDate())
                    .transmissionTime(message.getTransmissionTime())
                    .processingTime(message.getProcessingTime())
                    .hash(message.getHash())
                    .senderHost(message.getSender())
                    .recipientHost(NATIVE_HOST)
                    .build();
        }
        return null;
    }

    @Transactional
    public MessageOutDTO save(MessageInDTO messageInDTO, MultipartFile messageFile, String senderHost, Date receiveDate) throws
                                                                                                    FileSaveException,
                                                                                                    FileDownloadException,
                                                                                                    MessageDigestException,
                                                                                                    FileInputStreamException {
        log.debug("Request to save Message: {}", messageInDTO);

        Message message = fromDTO(messageInDTO);
        message.setReceiveDate(receiveDate);
        message.setSender(senderHost);

        StringBuilder filePath = new StringBuilder();
        String name = messageFile.getOriginalFilename();
        String hash = fileService.getHashByFile(messageFile, DIGEST);

        Message existMessage = messageRepository.getByHash(hash);

        if (existMessage == null && name != null) {
            filePath.append("/")
                    .append(nodeService.getNativeNode().getHost())
                    .append("/")
                    .append(hash)
                    .append(name.substring(name.lastIndexOf(".")));

            try {
                dropboxService.upload(messageFile.getInputStream(), filePath.toString());
            } catch (IOException exception) {
                log.error("File input stream error: ", exception);
                throw new FileInputStreamException("File input stream error");
            }

            message.setFilePath(filePath.toString());
            message.setSaveDate(new Date());
            message.setHash(hash);

            existMessage = messageRepository.saveAndFlush(calculateData(message));
            startPropagation(dropboxService.download(existMessage.getFilePath()), existMessage.getSender());
        }
        return toDTO(existMessage);
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

    @Transactional
    public boolean deleteAll() throws FileDeleteException {
        log.debug("Request to delete all Messages");
        dropboxService.delete("/" + nodeService.getNativeNode().getHost());
        messageRepository.deleteAll();
        return true;
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

    private void startPropagation(MultipartFile file, String senderHost) {
        Node nativeNode = nodeRepository.getNodeByNativeNodeIsTrue();
        for (Node node : nodeRepository.findAll()) {
            if (!node.isNativeNode() && !node.getHost().equals(senderHost)) {
                new PropagationThread(nativeNode.getHost(),
                                      node.getHost(),
                                      node.getPort(),
                                      file).start();
            }
        }
    }

    private Message calculateData(Message message) {
        if (message != null) {
            if (message.getReceiveDate() != null && message.getSendDate() != null) {
                message.setTransmissionTime(message.getReceiveDate().getTime() - message.getSendDate().getTime());
            }
            if (message.getSaveDate() != null && message.getReceiveDate() != null) {
                message.setProcessingTime(message.getSaveDate().getTime() - message.getReceiveDate().getTime());
            }
        }
        return message;
    }
}
