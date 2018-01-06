package com.concrete.type;

import java.io.Serializable;

public class UserType implements Serializable {

    public static final int USERTYPE_ADMIN = 0;
    public static final int USERTYPE_SG = 1;
    public static final int USERTYPE_JC = 2;
    public static final int USERTYPE_JD = 3;

    public int TBL_USERTYPE;
    public String TBL_USERDanWei;

    public UserType(int Type, String DanWei){
        TBL_USERTYPE = Type;
        TBL_USERDanWei = DanWei;
    }

}
