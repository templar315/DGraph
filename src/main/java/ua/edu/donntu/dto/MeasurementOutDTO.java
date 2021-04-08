package ua.edu.donntu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementOutDTO implements Serializable {

    private static final long serialVersionUID = -9072802813359373828L;

    private long id;
    private boolean finished;
    private Date startDate;
    private Date finishDate;
    private long size;
    private long transmissionMean;
    private long transmissionDeviation;
    private long processingMean;
    private long processingDeviation;
    private List<Long> units;
}
