package com.jerbo.llanteria.Venta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jerbo.llanteria.Models.Producto;
import com.jerbo.llanteria.PaginaPrincipal.MainActivity;
import com.jerbo.llanteria.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddVenta extends AppCompatActivity implements VentaDialogFragment.Metodos, ProductAdapter.onClickListeners {

    private static final String TAG = "AddVenta";
    Producto[] list_productos;
    List<Producto> product_show = new ArrayList<>();
    SharedPreferences preferences;
    SharedPreferences.Editor mEditor;
    String product_show_gson;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;
    AutoCompleteTextView producto;
    TextInputLayout product_l, cantidad_l, usuario;
    Button añadir, cancelar, clear;
    TextView precio_total;
    RecyclerView recycler_list_productos;
    ProductAdapter productAdapter;
    EditText cantidad;
    int actualizar_flag = 0;
    int pos;
    Producto modificar;
    Gson gson = new Gson();
    Spinner tipo_facturacion;
    ArrayAdapter<CharSequence> spinner_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venta);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = preferences.edit();
        createIU();
    }

    private void checkSharedPreferences() {
        String toParse = preferences.getString(getString(R.string.saved_products), "[]");
        Type collectionType = new TypeToken<ArrayList<Producto>>() {
        }.getType();
        try {
            product_show = gson.fromJson(toParse, collectionType);
        } catch (Exception e) {
            Log.d(TAG, "checkSharedPreferences: String " + toParse);
            e.printStackTrace();
        }
    }

    private void clearText() {
        producto.setText("");
        cantidad.setText("");
    }

    private boolean checkId(int pos) {
        if (pos == -1) {
            Toast.makeText(this, "Seleccione un elemento de la lista", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (Producto p : product_show) {
            if (p.getId() == list_productos[pos].getId())
                return false;
        }
        return true;
    }

    public boolean validateProducto() {
        String t = producto.getText().toString().trim();
        if (t.isEmpty()) {
            product_l.setError("Seleccione producto");
            return false;
        } else {
            product_l.setError(null);
            return true;
        }
    }

    public boolean validateCantidad() {
        String t = cantidad.getText().toString().trim();
        int index = buscarItem(producto.getText().toString());
        if (index ==-1 ){
            Toast.makeText(this, "Revise el producto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (t.isEmpty() & t.equals("") | Integer.parseInt(t) >= list_productos[index].getStock()) {
            cantidad_l.setError("Ingrese cantidad menor a :" + list_productos[buscarItem(producto.getText().toString())].getStock());
            return false;
        } else {
            cantidad_l.setError(null);
            return true;
        }
    }

    public boolean checkData() {
        if (!validateCantidad() | !validateProducto()) {
            return true;
        } else
            return false;
    }

    private void createIU() {
        recycler_list_productos = findViewById(R.id.recycler_list_products);
        precio_total = findViewById(R.id.precio_total);
        añadir = findViewById(R.id.venta_añadir);
        cancelar = findViewById(R.id.venta_cancelar);
        product_l = findViewById(R.id.producto_layout);
        cantidad_l = findViewById(R.id.cantidad_layout);
        producto = findViewById(R.id.producto_seleccionado);
        cantidad = findViewById(R.id.cantidad);
        clear = findViewById(R.id.producto_clear);
        usuario = findViewById(R.id.venta_usuario);
        tipo_facturacion = findViewById(R.id.venta_spinner);
        try {
            precio_total.setText(0);
        } catch (Exception ignored) {
        }
        checkSharedPreferences();
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.spinner_options, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo_facturacion.setAdapter(spinner_adapter);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        productAdapter = new ProductAdapter(this, product_show, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycler_list_productos.setLayoutManager(linearLayoutManager);
        recycler_list_productos.setAdapter(productAdapter);
        producto.setAdapter(adapter);
        cantidad.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });
        llenarProductos();
        listeners();
    }

    private void listeners() {
        clear.setOnClickListener(v -> producto.setText(""));
        añadir.setOnClickListener(v -> {
            if (actualizar_flag == 0) {
                pos = buscarItem(producto.getText().toString());
                if (!checkData() & checkId(pos)) {
                    añadirProducto(list_productos[pos]);
                    clearText();
                    updatePrice();
                } else {
                    if (pos != -1 & cantidad_l.getError() == null)
                        Toast.makeText(this, "Elemento ya existe en la lista", Toast.LENGTH_SHORT).show();
                }
            } else if (actualizar_flag == 1) {
                product_show.remove(modificar);
                pos = buscarItem(producto.getText().toString());
                if (!checkData() & checkId(pos)) {
                    añadirProducto(list_productos[pos]);
                    clearText();
                    añadir.setText("Añadir");
                }
                actualizar_flag = 0;
                updatePrice();
            }
        });
        cancelar.setOnClickListener(v -> {
            salir();
        });

    }

    private void salir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Desea salir de la compra actual?")
                .setCancelable(false)
                .setPositiveButton("Si", (dialog, which) -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int buscarItem(String b) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(b))
                return i;
        }
        return -1;
    }

    private void añadirProducto(Producto list_producto) {
        try {
            list_producto.setCantidad(Integer.parseInt(String.valueOf(cantidad.getText())));
            product_show.add(list_producto);
            productAdapter.notifyDataSetChanged();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private void llenarProductos() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://llanteriamari.000webhostapp.com/getop.php?p1=SELECT * FROM & p2=producto";
        final Gson gson = new Gson();
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
//                Type collectionType = new TypeToken<ArrayList<Producto>>() {
//                }.getType();
//                list = gson.fromJson(response.toString(), collectionType);

            list_productos = gson.fromJson(response.toString(), Producto[].class);
            list.clear();
            for (int i = 0; i < list_productos.length; i++) {
                list.add(list_productos[i].getProducto());
            }
            adapter.notifyDataSetChanged();
        }, error -> {

        });
        queue.add(stringRequest);
    }

    @Override
    public void eliminar(Producto p) {
        product_show.remove(p);
        updatePrice();
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void modificar(Producto p) {
        try {
            modificar = p;
            producto.setText(p.getProducto());
            cantidad.setText("");
            actualizar_flag = 1;
            añadir.setText("Modificar");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(Producto item) {

    }

    @Override
    public boolean onLongClick(Producto item) {
        FragmentManager fm = getSupportFragmentManager();
        VentaDialogFragment dialog = VentaDialogFragment.newInstance(item, this);
        dialog.show(fm, "fragment_edit_name");
        return true;
    }

    public void updatePrice() {
        int total = 0;
        for (Producto p : product_show) {
            total += p.getPrecio() * p.getCantidad();
        }
        precio_total.setText("Precio total: " + total);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        (this).getSupportActionBar().setTitle("Proforma de venta");
        getMenuInflater().inflate(R.menu.addventa_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addventa_confirmar:
                Toast.makeText(this, "Mostrar boleta xd", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case android.R.id.home:
                salir();
                return true;
            case R.id.addventa_guardar_compra:
                guardarCompra();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void guardarCompra() {
        product_show_gson = gson.toJson(product_show);
        mEditor.putString(getString(R.string.saved_products), product_show_gson);
        mEditor.commit();
        Toast.makeText(this, "Guardado exitosamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        salir();
    }


}
