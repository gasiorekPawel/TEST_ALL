package entitles.categoryparameters;

import lombok.Data;

import java.util.List;

@Data
public class Parameter {
    public String id;
    public String name;
    public String type;
    public boolean required;
    public boolean requiredForProduct;
    public String unit;
    public Options options;
    public List<Dictionary> dictionary;
    public Restrictions restrictions;
}
