package sv.edu.udb.comida_mex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.comida_mex.databinding.ActivityViewOrderBinding
import com.google.gson.Gson
import com.google.common.reflect.TypeToken
import android.content.SharedPreferences
import android.content.Context

class ViewOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewOrderBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPreferences: SharedPreferences
    private var orderItems: MutableList<Food> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        sharedPreferences = getSharedPreferences("OrderHistory", Context.MODE_PRIVATE)

        orderItems = intent.getParcelableArrayListExtra<Food>("orderItems")?.toMutableList() ?: sessionManager.getOrder().toMutableList()
        Log.d("ViewOrderActivity", "Recibidos ${orderItems.size} items")

        updateOrderText()

        binding.payButton.setOnClickListener {
            if (orderItems.isEmpty()) {
                Toast.makeText(this, "No hay productos en la orden. ¡Agrega productos antes de pagar!", Toast.LENGTH_SHORT).show()
            } else {
                saveOrderToHistory(orderItems)
                Toast.makeText(this, "¡Pago exitoso!", Toast.LENGTH_SHORT).show()

                // Enviar un resultado de éxito a SelectFoodActivity
                val resultIntent = Intent()
                resultIntent.putExtra("paymentSuccess", true)
                setResult(RESULT_OK, resultIntent)

                // Limpiar la orden actual
                orderItems.clear()
                sessionManager.saveOrder(orderItems)
                updateOrderText()

                finish() // Cerrar la actividad
            }
        }
    }

    private fun updateOrderText() {
        val orderText = if (orderItems.isEmpty()) {
            "Tu orden está vacía. ¡Agrega productos!"
        } else {
            buildString {
                appendLine("Orden:")
                orderItems.forEach { food ->
                    appendLine("${food.Nombre} - $${String.format("%.2f", food.Precio)}")
                }
                val total = orderItems.sumOf { it.Precio }
                appendLine("Total: $${String.format("%.2f", total)}")
            }
        }
        binding.orderTextView.text = orderText
    }

    private fun saveOrderToHistory(orderItems: List<Food>) {
        val gson = Gson()
        val editor = sharedPreferences.edit()

        val historyJson = sharedPreferences.getString("order_history", null)
        val type = object : TypeToken<MutableList<List<Food>>>() {}.type
        val history: MutableList<List<Food>> = if (historyJson != null) {
            gson.fromJson(historyJson, type)
        } else {
            mutableListOf()
        }

        history.add(orderItems)

        val updatedHistoryJson = gson.toJson(history)
        editor.putString("order_history", updatedHistoryJson)
        editor.apply()
    }
}
