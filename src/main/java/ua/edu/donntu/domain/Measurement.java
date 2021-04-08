package ua.edu.donntu.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "measurement")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Measurement implements Serializable {

    private static final long serialVersionUID = -6750471515636689423L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "finished", nullable = false)
    private boolean finished;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "finish_date")
    private Date finishDate;

    @Column(name = "size", nullable = false)
    private int size;

    @OneToMany(mappedBy = "measurement", fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MeasurementUnit> measurementUnits = new ArrayList<>();
}
