package ua.edu.donntu.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.donntu.dto.MessageInDTO;
import ua.edu.donntu.dto.MessageOutDTO;
import ua.edu.donntu.rest.utils.TestUtil;
import ua.edu.donntu.service.*;

import java.util.Arrays;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
@ComponentScan
public class MessageControllerTest {

    private final String MESSAGES = "/messages";

    @MockBean
    private FileService fileService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private NodeService nodeService;

    @MockBean
    private MeasurementService measurementService;

    @MockBean
    private MeasurementUnitService measurementUnitService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Date sendDate = new Date(0);
    private final Date receiveDate = new Date(100);
    private final Date saveDate = new Date(112);

    private final MessageInDTO messageIn = MessageInDTO.builder()
            .sendDate(sendDate)
            .build();
    private final MessageOutDTO messageOut = MessageOutDTO.builder()
            .id(1L)
            .sendDate(sendDate)
            .receiveDate(receiveDate)
            .saveDate(saveDate)
            .transmissionTime(receiveDate.getTime()-sendDate.getTime())
            .processingTime(saveDate.getTime()-receiveDate.getTime())
            .size(1024 * 1024)
            .hash("4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe")
            .senderHost("127.125.14.20")
            .recipientHost("127.125.25.20")
            .build();
    private final MessageOutDTO messageOut2 = MessageOutDTO.builder()
            .sendDate(sendDate)
            .receiveDate(receiveDate)
            .saveDate(saveDate)
            .transmissionTime(receiveDate.getTime()-sendDate.getTime())
            .processingTime(saveDate.getTime()-receiveDate.getTime())
            .size(1024 * 1024)
            .hash("707b33aa868149d6f9763b2bb2114e48158ebe93c0185945c27cf9df16cf2754")
            .senderHost("127.125.14.32")
            .recipientHost("127.125.25.20")
            .build();

    @Test
    public void save() throws Exception {
        Mockito.when(messageService.save(
                Mockito.any(MessageInDTO.class),
                Mockito.any(MultipartFile.class),
                Mockito.anyString(),
                Mockito.any(Date.class)))
                .thenReturn(messageOut);
        mvc.perform(MockMvcRequestBuilders.multipart(MESSAGES)
                .file(new MockMultipartFile(
                        "message",
                        "",
                        String.valueOf(ContentType.APPLICATION_JSON),
                        TestUtil.convertObjectToJsonBytes(messageIn)))
                .file(new MockMultipartFile(
                        "file",
                        "test.txt",
                        String.valueOf(ContentType.MULTIPART_FORM_DATA),
                        "some text".getBytes())))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(messageOut)));
        Mockito.when(messageService.save(
                Mockito.any(MessageInDTO.class),
                Mockito.any(MultipartFile.class),
                Mockito.anyString(),
                Mockito.any(Date.class)))
                .thenReturn(null);
        mvc.perform(MockMvcRequestBuilders.multipart(MESSAGES)
                .file(new MockMultipartFile(
                        "message",
                        "",
                        String.valueOf(ContentType.APPLICATION_JSON),
                        TestUtil.convertObjectToJsonBytes(messageIn)))
                .file(new MockMultipartFile(
                        "file",
                        "test.txt",
                        String.valueOf(ContentType.MULTIPART_FORM_DATA),
                        "some text".getBytes())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete() throws Exception {
        Mockito.when(messageService.delete(messageOut.getId())).thenReturn(true);
        mvc.perform(MockMvcRequestBuilders.delete(MESSAGES + "/" + messageOut.getId()))
                .andExpect(status().isOk());
        Mockito.when(messageService.delete(messageOut.getId())).thenReturn(false);
        mvc.perform(MockMvcRequestBuilders.delete(MESSAGES + "/" + messageOut.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAll() throws Exception {
        Mockito.when(messageService.deleteAll()).thenReturn(true);
        mvc.perform(MockMvcRequestBuilders.delete(MESSAGES))
                .andExpect(status().isOk());
    }

    @Test
    public void getOne() throws Exception {
        Mockito.when(messageService.getOne(messageOut.getId())).thenReturn(messageOut);
        mvc.perform(MockMvcRequestBuilders.get(MESSAGES + "/" + messageOut.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(messageOut)));
        Mockito.when(messageService.getOne(messageOut.getId())).thenReturn(null);
        mvc.perform(MockMvcRequestBuilders.get(MESSAGES + "/" + messageOut.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAll() throws Exception {
        Mockito.when(messageService.getAll()).thenReturn(Arrays.asList(messageOut, messageOut2));
        mvc.perform(MockMvcRequestBuilders.get(MESSAGES))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Arrays.asList(messageOut, messageOut2))));
    }

    @Test
    public void getAllBySender() throws Exception {
        Mockito.when(messageService.getAllMessagesBySender("127.125.14.20")).thenReturn(Arrays.asList(messageOut));
        mvc.perform(MockMvcRequestBuilders.get(MESSAGES).param("sender", "127.125.14.20"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Arrays.asList(messageOut))));
    }
}
