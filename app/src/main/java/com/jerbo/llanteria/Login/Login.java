package com.jerbo.llanteria.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jerbo.llanteria.PaginaPrincipal.MainActivity;
import com.jerbo.llanteria.R;


public class Login extends AppCompatActivity {
    ProgressBar progressBar;
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        progressBar = findViewById(R.id.login_progress_bar);
        final EditText user = findViewById(R.id.user);
        final EditText pass = findViewById(R.id.pass);
        Button sigin = findViewById(R.id.sig_in);
        sigin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "https://llanteriamari.000webhostapp.com/connection.php?p1=" + user.getText().toString() + "& p2=" + pass.getText().toString();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("0")) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(context, "Ocurrio un error al loguear, revise usuario/contraseña", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(context, MainActivity.class));
                        editor.putString(getString(R.string.saved_products),"[]");
                        editor.apply();
                        finish();
                    }
                }
            }, error -> progressBar.setVisibility(View.GONE));
            queue.add(stringRequest);
        });


    }
}
