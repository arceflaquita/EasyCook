package com.example.arce.easy_cook;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.fabric.sdk.android.Fabric;

public class Login extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener{
    private LoginButton loginButton;
    private TwitterLoginButton loginButtonTw;
    private CallbackManager callbackManager;
    private EditText editEmail, editContrasena;
    Button btnSesion;
    TextView txtCuenta,txtPassword;
    String urlREST = "";

    public static final int SIGN_IN_CODE=777;
    private static final String key="M6cxGI9rLBvxioRr2Oj7745J5";
    private static final String secret="4XnIHc1yFJlg0Fr51lrro67BtMJ4Qo5hIOFJLIfOa08MR1WmgQ";


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private AuthCredential credential;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private AccessTokenTracker accessTokenTracker ;
    private MediaPlayer mp;
    String userName;
    String correo,inicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        TwitterAuthConfig authConfig=new TwitterAuthConfig(key,secret);
        Fabric.with(this,new Twitter(authConfig));
        setContentView(R.layout.login);


        //componentes de twitter

        loginButtonTw = (TwitterLoginButton) findViewById(R.id.tw_log_btn);

        loginButtonTw.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                userName=  result.data.getUserName();
                correo="nose puede mostrar este dato!";
                inicio="Twitter";
                goMainScreen();
                finish();

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(),R.string.cancel_login,Toast.LENGTH_SHORT).show();
            }
        });

        //componente de Google
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        signInButton=(SignInButton)findViewById(R.id.SignInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,SIGN_IN_CODE);
            }
        });


        //Componentes de Facebook

        loginButton=(LoginButton)findViewById(R.id.fb_log_btn) ;

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                graphRequest(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),R.string.cancel_login,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),R.string.error_login,Toast.LENGTH_SHORT).show();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener=new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user!=null){
                    userName= user.getDisplayName();
                    correo=user.getEmail();
                    inicio="Google";
                    goMainScreen();
                }
            }
        };
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        //ejecutar ipconfig para ver la ip de la maquina
        urlREST = this.getResources().getString(R.string.urlREST);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editContrasena = (EditText) findViewById(R.id.editContrasena);
        btnSesion = (Button) findViewById(R.id.btnSesion);
        txtCuenta = (TextView) findViewById(R.id.txtCuenta);
        txtPassword=(TextView)findViewById(R.id.txtRecuperar) ;

        txtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abreCuenta = new Intent(Login.this, RecuperarPassword.class);
                startActivity(abreCuenta);
            }
        });

        txtCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abreCuenta = new Intent(Login.this, Cuenta.class);
                startActivity(abreCuenta);
            }
        });

        btnSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void graphRequest(AccessToken token){
        GraphRequest request = GraphRequest.newMeRequest(token,new GraphRequest.GraphJSONObjectCallback(){

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    Object name=object.get("last_name");
                    Object corr= object.get("email");
                    userName=String.valueOf(name);
                    correo=String.valueOf(corr);
                    inicio="Facebook";
                   // Toast.makeText(getApplicationContext(),"nombre:"+userName,Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(),"correo:"+correo,Toast.LENGTH_LONG).show();
                    goMainScreen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

              //  Toast.makeText(getApplicationContext(),object.toString(),Toast.LENGTH_LONG).show();

            }
        });

        Bundle b = new Bundle();
        b.putString("fields","id,email,first_name,last_name,picture.type(large)");
        request.setParameters(b);
        request.executeAsync();

    }


    private void goMainScreen() {
        Intent intent=new Intent(Login.this,MenuUser2.class);
         intent.putExtra("nombreUser", userName);
        intent.putExtra("correo", correo);
        intent.putExtra("inicio", inicio);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

        if(requestCode==SIGN_IN_CODE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSingnInResult(result);
        }

            loginButtonTw.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSingnInResult(GoogleSignInResult result) {
    if(result.isSuccess()){
        userName=result.getSignInAccount().getFamilyName();
        correo=result.getSignInAccount().getEmail();
        goMainScreen();
    }else{
        Toast.makeText(this,R.string.error_login,Toast.LENGTH_SHORT).show();
    }
    }

    public void loginUser(View view) {
        String user = editEmail.getText().toString();
        String passwd = editContrasena.getText().toString();
        if(user.compareTo("") != 0 && passwd.compareTo("") != 0){
            JSONObject jo = new JSONObject();
            try {
                jo.put("correo", user);
                jo.put("password", passwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AsyncHttpClient ac = new AsyncHttpClient();
            HttpEntity entity = null;
            try {
                entity = new StringEntity(jo.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //TODO: Cambiar por la ip de la PC que corre el servicio RestEC
            String url = urlREST + "/validaUsuario";
            ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {


                        //Status 200 quiere decir que se recibio respuesta
                        if (statusCode == 200) {
                            if(responseBody != null && responseBody.length > 0) {
                                JSONObject res = new JSONObject(new String(responseBody));
                                DatosUsuario datosUsuario=new DatosUsuario();
                                String nombreUser="";
                                Object valido = res.get("userValido");
                                Object validoCor = res.get("correoIgual");
                                Object correo = res.get("correo");
                                Object idUsuario = res.get("idUsuario");
                                Object nombre=res.get("nombre");
                                Object ap_paterno=res.get("ap_paterno");
                                Object ap_materno=res.get("ap_materno");

                                String nom=String.valueOf(nombre);
                                String apPat=String.valueOf(ap_paterno);
                                String apMat=String.valueOf(ap_materno);
                                nombreUser=nom+" "+apPat+" "+apMat;
                                datosUsuario.setCorreo(String.valueOf(correo));
                                datosUsuario.setIdUsuario(Integer.parseInt(idUsuario.toString()));
                                //si el usuario es valido lo redireccionamos al Activity principal
                                if(valido.toString().compareTo("true") == 0){

                                    Intent busq = new Intent(Login.this, MenuUser2.class);
                                    busq.putExtra("nombreUser", nombreUser);
                                    busq.putExtra("correo", datosUsuario.getCorreo());
                                    busq.putExtra("inicio", "Usuario de Easy-Cook");
                                    startActivity(busq);
                                    editEmail.setText("");
                                    editContrasena.setText("");
                                }else {
                                    //validarCajaTexto(validoCor,validoPas);
                                    if (validoCor.toString().compareTo("true") == 0) {

                                        editContrasena.setError("Contraceña Incorrecto");
                                        editContrasena.setText("");
                                        editEmail.setError("Usuario Incorrecto");
                                        editEmail.setText("");
                                    } else {

                                        editEmail.setError("Usuario Incorrecto");
                                        editEmail.setText("");
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                private void validarCajaTexto(Object validoCor,Object validoPas) {
                    if(validoCor.toString().compareTo("false") == 1){

                        editEmail.setError("Usuario Incorrecto");
                    }
                    if (validoPas.toString().compareTo("false") == 1){
                        editContrasena.setError("Contraceña Incorrecto");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplicationContext(), "Error onFailure:  "+error, Toast.LENGTH_LONG).show();
                }
            });

        }else{
          //  Toast.makeText(getApplicationContext(), "Por favor ingrese sus datos", Toast.LENGTH_LONG).show();
            alerta("Por favor ingrese sus datos");
        }
    }



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void alerta(String cadena){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //seleccionamos la cadena a mostrar
        dialogBuilder.setMessage(cadena);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);

        //elegimo un titulo y configuramos para que se pueda quitar
        dialogBuilder.setCancelable(true).setTitle("Warning");

        //mostramos el dialogBuilder
        dialogBuilder.create().show();

    }
}