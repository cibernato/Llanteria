package com.jerbo.llanteria.Venta

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jerbo.llanteria.Models.Producto
import com.jerbo.llanteria.PaginaPrincipal.MainActivity
import com.jerbo.llanteria.R
import kotlinx.android.synthetic.main.activity_add_venta.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddVenta : AppCompatActivity(), VentaDialogFragment.Metodos, ProductAdapter.onClickListeners, CoroutineScope {
    //Co routines
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    //Variables
    private lateinit var productAdapter: ProductAdapter
    private lateinit var list_productos: Array<Producto>
    private var product_show: MutableList<Producto> = ArrayList()
    private lateinit var preferences: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor
    private lateinit var product_show_gson: String
    private var list = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private var actualizar_flag = 0
    private var pos: Int = 0
    private lateinit var modificar: Producto
    private var gson = Gson()
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        job = Job()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_venta)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        mEditor = preferences.edit()
        createIU()
    }

    private fun checkSharedPreferences() {
        val toParse = preferences.getString(getString(R.string.saved_products), "[]")
        val collectionType = object : TypeToken<MutableList<Producto>>() {
        }.type
        try {
            product_show = gson.fromJson<List<Producto>>(toParse, collectionType).toMutableList()
        } catch (e: Exception) {
            Log.d(TAG, "checkSharedPreferences: String " + toParse!!)
            e.printStackTrace()
        }

    }

    private fun clearText() {
        producto_layout.editText?.setText("")
        cantidad.setText("")
    }

    private fun checkId(pos: Int): Boolean {
        if (pos == -1) {
            Toast.makeText(this, "Seleccione un elemento de la lista", Toast.LENGTH_SHORT).show()
            return false
        }
        for (p in product_show) {
            if (p.id == list_productos[pos].id)
                return false
        }
        return true
    }

    private fun validateProducto(): Boolean {
        val t = producto_layout.editText?.text.toString().trim { it <= ' ' }
        return if (t.isEmpty()) {
            producto_layout.error = "Seleccione producto"
            false
        } else {
            producto_layout.error = null
            true
        }
    }

    private fun validateCantidad(): Boolean {
        val t = cantidad.text.toString().trim { it <= ' ' }
        val index = buscarItem(producto_layout.editText?.text.toString())
        if (index == -1) {
            Toast.makeText(this, "Revise el producto", Toast.LENGTH_SHORT).show()
            return false
        }
        return if (t.isEmpty() and (t == "") or (Integer.parseInt(t) >= list_productos[index].stock)) {
            Log.e(TAG, "validateCantidad: Si entra el problea es la intergaz")
            cantidad_layout.error =
                "Ingrese cantidad menor a :" + list_productos[buscarItem(producto_layout.editText?.text.toString())].stock
            false
        } else {
            cantidad_layout.error = null
            true
        }
    }

    private fun checkData(): Boolean {
        return !validateCantidad() or !validateProducto()
    }

    private fun createIU() {
        try {
            precio_total.setText(0)
        } catch (ignored: Exception) {
        }
        queue = Volley.newRequestQueue(this)
        checkSharedPreferences()
        updatePrice()
//        spinner_adapter =
//            ArrayAdapter.createFromResource(this, R.array.spinner_options, android.R.layout.simple_spinner_item)
//        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        productAdapter = ProductAdapter(this, product_show, this)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_list_products.layoutManager = linearLayoutManager
        recycler_list_products.adapter = productAdapter
        producto_seleccionado.setAdapter(adapter)
        cantidad.setOnKeyListener { v, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            false
        }
        llenarProductos()
        listeners()
    }

    private fun listeners() {
        producto_clear.setOnClickListener { producto_seleccionado.setText("") }
        venta_añadir.setOnClickListener {
            if (actualizar_flag == 0) {
                pos = buscarItem(producto_seleccionado.text.toString())
                if (!checkData() and checkId(pos)) {
                    añadirProducto(list_productos[pos])
                    clearText()
                    updatePrice()
                } else {
                    if ((pos != -1) and (cantidad_layout.error == null))
                        Toast.makeText(this, "Elemento ya existe en la lista", Toast.LENGTH_SHORT).show()
                }
            } else if (actualizar_flag == 1) {
                product_show.remove(modificar)
                pos = buscarItem(producto_seleccionado.text.toString())
                if (!checkData() and checkId(pos)) {
                    añadirProducto(list_productos[pos])
                    clearText()
                    venta_añadir.text = "Añadir"
                }
                actualizar_flag = 0
                updatePrice()
            }
        }
        venta_cancelar.setOnClickListener { salir() }

    }

    private fun salir() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Desea salir de la compra actual?")
            .setCancelable(false)
            .setPositiveButton("Si") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                super.onBackPressed()
                finish()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun buscarItem(b: String): Int {
        for (i in list.indices) {
            if (list[i] == b)
                return i
        }
        return -1
    }

    private fun añadirProducto(list_producto: Producto) {
        try {
            list_producto.cantidad = Integer.parseInt(cantidad.text.toString())
            product_show.add(list_producto)
            productAdapter.notifyDataSetChanged()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

    }

    private fun llenarProductos() {

        val url = "https://llanteriamari.000webhostapp.com/getop.php?p1=SELECT * FROM & p2=producto"
        val stringRequest = JsonArrayRequest(Request.Method.GET, url, null, { response ->
            //                Type collectionType = new TypeToken<ArrayList<Producto>>() {
            //                }.getType();
            //                list = gson.fromJson(response.toString(), collectionType);

            list_productos = gson.fromJson(response.toString(), Array<Producto>::class.java)
            list.clear()
            for (p in list_productos) {
                list.add(p.producto)
            }
            adapter.notifyDataSetChanged()
        }, {})
        queue.add(stringRequest)
    }

    override fun eliminar(p: Producto) {
        product_show.remove(p)
        updatePrice()
        productAdapter.notifyDataSetChanged()
    }

    override fun modificar(p: Producto) {
        try {
            modificar = p
            producto_seleccionado.setText(p.producto)
            cantidad.setText("")
            actualizar_flag = 1
            venta_añadir.text = "Modificar"
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClick(item: Producto) {
    }

    override fun onLongClick(item: Producto): Boolean {
        val fm = supportFragmentManager
        val dialog = VentaDialogFragment.newInstance(item, this)
        dialog.show(fm, "fragment_edit_name")
        return true
    }

    private fun updatePrice() {
        var total = 0
        for (p in product_show) {
            total += p.precio * p.cantidad
        }
        precio_total.text = "Precio total: $total"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        this.supportActionBar!!.title = "Proforma de venta"
        menuInflater.inflate(R.menu.addventa_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addventa_confirmar -> {
                pushToDataBase()
                Log.e(TAG,"entra y error ")
                true
            }
            android.R.id.home -> {
                salir()
                true
            }
            R.id.addventa_guardar_compra -> {
                guardarCompra()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun pushToDataBase() {
        displayProgressBar(true)
        launch {
            if (!checkIfUserExists()) {
                val dniId = withContext(Dispatchers.IO) { getClienteDni() }
                val cabID = withContext(Dispatchers.IO) { insertCab(dniId) }
                if (withContext(Dispatchers.IO) { insertDet(cabID) }) {
                    clearUI()
                    llenarProductos()
                    Toast.makeText(this@AddVenta, "Venta realizada correctamente", Toast.LENGTH_SHORT).show()
                }
                displayProgressBar(false)
            } else {
                displayProgressBar(false)
                Toast.makeText(this@AddVenta, "Usuario no existe registrese primero", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun clearUI() {
        venta_usuario.editText?.setText("")
        cantidad.setText("")
        product_show.clear()
        mEditor.putString(getString(R.string.saved_products), "[]")
        mEditor.commit()
        productAdapter.notifyDataSetChanged()
        precio_total.text = "0"

    }

    suspend fun getClienteDni() = suspendCoroutine<String> {
        val url =
            "https://llanteriamari.000webhostapp.com/getClienteDni.php?p1=" + venta_usuario.editText?.text.toString().trim { it <= ' ' }
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                it.resume(response)
            },
            { })
        queue.add(stringRequest)

    }

    suspend fun insertDet(cabID: String) = suspendCoroutine<Boolean> {
        var values = ""
        this.product_show.forEach {
            values += "(${it.cantidad},${it.precio},${it.id},$cabID),"
        }
        val length = values.length - 1
        val substring = values.substring(0 until length)
        val url =
            "https://llanteriamari.000webhostapp.com/insert_comprobante_det.php?p1=$substring"
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                if (response != "0") {
                    it.resume(response == "1")
                    Log.e(TAG, " $response")
                } else {
                    it.resume(false)
                    displayProgressBar(false)
                    Log.e(TAG, "Error al insertar detalles  con: $response y $substring")
                    Toast.makeText(this, "Fallo al insertar detalles ", Toast.LENGTH_SHORT).show()
                }
            },
            { })
        queue.add(stringRequest)

    }

    suspend fun insertCab(dniId: String) = suspendCoroutine<String> {

        val headerData = "(1,$dniId,1)"
        val url = "https://llanteriamari.000webhostapp.com/insert_comprobante.php?p1=$headerData"
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                if (response != "0") {
                    it.resume(response)
                } else {
                    displayProgressBar(false)
                    Log.e(TAG, "Error al insertar cabecera con: $headerData y $response")
                    Toast.makeText(this, "Fallo al insertar elemtntos ", Toast.LENGTH_SHORT).show()
                }
            }, {
                displayProgressBar(false)
                Log.e(TAG, " $it")
            })
        queue.add(stringRequest)
    }

    suspend fun checkIfUserExists() = suspendCoroutine<Boolean> { cont ->
        val url =
            "https://llanteriamari.000webhostapp.com/checkDni.php?p1=" + venta_usuario.editText?.text.toString().trim { it <= ' ' }
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                cont.resume(response.trim() == "0")
            },
            { })
        queue.add(stringRequest)

    }

    private fun guardarCompra() {
        product_show_gson = gson.toJson(product_show)
        mEditor.putString(getString(R.string.saved_products), product_show_gson)
        mEditor.commit()
        Toast.makeText(this, "Guardado exitosamente", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        salir()
    }

    companion object {
        private const val TAG = "AddVenta"
    }

    private fun displayProgressBar(b: Boolean) {
        if (b) {
            venta_progress_bar.visibility = View.VISIBLE
        } else {
            venta_progress_bar.visibility = View.GONE
        }
    }

}