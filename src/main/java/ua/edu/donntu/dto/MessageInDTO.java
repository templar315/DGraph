package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @PastOrPresent(message = "Send date field is incorrect")
    private Date sendDate;
}
