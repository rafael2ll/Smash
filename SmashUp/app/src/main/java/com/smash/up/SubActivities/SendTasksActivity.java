package com.smash.up.SubActivities;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import com.smash.up.R;
import android.view.View.*;
import android.view.*;
import com.smash.up.Itens.*;
import com.smash.up.Adapters.*;
import android.content.*;
import android.util.*;
import com.malinskiy.materialicons.*;
import android.graphics.*;
import android.support.v7.view.menu.*;

public class SendTasksActivity extends AppCompatActivity
{
	RecyclerView taskRecyclerView;
	TaskService mService;
	boolean isBind=false;
	FloatingActionButton addTask;
	Toolbar toolbar;
	ServiceConnection connection=new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName p1, IBinder p2)
		{
			mService=((TaskService.TaskBind)p2).getService();
			isBind=true;
			if(addTask!=null)addTask.setEnabled(true);
			Log.w("TaskService","binded");
			taskRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
			mService.attachRecyclerViewAdapter(taskRecyclerView);
		}

		@Override
		public void onServiceDisconnected(ComponentName p1)
		{
			isBind=false;
		}
	};

	@Override
	protected void onStart()
	{
		bindService(new Intent(this,TaskService.class),connection,BIND_AUTO_CREATE);
		startService(new Intent(this,TaskService.class));
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.send_tasks,menu);
		MenuItem upAll=menu.findItem(R.id.upAll);
		upAll.setIcon(new IconDrawable(this,Iconify.IconValue.zmdi_upload).colorRes(R.color.icons)
		.actionBarSize());
		upAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		upAll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem p1)
				{
					mService.upAll();
					return false;
				}
			});
		return true;
	}

	
	
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.send_tasks);
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle("Send Area");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem p1)
				{
					if(p1.getItemId()!=R.id.upAll)finish();
					return false;
				}
			});
		addTask=(FloatingActionButton)findViewById(R.id.sendtasksFloatingActionButton);
		addTask.setVisibility(View.GONE);
		
	}
}
