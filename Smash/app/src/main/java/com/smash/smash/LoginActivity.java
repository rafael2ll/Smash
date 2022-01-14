package com.smash.smash;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.smash.smash.Models.*;

public class LoginActivity extends AppCompatActivity implements OnClickListener
{
	EditText usernameEditText, passwordEditText, usernameSignup, emailSignup, passwordSignup, repeatPasswordSignup;
    ProgressDialog progress;
    //LinearLayout signUpPanel;
    
    LinearLayout signinContainer, signupContainer;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		finds();
	}

	private void finds()
	{
		signinContainer = (LinearLayout) findViewById(R.id.signincontainer);
        signupContainer = (LinearLayout) findViewById(R.id.signupcontainer);
		
		usernameEditText = (EditText) findViewById(R.id.userNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        usernameSignup = (EditText) findViewById(R.id.userNameSignUp);
        passwordSignup = (EditText) findViewById(R.id.passwordSignUp);
        repeatPasswordSignup = (EditText) findViewById(R.id.repeatPasswordSignUp);
        emailSignup = (EditText) findViewById(R.id.emailSignUp);
		
		findViewById(R.id.custom_signin_button).setOnClickListener(this);
		findViewById(R.id.custom_signup_button).setOnClickListener(this);
		findViewById(R.id.user_signup_button).setOnClickListener(this);
	}
	   @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.login_fb_button){
            //do facebook login
            doFacebookLogin();
        } else if(id == R.id.login_google_button){
            //do google login
            doGoogleLogin();
        } else if(id == R.id.custom_signin_button){
            //custom signin implementation
            doCustomSignin();
        } else if(id == R.id.custom_signup_button){
            //custom signup implementation
            //AnimUtil.slideToTop(signinContainer);
            signinContainer.setVisibility(View.GONE);
            signupContainer.setVisibility(View.VISIBLE);
            findViewById(R.id.userNameSignUp).requestFocus();
        } else if(id == R.id.user_signup_button){
            doCustomSignup();
        }
    }

    private void doGoogleLogin() {
        /*// User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        progress = ProgressDialog.show(this, "", getString(R.string.logging_holder), true);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity , this /* On~ConnectionFailedListener )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        progress.dismiss();
        //mGoogleApiClient.connect();*/
    }

    private void doCustomSignup() {
        final String username = usernameSignup.getText().toString();
        String password = passwordSignup.getText().toString();
        String repeatPassword = repeatPasswordSignup.getText().toString();
        String email = emailSignup.getText().toString();
        if(username.equals("")){
            //DialogUtil.getErrorDialog(R.string.username_error, this).show();
            usernameSignup.setError(getResources().getText(R.string.username_error));
            usernameSignup.requestFocus();
        } else if(password.equals("")) {
            //DialogUtil.getErrorDialog(R.string.password_error, this).show();
            passwordSignup.setError(getResources().getText(R.string.password_error));
            passwordSignup.requestFocus();
        } else if(email.equals("")){
            //DialogUtil.getErrorDialog(R.string.no_email_error, this).show();
            emailSignup.setError(getResources().getText(R.string.no_email_error));
            emailSignup.requestFocus();
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //DialogUtil.getErrorDialog(R.string.invalid_email_error, this).show();
            emailSignup.setError(getResources().getText(R.string.invalid_email_error));
            emailSignup.requestFocus();
        } else if(!password.equals(repeatPassword)){
            //DialogUtil.getErrorDialog(R.string.password_mismatch, this).show();
            repeatPasswordSignup.setError(getResources().getText(R.string.password_mismatch));
            repeatPasswordSignup.requestFocus();
        }
        else {
            final ProgressDialog progress = ProgressDialog.show(this, "", getString(R.string.loading_holder), true);
               FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
				.addOnSuccessListener(this, new OnSuccessListener<AuthResult>(){

					@Override
					public void onSuccess(AuthResult p1)
					{
						if(p1!=null){
							User u = new User(username);
							User.getUserRef(p1.getUser().getUid()).setValue(u, new DatabaseReference.CompletionListener(){

									@Override
									public void onComplete(DatabaseError p1, DatabaseReference p2)
									{
										if(p1==null){
											startActivity(new Intent(LoginActivity.this, MainActivity.class));
											finish();
											}
										else{
											progress.dismiss();
										}
									}
								});
						
						}
					}
				})
				.addOnFailureListener(this, new OnFailureListener(){

					@Override
					public void onFailure(Exception p1)
					{
						progress.dismiss();
					}
				});
            }
    }

    private void doCustomSignin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(username.equals("")){
            //DialogUtil.getErrorDialog(R.string.username_error, this).show();
        	usernameEditText.setError(getResources().getText(R.string.email_error));
            usernameEditText.requestFocus();
			
        } else if(password.equals("")){
            //DialogUtil.getErrorDialog(R.string.password_error, this).show();
            passwordEditText.setError(getResources().getText(R.string.password_error));
            passwordEditText.requestFocus();
        } else {
            final ProgressDialog progress = ProgressDialog.show(this, "", getString(R.string.logging_holder), true);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
				.addOnSuccessListener(this, new OnSuccessListener<AuthResult>(){

					@Override
					public void onSuccess(AuthResult p1)
					{
						progress.dismiss();
						startActivity(new Intent(LoginActivity.this, MainActivity.class));
						finish();
					}
				});
        }
    }

        private void doFacebookLogin() {
       /* if(config.isFacebookEnabled()) {
            Toast.makeText(SmartLoginActivity.this, "Facebook login", Toast.LENGTH_SHORT).show();
            final ProgressDialog progress = ProgressDialog.show(this, "", getString(R.string.logging_holder), true);
            ArrayList<String> permissions = config.getFacebookPermissions();
            if (permissions == null) {
                permissions = SmartLoginConfig.getDefaultFacebookPermissions();
            }
            LoginManager.getInstance().logInWithReadPermissions(SmartLoginActivity.this, permissions);
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    progress.setMessage(getString(R.string.getting_data));
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            progress.dismiss();
                            UserUtil util = new UserUtil();
                            SmartFacebookUser facebookUser = util.populateFacebookUser(object);
                            if (facebookUser != null) {
                                finishLogin(facebookUser);
                            } else {
                                finish();
                            }
                        }
                    });
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    progress.dismiss();
                    finish();
                    Log.d("Facebook Login", "User cancelled the login process");
                }

                @Override
                public void onError(FacebookException e) {
                    progress.dismiss();
                    finish();
                    Toast.makeText(SmartLoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });*/
		}
    }
