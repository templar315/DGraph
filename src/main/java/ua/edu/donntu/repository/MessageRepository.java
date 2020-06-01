package ua.edu.donntu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.donntu.domain.Message;
import ua.edu.donntu.domain.Node;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message getByHash(String hash);
}
