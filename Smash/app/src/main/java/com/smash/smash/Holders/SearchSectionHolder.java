package com.smash.smash.Holders;
import android.view.*;
import android.widget.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.smash.Adapters.*;

public class SearchSectionHolder extends RecyclerAdapter.Holder
{
	@BindView(R.id.searchsectionTextView1) public TextView mText;
	
	public SearchSectionHolder(View v){
		super(v);
		ButterKnifeLite.bind(this, v);
	}
	public void feedIt(String s){
		mText.setText(s);
	}
}
