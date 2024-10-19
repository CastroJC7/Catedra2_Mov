package sv.edu.udb.comida_mex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import sv.edu.udb.comida_mex.databinding.ActivitySelectFoodBinding

class SelectFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectFoodBinding
    private lateinit var adapter: FoodAdapter
    private lateinit var sessionManager: SessionManager
    private val orderItems = mutableListOf<Food>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Iniciar sesión y agregar los artículos de la orden previamente guardados
        sessionManager = SessionManager(this)
        orderItems.addAll(sessionManager.getOrder())

        // Configuración del RecyclerView con GridLayoutManager
        binding.foodRecyclerView.layoutManager = GridLayoutManager(this, 3)

        // Inicialización del adaptador para el RecyclerView
        adapter = FoodAdapter(emptyList()) { food ->
            orderItems.add(food)
            updateOrderSummary()
            sessionManager.saveOrder(orderItems)
            Log.d("SelectFoodActivity", "Añadido a la orden: ${food.Nombre}")
            Toast.makeText(this, "${food.Nombre} añadido a la orden", Toast.LENGTH_SHORT).show()
        }

        binding.foodRecyclerView.adapter = adapter

        // Acción para ver la orden
        binding.viewOrderButton.setOnClickListener {
            val intent = Intent(this, ViewOrderActivity::class.java)
            intent.putParcelableArrayListExtra("orderItems", ArrayList(orderItems))
            startActivityForResult(intent, 1001) // Usa startActivityForResult para esperar un resultado
        }

        // Obtener los datos de Firebase
        fetchData()
        updateOrderSummary()

        // Configurar el BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    // Redirige a la actividad de inicio
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    true
                }
                R.id.action_view_order -> {
                    // Redirige a la actividad de la orden
                    startActivity(Intent(this, ViewOrderActivity::class.java))
                    true
                }
                R.id.action_order_history -> {
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_logout -> {
                    // Cerrar sesión en Firebase
                    FirebaseAuth.getInstance().signOut()

                    // Limpiar los datos de la orden en SharedPreferences
                    sessionManager.clearOrder()  // Asegúrate de tener este método en tu SessionManager

                    // Limpiar el historial de compras en SharedPreferences
                    val sharedPreferences = getSharedPreferences("OrderHistory", Context.MODE_PRIVATE)
                    sharedPreferences.edit().remove("order_history").apply()

                    // Redirigir al usuario a la pantalla de inicio de sesión
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val paymentSuccess = data?.getBooleanExtra("paymentSuccess", false) ?: false
            if (paymentSuccess) {
                // Limpiar la lista de items después del pago
                orderItems.clear()
                sessionManager.saveOrder(orderItems)
                updateOrderSummary()
            }
        }
    }

    private fun fetchData() {
        val db = FirebaseFirestore.getInstance()

        // Forzar lectura desde el servidor para obtener siempre los datos más recientes
        db.collection("comida")
            .get(Source.SERVER)  // Lee siempre desde el servidor
            .addOnSuccessListener { result ->
                val foodList = result.mapNotNull { document ->
                    try {
                        // Obtener los datos de Firebase y asegurarse de que los campos existen
                        val url = document.getString("url") ?: ""  // URL de la imagen

                        // Crear el objeto Food con los datos recuperados de Firestore
                        Food(
                            id = document.id,
                            Nombre = document.getString("Nombre") ?: "",
                            Precio = document.getDouble("Precio") ?: 0.0,
                            Descripcion = document.getString("Descripcion") ?: "",
                            url = url  // Asignar la URL de la imagen
                        )
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error parsing document", e)
                        null
                    }
                }

                // Actualiza los datos en el adaptador con los productos obtenidos
                if (foodList.isNotEmpty()) {
                    adapter.updateData(foodList)
                } else {
                    Toast.makeText(this, "No se encontraron productos disponibles.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents.", exception)
            }
    }

    private fun updateOrderSummary() {
        val totalItems = orderItems.size
        val totalPrice = orderItems.sumOf { it.Precio }
        binding.orderSummaryTextView.text = "Orden: $totalItems  - Total: $${String.format("%.2f", totalPrice)}"
    }
}
