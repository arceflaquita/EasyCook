package com.example.arce.easy_cook;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.arce.easy_cook.DatosUsuario.DatosUsuario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class InsReceta extends AppCompatActivity {
    String urlREST = "";
    private EditText editModoPrep,editIngrediente,editNombreRec,editVideo;
    private Button btnAgregar, btnAgregarRec;
    private ListView listIng;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private Spinner spnTipoComida, spnPorciones;
    private static String APP_DIRECTORY = "EasyCook/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Pictures";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private ImageView mSetImage;
    private Button mOptionButton;
    private RelativeLayout mRlView;
    private String mPath;
    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}
    float dX, dY;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        urlREST = this.getResources().getString(R.string.urlREST);
        setContentView(R.layout.ins_receta);
        editNombreRec = (EditText) findViewById(R.id.editNombreRec);
        spnTipoComida = (Spinner) findViewById(R.id.spnTipoComida);
        spnPorciones = (Spinner) findViewById(R.id.spnPorciones);
        editModoPrep = (EditText) findViewById(R.id.editModoPrep);
        editIngrediente = (EditText) findViewById(R.id.editIngrediente);
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnAgregarRec = (Button) findViewById(R.id.btnAgregarRec);
        listIng = (ListView) findViewById(R.id.listIng);
        mSetImage = (ImageView) findViewById(R.id.set_picture);
        mOptionButton = (Button) findViewById(R.id.show_options_button);
        mRlView = (RelativeLayout) findViewById(R.id.rl_view);
        editVideo = (EditText) findViewById(R.id.editVideo);

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        listIng.setAdapter(adapter);


        //Toast.makeText(getApplicationContext(), "id_usuario:"+DatosUsuario.getIdUsuario(), Toast.LENGTH_LONG).show();

        ArrayAdapter<CharSequence> tipo_comida_adap = ArrayAdapter.createFromResource(this,
                R.array.tipo_comida_arr, android.R.layout.simple_spinner_item);
        tipo_comida_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoComida.setAdapter(tipo_comida_adap);

        ArrayAdapter<CharSequence> porciones_adap = ArrayAdapter.createFromResource(this,
                R.array.porciones_arr, android.R.layout.simple_spinner_item);
        porciones_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPorciones.setAdapter(porciones_adap);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this line adds the data of your EditText and puts in your array
                arrayList.add(editIngrediente.getText().toString());
                // next thing you have to do is check if your adapter has changed
                editIngrediente.setText("");
                adapter.notifyDataSetChanged();
            }
        });

        btnAgregarRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agrega la receta
                if(editNombreRec.getText().toString()=="" || editModoPrep.getText().toString()=="" || 0 ==spnTipoComida.getSelectedItemPosition()){
                    alerta( "Capture toda la informacion!","Error");
                    //Toast.makeText(getApplicationContext(), "Capture toda la informacion!", Toast.LENGTH_LONG).show();
                    return;
                }
                registrarReceta(view);
            }
        });

        listIng.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        historicX = event.getX();
                        historicY = event.getY();
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getX() - historicX < -DELTA) {
                            int pos = listIng.pointToPosition((int)historicX, (int)historicY);
                            adapter.remove(adapter.getItem(pos));
                            return true;
                        }
                        /*else if (event.getX() - historicX > DELTA) {
                            FunctionDeleteRowWhenSlidingRight();
                            return true;
                        }*/
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });

        if(mayRequestStoragePermission())
            mOptionButton.setEnabled(true);
        else
            mOptionButton.setEnabled(false);

        mOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions();
            }
        });

    }// onCreate


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 1);
        return BitmapFactory.decodeByteArray(decodedBytes, 1, decodedBytes.length);
    }

    public void registrarReceta(View v) {

        String nombre = editNombreRec.getText().toString();
        String preparacion = editModoPrep.getText().toString();
        String tipo_comida = String.valueOf(spnTipoComida.getSelectedItemPosition()+1);
        String porciones = spnPorciones.getSelectedItem().toString();
        String url_video = editVideo.getText().toString();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
         bitmap = BitmapFactory.decodeFile(mPath, options);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = 668;
        int newHeight = 400;

        // calculamos el escalado de la imagen destino
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // para poder manipular la imagen
        // debemos crear una matriz

        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        // volvemos a crear la imagen con los nuevos valores
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, true);

        String photo = encodeToBase64(resizedBitmap, Bitmap.CompressFormat.JPEG, 50);



        JSONObject jo = new JSONObject();

        try {
            jo.put("nombre", nombre);
            jo.put("preparacion", preparacion);
            jo.put("tipo_comida", tipo_comida);
            jo.put("url_video", url_video);
            jo.put("porciones", porciones);
            jo.put("id_usuario", DatosUsuario.getIdUsuario());
            jo.put("image", photo);
            JSONArray ja = new JSONArray();
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listIng.getAdapter();
            for(int i=0; i<adapter.getCount();i++){
                String item = adapter.getItem(i);
                ja.put(item);
            }
            jo.put("ingredientes", ja);
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

        String url = urlREST + "/nuevaReceta";
        ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    //Status 200 quiere decir que se recibio respuesta
                    if (statusCode == 200) {
                        if(responseBody != null && responseBody.length > 0) {
                            alerta("Se registro correctamente!","Exito");
                            Toast.makeText(getApplicationContext(), "Se registro correctamente!", Toast.LENGTH_LONG).show();
                            editNombreRec.setText("");
                            editModoPrep.setText("");
                            editVideo.setText("");
                            Intent bus = new Intent(InsReceta.this, Mostrar_receta.class);
                            startActivity(bus);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Error onFailure: " + String.valueOf(statusCode) + " : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openCamera() {
        //File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        File file = Environment.getExternalStorageDirectory();
        //File file = new File(path);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 700;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + imageName;

            File newFile = new File(mPath);



            bitmap = BitmapFactory.decodeFile(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });
                    bitmap = BitmapFactory.decodeFile(mPath);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth = 668;
                    int newHeight = 400;

                    // calculamos el escalado de la imagen destino
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;

                    // para poder manipular la imagen
                    // debemos crear una matriz

                    Matrix matrix = new Matrix();
                    // resize the Bitmap
                    matrix.postScale(scaleWidth, scaleHeight);

                    // volvemos a crear la imagen con los nuevos valores
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            width, height, matrix, true);
                    bitmap=resizedBitmap;
                    mSetImage.setImageBitmap(bitmap);
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    mSetImage.setImageURI(path);
                    BitmapDrawable drawable = (BitmapDrawable) mSetImage.getDrawable();
                    bitmap = drawable.getBitmap();
                    break;

            }
        }
    }

    private void decodeBitmap(String dir) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(dir);
        mSetImage.setImageBitmap(bitmap);
    }

    //funciones
    private boolean mayRequestStoragePermission() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(mRlView, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(InsReceta.this);
        builder.setTitle("Eleige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    openCamera();
                }else if(option[which] == "Elegir de galeria"){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(InsReceta.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                mOptionButton.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InsReceta.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }

    public void alerta(String cadena,String tipo){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //seleccionamos la cadena a mostrar
        dialogBuilder.setMessage(cadena);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);

        //elegimo un titulo y configuramos para que se pueda quitar
        dialogBuilder.setCancelable(true).setTitle(tipo);

        //mostramos el dialogBuilder
        dialogBuilder.create().show();

    }

}

