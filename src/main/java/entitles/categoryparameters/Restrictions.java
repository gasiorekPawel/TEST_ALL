package entitles.categoryparameters;

import lombok.Data;

@Data
public class Restrictions {
    public boolean multipleChoices;
    public int minLength;
    public int maxLength;
    public int allowedNumberOfValues;
    public double min;
    public double max;
    public boolean range;
    public int precision;
}
