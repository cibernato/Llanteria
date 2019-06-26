package com.jerbo.llanteria.Clientes;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jerbo.llanteria.PaginaPrincipal.MainActivity;
import com.jerbo.llanteria.R;

public class AddCliente extends AppCompatActivity {
    TextInputLayout nombre, apellido, direccion, correo, dni;
    Button aceptar, cancelar;
    ProgressBar progressBar;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cliente);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createIU();
    }

    private void createIU() {
        nombre = findViewById(R.id.nombre_cliente);
        apellido = findViewById(R.id.apellidos_cliente);
        direccion = findViewById(R.id.direccion_cliente);
        correo = findViewById(R.id.email_cliente);
        dni = findViewById(R.id.dni_cliente);
        aceptar = findViewById(R.id.aceptar_button_cliente);
        cancelar = findViewById(R.id.cancelar_button_cliente);
        progressBar = findViewById(R.id.cliente_progress_bar);
        test = findViewById(R.id.test);
        aceptar.setOnClickListener(v -> {
            if (!checkData()) {
                createUser();
            }
        });
        cancelar.setOnClickListener(v -> {
            salir();
        });
    }
    public void salir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Desea cancelar la operacion actual?")
                .setCancelable(false)
                .setPositiveButton("Si", (dialog, which) -> {
                    startActivity(new Intent(this,MainActivity.class));
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void createUser() {
        displayProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://llanteriamari.000webhostapp.com/insert_user.php?" +
                "p1=" + dni.getEditText().getText().toString().trim() + " & " +
                "p2=" + nombre.getEditText().getText().toString().trim() + " & " +
                "p3=" + apellido.getEditText().getText().toString().trim() + " & " +
                "p4=" + direccion.getEditText().getText().toString().trim() + " & " +
                "p5=" + correo.getEditText().getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            if (response.equals("1")) {
                displayProgressBar(false);
                Toast.makeText(this, "Usuario añadido exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                displayProgressBar(false);
                Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
            }

        }, error -> {

        });
        queue.add(stringRequest);
    }

    private boolean checkData() {
        if (!validateNombre() | !validateApellido() | !validateDni() | !validateCorreo() | !validateDireccion()) {
            return true;
        }else
            return false;

    }

    private boolean validateCorreo() {
        String t = correo.getEditText().getText().toString().trim();
        if (!t.isEmpty() && t.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")) {
            correo.setError(null);
            return true;
        } else {
            correo.setError("Ingrese correo valido");
            return false;

        }
    }

    private boolean validateDireccion() {
        String t = direccion.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            direccion.setError("Ingrese direccion valida");
            return false;
        } else {
            direccion.setError(null);
            return true;
        }
    }

    private boolean validateDni() {
        String t = dni.getEditText().getText().toString().trim();
        if (!t.isEmpty() && t.length() == 8) {
            dni.setError(null);
            return true;
        } else {
            dni.setError("ingrese un DNI valido");
            return false;
        }
    }

    private boolean validateApellido() {
        String t = apellido.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            apellido.setError("Ingrese un apellido");
            return false;
        } else {
            apellido.setError(null);
            return true;
        }
    }

    private boolean validateNombre() {
        String t = nombre.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            nombre.setError("Ingrese un nombre");
            return false;
        } else {
            nombre.setError(null);
            return true;
        }
    }

    private void displayProgressBar(boolean b)  {
        if (b) {
            nombre.setVisibility(View.GONE);
            apellido.setVisibility(View.GONE);
            dni.setVisibility(View.GONE);
            direccion.setVisibility(View.GONE);
            correo.setVisibility(View.GONE);
            aceptar.setVisibility(View.GONE);
            cancelar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            nombre.setVisibility(View.VISIBLE);
            apellido.setVisibility(View.VISIBLE);
            dni.setVisibility(View.VISIBLE);
            direccion.setVisibility(View.VISIBLE);
            correo.setVisibility(View.VISIBLE);
            aceptar.setVisibility(View.VISIBLE);
            cancelar.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                salir();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        salir();
    }
}
