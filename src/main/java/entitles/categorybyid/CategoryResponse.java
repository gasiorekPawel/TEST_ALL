package entitles.categorybyid;

import entitles.categorybyid.Options;
import lombok.Data;

@Data
public class CategoryResponse {
    public String id;
    public String name;
    public Object parent;
    public boolean leaf;
    public Options options;
}
