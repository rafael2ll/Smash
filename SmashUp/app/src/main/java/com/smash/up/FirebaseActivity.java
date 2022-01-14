package com.smash.up;
import android.support.v7.app.*;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.smash.up.Itens.*;
import com.google.firebase.auth.*;

public class FirebaseActivity extends AppCompatActivity
{
	public FirebaseDatabase getDatabase(){
		return Commons.mDBInstance;
	}
	public FirebaseStorage getStorage(){
		return Commons.mStorageInstance;
	}
	public FirebaseAuth getAuth(){
		return Commons.mAuthReference;
	}
	public DatabaseReference getDBRef(){
		return Commons.mDefaultDatabaseRef;
	}
	public StorageReference getStorageRef(){
		return Commons.mDefaultStorageRef;
	}
	
}
