package com.example.android.notepad;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteSearcher extends Activity {
    //Global mutable variables
    private ContentResolver contentResolver;
    private SimpleAdapter simpleAdapter;
    private SearchView sv;
    private ListView list;
    private String searchKeywords;
    private Uri mUri;

    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID,          // Projection position 0, the note's id
            NotePad.Notes.COLUMN_NAME_TITLE,    // Projection position 1, the note's title
            NotePad.Notes.COLUMN_NAME_NOTE,     // Projection position 2, the note's content
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // Projection position 3, the note's modified date
            NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR   // Projection position 4, the note's background color
    };
    //search condition
    private String selection = "title like ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_search_main);

        final Intent intent = getIntent();
        mUri = intent.getData();

        contentResolver = getContentResolver();
        list = (ListView) findViewById(R.id.mylist);

        sv = (SearchView) findViewById(R.id.searchView);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            // This method is called when the user click the enter key or press the search button
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            // 当搜索内容改变时触发该方法
            // This method is called when the text of search box is changed
            @Override
            public boolean onQueryTextChange(String s) {
                searchKeywords = s;
                Intent intent = getIntent();

                // If there is no data associated with the Intent, sets the data to the default URI, which
                // accesses a list of notes.
                if (intent.getData() == null) {
                    intent.setData(NotePad.Notes.CONTENT_ID_URI_BASE);
                }

                String[] selectionArgs = new String[1];
                selectionArgs[0] = "%" + searchKeywords + "%";
                //用contentResolver来执行查询操作
                //executes query operation with contentResolver
                Cursor cursor = contentResolver.query(
                        NotePad.Notes.CONTENT_URI,
                        PROJECTION,
                        selection,
                        selectionArgs,
                        null
                );
                List<Map<String, Object>> resultItems = new ArrayList<Map<String, Object>>();
                //从数据库中一条一条地读取数据，并取出其中的数据
                //gets the needed data from cursor,and puts them into a list set
                while (cursor.moveToNext()) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("icon", R.drawable.menu_item_note);
                    item.put(NotePad.Notes.COLUMN_NAME_ID, cursor.getString(0));
                    item.put(NotePad.Notes.COLUMN_NAME_TITLE, cursor.getString(1));
                    item.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, cursor.getString(3));
                    resultItems.add(item);
                }
                //创建一个SimpleAdapter
                //creates a SimpleAdapter object,to bind the layout and the data together
                simpleAdapter = new SimpleAdapter(
                        NoteSearcher.this, resultItems,
                        R.layout.note_search_result_item,
                        new String[]{"icon",
                                NotePad.Notes.COLUMN_NAME_TITLE,
                                NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE},
                        new int[]{R.id.img, R.id.title, R.id.modifiedTime});
                //sets the adapter of listvew
                list.setAdapter(simpleAdapter);//为ListView设置Adapter
                //sets the OnItemClickListener for the listview
                list.setOnItemClickListener(new OnItemClickListenerImpl());//给lisiview设置监听事件
                return true;
            }
        });
    }

    //自定义监听器类，实现OnItemClickListener接口
    //customized OnItemClickListener class ,which implements the interface OnItemClickListener
    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Map<String, String> map = (Map<String, String>) NoteSearcher.this.simpleAdapter
                    .getItem(position);
            //获取被点击的那个item对应的note在数据库里的id
            //gets the clicked item's id in database
            int _id = Integer.decode(map.get(NotePad.Notes.COLUMN_NAME_ID));
            //appends the item's id to uri
            Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), _id);
            Intent intent = new Intent();
            intent.setData(noteUri);
            intent.setAction(Intent.ACTION_EDIT);
            //start NoteEditor activity
            startActivity(intent);
            finish();
        }
    }

}
