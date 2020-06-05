package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NodeInDTO implements Serializable {

    private static final long serialVersionUID = -2999756979758773207L;

    @NotNull(message = "Host field is null")
    @NotEmpty(message = "Host field is empty")
    private String host;

    @NotNull(message = "Port field is null")
    @NotEmpty(message = "Port field is empty")
    private String port;

}
