package ua.edu.donntu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.donntu.domain.Node;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    Node getByHost(String host);
    Node getNodeByNativeNodeIsTrue();
}
