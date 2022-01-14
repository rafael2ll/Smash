package com.smash.up.Frags;

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
import com.smash.up.Models.*;

import android.support.v4.app.Fragment;
import com.smash.up.R;
import com.mindorks.butterknifelite.annotations.*;
import com.mindorks.butterknifelite.ButterKnifeLite;
import android.view.View.*;
import com.smash.up.Dialogs.*;
import android.graphics.*;

public class LoginFragment extends Fragment implements OnClickListener
{
	View v;
	FirebaseAuth authRef;
	ProgressDialog pDialog;
	
	@BindView(R.id.field_email )EditText emailtil;
	@BindView(R.id.field_password )EditText passtil;
	@BindView(R.id.loginfragEditTextnewEmail )EditText newEmail;
	@BindView(R.id.loginfragEditTextnewUsername )EditText newUsername;
	@BindView(R.id.loginfragEditTextnewPass )EditText newPass;
	@BindView(R.id.loginfragEditTextneePassRedo )EditText newPassRedo;
	@BindView(R.id.loginButtonSignup )Button register;
	@BindView(R.id.loginButtonLogin )Button login;
	@BindView(R.id.loginfragButtonSeriousCreate )Button trueRegister;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		pDialog=new ProgressDialog(getActivity());
		authRef=FirebaseAuth.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		v=inflater.inflate(R.layout.login_frag,container,false);
		ButterKnifeLite.bind(this, v);
		finds();
		return v;
	}

	
	
	private void finds()
	{
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
		User.getUserRef(id).addListenerForSingleValueEvent(
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
		final String email=newEmail.getText().toString();
		String passMaim=newPass.getText().toString();
		final String passConfirm=newPassRedo.getText().toString();
		if(!passMaim.equals(passConfirm)){
			Snackbar.make(v,"Senhas não coincifem",2000).show();
			return;
		}
		new SimpleDialog(new SimpleDialog.OnPassListener(){

				@Override
				public void onKeyResult(boolean b)
				{
					if(b){
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
					}else{
						Snackbar.make(v, "Senha incorreta", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED)
						.setAction("OK",null).show();
					}
				}
			}).show(getChildFragmentManager(), "");
		
	}
	private void createNewUser(FirebaseUser user,final String username)
	{
		pDialog.setMessage("Criando usuário");
		pDialog.show();
		User nUser=new User(username);
		User.getUserRef(user.getUid()).setValue(nUser, new DatabaseReference.CompletionListener(){

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
