package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NodeOutDTO implements Serializable {

    private static final long serialVersionUID = -4961377427252156715L;

    private long id;

    private String host;

    private boolean nativeNode;

    private List<Long> sentMessages;

    private List<Long> receivedMessages;

}
