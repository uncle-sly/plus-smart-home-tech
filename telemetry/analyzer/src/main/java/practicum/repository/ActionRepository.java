package practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practicum.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {

}
