package entitles.categoryparameters;

import lombok.Data;

import java.util.List;

@Data
public class Dictionary {
    public String id;
    public String value;
    public List<Object> dependsOnValueIds;
}
