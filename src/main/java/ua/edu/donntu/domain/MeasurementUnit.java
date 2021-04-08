package ua.edu.donntu.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "measurement_unit")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementUnit implements Serializable {

    private static final long serialVersionUID = -6750471515636689423L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "transmission_time", nullable = false)
    private long transmissionTime;

    @Column(name = "processing_time", nullable = false)
    private long processingTime;

    @Column(name = "hash", nullable = false)
    private String hash;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "measurement", nullable = false)
    private Measurement measurement;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "node", nullable = false)
    private Node node;
}
