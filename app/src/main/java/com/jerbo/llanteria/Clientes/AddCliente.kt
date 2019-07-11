package com.jerbo.llanteria.Clientes

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jerbo.llanteria.PaginaPrincipal.MainActivity
import com.jerbo.llanteria.R
import kotlinx.android.synthetic.main.activity_add_cliente.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class AddCliente : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cliente)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        createIU()
    }

    private fun createIU() {
        job = Job()
        queue = Volley.newRequestQueue(this)
        aceptar_button_cliente.setOnClickListener {
            if (!checkData()) {
                displayProgressBar(true)
                launch {
                    if (checkIfUserExists())
                        createUser()
                    else {
                        displayProgressBar(false)
                        Toast.makeText(this@AddCliente, "Usuario ya registrado con ese DNI", Toast.LENGTH_SHORT).show()
                    }
                }


            }
        }
        cancelar_button_cliente.setOnClickListener { salir() }
    }

    suspend fun checkIfUserExists() = suspendCoroutine<Boolean> { cont ->
        val url =
            "https://llanteriamari.000webhostapp.com/checkDni.php?p1=" + dni_cliente.editText!!.text.toString().trim { it <= ' ' }
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                cont.resume(response.trim() == "0")
                Log.e(TAG, "checkIfUserExists: $response")
            },
            { })
        queue.add(stringRequest)

    }

    private fun salir() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Desea cancelar la operacion actual?")
            .setCancelable(false)
            .setPositiveButton("Si") { _, _ ->
                run {
                    startActivity(Intent(this, MainActivity::class.java))
                    super.onBackPressed()
                }
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun createUser() {


        val url = "https://llanteriamari.000webhostapp.com/insert_user.php?" +
                "p1=" + dni_cliente.editText!!.text.toString().trim { it <= ' ' } + " & " +
                "p2=" + nombre_cliente.editText!!.text.toString().trim { it <= ' ' } + " & " +
                "p3=" + apellidos_cliente.editText!!.text.toString().trim { it <= ' ' } + " & " +
                "p4=" + direccion_cliente.editText!!.text.toString().trim { it <= ' ' } + " & " +
                "p5=" + email_cliente.editText!!.text.toString().trim { it <= ' ' }
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            if (response == "1") {
                displayProgressBar(false)
                Toast.makeText(this, "Usuario añadido exitosamente", Toast.LENGTH_SHORT).show()
                dni_cliente.editText!!.setText("")
                nombre_cliente.editText!!.setText("")
                apellidos_cliente.editText!!.setText("")
                direccion_cliente.editText!!.setText("")
                email_cliente.editText!!.setText("")
            } else {
                displayProgressBar(false)
                Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_SHORT).show()
            }

        }, {})
        queue.add(stringRequest)
    }

    private fun checkData(): Boolean {
        return !validateNombre() or !validateApellido() or !validateDni() or !validateCorreo() or !validateDireccion()

    }

    private fun validateCorreo(): Boolean {
        val t = email_cliente.editText!!.text.toString().trim { it <= ' ' }
        return if (t.isNotEmpty() && t.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$".toRegex())) {
            email_cliente.error = null
            true
        } else {
            email_cliente.error = "Ingrese correo valido"
            false

        }
    }

    private fun validateDireccion(): Boolean {
        val t = direccion_cliente.editText!!.text.toString().trim { it <= ' ' }
        return if (t.isEmpty()) {
            direccion_cliente.error = "Ingrese direccion valida"
            false
        } else {
            direccion_cliente.error = null
            true
        }
    }

    private fun validateDni(): Boolean {
        val t = dni_cliente.editText!!.text.toString().trim { it <= ' ' }
        return if (t.isNotEmpty() && t.length == 8) {
            dni_cliente.error = null
            true
        } else {
            dni_cliente.error = "ingrese un DNI valido"
            false
        }
    }

    private fun validateApellido(): Boolean {
        val t = apellidos_cliente.editText!!.text.toString().trim { it <= ' ' }
        return if (t.isEmpty()) {
            apellidos_cliente.error = "Ingrese un apellido"
            false
        } else {
            apellidos_cliente.error = null
            true
        }
    }

    private fun validateNombre(): Boolean {
        val t = nombre_cliente.editText!!.text.toString().trim { it <= ' ' }
        return if (t.isEmpty()) {
            nombre_cliente.error = "Ingrese un nombre"
            false
        } else {
            nombre_cliente.error = null
            true
        }
    }

    private fun displayProgressBar(b: Boolean) {
        if (b) {
            nombre_cliente.visibility = View.GONE
            apellidos_cliente.visibility = View.GONE
            dni_cliente.visibility = View.GONE
            direccion_cliente.visibility = View.GONE
            email_cliente.visibility = View.GONE
            aceptar_button_cliente.visibility = View.GONE
            cancelar_button_cliente.visibility = View.GONE
            cliente_progress_bar.visibility = View.VISIBLE
        } else {
            nombre_cliente.visibility = View.VISIBLE
            apellidos_cliente.visibility = View.VISIBLE
            dni_cliente.visibility = View.VISIBLE
            direccion_cliente.visibility = View.VISIBLE
            email_cliente.visibility = View.VISIBLE
            aceptar_button_cliente.visibility = View.VISIBLE
            cancelar_button_cliente.visibility = View.VISIBLE
            cliente_progress_bar.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                salir()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        salir()
    }

    companion object {
        private const val TAG = "AddClientejava"
    }
}
