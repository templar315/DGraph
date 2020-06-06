package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageOutDTO implements Serializable {

    private static final long serialVersionUID = -4961377427252156715L;

    private long id;

    private Date sendDate;

    private Date receiveDate;

    private Date saveDate;

    private Long transmissionTime;

    private Long processingTime;

    private String hash;

    private String senderHost;

    private String recipientHost;
}
