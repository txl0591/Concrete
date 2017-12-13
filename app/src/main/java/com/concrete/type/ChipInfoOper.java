package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.util.List;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class ChipInfoOper {
        public String Table;
        public String Cmd;
        public int index;
        public List<ChipInfo> items;

        public ChipInfoOper(String Cmd, int index, List<ChipInfo> items){
            this.Table = getClass().getSimpleName();
            this.Cmd = Cmd;
            this.index = items.size();
            this.items = items;
            this.Table = ChipInfoList.TABLE;
        }

        public void PrintChipInfoOper(){
            this.Table = getClass().getSimpleName();
            this.Cmd = Cmd;
            this.items = items;
            nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"this.Cmd ["+this.Cmd+"]");
            nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"this.ClassName ["+this.Table+"]");
        }
}
