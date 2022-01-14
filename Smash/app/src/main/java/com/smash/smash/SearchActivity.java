package com.smash.smash;
import com.mindorks.butterknifelite.annotations.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.support.v7.app.*;
import com.smash.smash.Adapters.*;
import android.os.*;
import com.mindorks.butterknifelite.*;
import android.text.*;

public class SearchActivity extends AppCompatActivity
{
	@BindView(R.id.search_activityTextInput) TextInputLayout mTIL;
	@BindView(R.id.recyclerView) RecyclerView mRecycler;
	
	SearchRecyclerAdapter mAdapter = new SearchRecyclerAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		ButterKnifeLite.bind(this);
		
		mRecycler.setLayoutManager(new LinearLayoutManager(this));
		mRecycler.setAdapter(mAdapter);
		
		mTIL.getEditText().addTextChangedListener(new TextWatcher(){

				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					mAdapter.search(p1.toString());
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
				}
			});
	}
	
}
