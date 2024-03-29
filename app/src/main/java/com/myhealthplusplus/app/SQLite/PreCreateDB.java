package com.myhealthplusplus.app.SQLite;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PreCreateDB {

    public static void copyDB(Context context){
        try{
            String destPath = "/data/data/"+ context.getPackageName()
                    + "/databases";
            File f = new File(destPath);
            if(!f.exists()){
                f.mkdir();
                rawCopy(context.getAssets().open("HealthPlusPlus.db"), new FileOutputStream(destPath + "/HealthPlusPlus.db"));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void rawCopy(InputStream inputStream, OutputStream outputStream) throws IOException{
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) > 0){
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }
}
