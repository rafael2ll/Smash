package com.smash.up.Itens;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;
import java.io.*;
import com.smash.up.SubActivities.*;
import com.smash.up.Adapters.*;
import com.google.firebase.database.*;
import android.support.v7.widget.*;
import android.support.design.widget.*;
import android.util.*;
import java.util.*;
import android.widget.*;

public class TaskService extends Service
{
	IBinder binder=new TaskBind();
	int send_position=0;
	
	public final static String ACTION_CANCEL="com.smash.up.task.cancel";
	SendNotification mSendNotification;
	HashMap<SugarMusicRecord,UploadTask> uploadTasks;
	public TasksAdapter adapter=new TasksAdapter(this);
	FirebaseStorage storage;
	public static FirebaseDatabase database;
	public static DatabaseReference dbRef;
	public StorageReference storageRef;
	String sendind="sending",paused="paused",stopped="stopped",done="done";
	public static String ACTION_START="com.smash.music.ACTION_START",
	ACTION_PAUSE="com.smash.music.ACTION_STOP",
	ACTION_TOGGLE="com.smash.music.ACTION_TOGGLE",
	ACTION_CONTINUE="com.smash.music.ACTION_CONTINUE";

	public void upAll()
	{
		try{
			if(adapter.getItens().get(0).getState()!=sendind)upload(adapter.getItens().get(0), new OnSendDone(){

					@Override
					public void onSent()
					{
						if(adapter.getItemCount()!=0)if(adapter.getItens().get(0).getState()!=sendind)upload(adapter.getItens().get(0),this);
					}
				});
		}catch(IndexOutOfBoundsException i){}
	}
	
	public void cancel(SugarMusicRecord get)
	{
		if(uploadTasks.containsKey(get)){
			if(!uploadTasks.get(get).isCanceled())uploadTasks.get(get).cancel();
		}
	}
	@Override
	public IBinder onBind(Intent p1)
	{
		
		return binder;
	}
	public void attachRecyclerViewAdapter(RecyclerView v){
		v.setLayoutManager(new LinearLayoutManager(this));
		v.setAdapter(adapter);
		Snackbar.make(v,String.valueOf(adapter.getItemCount()),2000).show();
	}

	@Override
	public void onCreate()
	{
		mSendNotification=new SendNotification(this);
		mSendNotification.startNotification();
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		uploadTasks=new HashMap<>();
		if(intent.getAction()!=null){
			if(intent.getAction().equals(ACTION_CANCEL)){
				mSendNotification.cancel();
				stopSelf();
			}
		}
		try{
		
		storage=FirebaseStorage.getInstance(Commons.mApp);
		storageRef=storage.getReference(Commons.mApp.getOptions().getStorageBucket());
		database=FirebaseDatabase.getInstance(Commons.mApp);
		dbRef=database.getReference("musicas");
		}catch(Exception e){}
		adapter.reload();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mSendNotification.cancel();
		for(SugarMusicRecord r : adapter.getItens()){
			r.setError();
		}
	}
	
	
	
	public void upload(final SugarMusicRecord record,final OnSendDone o){
		if(record.getState().equals(done))return;
		if(record.getState().equals(paused)){
			if(uploadTasks.containsKey(record))uploadTasks.get(record).resume();
			return;
		}
		if(record.getState().equals(sendind) && uploadTasks.containsKey(record)){
			uploadTasks.get(record).pause();
			return;
		}
		try
		{
			FileInputStream inputFileStream= new FileInputStream(new File(record.path));
			UploadTask uTask=Commons.mDefaultStorageRef.child("musicas/" + record.nome + record.albumKey + ".mp3")
			.putStream(inputFileStream);
			UploadListener listener=new UploadListener(record,o);
			uTask.addOnSuccessListener(listener).addOnPausedListener(listener)
			.addOnProgressListener(listener).addOnFailureListener(listener);
			record.setSending();
			if(!uploadTasks.containsKey(record))uploadTasks.put(record,uTask);
			
		}
		catch (FileNotFoundException e)
		{}
		
	}
	
	public class UploadListener implements OnFailureListener,OnProgressListener<UploadTask.TaskSnapshot>,OnSuccessListener<UploadTask.TaskSnapshot>,OnPausedListener<UploadTask.TaskSnapshot>
	{
		SugarMusicRecord record;
		OnSendDone listener;
		long totalHex;
		public UploadListener(SugarMusicRecord record,OnSendDone o){
			this.record=record;this.listener=o;
			totalHex=new File(record.getPath()).length();
		}
		@Override
		public void onFailure(Exception p1)
		{
			record.setError();
			Log.w("Upload Error",p1);
			if(listener!=null)listener.onSent();
			adapter.updateField(record);
		}

		@Override
		public void onSuccess(final UploadTask.TaskSnapshot p1)
		{
			final Uri downloadUrl = p1.getDownloadUrl();
			record.setState(done);
			record.save();
			final Musica m= new Musica();
			m.createFromSugar(record, new ValueEventListener(){
					@Override
					public void onDataChange(DataSnapshot albumSnap)
					{
						Album b=albumSnap.getValue(Album.class);
						m.setAlbumNome(b.getNome());
						m.setAlbumKey(b.getKey());
						Artista.getArtista(b.getArtistaKey(),new ValueEventListener(){

								@Override
								public void onDataChange(DataSnapshot artistaSnap)
								{
									Artista artista=artistaSnap.getValue(Artista.class);
									m.setArtistaKey(artista.getKey());
									m.setArtistaNome(artista.getNome());
									m.setMusicaUri(p1.getDownloadUrl().toString());
									m.setMusicaKey(p1.getMetadata().getName());
									String key=Commons.mDefaultDatabaseRef.child("musicas").push().getKey();
									m.setKey(key);
									Commons.mDefaultDatabaseRef.child("musicas").child(key).setValue(m, new DatabaseReference.CompletionListener(){
											@Override
											public void onComplete(DatabaseError p1, DatabaseReference p2)
											{
												if(p1==null){
													Toast.makeText(TaskService.this,"Musica enviada",2000).show();
													mSendNotification.updateSent(record.nome);
													record.delete();
													adapter.reload();
													}else{
													Log.w("Error",p1.toException());
													Toast.makeText(TaskService.this,"Erro",2000).show();
												}
												if(listener!=null)listener.onSent();
											}
											});
										}

								@Override
								public void onCancelled(DatabaseError p1)
								{
									listener.onSent();
								}
							});
						}

					@Override
					public void onCancelled(DatabaseError p1)
					{
						// TODO: Implement this method
					}
				});
		}

		@Override
		public void onPaused(UploadTask.TaskSnapshot p1)
		{
			record.setPaused();
			
		}

		@Override
		public void onProgress(UploadTask.TaskSnapshot p1)
		{
			long meNow=p1.getBytesTransferred();
			System.out.println("Total:"+totalHex+"\nMeNow:"+meNow);
			double progress = 100 * (meNow/totalHex);
			System.out.println("Upload is " + progress + "% done");
			mSendNotification.updatePorcentagem((int)totalHex,(int)meNow);
			record.setProgressDone(p1.getBytesTransferred());
			record.save();
			adapter.updateField(record);
		}
		
		
	}
	public interface OnSendDone{
		public void onSent();
	}
	public class TaskBind extends Binder{
		public TaskService getService(){
			return TaskService.this;
		}
	}
}
