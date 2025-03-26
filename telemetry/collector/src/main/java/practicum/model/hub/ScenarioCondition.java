package practicum.model.hub;

import lombok.Data;

@Data
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperationType operation;
    private int value;

}
