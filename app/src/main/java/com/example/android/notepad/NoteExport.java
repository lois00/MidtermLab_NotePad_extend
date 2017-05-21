package com.example.android.notepad;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.example.android.notepad.Util.FileService;

/**
 * Created by Administrator on 2017-05-13.
 */

public class NoteExport extends Activity {
    public static final String EXPORT_NOTE_ACTION = "com.android.notepad.action.EXPORT_NOTE";

    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID,          // Projection position 0, the note's id
            NotePad.Notes.COLUMN_NAME_TITLE,   // Projection position 1, the note's title
            NotePad.Notes.COLUMN_NAME_NOTE     // Projection position 2, the note's modified date
    };
    private static final int COLUMN_INDEX_TITLE = 1;
    private static final int COLUMN_INDEX_NOTE = 2;

    //0表示保存成功，1表示sd卡错误，2表示保存失败
    //markable variables
    private static final int SAVE_SUCCESS = 0;
    private static final int SAVE_SDCARD_ERROR = 1;
    private static final int SAVE_FAIL = 2;
    private Cursor mCursor;
    private Uri mUri;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_export);
        tv = (TextView) findViewById(R.id.saveFileDir);

        mUri = getIntent().getData();
        mCursor = managedQuery(
                mUri,        // The URI for the note that is to be retrieved.
                PROJECTION,  // The columns to retrieve
                null,        // No selection criteria are used, so no where columns are needed.
                null,        // No where columns are used, so no where values are needed.
                null         // No sort order is needed.
        );
        if (mCursor != null) {

            // The Cursor was just retrieved, so its index is set to one record *before* the first
            // record retrieved. This moves it to the first record.
            mCursor.moveToFirst();
            //remove the blank spaces in note's title
            String title = mCursor.getString(COLUMN_INDEX_TITLE).replace(" ", "");//去除标题中的空格
            //gets the title of current note
            String noteText = mCursor.getString(COLUMN_INDEX_NOTE);//获取当前note的标题
            //exports the note's content
            int result = exportNote(noteText, title);
            //gets the directory of SD card
            String saveDir = Environment.getExternalStorageDirectory().toString();
            if (result == SAVE_SUCCESS) {//if saved success
                // Displays the current title text in the EditText object.
                tv.setText("New file \"" + title + ".txt\" has been saved in " + saveDir + "");
            } else if (result == SAVE_SDCARD_ERROR) {//if saved error
                tv.setText("SD card error！file \"" + title + ".txt\" saves fail!");
            } else {//if saved fail
                tv.setText("File \"" + title + ".txt\" saves fail!");
            }

        }

    }

    //将笔记文件导出到SD卡
    //exports the note's content into SD card
    private final int exportNote(String text, String title) {
        int result = SAVE_SUCCESS;
        String filename = title;    //the note's title
        String filecontent = text;  //the note's content
        //customized tool class object
        FileService service = new FileService(getApplicationContext());//创建自定义的工具类对象，保存文件到SD卡
        try {
            //判断SD卡是否存在并且可读写
            //if SD card exists and is readable and writable
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //uses the method of tool class,to save file into SD card directory
                service.saveToSDCard(filename, filecontent);//调用工具类，将文件保存到SD卡
            } else {
                //if SD card isn't ready,saves error
                result = SAVE_SDCARD_ERROR;
            }

        } catch (Exception e) {
            //if exists exception,saves fail
            result = SAVE_FAIL;
            e.printStackTrace();
        } finally {
            return result;  //return saved result
        }
    }

    public void onClickOk(View v) {
        finish();
    }
}


