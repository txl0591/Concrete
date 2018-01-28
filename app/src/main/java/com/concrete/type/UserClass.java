package com.concrete.type;

/**
 * Created by Tangxl on 2018/1/13.
 */

public class UserClass {
    private String TBL_USERNAME;
    private String TBL_USERPWD;
    private String TBL_USERTYPE;
    private String TBL_USERDanWei;

    public UserClass(String TBL_USERNAME,String TBL_USERPWD, String TBL_USERTYPE, String TBL_USERDanWei){
        this.TBL_USERNAME = TBL_USERNAME;
        this.TBL_USERPWD = TBL_USERPWD;
        this.TBL_USERTYPE = TBL_USERTYPE;
        this.TBL_USERDanWei = TBL_USERDanWei;
    }
}
