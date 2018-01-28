package com.concrete.type;

import java.io.Serializable;

/**
 * Created by Tangxl on 2018/1/6.
 */

public class UserProject implements Serializable {
    public String User_prj_uuid;
    public String project_UUID;
    public String TBL_USERNAME;

    public UserProject(String User_prj_uuid,String project_UUID,String TBL_USERNAME){
        this.User_prj_uuid = User_prj_uuid;
        this.project_UUID = project_UUID;
        this.TBL_USERNAME = TBL_USERNAME;
    }
}
