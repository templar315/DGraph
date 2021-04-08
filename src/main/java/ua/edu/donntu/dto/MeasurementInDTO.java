package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementInDTO implements Serializable {

    private static final long serialVersionUID = 5989185965334613584L;

    @Min(1)
    @Max(200 * 1024 * 1024)
    private int size;

    @Min(1)
    @Max(100)
    private int measurements;

    private List<Long> nodes;
}
