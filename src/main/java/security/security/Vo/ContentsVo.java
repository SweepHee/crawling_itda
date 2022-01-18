package security.security.Vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ContentsVo {

    private int indexid;
    private String targetname;
    private String targetnamecode;
    private String targettype;
    private String targettypecode;
    private String targetcost;
    private String loccode;
    private String title;
    private String bodyurl;
    private String targetlike;
    private String targetshare;
    private String targetadd;
    private String activeFlag;
    private String endTime;
    private String saveTime;
    private int cntview;

}
