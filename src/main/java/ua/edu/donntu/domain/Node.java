package ua.edu.donntu.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "node")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Node implements Serializable {

    private static final long serialVersionUID = -6036504330683710064L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "host", nullable = false, unique = true)
    private String host;

    @Column(name = "native_node", nullable = false)
    @ColumnDefault("false")
    private boolean nativeNode;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Message> receivedMessages;
}
