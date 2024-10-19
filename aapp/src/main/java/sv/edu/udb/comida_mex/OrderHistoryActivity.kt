package sv.edu.udb.comida_mex

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sv.edu.udb.comida_mex.databinding.ActivityOrderHistoryBinding

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("OrderHistory", Context.MODE_PRIVATE)

        // Obtener el historial de compras desde SharedPreferences
        val orderHistory = getOrderHistory()

        // Configurar RecyclerView con el historial de compras
        orderHistoryAdapter = OrderHistoryAdapter(orderHistory)
        binding.orderHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.orderHistoryRecyclerView.adapter = orderHistoryAdapter
    }

    private fun getOrderHistory(): List<List<Food>> {
        val gson = Gson()
        val historyJson = sharedPreferences.getString("order_history", null)
        val type = object : TypeToken<List<List<Food>>>() {}.type

        return if (historyJson != null) {
            gson.fromJson(historyJson, type)
        } else {
            emptyList()
        }
    }

    // Método para borrar el historial de órdenes
    fun clearOrderHistory() {
        sharedPreferences.edit().remove("order_history").apply()
    }
}
