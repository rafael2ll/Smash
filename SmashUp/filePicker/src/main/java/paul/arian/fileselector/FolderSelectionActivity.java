package paul.arian.fileselector;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.commonsware.cwac.merge.*;
import java.io.*;
import java.util.*;
import paul.arian.fileselector.*;

import android.support.v7.widget.Toolbar;
import paul.arian.fileselector.R;

public class FolderSelectionActivity extends AppCompatActivity {

    private static final String TAG = "FileSelection";
    private static final String FILES_TO_UPLOAD = "upload";
    File mainPath = new File(Environment.getExternalStorageDirectory()+"");
    private ArrayList<File> resultFileList;
	Toolbar toolbar;
    private ListView directoryView;
    private ArrayList<File> directoryList = new ArrayList<File>();
    private ArrayList<String> directoryNames = new ArrayList<String>();
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<String> fileNames = new ArrayList<String>();
    
    Boolean switcher = false;
    String primary_sd;
    String secondary_sd;

    int index = 0;
    int top = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selection);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        directoryView = (ListView)findViewById(R.id.directorySelectionList);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		configToolbar();

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
					}catch(Throwable e){
						mainPath = lastPath;
						loadLists();
					}
				}
			});
		
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.file_picker_menu,menu);
		menu.findItem(R.id.selectAll).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.allDone).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		setImageToMenuItem(menu.findItem(R.id.selectAll), R.drawable.ic_check);
		setImageToMenuItem(menu.findItem(R.id.allDone), R.drawable.ic_select_all);

		return true;
	}

	private void configToolbar()
	{
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem p1)
				{
					if(p1.getItemId()==R.id.allDone)ok();
					return false;
				}
			});
	}

	private void setImageToMenuItem(MenuItem m, int res)
	{
		m.setIcon(res);
	}
	
    @Override
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
        Intent result = this.getIntent();
        result.putExtra(FILES_TO_UPLOAD, mainPath);
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
            directoryView.setAdapter(directoryAdapter);

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



            setTitle(mainPath.toString());
            iconload();
       // }
    }

    public void iconload(){
        String[] foldernames = new String[directoryNames.size()];
        foldernames = directoryNames.toArray(foldernames);

        String[] filenames = new String[fileNames.size()];
        filenames = fileNames.toArray(filenames);

        CustomListSingleOnly adapter1 = new CustomListSingleOnly(FolderSelectionActivity.this, directoryNames.toArray(foldernames), mainPath.getPath());
        CustomListSingleOnly adapter2 = new CustomListSingleOnly(FolderSelectionActivity.this, fileNames.toArray(filenames), mainPath.getPath());


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
