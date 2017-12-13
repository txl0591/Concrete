package com.concrete.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Tangxl on 2017/11/22.
 */

public class FileUtil {

    public static void writefile(String AllPath,String s){
        try {
            nlog.Info("AllPath ==============["+AllPath+"]");
            FileOutputStream outStream = new FileOutputStream(AllPath,true);
            OutputStreamWriter writer = new OutputStreamWriter(outStream,"utf-8");
            writer.write(s);
            writer.write("\r\n");
            writer.flush();
            writer.close();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
