# MidtermLab_NotePad_Extend
## 项目说明：本实验是基于谷歌SDK NotePad应用做的功能扩展。<br>
## 功能列表：<br>
### 1.初始功能：<br>
列出笔记条目、添加笔记、保存笔记、删除笔记、修改笔记的标题、
粘贴、编辑笔记内容与撤销编辑操作<br>
### 2.扩展功能：<br>
笔记条目增加时间戳显示、根据标题查询笔记、UI美化、笔记排序、导出笔记、更改记事本的背景<br>
## 功能说明及展示：<br>
### 一、列出笔记条目,并显示时间戳<br>
&emsp;&emsp;当应用启动，系统将列出数据库所有的笔记条目。这是应用程序的主界面。<br>
每条笔记显示了笔记的标题以及最后修改时间，每条笔记都有各自的背景色。<br>
![笔记列表](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/list_1.png)<br>
### 二、笔记排序<br>
&emsp;&emsp;标题栏上显式的有两个菜单，分别对应查询笔记和添加笔记的操作。<br>
点击右边的三个冒号按钮，出现隐藏的菜单，分别是粘贴和排序。这里先讲笔记排序。<br>
点击下图“Sort”菜单，<br>
![笔记排序](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/sort_1.png)<br>
将弹出一个对话框，如下图，对话框中列出所有可选的排序方式，按顺序分别是：<br>
&emsp;&emsp;按标题名称升序排序，<br>
&emsp;&emsp;按标题名称降序排序，<br>
&emsp;&emsp;按最后修改时间降序排序，<br>
&emsp;&emsp;按最后修改时间升序排序。<br>
![笔记排序](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/sort_2.png)<br>
&emsp;&emsp;比如，我们选择第一个，按标题名称升序排序，则排序结果如下，<br>
![笔记排序](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/sort_3.png)<br>
&emsp;&emsp;或者，我们选择第三个，按最后修改时间降序排序（默认排序方式），则排序结果如下，<br>
![笔记排序](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/sort_4.png)<br>
笔记排序实现关键代码如下所示：<br>
```
public void sortListItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//新建一个对话框
        builder.setIcon(android.R.drawable.ic_dialog_info);//设置对话框的图标
        builder.setTitle("Select Sort Mode:");//设置对话框的标题
        //可供用户选择的排序方式
        //the alternative sort orders for users
        final String[] sortOrders = new String[]{"By title name asc", "By title name desc",
                "By modified date desc", "By modified date asc"};

        builder.setSingleChoiceItems(sortOrders, -1, new OnClickListener() {
            //默认排序方式为修改时间降序
            //the default sort order is modified desc
            String order = NotePad.Notes.DEFAULT_SORT_ORDER;

            /**
             * 当用户点击了一个排序选项时被调用
             * The method is called when the user click an option in the displayed list
             * @param dialog 触发这个方法的对话框 the dialog which invokes this method
             * @param which  用户点击的选项的位置 the position of option which the user clicks
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sort_mode = sortOrders[which];
                switch (sort_mode) {
                    case "By title name asc":
                        //按标题升序排列
                        //sort the items of listview by title name ascending
                        order = NotePad.Notes.SORT_ORDER_Title_ASC;
                        break;
                    case "By title name desc":
                        //按标题降序排列
                        //sort the items of listview by title name descending
                        order = NotePad.Notes.SORT_ORDER_Title_DESC;
                        break;
                    case "By modified date desc":
                        //按修改时间降序排列
                        //sort the items of listview by modified date descending
                        order = NotePad.Notes.DEFAULT_SORT_ORDER;
                        break;
                    case "By modified date asc":
                        //按修改时间升序排列
                        //sort the items of listview by modified date ascending
                        order = NotePad.Notes.SORT_ORDER_MODIFIED_ASC;
                        break;
                    default:
                        break;
                }
                //使用新的排序方式从数据库读取数据，这样游标中读出来的数据就是已按选择的排序方式排好序的序列了。
                //use the new order to query needed columns from database,so that the data in cursor
                // has been organized by selected sort order.
                cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, order);
                //使用新的游标，创建一个自定义SimpleCursorAdapter对象
                //recreates a customized　SimpleCursorAdapter object,use the new cursor
                adapter = new MySimpleCursorAdapter(NotesList.this, R.layout.noteslist_item, cursor, dataColumns, viewIDs, 0);
                //重置listview的适配器
                //reset the addpter of listview
                setListAdapter(adapter);
                //更新listView的数据
                //refresh the data of listview
                adapter.notifyDataSetChanged();
                //当用户选择了一个值后，对话框消失
                //when the user selected an option,the dialog dismissed.
                dialog.dismiss();
            }
        });
        //显示对话框
        //show the dialog
        builder.show();
    }
```
### 三、添加笔记、修改背景色、保存笔记<br>
&emsp;&emsp;点击主界面的加号菜单，即可添加新的笔记内容。如下图，标题栏上显示的菜单分别是保存，删除以及更改背景色。<br>
![添加笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/addnote_1.png)<br>
&emsp;&emsp;接下来，我们输入笔记内容，并更换背景色（默认的背景色是淡青色）。<br>
例如，我们输入“ggg”,然后点击更改背景色菜单，弹出颜色选择框，<br>
![添加笔记并修改背景色](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/addnote_color_2.png)<br>
&emsp;&emsp;我们选择黄色作为这条笔记的背景色,效果如下图，更改背景色成功。<br>
![添加笔记并修改背景色](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/addnote_color_3.png)<br>
&emsp;&emsp;点击保存菜单，程序回到应用主界面，如下图。可以看到新添加的笔记（标题为“ggg”）出现在列表的第一条，而且背景色也是按我们设定的显示出来了。<br>
![保存笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/addnote_color_4.png)<br>
&emsp;&emsp;如果想要重新更改背景色，可以点击这条新的笔记“ggg”，进入编辑界面，点击右上角菜单，出现“Background”，“Edit title”，“Expor”三个菜单，分别是更改背景色，修改标记标题，导出笔记操作。<br>
![添加笔记并修改背景色](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/color_1.png)<br>
&emsp;&emsp;这里点击“Bground”菜单，又弹出颜色选择框，<br>
![添加笔记并修改背景色](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/color_2.png)<br>
&emsp;&emsp;这次我们选择粉红色作为背景色，效果下图所示，说明修改背景色成功。
![添加笔记并修改背景色](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/color_3.png)<br>
具体实现原理说明：<br>
&emsp;&emsp;首先是颜色选择框的实现，实现代码如下：<br>
```
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
```
&emsp;&emsp;当我们单击了某个背景色后，程序将用户选定的背景色通过intent回传给NoteEditor,代码如下：<br>
```
                Intent intent = new Intent();//新建一个intent
                //puts the data of backgroundColor into the new intent,
                // and passes it to the activity which invokes current activity.
                intent.putExtra("color", backgroundColor);//将用户选择的背景色数据传给调用当前activity的activity
                NoteChangeColor.this.setResult(0, intent);
```
&emsp;&emsp;然后NoteEditor通过重写回调函数onActivityResult，接收传递回来的intent中的背景色参数，并将当前编辑界面的背景色更改为用户选定的背景色。
同时，将该背景色参数通过ContentProvider保存至数据库，更新数据库中该条笔记的背景色数据。代码如下：<br>
```
 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            int backgroundColor = data.getIntExtra("color", 1);
            //uses the data carried by intent,which contains the background color the user selected
            //to set the note content background color
            tvNote.setBackgroundResource(backgroundColor);//使用传回来的背景色数据更改当前note内容的颜色

            ContentValues values = new ContentValues();
            values.put(NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR, backgroundColor);
            //updates the database with new background color
            getContentResolver().update(mUri, values, null, null);//更新数据库对应记录的背景色数据
        }
    }
```
### 四、修改笔记的标题<br>
&emsp;&emsp;在刚才新建笔记的编辑界面中，点击“Edit title”菜单，弹出如下对话框，对话框中显示默认标题为“ggg”（取笔记内容的前30个字符作为默认标题），<br>
![修改笔记的标题](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/title_1.png)<br>
&emsp;&emsp;我们将标题改为“backup”,点击“OK”按钮，如下图，<br>
![修改笔记的标题](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/title_2.png)<br>
&emsp;&emsp;下图的标题栏上的显示的标题变为“Edit:backup”，说明成功将标题修改为“backup”。<br>
![修改笔记的标题](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/title_3.png)<br>
### 五、导出笔记<br>
&emsp;&emsp;接下来，我们在刚才的“backup”笔记编辑界面中，点击右上角隐藏的菜单项，选择“Export”菜单，将弹出一个信息框，如下图，信息框中提示新的文件“backup,txt”已经导出保存在/mnt/sdcard目录下。<br>
![导出笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/export_1.png)<br>
&emsp;&emsp;点击“OK”按钮，提示框消失，<br>
![导出笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/export_2.png)<br>
&emsp;&emsp;点击保存菜单，程序回到主界面，可以看到标题为“backup”的笔记显示在列表首，笔记保存成功。<br>
![保存笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/save_1.png)<br>
&emsp;&emsp;为了验证笔记是否导出成功，我们查找一下导出的笔记。如下图，我们在SD卡目录下找到了刚才导出的文件“backup.txt”，说明笔记导出成功。<br>
![导出笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/export_3.png)<br>
导出笔记实现关键代码如下：<br>
```
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
```
&emsp;&emsp;其中，FileService是我自己编写的一个工具类，里面有一个方法saveToSDCard，用于将文件保存到SD卡中。具体实现如下：<br>
```
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
```
### 六、根据标题查询笔记<br>
&emsp;&emsp;回到程序主界面，点击搜索菜单，出现Search note界面如下，<br>
![查询笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/search_1.png)<br>
&emsp;&emsp;在搜索输入框中，我们输入“b”,搜索框下显示了两条标题中含有“b”的笔记条目，如下所示，<br>
![查询笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/search_2.png)<br>
&emsp;&emsp;点击其中一条“backup”，进入笔记编辑界面，如下图，可对该笔记进行编辑。
![查询笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/search_3.png)<br>
 &emsp;&emsp;_注：这里搜索出来的笔记条目的背景色与编辑笔记界面内部的背景色不一致，是有意为之。个人觉得这样搜索出来的结果风格比较统一，而且每个item在UI上也做了些美化。_<br>
笔记查询的实现关键代码如下：<br>
&emsp;&emsp;当用户点击搜索菜单时，启动NoteSearcher activity,<br>
```
 @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            ...
            case R.id.menu_search:
         /**
          * 使用intent来启动一个新的activity,这个activity的intent filter必须包含action为ACTION_SEARCH。
          * 没有设置category，默认为DEFAULT category。实际上，这里启动的是NoteSearcher Activity。
          */
          /*
           * Launches a new Activity using an Intent. The intent filter for the Activity
           * has to have action ACTION_SEARCH. No category is set, so DEFAULT is assumed.
           * In effect, this starts the NoteSearcher Activity in NotePad.
           */
                startActivity(new Intent(Intent.ACTION_SEARCH, getIntent().getData()));
                return true;
                ...
    }
```
&emsp;&emsp;在NoteSearcher中，实现对搜索输入框文本的监听，当输入文本发生改变时，将输入文本作为数据库查询操作的条件，执行数据库查询操作，查询出标题中含有输入文本的记录，并将结果显示在搜索结果列表中。实现代码如下：<br>
```
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
    ...
}
```
&emsp;&emsp;对于搜索结果列表中的笔记条目，要想实现点击它可以跳转到编辑该笔记的界面，还需实现对列表的OnItemClickListener的监听。为此我自定义了一个监听器类，实现跳转操作。具体实现如下：<br>
```
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
```
### 七、删除笔记<br>
&emsp;&emsp;接上一步的操作，在刚才笔记“backup”的编辑界面，点击菜单栏上的删除图标，则程序回到主界面，如下图，可以看到标题为“backup”的笔记条目已被成功删除。<br>
![删除笔记](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/delete_1.png)<br>
### 八、粘贴笔记内容<br>
&emsp;&emsp;回到程序主界面，点击隐藏菜单中的“paste”菜单，如下图，<br>
![粘贴笔记内容](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/paste_1.png)<br>
&emsp;&emsp;程序自动打开笔记编辑界面，并将当前系统剪贴板中的内容，粘贴在笔记内容中，如下图所示，<br>
![粘贴笔记内容](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/paste_2.png)<br>
&emsp;&emsp;由于默认标题为笔记内容的前30个字符，过长，所以接下来我们可以选择“Edit title”菜单，修改笔记的标题，如下图，<br>
![粘贴笔记内容](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/title_paste_3.png)<br>
&emsp;&emsp;这里我们将标题修改为“NOTE”，如下图。<br>
![粘贴笔记内容](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/title_paste_4.png)<br>
### 九、编辑笔记内容与撤销编辑操作<br>
&emsp;&emsp;接上一步的操作，对刚才通过粘贴操作新建的笔记“NOTE”进行编辑。输入一些文本“\*\*\*\*\*\*\*\*”,如下图，然后如果我们想要撤销刚才的输入操作，点击隐藏菜单，菜单栏上出现撤销菜单(U型转弯图标，该菜单只有在笔记内容被编辑修改后才会出现，才可执行撤销操作)，<br>
![编辑笔记内容与撤销](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/revert_1.png)<br>
&emsp;&emsp;点击撤销菜单，程序回到主界面，如下图，<br>
![编辑笔记内容与撤销](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/revert_2.png)<br>
&emsp;&emsp;为了验证撤销操作，我们再点击“NOTE”笔记，进入编辑界面，可以看到刚才输入的文本“\*\*\*\*\*\*\*\*”不见了，说明撤销操作执行成功。<br>
![编辑笔记内容与撤销](https://github.com/lois00/MidtermLab_NotePad_extend/blob/master/images/revert_3.png)<br>
&emsp;&emsp;至此，程序说明完毕！
