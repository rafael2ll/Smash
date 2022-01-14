package com.smash.up.Adapters;
import android.app.*;
import android.content.*;
import android.media.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.cocosw.bottomsheet.*;
import com.google.firebase.database.*;
import com.malinskiy.materialicons.*;
import com.smash.up.*;
import com.smash.up.Dialogs.*;
import com.smash.up.Itens.*;
import java.util.*;

import android.support.v7.app.AlertDialog;
import com.smash.up.R;
import com.google.android.gms.tasks.*;
import android.net.*;
import android.os.*;
import java.io.*;
import com.google.firebase.storage.*;

public class SeeMusicasHolder extends RecyclerAdapter.Holder
{
	public TextView nome,artistaNome;
	public ImageView more;
	DatabaseReference aRef;
	Album album;
	public int position=0;
	Artista artista;
	MediaPlayer mPlayer;
	Musica mMusica;
	Activity ctx;
	public SeeMusicasHolder(View v){
		super(v);
		nome=(TextView)v.findViewById(R.id.seemusicsitemTextViewNome);
		artistaNome=(TextView)v.findViewById(R.id.seemusicsitemTextViewArtista);
		more=(ImageView)v.findViewById(R.id.seemusicsitemImageViewPlay);
		more.setOnClickListener(this);
		v.setOnClickListener(this);
		nome.setOnClickListener(this);
	}
	public void feedItWithArtista(Activity ctx,Artista artista,DatabaseReference ref){
		this.ctx=ctx;this.artista=artista;this.aRef=ref;
		more.setImageDrawable(new IconDrawable(ctx,Iconify.IconValue.zmdi_image_o).sizeDp(25));
		nome.setTextAppearance(ctx,R.style.TextAppearance_AppCompat);
		if(artista.getImageKey().equals("toAdd"))more.setImageDrawable(new IconDrawable(ctx,Iconify.IconValue.zmdi_image_o).colorRes(R.color.red_a400).sizeDp(25));
		else more.setImageDrawable(new IconDrawable(ctx,
														 Iconify.IconValue.zmdi_image_o).colorRes(R.color.green_a400).sizeDp(25));
		nome.setText(artista.getNome());
		artistaNome.setVisibility(View.GONE);
	}
	public void feedItWithAlbum(Activity ctx,Album album,DatabaseReference ref){
		this.ctx=ctx;this.album=album;this.aRef=ref;
		nome.setText(album.getNome());
		nome.setTextAppearance(ctx,R.style.TextAppearance_AppCompat);
		if(album.getImageUri().equals("toAdd"))more.setImageDrawable(new IconDrawable(ctx,
		Iconify.IconValue.zmdi_image_o).colorRes(R.color.red_a400).sizeDp(25));
		else more.setImageDrawable(new IconDrawable(ctx,
		Iconify.IconValue.zmdi_image_o).colorRes(R.color.green_a400).sizeDp(25));
		
		Artista.getArtista(album.getArtistaKey(), new ValueEventListener(){
				@Override
				public void onDataChange(DataSnapshot p1)
				{
					artistaNome.setText(p1.getValue(Artista.class).getNome());
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}
	
	public void feedItWithMusic(Activity ctx,Musica m){
		this.ctx=ctx;this.mMusica=m;
		nome.setTextAppearance(ctx,R.style.TextAppearance_AppCompat);
		more.setImageDrawable(new IconDrawable(ctx,Iconify.IconValue.zmdi_more_horiz).colorRes(R.color.grey_700).sizeDp(25));
		nome.setText(m.getNome());
		Artista.getArtista(m.getArtistaKey(),new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					artistaNome.setText(p1.getValue(Artista.class).getNome());
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{}
			});
	}
	@Override
	public void onClick(View v)
	{
		if(mMusica!=null){
		new BottomSheet.Builder(ctx, R.style.BottomSheet_Dialog)
            .title(mMusica.getNome())
            .icon(new IconDrawable(ctx,Iconify.IconValue.zmdi_info).colorRes(R.color.red_500))
  			.sheet(R.id.edit,new IconDrawable(ctx,Iconify.IconValue.zmdi_edit).colorRes(R.color.red_500),"Renomear")
			.sheet(R.id.delete,new IconDrawable(ctx,Iconify.IconValue.zmdi_delete).colorRes(R.color.red_500),"Apagar")
			.sheet(R.id.details,new IconDrawable(ctx,Iconify.IconValue.zmdi_info_outline).colorRes(R.color.red_500),"Detalhes")
			.sheet(R.id.details,"Ao usuário")
			.sheet(R.id.play,new IconDrawable(ctx,Iconify.IconValue.zmdi_play).colorRes(R.color.red_500),"Play")
			.sheet(R.id.addOnUser,new IconDrawable(ctx,Iconify.IconValue.zmdi_plus_circle_o).colorRes(R.color.red_500),"Adicionar")
            .listener(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.w("TAG",String.valueOf(which));
					switch(which){
						case R.id.edit:new SimpleDialog(mMusica).show(((AppCompatActivity)ctx).getSupportFragmentManager(),"");break;
						case R.id.addOnUser:User.addMusica(mMusica);break;
						case R.id.play:
							for(Musica a :MusicMasterHandler.getToPlay()){
								if(mMusica.getKey().equals(a.getKey()))position=MusicMasterHandler.getToPlay().indexOf(a);
							}
							Log.w("It",String.valueOf(position));
							MusicMasterHandler.setToPlayPosition(position);
							ctx.startService(new Intent(ctx,MusicService.class).setAction(MusicService.ACTION_PLAY));
							//ctx.startActivity(new Intent(ctx,PlayerActivity.class));
							break;
						case R.id.details:download(Uri.parse(mMusica.getMusicaUri()));
						break;
						case R.id.delete:Snackbar.make(nome, "Deseja realmente APAGAR?", 3000).setAction("OK", new OnClickListener(){

									@Override
									public void onClick(View p1)
									{
										mMusica.delete();
									}
								}).show();
					}
				}
			}).show();
		}
		else if(artista!=null){
			new BottomSheet.Builder(ctx, R.style.BottomSheet_Dialog)
				.title(artista.getNome())
				.icon(new IconDrawable(ctx,Iconify.IconValue.zmdi_info).colorRes(R.color.red_500))
				.sheet(R.id.edit,new IconDrawable(ctx,Iconify.IconValue.zmdi_edit).colorRes(R.color.red_500),"Renomear")
				.sheet(R.id.playermainTextViewNome,new IconDrawable(ctx,Iconify.IconValue.zmdi_image_o).colorRes(R.color.red_500),"Add Photo")
				.sheet(R.id.delete,new IconDrawable(ctx,Iconify.IconValue.zmdi_delete).colorRes(R.color.red_500),"Apagar")
				.sheet(R.id.details,new IconDrawable(ctx,Iconify.IconValue.zmdi_info_outline).colorRes(R.color.red_500),"Detalhes")
				.sheet(R.id.details,"Ao usuário")
				.sheet(R.id.play,new IconDrawable(ctx,Iconify.IconValue.zmdi_play).colorRes(R.color.red_500),"Tocar Todas")
				.sheet(R.id.addOnUser,new IconDrawable(ctx,Iconify.IconValue.zmdi_plus_circle_o).colorRes(R.color.red_500),"Adicionar Todas")
				.listener(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.w("TAG",String.valueOf(which));
						switch(which){
							case R.id.playermainTextViewNome:new AttachPhotoDialog(artista).show(((AppCompatActivity)ctx).getSupportFragmentManager(),"");
								break;
							case R.id.edit:new SimpleDialog(artista).show(((AppCompatActivity)ctx).getSupportFragmentManager(),"");
								break;
							case R.id.details:
								new AlertDialog.Builder(ctx).setTitle("Detalhes").setIcon(new IconDrawable(ctx,Iconify.IconValue.zmdi_info_outline).colorRes(R.color.red_a700).sizeDp(30))
									.setItems(new String[]{"Nome: "+artista.nome,"Key: "+artista.key,"Image Key: "+artista.imageKey},null).show();
								break;
							case R.id.delete:Snackbar.make(nome, "Deseja realmente APAGAR?", 3000).setAction("OK", new OnClickListener(){

										@Override
										public void onClick(View p1)
										{
											artista.delete();
										}
									}).show();
					}
					}
					}).show();
		}
		else if(album!=null){
			new BottomSheet.Builder(ctx, R.style.BottomSheet_Dialog)
				.title(album.getNome())
				.icon(new IconDrawable(ctx,Iconify.IconValue.zmdi_info).colorRes(R.color.red_500))
				.sheet(R.id.edit,new IconDrawable(ctx,Iconify.IconValue.zmdi_edit).colorRes(R.color.red_500),"Renomear")
				.sheet(R.id.playermainTextViewNome,new IconDrawable(ctx,Iconify.IconValue.zmdi_image_o).colorRes(R.color.red_500),"Add Photo")
				.sheet(R.id.delete,new IconDrawable(ctx,Iconify.IconValue.zmdi_delete).colorRes(R.color.red_500),"Apagar")
				.sheet(R.id.details,new IconDrawable(ctx,Iconify.IconValue.zmdi_info_outline).colorRes(R.color.red_500),"Detalhes")
				.sheet(R.id.details,"Ao usuário")
				.sheet(R.id.play,new IconDrawable(ctx,Iconify.IconValue.zmdi_play).colorRes(R.color.red_500),"Tocar Todas")
				.sheet(R.id.addOnUser,new IconDrawable(ctx,Iconify.IconValue.zmdi_plus_circle_o).colorRes(R.color.red_500),"Adicionar Todas")
				.listener(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.w("TAG",String.valueOf(which));
						switch(which){
							case R.id.addOnUser:User.addMusicas(album);break;
							case R.id.play:
								ProgressDialog.show(ctx,"","Preparando",true,true);
								Album.getMusicas(album.key, new ValueEventListener(){

										@Override
										public void onDataChange(DataSnapshot p1)
										{
											List<Musica> musicas= new ArrayList<>();
											for(DataSnapshot ds : p1.getChildren()){
												musicas.add(ds.getValue(Musica.class));
											}
											MusicMasterHandler.setToPlay(ctx,musicas);
											MusicMasterHandler.setToPlayPosition(0);
											ctx.startService(new Intent(ctx,MusicService.class).setAction(MusicService.ACTION_PLAY));
											ctx.startActivity(new Intent(ctx,PlayerActivity.class));
											
										}

										@Override
										public void onCancelled(DatabaseError p1)
										{
											// TODO: Implement this method
										}
									});
								break;
							case R.id.playermainTextViewNome:new AttachPhotoDialog(album).show(((AppCompatActivity)ctx).getSupportFragmentManager(),"");
								break;
							case R.id.edit:new SimpleDialog(album).show(((AppCompatActivity)ctx).getSupportFragmentManager(),"");
								break;
								
							case R.id.details:
								new AlertDialog.Builder(ctx).setTitle("Detalhes").setIcon(new IconDrawable(ctx,Iconify.IconValue.zmdi_info_outline).colorRes(R.color.red_a700).sizeDp(30))
									.setItems(new String[]{"Nome: "+album.nome,"Key: "+album.key,"Image Key: "+album.key,"Artista Key: "+album.artistaKey},null).show();
								break;
							case R.id.delete:Snackbar.make(nome, "Deseja realmente APAGAR?", 3000).setAction("OK", new OnClickListener(){

										@Override
										public void onClick(View p1)
										{
											album.delete();
										}
									}).show();
						}
					}
				}).show();
		}
		super.onClick(v);
	}
	public long download(Uri i){
		long downloadReference;
		DownloadManager downloadManager = (DownloadManager)nome.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(i);

        //Setting title of request
        request.setTitle(mMusica.nome);

        //Setting description of request
        request.setDescription(mMusica.getMusicaUri());

        //Set the local destination for the downloaded file to a path within the application's external files directory
		request.setDestinationInExternalFilesDir(nome.getContext(), Environment.getExternalStorageDirectory().getAbsolutePath(), mMusica.nome);
        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
		
	}
}
