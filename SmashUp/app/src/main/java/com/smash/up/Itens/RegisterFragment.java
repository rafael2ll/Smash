package com.smash.up.Itens;
import android.app.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.smash.up.*;

import android.support.v4.app.Fragment;
import com.smash.up.R;
import android.content.*;

public class RegisterFragment extends Fragment implements View.OnClickListener
{
	View v;
	FirebaseAuth authRef;
	ProgressDialog pDialog;
	EditText emailtil,passtil,newEmail,newUsername,newPass,newPassRedo;
	Button register,login,trueRegister;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		pDialog=new ProgressDialog(getActivity());
		authRef=FirebaseAuth.getInstance(Commons.mApp);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		v=inflater.inflate(R.layout.login_frag,container,false);
		finds();
		return v;
	}

	
	
	private void finds()
	{
		login=(Button)v.findViewById(R.id.loginButtonLogin);
		register=(Button)v.findViewById(R.id.loginButtonSignup);
		emailtil=(EditText)v.findViewById(R.id.field_email);
		passtil=(EditText)v.findViewById(R.id.field_password);
		newEmail=(EditText)v.findViewById(R.id.loginfragEditTextnewEmail);
		newUsername=(EditText)v.findViewById(R.id.loginfragEditTextnewUsername);
		newPass=(EditText)v.findViewById(R.id.loginfragEditTextnewPass);
		newPassRedo=(EditText)v.findViewById(R.id.loginfragEditTextneePassRedo);
		trueRegister=(Button)v.findViewById(R.id.loginfragButtonSeriousCreate);
		trueRegister.setOnClickListener(this);
		register.setOnClickListener(this);
		login.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View p1)
	{
		String[] data = new String[2];
		data[0]=emailtil.getText().toString();
		data[1]=passtil.getText().toString();
		if(!data[0].contains("@")){
			Snackbar.make(v,"Verifique seu email e tente again.",2000).show();
			return;
			}
		if(data[1].length()<=5 || data[1].length()>12){
			Snackbar.make(v,"Senha deve conter mais do que 5 digitos e menos de 12",2000).show();
		}
		if(p1==login){
			pDialog.setMessage("Logando");
			pDialog.show();
			authRef.signInWithEmailAndPassword(emailtil.getText().toString().trim(),
		    passtil.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
					@Override
					public void onComplete(Task<AuthResult> p1)
					{
						if(p1.isSuccessful()){
							
							loadUser(p1.getResult().getUser().getUid());
						}else{
							pDialog.dismiss();
							Snackbar.make(v,p1.getException().getMessage(),2000).show();
						}
					}
		  });}else if(p1==register){
		 	initRegister();
			}
			else if(p1==trueRegister){
				register();
			}
	}
	private void loadUser(String id)
	{
		pDialog.dismiss();
		pDialog.setMessage("Loading user");
		pDialog.show();
		FirebaseDatabase.getInstance(Commons.mApp)
		.getReference().child("users").child(id).addListenerForSingleValueEvent(
			new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					pDialog.dismiss();
					if(p1!=null)p1.getRef().keepSynced(true);
					else Snackbar.make(v,"Erro durante login",2000).show();
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					// TODO: Implement this method
				}
			});
	}
	private void register(){
		final String username=newUsername.getText().toString();
		String email=newEmail.getText().toString();
		String passMaim=newPass.getText().toString();
		String passConfirm=newPassRedo.getText().toString();
		if(!passMaim.equals(passConfirm)){
			Snackbar.make(v,"Senhas não coincifem",2000).show();
			return;
		}
		pDialog.setMessage("Criando conta");
		pDialog.show();
		authRef.createUserWithEmailAndPassword(email,passConfirm).addOnCompleteListener(
			new OnCompleteListener<AuthResult>(){
				@Override
				public void onComplete(Task<AuthResult> p1)
				{
					pDialog.dismiss();
					if(p1.isSuccessful()){
						createNewUser(p1.getResult().getUser(),username);
					}else{
						Snackbar.make(v,p1.getException().getMessage(),2000).show();}
				}
			});
	}
	private void createNewUser(FirebaseUser user,final String username)
	{
		pDialog.setMessage("Criando usuário");
		pDialog.show();
		User nUser=new User(username);
		FirebaseDatabase.getInstance(Commons.mApp)
			.getReference().child("users").child(user.getUid()).setValue(nUser, new DatabaseReference.CompletionListener(){

				@Override
				public void onComplete(DatabaseError p1, DatabaseReference p2)
				{
					pDialog.dismiss();
					if(p1!=null)Snackbar.make(v,"Erro durante a criação",2000).show();
				}
			});
	}
	private void initRegister()
	{
		toggleLayouts();
		newEmail.setText(emailtil.getText().toString());
		newPass.setText(passtil.getText().toString());
	}
	private void toggleLayouts(){
		LinearLayout registerLay=(LinearLayout)v.findViewById(R.id.loginfragLinearLayoutNew);
		registerLay.setVisibility(registerLay.getVisibility()==View.VISIBLE ? View.GONE : View.VISIBLE);
		RelativeLayout loginLay=(RelativeLayout)v.findViewById(R.id.loginfragRelativeLayoutLogin);
		loginLay.setVisibility(loginLay.getVisibility()==View.VISIBLE ? View.GONE : View.VISIBLE);
	
	}
}
