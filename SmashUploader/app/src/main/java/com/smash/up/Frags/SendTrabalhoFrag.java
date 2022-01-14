package com.smash.up.Frags;
import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.pddstudio.urlshortener.*;
import com.smash.up.*;
import com.smash.up.Models.*;
import java.io.*;
import java.util.*;
import paul.arian.fileselector.*;

import com.smash.up.R;
import android.app.ProgressDialog;
import com.smash.up.Helpers.*;

public class SendTrabalhoFrag extends Fragment
{
	@BindView(R.id.sendarchivefragListView) ListView mListView;
	@BindView(R.id.sendarchivefragButtonPick) Button mButton;
	TrabalhoAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.send_archive_frag, container, false);
		ButterKnifeLite.bind(this,v);
		
		mAdapter = new TrabalhoAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setDividerHeight(0);
		mButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
    				startActivityForResult(new Intent(getActivity(), FileSelectionActivity.class), 5);
				}
			});
		return v;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		for(final File f : data.getParcelableArrayListExtra(FileSelectionActivity.FILES_TO_UPLOAD)){
		try
		{
			final ProgressDialog mDialog = new ProgressDialog(getActivity());
			mDialog.setMessage("Enviando...");
			mDialog.setMax((int)f.length());
			mDialog.show();
			UploadTask mTask = Trabalho.getStorageRef().child(f.getName()).putStream(new FileInputStream(f));
			mTask.addOnProgressListener(getActivity(), new OnProgressListener<UploadTask.TaskSnapshot>(){

					@Override
					public void onProgress(UploadTask.TaskSnapshot p1)
					{
						mDialog.setProgress((int)p1.getBytesTransferred());
						mDialog.show();
					}
				}).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>(){

					@Override
					public void onSuccess(final UploadTask.TaskSnapshot p1)
					{
						PrettyToast.showSuccess(getActivity(), "File enviada");
						final Trabalho t = new Trabalho();
						t.setNome(p1.getMetadata().getName());
						t.setDate(new Date().getTime());
						t.setFileKey(p1.getMetadata().getName());
						t.setFileUri(p1.getMetadata().getDownloadUrl().toString());
						t.setKey(Trabalho.getRef().push().getKey());
						t.setLenght(f.length());
						mDialog.setMessage("Gerando link");
						mDialog.show();
						
						URLShortener.shortUrl(p1.getMetadata().getDownloadUrl().toString(), new URLShortener.LoadingCallback() {
								@Override
								public void startedLoading() {
					
								}

								@Override
								public void finishedLoading(@Nullable String shortUrl) {
									//make sure the string is not null
									if(shortUrl != null) t.setShortLink(shortUrl);
									Trabalho.save(t);
									mDialog.dismiss();
					}
				});
		};
		});
		}
		catch (FileNotFoundException e)
		{
			PrettyToast.showError(getActivity(), "Erro ao ler o arquivo");
		}
	}
	}
	public class TrabalhoAdapter extends BaseAdapter
	{
		
		List<Trabalho> mItens = new ArrayList<>();
		
		List<Colors> mColors = Arrays.asList(Colors.values());
		Random mRandom = new Random();
		
		public TrabalhoAdapter(){
			Trabalho.getRef().addValueEventListener(new ValueEventListener(){

					@Override
					public void onDataChange(DataSnapshot p1)
					{
						mItens.clear();
					
						for(DataSnapshot ds : p1.getChildren()){
							mItens.add(ds.getValue(Trabalho.class));
							
							notifyDataSetChanged();
						}
					}

					@Override
					public void onCancelled(DatabaseError p1)
					{
						// TODO: Implement this method
					}
				});
		}
		@Override
		public int getCount()
		{
			return mItens.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return mItens.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			return 0;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			final Trabalho t = mItens.get(p1);
			p2 = LayoutInflater.from(getActivity()).inflate(R.layout.send_archive_holder, p3, false);
			TextView mText = (TextView) p2.findViewById(R.id.sendarchiveholderTextView1);
			TextView mText2 = (TextView) p2.findViewById(R.id.sendarchiveholderTextView2);
			View colorTag = p2.findViewById(R.id.sendarchiveholderView);
			colorTag.setBackgroundColor(mColors.get( mRandom.nextInt(mColors.size())).asColor());
			
			mText.setText(t.nome);
			mText.setSelected(true);
			mText2.setSelected(true);
			mText2.setText(t.shortLink + " • " + android.text.format.Formatter.formatFileSize(getActivity(), t.lenght));
			if(t.shortLink==null){
				URLShortener.shortUrl(t.getFileUri(), new URLShortener.LoadingCallback(){

						@Override
						public void startedLoading()
						{
							// TODO: Implement this method
						}

						@Override
						public void finishedLoading(String link)
						{
							if(link!=null) Trabalho.getRef().child(t.key)
								.child("shortLink").setValue(link);
								notifyDataSetChanged();
						}
					});
			}
			p2.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						ClipboardManager cm =(ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
						cm.setPrimaryClip(ClipData.newPlainText("",t.shortLink));
						Toast.makeText(getActivity(), "Copiado", 2).show();
					}
				});
			return p2;
		}
	}
}
