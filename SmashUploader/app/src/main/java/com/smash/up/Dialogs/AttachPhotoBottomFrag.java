package com.smash.up.Dialogs;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;
import com.mindorks.butterknifelite.*;
import com.mindorks.butterknifelite.annotations.*;
import com.smash.up.*;
import com.smash.up.Models.*;
import com.squareup.picasso.*;
import java.io.*;
import java.util.*;

import com.smash.up.R;
import com.smash.up.Helpers.*;

public class AttachPhotoBottomFrag extends BottomSheetDialogFragment
{
	@BindView(R.id.attachimagefragTextView1) TextView mNome;
	@BindView(R.id.attachimagefragImageViewSend) ImageView mSend;
	@BindView(R.id.attachimagefragImageView) ImageView mImage;

	Uri selectedImage;
	Album album;
	Artista artista;
	
	public AttachPhotoBottomFrag(Album a){
		this.album=a;
	}
	public AttachPhotoBottomFrag(Artista b){
		this.artista=b;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.attach_image_frag, container, false);
		ButterKnifeLite.bind(this, v);
		
		mImage.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent 	galleryIntent = new Intent(Intent.ACTION_PICK,
													   android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// Start the Intent
					startActivityForResult(galleryIntent, 55);
					
				}
			});
		mSend.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v)
				{
					if(selectedImage==null)return;
					String fileName=new File(selectedImage.getPath()).getName();
					if(album!=null)
						Album.getAlbumArtStorage().child(album.key)
							.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
								@Override
								public void onSuccess(UploadTask.TaskSnapshot p1)
								{
									Log.i("TAG","albumArtSent");
									Map<String,Object> values=new HashMap<String,Object>();
									values.put("imageUri",p1.getDownloadUrl().toString());
									Album.getMainRef().child(album.key).updateChildren(values);
								}
							});
					else if(artista!=null){
						Artista.getArtistaImageStorage().child(artista.getKey())
							.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
								@Override
								public void onSuccess(UploadTask.TaskSnapshot p1)
								{
									Log.i("TAG","albumArtSent");
									Map<String,Object> values=new HashMap<String,Object>();
									values.put("imageUri",p1.getDownloadUrl().toString());
									artista.setImageKey(p1.getMetadata().getName());
									Artista.getMainRef().child(artista.key).updateChildren(values);
								}
							});
					}
				}
			});
		return v;
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 55 && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data
                selectedImage = data.getData();
				Picasso.with(getActivity()).load(selectedImage).fit().centerCrop()
					.into(mImage);
            } else {
               PrettyToast.showError(getActivity(), "You haven't picked Image");
            }
        } catch (Exception e) {
            PrettyToast.showError(getActivity(), "Something went wrong");
        }

    }
}
