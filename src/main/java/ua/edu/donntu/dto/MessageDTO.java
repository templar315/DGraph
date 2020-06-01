package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    private static final long serialVersionUID = -4961377427252156715L;

    private long id;

    private Date sendDate;

    private Date receiveDate;

    private Date saveDate;

    private String hash;

    private String senderHost;

    private String recipientHost;
}
