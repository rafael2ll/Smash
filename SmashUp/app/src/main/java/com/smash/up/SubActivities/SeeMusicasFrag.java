package com.smash.up.SubActivities;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import com.cocosw.bottomsheet.*;
import com.firebase.ui.database.*;
import com.google.firebase.database.*;
import com.malinskiy.materialicons.*;
import com.smash.up.*;
import com.smash.up.Adapters.*;
import com.smash.up.Dialogs.*;
import com.smash.up.Itens.*;
import java.util.*;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import com.smash.up.R;

public class SeeMusicasFrag extends Fragment
{
	RecyclerView mRecycler,mAlbumRecycler;
	FirebaseRecyclerAdapter adapter;
	Query query;
	Context ctx;
	FirebaseRecyclerAdapter albumAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		query= Musica.getQuery();
		ctx=getActivity();
		albumAdapter = new FirebaseRecyclerAdapter<Album,CircleAlbumHolder>(Album.class, R.layout.album_item, CircleAlbumHolder.class, Album.getQuery()){
			@Override
			protected void populateViewHolder(final CircleAlbumHolder p1, Album p2, int p3)
			{
				Log.w("Adapter",p2.getNome());
				p1.populateHolder(getActivity(), p2, new CircleAlbumHolder.OnAlbumClick(){

						@Override
						public void onLongClick(final Album album)
						{
						
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
															//ctx.startActivity(new Intent(ctx,PlayerActivity.class));

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
											case R.id.delete:Snackbar.make(p1.itemView, "Deseja realmente APAGAR?", 3000).setAction("OK", new OnClickListener(){

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
						
						@Override
						public void onClick(Album a)
						{
							query=Musica.getQuery().orderByChild("albumKey").equalTo(a.key);
							updateQuery();
						}
					});
			}
		};
	}
	
	public void updateQuery(){
		query.addListenerForSingleValueEvent(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					List<Musica> musicas=new ArrayList<>();
					for(DataSnapshot ds : p1.getChildren()){
						musicas.add(ds.getValue(Musica.class));
					}
					MusicMasterHandler.setToPlay(getActivity(),musicas);
					getActivity().startService(new Intent(getActivity(),MusicService.class).setAction(MusicService.ACTION_START));
					LogHelper.w("Tag",musicas.toString());
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});

		adapter = new FirebaseRecyclerAdapter<Musica,SeeMusicasHolder>(Musica.class, R.layout.see_musics_item, SeeMusicasHolder.class, query){

			@Override
			protected void populateViewHolder(SeeMusicasHolder p1, Musica p2, int p3)
			{

				Log.w("Adapter",p2.getNome());
				p1.feedItWithMusic(getActivity(),p2);
			}
		};
		mRecycler.setAdapter(adapter);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.simple_recycler,container,false);
		mRecycler=(RecyclerView)v.findViewById(R.id.recyclerView);
		mAlbumRecycler=(RecyclerView)v.findViewById(R.id.recyclerViewHorizont);
		mAlbumRecycler.setVisibility(View.VISIBLE);
		mAlbumRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
		mAlbumRecycler.setAdapter(albumAdapter);
		mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		updateQuery();
		mRecycler.setAdapter(adapter);
		return v;
	}
	
}
