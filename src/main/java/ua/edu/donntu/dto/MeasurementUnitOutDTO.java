package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementUnitOutDTO implements Serializable {

    private static final long serialVersionUID = -4381354977628224932L;

    private long id;
    private long transmissionTime;
    private long processingTime;
    private String hash;
    private long measurement;
    private long node;
}
