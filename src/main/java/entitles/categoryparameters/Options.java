package entitles.categoryparameters;

import lombok.Data;

@Data
public class Options {
    public boolean variantsAllowed;
    public boolean variantsEqual;
    public Object ambiguousValueId;
    public Object dependsOnParameterId;
    public Object requiredDependsOnValueIds;
    public Object displayDependsOnValueIds;
    public boolean describesProduct;
    public boolean customValuesEnabled;
}
