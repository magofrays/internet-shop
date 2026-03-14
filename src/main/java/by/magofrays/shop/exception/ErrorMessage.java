package by.magofrays.shop.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class ErrorMessage {
    private String title;
    private int status;
    private Map<String, String> properties = new HashMap<>();
    private String detail;
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
}
