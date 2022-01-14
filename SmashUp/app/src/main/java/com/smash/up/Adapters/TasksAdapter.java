package com.smash.up.Adapters;

import android.view.*;
import android.widget.*;
import com.smash.up.R;
import com.smash.up.Itens.*;
import android.content.*;
import java.util.*;
import com.malinskiy.materialicons.*;
import android.graphics.*;

public class TasksAdapter extends RecyclerAdapter<TasksAdapter.VHolder>
{
	TaskService taskService;
	List<SugarMusicRecord> tasks= new ArrayList<>();
	public TasksAdapter(TaskService taskService){
		this.taskService=taskService;
	}

	
	
	public List<SugarMusicRecord> getItens()
	{
		return tasks;
	}
	public final void reload(){
		tasks=SugarMusicRecord.listAll(SugarMusicRecord.class);
		notifyDataSetChanged();
	}
	public void updateField(SugarMusicRecord t){
		notifyItemChanged(tasks.indexOf(t));
	}
	@Override
	public VHolder onCreateViewHolder(ViewGroup group, int type)
	{
		
		return new VHolder(LayoutInflater.from(taskService).inflate(R.layout.send_task_item,group,false));
	}

	@Override
	public void onBindViewHolder(VHolder holder, int position)
	{
		SugarMusicRecord task=tasks.get(position);
		holder.nome.setText(task.getNome());
		holder.nome.setTextAppearance(taskService,R.style.TextAppearance_AppCompat);
		holder.nome.setTextColor(Color.BLACK);
		if(task.getState()!=null){
			if(task.getState().equals("paused"))holder.cancel.setImageDrawable(new IconDrawable(taskService,Iconify.IconValue.zmdi_play).colorRes(R.color.accent).sizeDp(25));
			else if(task.getState().equals("stopped"))holder.cancel.setImageDrawable(new IconDrawable(taskService,Iconify.IconValue.zmdi_upload).colorRes(R.color.accent).sizeDp(25));
			else if(task.getState().equals("sending"))holder.cancel.setImageDrawable(new IconDrawable(taskService,Iconify.IconValue.zmdi_pause).colorRes(R.color.accent).sizeDp(25));
			else if(task.getState().equals("done"))holder.cancel.setImageDrawable(new IconDrawable(taskService,Iconify.IconValue.zmdi_check_circle).colorRes(R.color.green_a400).sizeDp(25));
			else holder.cancel.setImageDrawable(new IconDrawable(taskService,Iconify.IconValue.zmdi_upload).colorRes(R.color.red_a400).sizeDp(25));
		}else holder.cancel.setImageDrawable(new IconDrawable(taskService,Iconify.IconValue.zmdi_bug).colorRes(R.color.blue_grey_800).sizeDp(25));
		holder.progress.setMax((int)task.getMax());
		holder.progress.setBackgroundColor(Color.WHITE);
		
		if(task.getProgressDone()>0)holder.progress.setProgress((int)task.getProgressDone());

	}
	@Override
	public int getItemCount()
	{
		return tasks.size();
	}
	public class VHolder extends RecyclerAdapter.Holder{
		View v;
		ImageView cancel;TextView nome;
		ProgressBar progress;
		
		public VHolder(View v){
			super(v);
			this.v=v;
			cancel=(ImageView)v.findViewById(R.id.sendtaskitemImageViewCancel);
			cancel.setOnClickListener(this);
			nome=(TextView)v.findViewById(R.id.sendtaskitemTextViewNome);
			progress=(ProgressBar)v.findViewById(R.id.sendtaskitemProgressBar);
			cancel.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v)
		{
			super.onClick(v);
			if(v.equals(cancel))taskService.upload(tasks.get(getPosition()),null);
			else taskService.cancel(tasks.get(getPosition()));
		}
	}
}
