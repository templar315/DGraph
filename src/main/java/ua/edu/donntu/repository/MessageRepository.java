package ua.edu.donntu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.donntu.domain.Message;
import ua.edu.donntu.domain.Node;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message getByHash(String hash);
    List<Message> getAllBySender(String senderHost);
}
