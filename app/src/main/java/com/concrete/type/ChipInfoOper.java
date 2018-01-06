package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.util.List;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class ChipInfoOper {
        public String Cmd;
        public int index;
        public List<ChipInfo> items;

        public ChipInfoOper(String Cmd, List<ChipInfo> items){
            this.Cmd = Cmd;
            this.index = items.size();
            this.items = items;
        }

        public void PrintChipInfoOper(){
            this.Cmd = Cmd;
            this.items = items;
            nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"this.Cmd ["+this.Cmd+"]");
            nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"this.items.size() ["+this.items.size()+"]");
        }
}
