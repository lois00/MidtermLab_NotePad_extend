/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.notepad;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * Displays a list of notes. Will display notes from the {@link Uri}
 * provided in the incoming Intent if there is one, otherwise it defaults to displaying the
 * contents of the {@link NotePadProvider}.
 * <p>
 * NOTE: Notice that the provider operations in this Activity are taking place on the UI thread.
 * This is not a good practice. It is only done here to make the code more readable. A real
 * application should use the {@link android.content.AsyncQueryHandler} or
 * {@link android.os.AsyncTask} object to perform operations asynchronously on a separate thread.
 */
public class NotesList extends ListActivity {

    // For logging and debugging
    private static final String TAG = "NotesList";
    /**
     * Creates a projection that returns the note ID, the note title,the note modified date
     * and the background color of note.
     */
    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID,        // Projection position 0, the note's id
            NotePad.Notes.COLUMN_NAME_TITLE,    // Projection position 1, the note's title
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // Projection position 2, the note's modified date
            NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR   // Projection position 3, the note's background color
    };
    /**
     * 从数据库中取出标题和修改时间数据列
     * fetch the title column and the modified date column from cursor
     */
    private static final String[] dataColumns = {
            NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE};

    // The view IDs that will display the cursor columns, initialized to the TextView in
    // noteslist_item.xml
    private static final int[] viewIDs = {android.R.id.text1, R.id.modifiedTime};
    /**
     * The index of the title column
     */
    private static final int COLUMN_INDEX_TITLE = 1;
    private Cursor cursor;
    private MySimpleCursorAdapter adapter;  //自定义SimpleCursorAdapter

    /**
     * onCreate is called when Android starts this Activity from scratch.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置界面背景底色为白色
        //set the background of whole window to be white
        this.getWindow().getDecorView().setBackgroundResource(R.color.color_white);


        // The user does not need to hold down the key to use menu shortcuts.
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);//可使用快捷键

        /* If no data is given in the Intent that started this Activity, then this Activity
         * was started when the intent filter matched a MAIN action. We should use the default
         * provider URI.
         */
        // Gets the intent that started this Activity.
        Intent intent = getIntent();

        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        /*
         * Sets the callback for context menu activation for the ListView. The listener is set
         * to be this Activity. The effect is that context menus are enabled for items in the
         * ListView, and the context menu is handled by a method in NotesList.
         */
        getListView().setOnCreateContextMenuListener(this);

        /* Performs a managed query. The Activity handles closing and requerying the cursor
         * when needed.
         *
         * Please see the introductory note about performing provider operations on the UI thread.
         */
        cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                null,                             // No where clause, return all records.
                null,                             // No where clause, therefore no where column values.
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );

        /*
         * The following two arrays create a "map" between columns in the cursor and view IDs
         * for items in the ListView. Each element in the dataColumns array represents
         * a column name; each element in the viewID array represents the ID of a View.
         * The SimpleCursorAdapter maps them in ascending order to determine where each column
         * value will appear in the ListView.
         */
        // Creates the backing adapter for the ListView.
        adapter = new MySimpleCursorAdapter(
                this,                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor,                           // The cursor to get items from
                dataColumns,
                viewIDs,
                0
        );
        //去掉listview的item之间的分割线
        //remove the dividers between the items of listview
        getListView().setDivider(null);
        // Sets the ListView's adapter to be the cursor adapter that was just created.
        setListAdapter(adapter);
    }

    /**
     * 自定义SimpleCursorAdapter类
     * customize SimpleCursorAdapter class
     */
    class MySimpleCursorAdapter extends SimpleCursorAdapter {
        private Cursor m_cursor;
        private Context m_context;
        private LayoutInflater inflater;

        public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            m_context = context;
            m_cursor = c;
            inflater = getLayoutInflater();
        }

        /**
         * 重写bindView函数
         * overwrite bindView,to make SimpleCursorAdapter work well
         *
         * @param view    当前activity的view
         * @param context 上下文环境
         * @param cursor  游标
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            View convertView = null;
            if (view == null) {
                convertView = inflater.inflate(R.layout.noteslist_item, null);
            } else {
                convertView = view;
            }
            TextView title = (TextView) convertView
                    .findViewById(android.R.id.text1);
            TextView modifiedTime = (TextView) convertView
                    .findViewById(R.id.modifiedTime);
            TableRow row = (TableRow) convertView
                    .findViewById(R.id.note_list_item);

            //设置标题文本内容，数据来自数据库
            //sets text of title,use the column data title from database
            title.setText(cursor.getString(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE)));
            //设置修改日期文本内容，数据来自数据库
            //sets text of modifiedTime,use the column data modified from database
            modifiedTime.setText(cursor.getString(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE)));
            //设置listview每个item的背景色，数据来自数据库
            //sets the background of each item of the listview,use the column data color from database
            int color = cursor.getInt(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_BACKGROUND_COLOR));
            row.setBackgroundColor(getResources().getColor(color));
        }
    }

    //设置Overflow中的选项显示图标
    //makes the optional icons in overflow visible
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    //利用反射机制来获取设置选项菜单的图标是否可见的方法setOptionalIconsVisible
                    //make use of reflex mechanism to fetch the setOptionalIconsVisible method,
                    // which can set the optional icons visible.
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);//设置选项菜单图标可见
                    m.invoke(menu, true);//调用该方法
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * Called when the user clicks the device's Menu button the first time for
     * this Activity. Android passes in a Menu object that is populated with items.
     * <p>
     * Sets up a menu that provides the Insert option plus a list of alternative actions for
     * this Activity. Other applications that want to handle notes can "register" themselves in
     * Android by providing an intent filter that includes the category ALTERNATIVE and the
     * mimeTYpe NotePad.Notes.CONTENT_TYPE. If they do this, the code in onCreateOptionsMenu()
     * will add the Activity that contains the intent filter to its list of options. In effect,
     * the menu will offer the user other applications that can handle notes.
     *
     * @param menu A Menu object, to which menu items should be added.
     * @return True, always. The menu should be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);

        // Generate any additional actions that can be performed on the
        // overall list.  In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // The paste menu item is enabled if there is data on the clipboard.
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);

        MenuItem mPasteItem = menu.findItem(R.id.menu_paste);

        // If the clipboard contains an item, enables the Paste option on the menu.
        if (clipboard.hasPrimaryClip()) {
            mPasteItem.setEnabled(true);
        } else {
            // If the clipboard is empty, disables the menu's Paste option.
            mPasteItem.setEnabled(false);
        }

        // Gets the number of notes currently being displayed.
        final boolean haveItems = getListAdapter().getCount() > 0;

        // If there are any notes in the list (which implies that one of
        // them is selected), then we need to generate the actions that
        // can be performed on the current selection.  This will be a combination
        // of our own specific actions along with any extensions that can be
        // found.
        if (haveItems) {

            // This is the selected item.
            Uri uri = ContentUris.withAppendedId(getIntent().getData(), getSelectedItemId());

            // Creates an array of Intents with one element. This will be used to send an Intent
            // based on the selected menu item.
            Intent[] specifics = new Intent[1];

            // Sets the Intent in the array to be an EDIT action on the URI of the selected note.
            specifics[0] = new Intent(Intent.ACTION_EDIT, uri);

            // Creates an array of menu items with one element. This will contain the EDIT option.
            MenuItem[] items = new MenuItem[1];

            // Creates an Intent with no specific action, using the URI of the selected note.
            Intent intent = new Intent(null, uri);

            /* Adds the category ALTERNATIVE to the Intent, with the note ID URI as its
             * data. This prepares the Intent as a place to group alternative options in the
             * menu.
             */
            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

            /*
             * Add alternatives to the menu
             */
            menu.addIntentOptions(
                    Menu.CATEGORY_ALTERNATIVE,  // Add the Intents as options in the alternatives group.
                    Menu.NONE,                  // A unique item ID is not required.
                    Menu.NONE,                  // The alternatives don't need to be in order.
                    null,                       // The caller's name is not excluded from the group.
                    specifics,                  // These specific options must appear first.
                    intent,                     // These Intent objects map to the options in specifics.
                    Menu.NONE,                  // No flags are required.
                    items                       // The menu items generated from the specifics-to-
                    // Intents mapping
            );
            // If the Edit menu item exists, adds shortcuts for it.
            if (items[0] != null) {

                // Sets the Edit menu item shortcut to numeric "1", letter "e"
                items[0].setShortcut('1', 'e');
            }
        } else {
            // If the list is empty, removes any existing alternative actions from the menu
            menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
        }

        // Displays the menu
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new MySimpleCursorAdapter(
                this,                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor,                           // The cursor to get items from
                dataColumns,
                viewIDs,
                0
        );
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * This method is called when the user selects an option from the menu, but no item
     * in the list is selected. If the option was INSERT, then a new Intent is sent out with action
     * ACTION_INSERT. The data from the incoming Intent is put into the new Intent. In effect,
     * this triggers the NoteEditor activity in the NotePad application.
     * <p>
     * If the item was not INSERT, then most likely it was an alternative option from another
     * application. The parent method is called to process the item.
     *
     * @param item The menu item that was selected by the user
     * @return True, if the INSERT menu item was selected; otherwise, the result of calling
     * the parent method.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
          /*
           * Launches a new Activity using an Intent. The intent filter for the Activity
           * has to have action ACTION_INSERT. No category is set, so DEFAULT is assumed.
           * In effect, this starts the NoteEditor Activity in NotePad.
           */
                startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
                return true;
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
            case R.id.menu_paste:
          /*
           * Launches a new Activity using an Intent. The intent filter for the Activity
           * has to have action ACTION_PASTE. No category is set, so DEFAULT is assumed.
           * In effect, this starts the NoteEditor Activity in NotePad.
           */
                startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
                return true;
            case R.id.menu_sort:
          /*
           * Launches a new Activity using an Intent. The intent filter for the Activity
           * has to have action ACTION_PASTE. No category is set, so DEFAULT is assumed.
           * In effect, this starts the NoteEditor Activity in NotePad.
           */
                sortListItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method is called when the user context-clicks a note in the list. NotesList registers
     * itself as the handler for context menus in its ListView (this is done in onCreate()).
     * <p>
     * The only available options are COPY and DELETE.
     * <p>
     * Context-click is equivalent to long-press.
     *
     * @param menu     A ContexMenu object to which items should be added.
     * @param view     The View for which the context menu is being constructed.
     * @param menuInfo Data associated with view.
     * @throws ClassCastException
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        // Tries to get the position of the item in the ListView that was long-pressed.
        try {
            // Casts the incoming data object into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            // If the menu object can't be cast, logs an error.
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        /*
         * Gets the data associated with the item at the selected position. getItem() returns
         * whatever the backing adapter of the ListView has associated with the item. In NotesList,
         * the adapter associated all of the data for a note with its list item. As a result,
         * getItem() returns that data as a Cursor.
         */
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);

        // If the cursor is empty, then for some reason the adapter can't get the data from the
        // provider, so returns null to the caller.
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu, menu);

        // Sets the menu header to be the title of the selected note.
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

        // Append to the
        // menu items for any other activities that can do stuff with it
        // as well.  This does a query on the system for any activities that
        // implement the ALTERNATIVE_ACTION for our data, adding a menu item
        // for each one that is found.
        Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(),
                Integer.toString((int) info.id)));
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);
    }

    /**
     * This method is called when the user selects an item from the context menu
     * (see onCreateContextMenu()). The only menu items that are actually handled are DELETE and
     * COPY. Anything else is an alternative option, for which default handling should be done.
     *
     * @param item The selected menu item
     * @return True if the menu item was DELETE, and no default processing is need, otherwise false,
     * which triggers the default handling of the item.
     * @throws ClassCastException
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        /*
         * Gets the extra info from the menu item. When an note in the Notes list is long-pressed, a
         * context menu appears. The menu items for the menu automatically get the data
         * associated with the note that was long-pressed. The data comes from the provider that
         * backs the list.
         *
         * The note's data is passed to the context menu creation routine in a ContextMenuInfo
         * object.
         *
         * When one of the context menu items is clicked, the same data is passed, along with the
         * note ID, to onContextItemSelected() via the item parameter.
         */
        try {
            // Casts the data object in the item into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {

            // If the object can't be cast, logs an error
            Log.e(TAG, "bad menuInfo", e);

            // Triggers default processing of the menu item.
            return false;
        }
        // Appends the selected note's ID to the URI sent with the incoming Intent.
        Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);

        /*
         * Gets the menu item's ID and compares it to known actions.
         */
        switch (item.getItemId()) {
            case R.id.context_open:
                // Launch activity to view/edit the currently selected item
                startActivity(new Intent(Intent.ACTION_EDIT, noteUri));
                return true;
            //BEGIN_INCLUDE(copy)
            case R.id.context_copy:
                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                // Copies the notes URI to the clipboard. In effect, this copies the note itself
                clipboard.setPrimaryClip(ClipData.newUri(   // new clipboard item holding a URI
                        getContentResolver(),               // resolver to retrieve URI info
                        "Note",                             // label for the clip
                        noteUri)                            // the URI
                );

                // Returns to the caller and skips further processing.
                return true;
            //END_INCLUDE(copy)
            case R.id.context_delete:

                // Deletes the note from the provider by passing in a URI in note ID format.
                // Please see the introductory note about performing provider operations on the
                // UI thread.
                getContentResolver().delete(
                        noteUri,  // The URI of the provider
                        null,     // No where clause is needed, since only a single note ID is being
                        // passed in.
                        null      // No where clause is used, so no where arguments are needed.
                );

                // Returns to the caller and skips further processing.
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * This method is called when the user clicks a note in the displayed list.
     * <p>
     * This method handles incoming actions of either PICK (get data from the provider) or
     * GET_CONTENT (get or create data). If the incoming action is EDIT, this method sends a
     * new Intent to start NoteEditor.
     *
     * @param l        The ListView that contains the clicked item
     * @param v        The View of the individual item
     * @param position The position of v in the displayed list
     * @param id       The row ID of the clicked item
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

        // Gets the action from the incoming Intent
        String action = getIntent().getAction();

        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {

            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }

    /**
     * 自定义方法，用来对listview的item排序
     * customized method which can sort the sequence of items in the listview
     */
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

}
