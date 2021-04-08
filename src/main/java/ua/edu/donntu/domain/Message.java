package ua.edu.donntu.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "message")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = -1348939457586715703L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "send_date", nullable = false)
    private Date sendDate;

    @Column(name = "receive_date", nullable = false)
    private Date receiveDate;

    @Column(name = "save_date", nullable = false)
    private Date saveDate;

    @Column(name = "transmission_time", nullable = false)
    private long transmissionTime;

    @Column(name = "processing_time", nullable = false)
    private long processingTime;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "hash", nullable = false, unique = true)
    private String hash;

    @Column(name = "size", nullable = false)
    private int size;

    @Column(name = "sender", nullable = false)
    private String sender;
}
