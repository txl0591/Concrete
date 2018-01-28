package com.concrete.type;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Tangxl on 2017/12/23.
 */

public class PrjectInfo implements Serializable {
    public String project_UUID;
    public String project_id;
    public String dcPK;
    public String compact_type;
    public String project_code;
    public String project;
    public String check_unit_id;
    public String consCorpNames;
    public String superCorpNames;
    public String corpname;
    public String check_unit;
    public String consCorp_id;
    public String superCorp_id;
    public String corpcode;
    public String createDate;
    public String updatetime;
    public String district_id;
    public String safety_id;
    public String gongchengmianji;
    public String touzijinne;

    public PrjectInfo(String project_UUID,
                      String project_id,
                      String dcPK,
                      String compact_type,
                      String project_code,
                      String project,
                      String check_unit_id,
                      String consCorpNames,
                      String superCorpNames,
                      String corpname,
                      String check_unit,
                      String consCorp_id,
                      String superCorp_id,
                      String corpcode,
                      String createDate,
                      String updatetime,
                      String district_id,
                      String safety_id,
                      String gongchengmianji,
                      String touzijinne){
        this.project_UUID=project_UUID;
        this.project_id=project_id;
        this.dcPK=dcPK;
        this.compact_type=compact_type;
        this.project_code=project_code;
        this.project=project;
        this.check_unit_id=check_unit_id;
        this.consCorpNames=consCorpNames;
        this.superCorpNames=superCorpNames;
        this.corpname=corpname;
        this.check_unit=check_unit;
        this.consCorp_id=consCorp_id;
        this.superCorp_id=superCorp_id;
        this.corpcode=corpcode;
        this.createDate=createDate;
        this.updatetime=updatetime;
        this.district_id=district_id;
        this.safety_id=safety_id;
        this.gongchengmianji=gongchengmianji;
        this.touzijinne=touzijinne;
    }
}
