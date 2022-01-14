package paul.arian.fileselector;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.commonsware.cwac.merge.MergeAdapter;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.view.View.*;

public class FileSelectionActivity extends AppCompatActivity {

    private static final String TAG = "FileSelection";
    public static final String FILES_TO_UPLOAD = "upload";
	
    File mainPath = new File(Environment.getExternalStorageDirectory()+"");
    private ArrayList<File> resultFileList;
	private Toolbar toolbar;
    private ListView directoryView;
    private ArrayList<File> directoryList = new ArrayList<File>();
    private ArrayList<String> directoryNames = new ArrayList<String>();
    //private ListView fileView;
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<String> fileNames = new ArrayList<String>();
   
 
    Boolean Switch = false;
    String primary_sd;
    String secondary_sd;

    int index = 0;
    int top = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selection);
      
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		configToolbar();
		toolbar.setTitleTextAppearance(this,R.style.TextAppearance_Widget_AppCompat_Toolbar_Subtitle);
		toolbar.setTitleTextColor(Color.WHITE);
		
        directoryView = (ListView)findViewById(R.id.directorySelectionList);
        
        loadLists();ExtStorageSearch();
		
        directoryView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                index = directoryView.getFirstVisiblePosition();
                View v = directoryView.getChildAt(0);
                top = (v == null) ? 0 : v.getTop();
				
                File lastPath = mainPath;
                try {
                    if (position < directoryList.size()) {
                        mainPath = directoryList.get(position);
                        loadLists();
                    }
                }catch (Throwable e){
                    mainPath = lastPath;
                    loadLists();
                }}
        });
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.file_picker_menu,menu);
		menu.findItem(R.id.selectAll).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.allDone).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		setImageToMenuItem(menu.findItem(R.id.selectAll), R.drawable.ic_select_all);
		setImageToMenuItem(menu.findItem(R.id.allDone), R.drawable.ic_check);

		return true;
	}
	
	private void configToolbar()
	{
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileSelectionActivity.this.finish();
                }
            });
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem p1)
				{
					if(p1.getItemId()==R.id.selectAll){
						if(!Switch){
								for (int i = directoryList.size(); i < directoryView.getCount(); i++){
									directoryView.setItemChecked(i, true);
								}
								p1.setTitle("Select all");
								Switch = true;
							}else if(Switch){
								for (int i = directoryList.size(); i < directoryView.getCount(); i++) {
									directoryView.setItemChecked(i, false);
								}
								Switch = false;
								p1.setTitle("Unselect all");
							}
							}
						else if(p1.getItemId()== R.id.allDone)ok();
					return false;
				}
		});
	}

	private void setImageToMenuItem(MenuItem m,int res)
	{
		m.setIcon(res);
	}

    public void onBackPressed() {
        try {
            if(mainPath.equals(Environment.getExternalStorageDirectory().getParentFile().getParentFile())){
                finish();
            }else{
                File parent = mainPath.getParentFile();
                mainPath = parent;
                loadLists();
                directoryView.setSelectionFromTop(index, top);
            }

        }catch (Throwable e){

        }
    }

    public void ok(){
        Log.d(TAG, "Upload clicked, finishing activity");


        resultFileList = new ArrayList<File>();

        for(int i = 0 ; i < directoryView.getCount(); i++){
            if(directoryView.isItemChecked(i)){
                resultFileList.add(fileList.get(i-directoryList.size()));
            }
        }
        if(resultFileList.isEmpty()){
            Log.d(TAG, "Nothing selected");
            finish();
        }
        Log.d(TAG, "Files: "+resultFileList.toString());
        Intent result = this.getIntent();
        result.putExtra(FILES_TO_UPLOAD, resultFileList);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void loadLists(){
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        FileFilter directoryFilter = new FileFilter(){
            public boolean accept(File file){
                return file.isDirectory();
            }
        };
        //if(mainPath.exists() && mainPath.length()>0){
        //Lista de directorios
        File[] tempDirectoryList = mainPath.listFiles(directoryFilter);

        if (tempDirectoryList != null && tempDirectoryList.length > 1) {
            Arrays.sort(tempDirectoryList, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }
        directoryList = new ArrayList<File>();
        directoryNames = new ArrayList<String>();
        for(File file: tempDirectoryList){
            directoryList.add(file);
            directoryNames.add(file.getName());
        }
		
        ArrayAdapter<String> directoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, directoryNames);
        //Lista de ficheros
        File[] tempFileList = mainPath.listFiles(fileFilter);

        if (tempFileList != null && tempFileList.length > 1) {
            Arrays.sort(tempFileList, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }
        fileList = new ArrayList<File>();
        fileNames = new ArrayList<String>();
        for(File file : tempFileList){
            fileList.add(file);
            fileNames.add(file.getName());
        }
        iconload();
      	setTitle(mainPath.toString());
        //}
    }
    public void iconload(){
        String[] foldernames = new String[directoryNames.size()];
        foldernames = directoryNames.toArray(foldernames);

        String[] filenames = new String[fileNames.size()];
        filenames = fileNames.toArray(filenames);

        CustomListSingleOnly adapter1 = new CustomListSingleOnly(FileSelectionActivity.this, directoryNames.toArray(foldernames), mainPath.getPath());
        CustomList adapter2 = new CustomList(FileSelectionActivity.this, fileNames.toArray(filenames), mainPath.getPath());


        MergeAdapter adap = new MergeAdapter();

        adap.addAdapter(adapter1);
        adap.addAdapter(adapter2);


        directoryView.setAdapter(adap);
    }

    public void ExtStorageSearch(){
        String[] extStorlocs = {"/storage/sdcard1","/storage/extsdcard","/storage/sdcard0/external_sdcard","/mnt/extsdcard",
                "/mnt/sdcard/external_sd","/mnt/external_sd","/mnt/media_rw/sdcard1","/removable/microsd","/mnt/emmc",
                "/storage/external_SD","/storage/ext_sd","/storage/removable/sdcard1","/data/sdext","/data/sdext2",
                "/data/sdext3","/data/sdext4","/storage/sdcard0"};

        //First Attempt
        primary_sd = System.getenv("EXTERNAL_STORAGE");
        secondary_sd = System.getenv("SECONDARY_STORAGE");


        if(primary_sd == null) {
            primary_sd = Environment.getExternalStorageDirectory()+"";
        }
        if(secondary_sd == null) {//if fail, search among known list of extStorage Locations
            for(String string: extStorlocs){
                if((new File(string)).exists() && (new File(string)).isDirectory() ){
                    secondary_sd = string;
                    break;
                }
            }
        }

    }



}
