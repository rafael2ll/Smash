package com.smash.player.Frags;
import android.support.v7.widget.*;
import com.smash.player.*;
import com.smash.player.Helpers.*;
import com.smash.player.Holders.*;
import com.smash.player.Models.*;

public class MainMusicFrag extends RecyclerFragment
{

	@Override
	public void readyToGo()
	{
		setRecyclerAdapter(new LinearLayoutManager(getActivity()), new EasyRecyclerAdapter<Musica,MainItensHolder>(MusicProvider.allMusics, Musica.class, R.layout.music_main_item, MainItensHolder.class){
				@Override
				protected void populateViewHolder(MainItensHolder viewHolder, Musica model, int position)
				{
					viewHolder.populateMusica(getActivity(),model);
				}
		});
		hideLoading();
	}
}
