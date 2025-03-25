package practicum.model.hub;

import lombok.Data;

@Data
public class ScenarioCondition {
    private String sensor_id;
    private ConditionType type;
    private ConditionOperationType operation;
    private int value;

}
