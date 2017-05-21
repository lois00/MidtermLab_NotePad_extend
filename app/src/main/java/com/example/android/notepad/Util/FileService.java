package com.example.android.notepad.Util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017-05-12.
 */

/**
 * Tool class,invoked when the user click the "Export" menu.
 * 工具类，当用户选择导出笔记菜单时调用。
 */
public class FileService {
    private Context context;

    public FileService(Context context) {
        this.context = context;
    }

    /**
     * saves new fie into SD card
     * 保存文件到SD卡
     *
     * @param filename    保存的文件名称 saved file name
     * @param filecontent 保存的文本内容 saved file content
     * @throws Exception
     */
    public void saveToSDCard(String filename, String filecontent) throws Exception {
         /*
         * 保存文件到sd卡，sd卡用于保存大文件（视频，音乐，文档等）
         * 获取sd卡路径Environment.getExternalStorageDirectory()
         * android版本不同，sd卡的路径也不相同，所以这里不能写绝对路径
         * */
        //gets the directory of SD card
        File sdCardDir = Environment.getExternalStorageDirectory();
        //createa an new file object
        File file = new File(sdCardDir, filename + ".txt");
        //creates new file in memory
//        FileOutputStream outStream = context.openFileOutput(filename,Context.MODE_PRIVATE);
        //creates new file in SD card directory
        FileOutputStream outStream = new FileOutputStream(file);
        /**
         *
         * 注意：这里创建文件输出流对象 就不能使用context.openFileOutput(filename, Context.MODE_PRIVATE)，
         * 这种方式会直接在手机自带的内存中创建文件。在sd卡下创建文件要使用new FileOutputStream(file);
         */
        //write new file in SD card directory
        outStream.write(filecontent.getBytes());
        outStream.close();
    }
}
