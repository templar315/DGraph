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

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "hash", nullable = false, unique = true)
    private String hash;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "sender")
    private Node sender;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "recipient")
    private Node recipient;
}
