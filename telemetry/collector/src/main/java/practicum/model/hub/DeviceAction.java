package practicum.model.hub;

import lombok.Data;

@Data
public class DeviceAction {
    private String sensor_id;
    private ActionType type;
    private int value;

}
