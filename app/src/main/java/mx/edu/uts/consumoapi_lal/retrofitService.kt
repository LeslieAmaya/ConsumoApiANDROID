package mx.edu.uts.consumoapi_lal

import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface retrofitService {
    @GET("Productos")
    suspend fun listProductos():List<Productos>

    @POST("Productos")
    fun crearProducto(@Body producto:Productos): Call<Void>

    @GET("Productos/{id}")
    suspend fun obtieneProductos(@Path("id") clave:String):Productos

    @DELETE("Productos/{id}")
    fun eliminarProducto(@Path("id") clave:String): Call<Void>

    @PUT("Productos/{id}")
    fun actualizarProductos(@Path("id") clave:String, @Body producto: Productos): Call<Void>
}
object RetrofitServiceFactory{
    fun makeRetroFitService(): retrofitService {
        return Retrofit.Builder()
            .baseUrl("http://apiprod.somee.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(retrofitService::class.java)
    }
}