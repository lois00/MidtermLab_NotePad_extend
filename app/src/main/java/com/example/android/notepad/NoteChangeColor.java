package com.example.android.notepad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteChangeColor extends Activity {
    // Global mutable variables
    private GridView gridView;  //显示背景色选项的网格列表
    private int backgroundColor;  //用来存储用户选择的背景色的变量 the variable to save the selected background color

    //可供用户选择的背景色数组
    //the array which contains the alternative background colors for the user
    private static final int[] imgColorArr = new int[]{
            R.color.color_note_background_light_blue, R.color.color_note_background_light_cyan,
            R.color.color_note_background_light_pink, R.color.color_note_background_light_yellow,
            R.color.color_note_background_light_green, R.color.color_note_background_light_gray,
            R.color.color_note_background_light_red, R.color.color_note_background_light_purple};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_color_main);
        //sets title of current activity
        setTitle("Select Background Color");//设置标题
        //sets the color of title text
        setTitleColor(Color.WHITE);//设置标题文本的颜色

        //创建一个list对象，存储背景色键值对
        //creates a list object,to save imgColorArr array data in a key-value way.
        List<Map<String, Object>> colorItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < imgColorArr.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("color", imgColorArr[i]);
            colorItems.add(map);
        }
        //finds the gridview which contains the optional background colors
        gridView = (GridView) this.findViewById(R.id.gridView_id);//找到显示背景色选项列表的gridview
        //创建一个SimpleAdapter适配器对象，将布局与背景色数据绑定在一起
        //creates a SimpleAdapter object,to bind the layout and color data together
        SimpleAdapter adapter = new SimpleAdapter(this, colorItems, R.layout.background_color_item,
                new String[]{"color"}, new int[]{R.id.item_imageView_id});
        //sets the adapter of gridview
        gridView.setAdapter(adapter);//设置gridview的适配器

        /**
         * 当用户点击了背景色选择列表里的一个选项时该方法被调用。
         * the method is called when the user clicks a background color option in the gridview
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                //gets the clicked background color option through the position of
                // clicked imageview in the gridview.
                backgroundColor = imgColorArr[position];//获得用户选择的背景色选项
                //creates a new intent
                Intent intent = new Intent();//新建一个intent
                //puts the data of backgroundColor into the new intent,
                // and passes it to the activity which invokes current activity.
                intent.putExtra("color", backgroundColor);//将用户选择的背景色数据传给调用当前activity的activity
                NoteChangeColor.this.setResult(0, intent);
                //结束当前activity
                //finish the current activity
                finish();
            }
        });
    }

}