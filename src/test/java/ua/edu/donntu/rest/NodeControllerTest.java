package ua.edu.donntu.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.edu.donntu.dto.NodeInDTO;
import ua.edu.donntu.dto.NodeOutDTO;
import ua.edu.donntu.service.FileService;
import ua.edu.donntu.service.MessageService;
import ua.edu.donntu.service.NodeService;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NodeController.class)
@ComponentScan
public class NodeControllerTest {

    private final String NODES = "/nodes";

    @MockBean
    private FileService fileService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private NodeService nodeService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final NodeInDTO nodeInDTO = NodeInDTO.builder()
            .host("127.125.14.22")
            .port("8080")
            .build();
    private final NodeOutDTO nodeOutDTO = NodeOutDTO.builder()
            .id(1L)
            .host("127.125.14.22")
            .port("8080")
            .nativeNode(true)
            .build();
    private final NodeOutDTO nodeOutDTO2 = NodeOutDTO.builder()
            .id(2L)
            .host("127.125.14.55")
            .port("8080")
            .nativeNode(false)
            .build();

    @Test
    public void save() throws Exception {
        Mockito.when(nodeService.save(nodeInDTO)).thenReturn(nodeOutDTO);
        mvc.perform(post(NODES)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nodeInDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(nodeOutDTO)));
        Mockito.when(nodeService.save(nodeInDTO)).thenReturn(null);
        mvc.perform(post(NODES)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nodeInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update() throws Exception {
        Mockito.when(nodeService.update(nodeOutDTO.getId(), nodeInDTO)).thenReturn(nodeOutDTO);
        mvc.perform(put(NODES + "/" + nodeOutDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nodeInDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(nodeOutDTO)));
        Mockito.when(nodeService.update(nodeOutDTO.getId(), nodeInDTO)).thenReturn(null);
        mvc.perform(put(NODES + "/" + nodeOutDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nodeInDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete() throws Exception {
        Mockito.when(nodeService.delete(nodeOutDTO.getId())).thenReturn(true);
        mvc.perform(MockMvcRequestBuilders.delete(NODES + "/" + nodeOutDTO.getId()))
                .andExpect(status().isOk());
        Mockito.when(nodeService.delete(nodeOutDTO.getId())).thenReturn(false);
        mvc.perform(MockMvcRequestBuilders.delete(NODES + "/" + nodeOutDTO.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOne() throws Exception {
        Mockito.when(nodeService.getOne(nodeOutDTO.getId())).thenReturn(nodeOutDTO);
        mvc.perform(MockMvcRequestBuilders.get(NODES + "/" + nodeOutDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(nodeOutDTO)));
        Mockito.when(nodeService.getOne(nodeOutDTO.getId())).thenReturn(null);
        mvc.perform(MockMvcRequestBuilders.get(NODES + "/" + nodeOutDTO.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAll() throws Exception {
        Mockito.when(nodeService.getAll()).thenReturn(Arrays.asList(nodeOutDTO, nodeOutDTO2));
        mvc.perform(MockMvcRequestBuilders.get(NODES))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Arrays.asList(nodeOutDTO, nodeOutDTO2))));
    }

    @Test
    public void getNative() throws Exception {
        Mockito.when(nodeService.getNativeNode()).thenReturn(nodeOutDTO);
        mvc.perform(MockMvcRequestBuilders.get(NODES + "/native"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(nodeOutDTO)));
        Mockito.when(nodeService.getNativeNode()).thenReturn(null);
        mvc.perform(MockMvcRequestBuilders.get(NODES + "/native"))
                .andExpect(status().isNotFound());
    }
}
