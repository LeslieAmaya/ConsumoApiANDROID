package mx.edu.uts.consumoapi_lal

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val service=RetrofitServiceFactory.makeRetroFitService()
    lateinit var tbProductos:TableLayout
    lateinit var ctClave:EditText
    lateinit var ctNombre:EditText
    lateinit var ctPrecio:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tbProductos=findViewById(R.id.tablaDatos)

        ctClave=findViewById(R.id.ctClave)
        ctNombre=findViewById(R.id.ctNombre)
        ctPrecio=findViewById(R.id.ctPrecio)
        var btnNuevo:Button=findViewById(R.id.btnNuevo)
        var btnGuardar:Button=findViewById(R.id.btnGuardar)
        var btnEliminar:Button=findViewById(R.id.btnEliminar)

        btnGuardar.setOnClickListener(){
            val datos=Productos(
                ctClave.text.toString(),
                ctNombre.text.toString(),
                ctPrecio.text.toString().toDouble()
            )
            if(ctClave.isEnabled)
                Guardar(datos);
            else
                Actualizar(datos);
        }

        ctClave.doAfterTextChanged {
            llenarForma(ctClave.text.toString())
        }
        btnNuevo.setOnClickListener() {
            nuevo()
        }

        btnEliminar.setOnClickListener(){
            eliminar(ctClave.text.toString())
        }
        cargaDatos()
    }

    fun llenarForma(clave:String){
        if(ctClave.isEnabled){
            lifecycleScope.launch {
                try {
                    val miProducto=service.obtieneProductos(clave)
                    ctClave.setText(miProducto.clave)
                    ctNombre.setText(miProducto.nombre)
                    ctPrecio.setText(miProducto.precio.toString())
                    ctClave.isEnabled=false
                }
                catch (ex:Exception){
                    println(ex)
                }
            }
        }
    }
    fun nuevo(){
        ctClave.setText("")
        ctNombre.setText("")
        ctPrecio.setText("")
        ctClave.isEnabled=true
        ctClave.requestFocus()
        cargaDatos()
    }

    fun Guardar(producto:Productos){
        service.crearProducto(producto).enqueue(object :Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(this@MainActivity,"Producto Creado",
                        Toast.LENGTH_SHORT).show()
                    ctClave.isEnabled=false
                    cargaDatos()
                }
                else
                {
                    Toast.makeText(this@MainActivity,response.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t:Throwable){
                Toast.makeText(this@MainActivity,t.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun eliminar(clave:String){
        if(!ctClave.isEnabled)
        {
            service.eliminarProducto(clave).enqueue(object: Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>){
                    if(response.isSuccessful){
                        Toast.makeText(this@MainActivity, "Producto Eliminado", Toast.LENGTH_LONG).show()
                        nuevo()
                    }
                }
                override fun onFailure(call:Call<Void>, t: Throwable){
                    Toast.makeText(this@MainActivity,t.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    fun Actualizar(productos: Productos){
        service.actualizarProductos(productos.clave,productos).enqueue(object: Callback<Void>{
            override fun onResponse(call:Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity, "Producto Creado",
                        Toast.LENGTH_LONG
                    ).show()
                    ctClave.isEnabled = false
                    cargaDatos()
                }
                else {
                    Toast.makeText(
                        this@MainActivity, response.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: Call<Void>, t:Throwable){
                Toast.makeText(this@MainActivity, t.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun cargaDatos(){
        tbProductos.removeAllViews()
        lifecycleScope.launch {
            val misProductos=service.listProductos()
            // println(misProductos)

            /*  ENCABEZADO */

            val encabezado:View?=LayoutInflater.from(this@MainActivity).inflate(R.layout.table_row_n,null,false)
            val registro:View?=LayoutInflater.from(this@MainActivity).inflate(R.layout.table_row_n,null,false)
            val c1:TextView=encabezado?.findViewById<View>(R.id.colClave) as TextView
            val c2:TextView=encabezado.findViewById<View>(R.id.colNombre) as TextView
            val c3:TextView=encabezado.findViewById<View>(R.id.colPrecio) as TextView
            c1.text="CLAVE"
            c2.text="NOMBRE"
            c3.text="PRECIO"
            encabezado.setBackgroundColor(Color.MAGENTA)
            c1.setTextColor(Color.LTGRAY)
            c2.setTextColor(Color.LTGRAY)
            c3.setTextColor(Color.LTGRAY)

            tbProductos.addView(encabezado)

            for(i in misProductos.indices)
            {
                val registro:View?=LayoutInflater.from(this@MainActivity).inflate(R.layout.table_row_n,null,false)
                val c1:TextView=registro?.findViewById<View>(R.id.colClave) as TextView
                val c2:TextView=registro.findViewById<View>(R.id.colNombre) as TextView
                val c3:TextView=registro.findViewById<View>(R.id.colPrecio) as TextView
                c1.text=misProductos[i].clave
                c2.text=misProductos[i].nombre
                c3.text=misProductos[i].precio.toString()
                tbProductos.addView(registro)
            }

        }
    }
}