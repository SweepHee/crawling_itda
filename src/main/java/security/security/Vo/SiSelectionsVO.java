package security.security.Vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class SiSelectionsVO {

    private int id;
    private String name;
    private int price;
    private String tag;
    private String description;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private String etc;

}
