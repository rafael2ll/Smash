package com.smash.up.Dialogs;

import android.app.*;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.malinskiy.materialicons.*;
import com.smash.up.frags.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.smash.up.frags.R;
import com.smash.up.Itens.*;
import java.io.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;
import com.google.firebase.database.*;
import android.util.*;
import com.squareup.picasso.*;
import java.util.*;

public class AttachPhotoDialog extends DialogFragment
{
	View v;ImageView attachImage;TextView nome;
	//DatabaseReference mRef;
	Album album;Artista artista;
	Uri selectedImage;
	public AttachPhotoDialog(Album a){
		this.album=a;
	}
	public AttachPhotoDialog(Artista b){
		this.artista=b;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		v=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_to_image,null,false);
		finds();
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
			.setView(v).setPositiveButton("SALVAR", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					if(selectedImage==null)return;
					String fileName=new File(selectedImage.getPath()).getName();
					if(album!=null)
						Commons.mDefaultStorageRef.child("albumImages").child(album.key)
							.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
								@Override
								public void onSuccess(UploadTask.TaskSnapshot p1)
								{
										Log.i("TAG","albumArtSent");
										Map<String,Object> values=new HashMap<String,Object>();
										values.put("imageUri",p1.getDownloadUrl().toString());
										Commons.mDefaultDatabaseRef.child("albuns").child(album.key).updateChildren(values);
										}
							});
					else if(artista!=null){
						Commons.mDefaultStorageRef.child("artistaImages").child(artista.getKey())
							.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
								@Override
								public void onSuccess(UploadTask.TaskSnapshot p1)
								{
									Log.i("TAG","albumArtSent");
									Map<String,Object> values=new HashMap<String,Object>();
									values.put("imageUri",p1.getDownloadUrl().toString());
									artista.setImageKey(p1.getMetadata().getName());
									Commons.mDefaultDatabaseRef.child("artista").child(artista.key).updateChildren(values);
								}
							});
					}
				}
			});
		return builder.show();
	}

	private void finds()
	{
		nome=(TextView)v.findViewById(R.id.dialogtoimageTextView);
		attachImage=(ImageView)v.findViewById(R.id.dialogtoimageImageView);
		
		if(album!=null){
			nome.setText(album.getNome());
			try{
				Picasso.with(getActivity()).load(album.imageUri)
					.fit().centerCrop().into(attachImage);
				
			}catch(Exception e){
			 attachImage.setImageDrawable(new IconDrawable(getActivity(),
										 Iconify.IconValue.zmdi_plus).colorRes(R.color.red_a700).sizeDp(50));
			}
			}
		if(artista!=null){
			nome.setText(artista.getNome());
			if(!artista.getImageKey().equals("toAdd")){
				Picasso.with(getActivity()).load(artista.imageUri)
					.fit().centerCrop().into(attachImage);
				
			}
			else attachImage.setImageDrawable(new IconDrawable(getActivity(),
											 Iconify.IconValue.zmdi_plus).colorRes(R.color.red_a700).sizeDp(50));
		}
		attachImage.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Intent 	galleryIntent = new Intent(Intent.ACTION_PICK,
        			android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// Start the Intent
					startActivityForResult(galleryIntent, 55);

				}
			});
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 55 && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data
                selectedImage = data.getData();
				Picasso.with(getActivity()).load(selectedImage).fit().centerCrop()
				.into(attachImage);
            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
							   Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
				.show();
        }

    }
}
