package com.concrete.type;

import java.util.List;

/**
 * Created by Tangxl on 2017/12/22.
 */

public class ImageInfoOper {
    public String Cmd;
    public int index;
    public List<ImageInfo> item;

    public ImageInfoOper(String Cmd, List<ImageInfo> item){
        this.Cmd = Cmd;
        this.item = item;
        this.index = item.size();
    }
}
