package org.example.eventos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by vicch on 25/03/2018.
 */

public class EventosWeb extends AppCompatActivity{

    String evento;
    WebView navegador;

    ProgressDialog dialogo;

    final InterfazComunicacion miInterfazJava = new InterfazComunicacion(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventos_web);

        Bundle extras = getIntent().getExtras();
        evento = extras.getString("evento");

        navegador = (WebView) findViewById(R.id.webkit);
        navegador.getSettings().setJavaScriptEnabled(true);
        navegador.getSettings().setBuiltInZoomControls(false);

        if (comprobarConectividad()) {

            //navegador.loadUrl("https://eventos-288ba.firebaseapp.com/index.html");
            //navegador.loadUrl("https://eventos-288ba.firebaseapp.com/index2.html");
            navegador.loadUrl("file:///android_asset/index.html");

            navegador.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*
                String url_filtro = "http://www.androidcurso.com/";
                if (!url.toString().equals(url_filtro)){
                    view.loadUrl(url_filtro);
                }
                */
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if (comprobarConectividad()) {
                        dialogo = new ProgressDialog(EventosWeb.this);
                        dialogo.setMessage("Cargando...");
                        dialogo.setCancelable(true);
                        dialogo.show();
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    dialogo.dismiss();
                    navegador.loadUrl("javascript:muestraEvento(\""+evento+"\");");
                }

            });

            navegador.setWebChromeClient(new WebChromeClient() {

                @Override
                public boolean onJsAlert(WebView view, String url, String message,
                                         final JsResult result) {
                    new AlertDialog.Builder(EventosWeb.this).setTitle("Mensaje")
                            .setMessage(message).setPositiveButton
                            (android.R.string.ok, new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            }).setCancelable(false).create().show();
                    return true;
                }

            });

        }

        navegador.addJavascriptInterface(miInterfazJava, "jsInterfazNativa");

    }

    @Override
    public void onBackPressed() {
        if (navegador.canGoBack()) {
            navegador.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private boolean comprobarConectividad() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if ((info == null || !info.isConnected() || !info.isAvailable())) {
            //Toast.makeText(EventosWeb.this,"Oops! No tienes conexión a internet",Toast.LENGTH_LONG).show();
            mostrarNoConectividad();
            return false;
        }
        return true;
    }

    private void mostrarNoConectividad(){
        AlertDialog alertDialog = new AlertDialog.Builder(EventosWeb.this).create();
        alertDialog.setTitle("Mensaje:");
        alertDialog.setMessage("No hay conectividad");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CERRAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialog.show();
    }

    public class InterfazComunicacion {
        Context mContext;
        InterfazComunicacion(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void volver(){
            finish();
        }
    }

}
