package sv.edu.udb.comida_mex

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Guardar la orden
    fun saveOrder(orderItems: List<Food>) {
        val gson = Gson()
        val json = gson.toJson(orderItems)
        sharedPreferences.edit().putString("order_history", json).apply()
    }

    // Obtener la orden
    fun getOrder(): List<Food> {
        val gson = Gson()
        val json = sharedPreferences.getString("order_history", null)
        val type = object : TypeToken<List<Food>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // Limpiar el historial cuando se cierra sesión
    fun clearOrder() {
        sharedPreferences.edit().remove("order_history").apply()
    }
}
