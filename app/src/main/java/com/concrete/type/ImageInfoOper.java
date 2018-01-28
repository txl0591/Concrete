package com.concrete.type;

import java.util.List;

/**
 * Created by Tangxl on 2017/12/22.
 */

public class ImageInfoOper {
    public String Cmd;
    public int index;
    public List<ImageInfo> items;

    public ImageInfoOper(String Cmd, List<ImageInfo> item){
        this.Cmd = Cmd;
        this.items = item;
        this.index = item.size();
    }
}
