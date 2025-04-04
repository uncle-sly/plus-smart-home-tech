package practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

//@Entity
//@Table(name = "sensors")
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Sensor {
//
//    @Id
// //    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private String id;
//
//    @Column(name = "hub_id")
//    private String hubId;
//
//    @OneToMany(cascade = CascadeType.ALL)
//    @MapKeyColumn(table = "scenario_conditions", name = "scenario_id")
//    @JoinTable(name = "scenario_conditions", joinColumns = @JoinColumn(name = "sensor_id"), inverseJoinColumns = @JoinColumn(name = "condition_id"))
//    private Map<Long, Condition> conditions = new HashMap<>();
//
//    @OneToMany(cascade = CascadeType.ALL)
//    @MapKeyColumn(table = "scenario_actions", name = "scenario_id")
//    @JoinTable(name = "scenario_actions", joinColumns = @JoinColumn(name = "sensor_id"), inverseJoinColumns = @JoinColumn(name = "action_id"))
//    private Map<Long, Action> actions = new HashMap<>();
//
//}

@Entity
@Table(name = "sensors")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {
    @Id
    private String id;

    private String hubId;
}
