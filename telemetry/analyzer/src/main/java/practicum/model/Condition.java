package practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@ToString
@SecondaryTable(
        name = "scenario_conditions",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "condition_id")
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConditionTypeAvro type;

    @Enumerated(EnumType.STRING)
    private ConditionOperationAvro operation;

    private int value;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

}