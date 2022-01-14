package com.smash.player.Holders;
import android.content.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.smash.player.*;
import com.smash.player.Helpers.*;
import com.smash.player.Models.*;
import org.cmc.music.metadata.*;
import org.cmc.music.myid3.*;
import android.net.*;
import java.io.*;
import org.cmc.music.common.*;

public class MainItensHolder extends RecyclerAdapter.Holder
{
	TextView musicaNome,musicaArtista,albumNome,artistaNome;
	ImageView musicMore,albumImage,artistaImage;
	Musica m;
	MusicMetadataSet src_set = null;
	IMusicMetadata metadata = null;
	Context ctx;
	public final static enum Type{
		MUSIC,ALBUM,ARTISTA
	}
	public MainItensHolder(View v){
		super(v);
		Log.d("FireCrasher.err",v.getTag().toString());
		if(v.getTag().toString().equals(String.valueOf(R.layout.music_main_item))){
			Log.i("FireCrasher.err","Here");
			musicaNome=(TextView)v.findViewById(R.id.musicmainitemTextViewNome);
			musicaArtista=(TextView)v.findViewById(R.id.musicmainitemTextViewArtista);
			musicMore=(ImageView)v.findViewById(R.id.musicmainitemImageViewMore);
			
			v.setOnClickListener(this);
			v.setOnLongClickListener(new View.OnLongClickListener(){

					@Override
					public boolean onLongClick(View p1)
					{
						 final Uri trackUri = ContentUris.withAppendedId(
						 android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,m.getId());
						 try
						 {
						 src_set = new MyID3().read(new File(m.path));
						 if(new File(trackUri.toString()).isFile())Log.w("File","is "+trackUri.getPath());
						 else Log.w("File","isnt "+trackUri.toString()+ trackUri.getPath());
						 metadata= src_set.getSimplified();

						 View v=LayoutInflater.from(ctx).inflate(R.layout.edit_music_dialog, null, false);
						 final EditText nome=(EditText)v.findViewById(R.id.editmusicdialogEditTextNome),
						 artista=(EditText)v.findViewById(R.id.editmusicdialogEditTextArtista),
						 album=(EditText)v.findViewById(R.id.editmusicdialogEditTextAlbum);
						 nome.setText(metadata.getSongTitle());
						 artista.setText(metadata.getArtist());
						 album.setText(metadata.getAlbum());
						 new AlertDialog.Builder(ctx).setView(v)
						 .setPositiveButton("SALVAR", new DialogInterface.OnClickListener(){

						 @Override
						 public void onClick(DialogInterface p1, int p2)
						 {
						 metadata.setAlbum(album.getText().toString().trim());
						 metadata.setArtist(artista.getText().toString().trim());
						 metadata.setSongTitle(nome.getText().toString().trim());
						 try
						 {
							 
						 new MyID3().update(new File(m.path), src_set, metadata);
						 }
						 catch (IOException e)
						 {
						 throw new RuntimeException(e);
						 }
						 catch (ID3WriteException e)
						 {
						 throw new RuntimeException(e);
						 }
						 }


						 }).show();
						 }catch(Exception e){
						 throw new RuntimeException(e);
						 }
						return false;
					}
				});
		}else if(v.getTag()==R.layout.album_main_item){
			albumNome=(TextView)v.findViewById(R.id.albummainitemTextView);
			albumImage=(ImageView)v.findViewById(R.id.albummainitemImageView);
		}else if(v.getTag()==R.layout.artista_main_item){
			artistaNome=(TextView)v.findViewById(R.id.artistamainitemTextView);
			artistaImage=(ImageView)v.findViewById(R.id.artistamainitemImageView);
		}
	}
	public void populateMusica(Context ctx,Musica m){
		this.m=m;this.ctx=ctx;
		musicaNome.setTextAppearance(ctx,R.style.TextAppearance_AppCompat);
		musicaNome.setText(m.getNome());
		musicaArtista.setText(m.getArtista());
	}
	
	public void onClick(View view){
		MusicProvider.setToPlay(ctx,MusicProvider.allMusics);
		MusicProvider.setToPlayPosition(MusicProvider.allMusics.indexOf(m));
		ctx.startService(new Intent(ctx,MusicService.class).setAction(MusicService.ACTION_START));
		ctx.startService(new Intent(ctx,MusicService.class).setAction(MusicService.ACTION_PLAY));
	}
}
