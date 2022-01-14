package com.smash.up.Helpers;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.util.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.smash.up.Adapters.*;
import com.smash.up.Models.*;
import java.io.*;
import java.util.*;
import com.smash.up.*;

public class TaskService extends Service
{
	IBinder binder=new TaskBind();
	int send_position=0;
	
	public final static String ACTION_CANCEL="com.smash.up.task.cancel";
	SendNotification mSendNotification;
	HashMap<SugarMusicRecord,UploadTask> uploadTasks = new HashMap<>();
	
	public static List<SugarMusicRecord> mSendItens = new ArrayList<>();
	public OnSendListener mListener;
	
	FirebaseStorage storage;
	public static FirebaseDatabase database;
	public static DatabaseReference dbRef;
	public StorageReference storageRef;
	String sendind="sending",paused="paused",stopped="stopped",done="done";
	Context ctx;
	public static String ACTION_START="com.smash.music.ACTION_START",
	ACTION_PAUSE="com.smash.music.ACTION_STOP",
	ACTION_TOGGLE="com.smash.music.ACTION_TOGGLE",
	ACTION_CONTINUE="com.smash.music.ACTION_CONTINUE";

	
	@Override
	public IBinder onBind(Intent p1)
	{
		
		return binder;
	}
	
	@Override
	public void onCreate()
	{
		mSendNotification=new SendNotification(this);
		mSendNotification.startNotification();
		mSendItens= SugarMusicRecord.listAll(SugarMusicRecord.class);
		ctx = getApplicationContext();
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

		storage=FirebaseStorage.getInstance();
		storageRef=storage.getReference();
		database=FirebaseDatabase.getInstance();
		
		dbRef=database.getReference("musicas");
	
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
	}
	public void setListener(OnSendListener mListener){
		this.mListener= mListener;
	}
	public void upAll()
	{
		
		try{
			if(mSendItens.get(0).getState()!=sendind)upload(mSendItens.get(0), new OnSendDone(){

						@Override
						public void onSent()
						{
							if(mSendItens.size()!=0)if(mSendItens.get(0).getState()!=sendind)upload(mSendItens.get(0),this);
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
			UploadTask uTask=Musica.getMusicasStorage().child(record.artistaKey)
			.child(record.albumKey).child(record.nome+".mp3")
			.putStream(inputFileStream);
			UploadListener listener=new UploadListener(record,o);
			uTask.addOnSuccessListener(listener).addOnPausedListener(listener)
			.addOnProgressListener(listener).addOnFailureListener(listener);
			record.setSending();
			if(!uploadTasks.containsKey(record))uploadTasks.put(record,uTask);
			mListener.onStartSending(record);
			
		}
		catch (FileNotFoundException e)
		{
			Log.w("FireCrasher.err",e);
		}
		
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
			mSendItens.remove(record);
			Log.w("Upload Error",p1);
			mListener.onSent(false);
			if(listener!=null)listener.onSent();
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
						m.setArtistaKey(b.getArtistaKey());
						m.setSentData(new Date());
						m.setArtistaNome(b.artistaNome);
						m.setMusicaUri(p1.getDownloadUrl().toString());
						m.setMusicaKey(p1.getMetadata().getName());
						String key=Musica.getMainRef().push().getKey();
						m.setKey(key);
						Musica.getMainRef().child(key).setValue(m, new DatabaseReference.CompletionListener(){
								@Override
								public void onComplete(DatabaseError p1, DatabaseReference p2)
								{
									if(p1==null){
										mSendNotification.updateSent(record.nome);
										mSendItens.remove(record);
										record.delete();
										mListener.onSent(true);
										try{
										PrettyToast.showSuccess(MainActivity.getContext(),"Musica enviada!");
										}catch(Exception e){
											Log.w("FireCrasher.err", e);
										}
									}else{
										Log.w("Error",p1.toException());
										PrettyToast.showError(TaskService.this,"Falha no Envio");
									}
									if(listener!=null)listener.onSent();
								}
							});
							
						}

					@Override
					public void onCancelled(DatabaseError p1)
					{
						mListener.onSent(false);
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
			mListener.onSendProgress((int)totalHex, (int)meNow);
		}
		
		
	}
	public interface OnSendDone
	{

		public void onSent();

		
	}
	public interface OnSendListener{
		public void onStartSending(SugarMusicRecord o);
		public void onSendProgress(int max, int sent);
		public void onSent(boolean b);
	}
	public class TaskBind extends Binder{
		public TaskService getService(){
			return TaskService.this;
		}
	}
}
