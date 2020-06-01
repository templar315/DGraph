package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageInDTO implements Serializable {

    private static final long serialVersionUID = -8450413248041319081L;

    @NotNull(message = "Send date field is null")
    @NotEmpty(message = "Send date field is empty")
    @PastOrPresent(message = "Send date field is incorrect")
    private Date sendDate;

    @NotNull(message = "Sender host field is null")
    @NotEmpty(message = "Sender host field is empty")
    private String senderHost;

    @NotNull(message = "Recipient host field is null")
    @NotEmpty(message = "Recipient host field is empty")
    private String recipientHost;
}
