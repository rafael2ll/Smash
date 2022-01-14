package com.smash.up.Itens;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.common.io.*;
import com.google.firebase.storage.*;
import com.smash.up.*;
import com.smash.up.Adapters.*;
import java.io.*;
import java.util.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.smash.up.R;
import paul.arian.fileselector.*;
import com.squareup.picasso.*;
import android.media.*;
import com.malinskiy.materialicons.*;

public class NewTaskDialog // extends DialogFragment implements OnClickListener
{
	/*View v;
	SugarMusicRecord record;
	MediaPlayer mp;
	ProgressBar imageUploadProgress;
	int IMAGE_FILE_CODE=155,MUSIC_FILE_CODE=156;
	String imageLink=new String(),musicaLink=new String();
	TextInputLayout nome,albumNome,artistaNome;
	Button attachMusicFile,save;
	Context ctx;
	ImageView attachImage,playMusic;
	TasksAdapter adapter;
	public NewTaskDialog(TasksAdapter t){
		this.adapter=t;
		ctx=getActivity();
		record=new SugarMusicRecord();
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		v=LayoutInflater.from(getActivity()).inflate(R.layout.music_upload,null,false);
		imageUploadProgress=(ProgressBar)v.findViewById(R.id.musicuploadProgressBar);
		nome=findTIL(R.id.tilMusicName);
		albumNome=findTIL(R.id.tilAlbumName);
		artistaNome=findTIL(R.id.tilArtistaName);
		attachImage=(ImageView)v.findViewById(R.id.musicuploadImageView);
		attachMusicFile=findBut(R.id.musicuploadButtonMusic);
		save=findBut(R.id.musicuploadButtonSend);
		attachMusicFile.setOnClickListener(this);
		attachImage.setOnClickListener(this);
		save.setOnClickListener(this);
		imageUploadProgress.setIndeterminate(false);
		imageUploadProgress.setVisibility(View.GONE);
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
		.setView(v);
		return builder.create();
	}
	
	@Override
	public void onClick(View p1)
	{
		if(p1.equals(playMusic)){
			if(mp.isPlaying())mp.pause();else mp.start();
		}
		String albumN=albumNome.getEditText().getText().toString(),
		musicaN=nome.getEditText().getText().toString(),
		artistaN=artistaNome.getEditText().getText().toString();
		if(p1.equals(attachMusicFile)){
			Intent intent = new Intent(ctx, FileSelectionActivity.class);
			startActivityForResult(intent, MUSIC_FILE_CODE);
		}else if(p1.equals(attachImage)){
			Intent intent = new Intent(getActivity(), FileSelectionActivity.class);
			startActivityForResult(intent, IMAGE_FILE_CODE);
			}else if(p1.equals(save)){
				if(musicaLink==null || imageLink==null){
					Snackbar.make(v,"Nem tudo esta preenchido",2000).show();
					return;
				}
			 
			record.setNome(musicaN).setAlbumNome(albumN)
			.setArtistaNome(artistaN).setAlbumArt(imageLink)
			.setMusicaLink(musicaLink)
			.setMusicaIsSent(false).setAllDone(false);
			record.save();
			adapter.reload();
			dismiss();
		}
		
	}

	@TargetApi(Build . VERSION_CODES .JELLY_BEAN )
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_FILE_CODE && resultCode == Activity .RESULT_OK && Commons.mApp!=null ){
			ArrayList<File> Files = (ArrayList<File>) data.getSerializableExtra(FileSelectionActivity.FILES_TO_UPLOAD); //file array list
			File image=Files.get(0);
			Snackbar.make(v,image.getAbsolutePath(),2000).show();
			Picasso.with(getActivity()).load(image).fit().centerCrop().into(attachImage);
			imageLink=image.getAbsolutePath();
		}
		else if(requestCode == MUSIC_FILE_CODE && resultCode== Activity.RESULT_OK){
			ArrayList<File> Files = (ArrayList<File>) data.getSerializableExtra(FileSelectionActivity.FILES_TO_UPLOAD); //file array list
			File musica=Files.get(0);
			playMusic=(ImageView)v.findViewById(R.id.musicuploadImageViewPlay);
			playMusic.setOnClickListener(this);
			playMusic.setImageDrawable(new IconDrawable(getActivity(),Iconify.IconValue.zmdi_play).colorRes(R.color.red_a700).sizeDp(30));
			musicaLink=musica.getAbsolutePath();
			mp = new MediaPlayer();try
			{
				mp.setDataSource(musicaLink);
			}
			catch (IOException e)
			{}
		}
	
}
	public TextInputLayout findTIL(int res){
		return (TextInputLayout)v.findViewById(res);
	}
	public Button findBut(int res){
		return(Button)v.findViewById(res);
	}*/
}
