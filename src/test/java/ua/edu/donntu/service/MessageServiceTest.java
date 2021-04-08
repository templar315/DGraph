package ua.edu.donntu.service;

import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.donntu.domain.Message;
import ua.edu.donntu.domain.Node;
import ua.edu.donntu.dto.MessageInDTO;
import ua.edu.donntu.repository.MessageRepository;
import ua.edu.donntu.repository.NodeRepository;
import ua.edu.donntu.service.exceptions.*;
import ua.edu.donntu.service.utils.PropagationThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        MessageRepository.class,
        NodeRepository.class,
        MessageService.class,
        FileService.class,
        PropagationThread.class})
public class MessageServiceTest {

    private final MessageRepository messageRepository = PowerMockito.mock(MessageRepository.class);
    private final NodeRepository nodeRepository = PowerMockito.mock(NodeRepository.class);
    private final FileService fileService = PowerMockito.mock(FileService.class);
    private final PropagationThread propagationThread = PowerMockito.mock(PropagationThread.class);
    private final MessageService messageService = PowerMockito.spy(
            new MessageService(nodeRepository, messageRepository, fileService));

    private final MessageInDTO messageIn = MessageInDTO.builder()
            .sendDate(new Date())
            .build();
    private final Message messageOut = Message.builder()
            .id(1L)
            .sendDate(new Date())
            .receiveDate(new Date())
            .saveDate(new Date())
            .transmissionTime(125L)
            .processingTime(10L)
            .filePath("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe.txt")
            .hash("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe")
            .sender("127.125.14.20")
            .build();
    private final Message messageOut2 = Message.builder()
            .sendDate(new Date())
            .receiveDate(new Date())
            .saveDate(new Date())
            .transmissionTime(92L)
            .processingTime(15L)
            .filePath("707b33aa868149d6f9763b2bb2114e48158ebe93c0185945c27cf9df16cf2754.txt")
            .hash("707b33aa868149d6f9763b2bb2114e48158ebe93c0185945c27cf9df16cf2754")
            .sender("127.125.14.32")
            .build();
    private final MultipartFile multipartFile = new MockMultipartFile(
            "newFile",
            "newFile.txt",
            String.valueOf(ContentType.MULTIPART_FORM_DATA),
            "file".getBytes());
    private final Node nodeReturn = Node.builder()
            .id(3L)
            .host("127.125.14.22")
            .port("8080")
            .build();
    private final Node nodeReturn2 = Node.builder()
            .id(4L)
            .host("127.125.14.55")
            .port("8080")
            .build();
    private final Node nodeReturn3 = Node.builder()
            .id(5L)
            .host("185.156.14.78")
            .port("8080")
            .build();

    @Test
    public void save() throws FileInputStreamException, FileSaveException, MessageDigestException {
        PowerMockito.when(fileService.getHash(Mockito.any(byte[].class), Mockito.anyString()))
                .thenReturn("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe");
        PowerMockito.when(messageRepository.getByHash("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe"))
                .thenReturn(null);
        PowerMockito.when(messageRepository.saveAndFlush(Mockito.any(Message.class)))
                .thenReturn(messageOut);
        PowerMockito.stub(MemberMatcher.method(MessageService.class, "calculateData")).toReturn(messageOut);
        assertThat(messageService.save(messageIn, multipartFile, "127.125.14.20", new Date()).getSenderHost())
                .isEqualTo("127.125.14.20");
    }

    @Test
    public void saveIfMessageExist() throws MessageDigestException, FileInputStreamException, FileSaveException {
        PowerMockito.when(fileService.getHash(Mockito.any(byte[].class), Mockito.anyString()))
                .thenReturn("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe");
        PowerMockito.when(messageRepository.getByHash("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe"))
                .thenReturn(messageOut);
        assertThat(messageService.save(messageIn, multipartFile, "127.125.14.55", new Date()).getSenderHost())
                .isEqualTo("127.125.14.20");
    }

    @Test
    public void delete() {
        PowerMockito.when(messageRepository.existsById(1L)).thenReturn(true);
        PowerMockito.when(messageRepository.getOne(1L)).thenReturn(messageOut);
        PowerMockito.when(fileService.deleteFile(Mockito.anyString())).thenReturn(true);
        assertThat(messageService.delete(1L)).isTrue();
    }

    @Test
    public void deleteNotExistMessage() {
        PowerMockito.when(messageRepository.existsById(1L)).thenReturn(false);
        assertThat(messageService.delete(1L)).isFalse();
    }

    @Test
    public void deleteAll() {
        assertThat(messageService.deleteAll()).isTrue();
    }

    @Test
    public void getOne() {
        PowerMockito.when(messageRepository.getOne(1L)).thenReturn(messageOut);
        assertThat(messageService.getOne(1L).getSenderHost()).isEqualTo("127.125.14.20");
    }

    @Test
    public void getAll() {
        List<Message> messages = new ArrayList<>(Arrays.asList(messageOut, messageOut2));
        PowerMockito.when(messageRepository.findAll()).thenReturn(messages);
        assertThat(messageService.getAll().size()).isEqualTo(2);
    }

    @Test
    public void getAllMessagesBySender() {
        List<Message> messages = new ArrayList<>(Arrays.asList(messageOut));
        PowerMockito.when(messageRepository.getAllBySender("127.125.14.20")).thenReturn(messages);
        assertThat(messageService.getAllMessagesBySender("127.125.14.20").size()).isEqualTo(1);
    }

    @Test
    public void startPropagation() throws Exception {
        List<Node> nodes = new ArrayList<>(Arrays.asList(nodeReturn, nodeReturn2, nodeReturn3));
        PowerMockito.when(nodeRepository.findAll()).thenReturn(nodes);
        PowerMockito.whenNew(PropagationThread.class).withAnyArguments().thenReturn(propagationThread);
        Whitebox.invokeMethod(
                messageService,
                "startPropagation",
                multipartFile.getOriginalFilename(),
                multipartFile.getBytes());
        PowerMockito.verifyNew(PropagationThread.class).withArguments(
                "185.156.14.78",
                "8080",
                multipartFile.getOriginalFilename(),
                multipartFile.getBytes());
    }

    @Test
    public void calculateData() throws Exception {
        messageOut.setSendDate(new Date(0));
        messageOut.setReceiveDate(new Date(100));
        messageOut.setSaveDate(new Date(112));
        messageOut.setProcessingTime(0L);
        messageOut.setTransmissionTime(0L);
        Message message = Whitebox.invokeMethod(messageService, "calculateData", messageOut);
        assertThat(message.getTransmissionTime()).isEqualTo(100);
        assertThat(message.getProcessingTime()).isEqualTo(12);
        message = Whitebox.invokeMethod(messageService, "calculateData", null);
        assertThat(message).isNull();
    }
}
