package com.smash.smash.Models;
import java.util.*;
import android.content.*;
import com.smash.smash.*;

public class MoreDialogItem
{
	public int imageRes;
	public String text;
	
	public MoreDialogItem(String text, int res){
		this.text=text;
		this.imageRes=res;
	}
	
	public static List<MoreDialogItem> getMusicaList(Context ctx, Musica m){
		List<MoreDialogItem> mList  = new ArrayList<>();
		
		int[] images = new int[]{R.drawable.ic_inbox, R.drawable.ic_heart, R.drawable.ic_cloud_download, R.drawable.ic_playlist_plusb, 
		R.drawable.ic_indent, R.drawable.ic_album, R.drawable.ic_account_circleb, R.drawable.ic_link, R.drawable.ic_share};
		int i=0;
		for(String s : ctx.getResources().getStringArray(R.array.musicDialogArray)){
			mList.add(new MoreDialogItem(s, images[i]));
			i++;
		}
		return mList;
	}

}
