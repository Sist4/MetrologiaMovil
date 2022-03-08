package com.alexian.metrologia;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;




public class servidorftp extends AppCompatActivity {
    private EditText txtservidor,txtusuario,txtpassword;
    private String metrologo;
    private Button btnConfirmar;
    private ImageButton ibllave;
    private String contrasena="";
    public static Activity cl_servidor_ftp;






// **********************************************permisos en versiones nuevas de android***********************************
    private void messageBox(String method, String message)
    {
        Log.d("EXCEPTION: " + method, message);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(method); messageBox.setMessage(message); messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }






    // Banderas que indicarán si tenemos permisos
    private boolean tienePermisoleer = false,tienePermisoAlmacenamiento = false;
    // Código de permiso, defínelo tú mismo
    private static final int CODIGO_PERMISOS_LEER = 1,CODIGO_PERMISOS_ALMACENAMIENTO = 2;

     private void permisoDeAlmacenamientoDenegado()
     {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
         try
         {
             Toast.makeText(servidorftp.this, "El permiso para el almacenamiento está denegado", Toast.LENGTH_SHORT).show();
         }
         catch(Exception e)
         {
             messageBox("ERROR", e.getMessage());
         }
     }
    private void PermisosdeLeerDenegados()
    {
        Toast.makeText(servidorftp.this, "El permiso para leer la inframacion fueron denegado", Toast.LENGTH_SHORT).show();
    }

    private void permisoDeLeerConcedido()
    {

        // Aquí establece las banderas o haz lo que
        // ibas a hacer cuando el permiso se concediera. Por
        // ejemplo puedes poner la bandera en true y más
        // tarde en otra función comprobar esa bandera
        try
        {
            Toast.makeText(servidorftp.this, "El permiso para leer está concedido", Toast.LENGTH_SHORT).show();
            tienePermisoleer = true;
        }
        catch(Exception e)
        {
            messageBox("ERROR", e.getMessage());
        }
    }

    private void VerificarYPedirPermisodeLeer()
    {
         try
         {
             int estadoDePermiso = ContextCompat.checkSelfPermission(servidorftp.this, Manifest.permission.READ_EXTERNAL_STORAGE);
             if (estadoDePermiso == PackageManager.PERMISSION_GRANTED)
             {
                 // En caso de que haya dado permisos ponemos la bandera en true
                 // y llamar al método
                 permisoDeLeerConcedido();
             }

             {
                 // Si no, entonces pedimos permisos. Ahora mira onRequestPermissionsResult
                 ActivityCompat.requestPermissions(servidorftp.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},CODIGO_PERMISOS_LEER);
                 PermisosdeLeerDenegados();
             }
         }
         catch(Exception e)
            {
                messageBox("ERROR", e.getMessage());
            }


    }
     private void verificarYPedirPermisosDeAlmacenamiento()
    {
        try
        {
                int estadoDePermiso = ContextCompat.checkSelfPermission(servidorftp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
                    // En caso de que haya dado permisos ponemos la bandera en true
                    // y llamar al método
                    permisoDeAlmacenamientoConcedido();
                } else {
                    // Si no, entonces pedimos permisos. Ahora mira onRequestPermissionsResult
                    ActivityCompat.requestPermissions(servidorftp.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            CODIGO_PERMISOS_ALMACENAMIENTO);
                    permisoDeAlmacenamientoDenegado();
                }
        }
        catch(Exception e)
        {
            messageBox("ERROR", e.getMessage());
        }
    }
    private void permisoDeAlmacenamientoConcedido()
    {
        // Aquí establece las banderas o haz lo que
        // ibas a hacer cuando el permiso se concediera. Por
        // ejemplo puedes poner la bandera en true y más
        // tarde en otra función comprobar esa bandera
        try
        {
        Toast.makeText(servidorftp.this, "El permiso para el almacenamiento está concedido", Toast.LENGTH_SHORT).show();
        tienePermisoAlmacenamiento = true;
        }
        catch(Exception e)
        {
            messageBox("ERROR", e.getMessage());
        }
    }


// **********************************************FIN permisos en versiones nuevas de android***********************************






    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificarYPedirPermisosDeAlmacenamiento();
        VerificarYPedirPermisodeLeer();

        try
        {
        cl_servidor_ftp = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.servidorftp);

        metrologo = getIntent().getExtras().getString("codmetro");


        //Seteamos componenetes
        txtservidor=(EditText)findViewById(R.id.txtServidorFTP);
        txtusuario=(EditText)findViewById(R.id.txtUsuarioFTP);
        txtpassword=(EditText)findViewById(R.id.txtContrasenaFTP);
        btnConfirmar=(Button)findViewById(R.id.btnConfirmarFTP);
        ibllave=(ImageButton)findViewById(R.id.ibllave);

        //deshabilitar los EditText para evitar cambios no autorizados
        txtservidor.setEnabled(false);
        txtusuario.setEnabled(false);
        txtpassword.setEnabled(false);
        btnConfirmar.setText("Continuar");


        //Verifica o crea carpetas SisMetPrec y Salida
        File folder=new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec");
        if (folder.exists()){
            File folder_ftp=new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Ftp");
            if (folder_ftp.exists()){}else{
                //Creamos el forlder para el archivo que contendrá las credenciales del servidor ftp.
                folder_ftp.mkdirs();
            }
        }else{
            folder.mkdirs();
            File folder_sal=new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Salida");
            folder_sal.mkdirs();
            //Creamos el forlder para el archivo que contendrá las credenciales del servidor ftp.
            folder.mkdirs();
            File folder_ftp=new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Ftp");
            folder_ftp.mkdirs();
        }
        /*Verificamos la existencia del archivo. En caso de que no exista lo creamos con las palabras por defecto que
          indicarán que es la primera vez que se inicia la aplicación por lo que el usuario deberá ingresar los parámetros
          correctos para obtener conexión. En caso de que el archivo si exista, no se realiza ninguna acción para
          mantener los datos ingresados la última vez.*/
        }
        catch(Exception e)
        {
            messageBox("ERROR", e.getMessage());
        }
        try {
            File archivoftp=new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Ftp/credenciales.txt");
            if (archivoftp.exists()){
                //archivoftp.delete();
            }else{
                OutputStreamWriter fout=new OutputStreamWriter(new FileOutputStream(archivoftp));
                fout.write("Servidor,Usuario,Clave");
                fout.close();
            }
        }
        catch (Exception ex)
        {
            messageBox("ERROR", ex.getMessage());
            Log.e("Error","Error al escribir las credenciales del servidor FTP");
           // Toast.makeText(getApplicationContext(),"Error al escribir las credenciales del servidor FTP",Toast.LENGTH_SHORT).show();
        }

        try {
            String srv = "";
            String usr = "";
            String pss = "";
            String cadena="";
            File archivoftp = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Ftp/credenciales.txt");
            BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(archivoftp)));

            String linea;
            while((linea = fin.readLine()) != null)
                cadena=linea;
            fin.close();
            //Separamos los datos de la cadena
            String[] parts = cadena.split(",");
            srv = parts[0];
            usr = parts[1];
            pss = parts[2];

            txtservidor.setText(srv);
            txtusuario.setText(usr);
            txtpassword.setText(pss);
        }
        catch (Exception ex){
            //Log.e("Error","Error al leer las credenciales del servidor FTP");
            messageBox("ERROR", ex.getMessage());
            Toast.makeText(getApplicationContext(),"Error al leer las credenciales del servidor FTP",Toast.LENGTH_SHORT).show();
        }

        /*Evento onclick del botón donde se codifica que los textos que se encuentran en los Edittext reemplazarán
          los datos en el archivo de texto. De esta forma se obtiene que si el usuario cambia las credenciales estas serán
          almacenadas instantáneamente y se pasará al siguiente activity donde se intentará la conexión. En caso de que las
          credenciales no sean correctas, no se iniciará la transmisión y se presentará un error de conexión.*/
        final String titulo=btnConfirmar.getText().toString();
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titulo.equals("Continuar")){
                    //Instanciamos la siguiente actividad enviando los datos del servidor ftp
                    //Toast.makeText(servidorftp.this, "Llegó", Toast.LENGTH_LONG).show();

                    try {
                        File archivoftp = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Ftp/credenciales.txt");
                        OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(archivoftp));
                        fout.write(txtservidor.getText().toString() + "," + txtusuario.getText().toString() + "," + txtpassword.getText().toString());
                        fout.close();
                        //Instanciamos la siguiente actividad enviando los datos del servidor ftp
                        Intent selproyecto = new Intent(servidorftp.this, Selec_Proyecto.class);
                        selproyecto.putExtra("codmetro", metrologo);
                        selproyecto.putExtra("servidor", txtservidor.getText().toString());
                        selproyecto.putExtra("usuario", txtusuario.getText().toString());
                        selproyecto.putExtra("password", txtpassword.getText().toString());
                        startActivity(selproyecto);
                    } catch (Exception ex) {
                        // Log.e("Error", "Error al escribir las credenciales del servidor FTP");
                        Toast.makeText(getApplicationContext(),"Error al leer las credenciales del servidor FTP",Toast.LENGTH_SHORT).show();
                        messageBox("ERROR", ex.getMessage());

                    }
                }else {
                    try {
                        File archivoftp = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Ftp/credenciales.txt");
                        OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(archivoftp));
                        fout.write(txtservidor.getText().toString() + "," + txtusuario.getText().toString() + "," + txtpassword.getText().toString());
                        fout.close();
                        //Instanciamos la siguiente actividad enviando los datos del servidor ftp
                        Intent selproyecto = new Intent(servidorftp.this, Selec_Proyecto.class);
                        selproyecto.putExtra("codmetro", metrologo);
                        selproyecto.putExtra("servidor", txtservidor.getText().toString());
                        selproyecto.putExtra("usuario", txtusuario.getText().toString());
                        selproyecto.putExtra("password", txtpassword.getText().toString());
                        startActivity(selproyecto);
                    } catch (Exception ex) {
                        Log.e("Error", "Error al escribir las credenciales del servidor FTP");
                        messageBox("ERROR", ex.getMessage());

                        //   Toast.makeText(getApplicationContext(),"Error al leer las credenciales del servidor FTP",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ibllave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(servidorftp.this);
                    builder.setTitle("Ingrese contraseña de administrador:");

                    // Set up the input
                    final EditText input = new EditText(servidorftp.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("Habilitar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            contrasena = input.getText().toString();
                            if (contrasena.equals("")) {
                                Toast.makeText(servidorftp.this, "Debe ingresar la contraseña correcta. Campo obligatorio.", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                //Toast.makeText(servidorftp.this, contrasena, Toast.LENGTH_LONG).show();
                                if (contrasena.equals("admin123*")) {
                                    txtservidor.setEnabled(true);
                                    txtusuario.setEnabled(true);
                                    txtpassword.setEnabled(true);
                                    btnConfirmar.setText("Confirmar");
                                } else {
                                    Toast.makeText(servidorftp.this, "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                catch (Exception ex) {
                    messageBox("ERROR", ex.getMessage());
                }

            }
        });

    }

    @Override


    public void onBackPressed() {

        AlertDialog.Builder dialogo1=new AlertDialog.Builder(servidorftp.this);
        dialogo1.setTitle("Cerrar Metrología");
        dialogo1.setMessage("¿Salir de Metrología?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }

        });
        dialogo1.show();

    }
}