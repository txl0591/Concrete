package com.concrete.type;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tangxl on 2017/12/23.
 */

public class ProjectInfoList implements Serializable{
        public boolean result;
        public String error;
        public int echo_code;
        public int index;
        public ArrayList<PrjectInfo> items;
}
