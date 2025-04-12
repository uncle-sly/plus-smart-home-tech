package practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practicum.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {

}
