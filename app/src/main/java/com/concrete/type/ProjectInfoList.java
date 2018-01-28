package com.concrete.type;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tangxl on 2017/12/23.
 */

public class ProjectInfoList implements Serializable {
        public boolean result;
        public String error;
        public int echo_code;
        public int index;
        public ArrayList<PrjectInfo> items;

        public ProjectInfoList(boolean result, String error, int echo_code, ArrayList<PrjectInfo> items){
                this.result = result;
                this.error = error;
                this.echo_code = echo_code;
                this.index = items.size();
                this.items = items;
        }
}
