package com.smash.player.Helpers;
import android.content.*;
import android.support.v4.app.*;
import java.util.*;

public class FragmentNavigation
{
	List<Fragment> fragList=new ArrayList<>();
	Context ctx;
	public FragmentNavigation(Context ctx){
		this.ctx=ctx;
	}
	public void addFrag(Fragment t){
		if(!fragList.contains(t))
			fragList.add(t);
	}
}
