package sv.edu.udb.comida_mex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    private lateinit var imageSlider: ViewPager2
    private lateinit var sliderAdapter: ImageSliderAdapter
    private var currentPage = 0
    private val handler = Handler(Looper.getMainLooper())
    private val delay: Long = 5000 // 5 segundos entre cada pase automático

    private val sliderRunnable = object : Runnable {
        override fun run() {
            if (currentPage == sliderAdapter.itemCount) {
                currentPage = 0
            }
            imageSlider.setCurrentItem(currentPage++, true)
            handler.postDelayed(this, delay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si el usuario está autenticado
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // Si no está autenticado, redirigir a la pantalla de inicio de sesión
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Terminar esta actividad para que no vuelva atrás
            return
        }

        setContentView(R.layout.activity_welcome)

        // Vincula el ViewPager2 (slider)
        imageSlider = findViewById(R.id.imageSlider)

        // Configura el slider de imágenes
        val images = listOf(
            R.drawable.loxi,
            R.drawable.splider2,
            R.drawable.dox,
            R.drawable.pro
        )
        sliderAdapter = ImageSliderAdapter(images)
        imageSlider.adapter = sliderAdapter

        // Iniciar el pase automático
        handler.postDelayed(sliderRunnable, delay)

        // Configurar el BottomNavigationView en la parte superior
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> true
                R.id.action_select_food -> {
                    val intent = Intent(this, SelectFoodActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_view_order -> {
                    val intent = Intent(this, ViewOrderActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_order_history -> {
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_logout -> {
                    // Cerrar sesión y redirigir a la pantalla de login
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Cargar los íconos de Creative Commons
        loadCreativeCommonsIcons()

        // Configurar el enlace de la licencia
        val ccLinkTextView = findViewById<TextView>(R.id.cc_link)
        ccLinkTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://creativecommons.org/licenses/by-nc-nd/4.0/?ref=chooser-v1"))
            startActivity(browserIntent)
        }
    }

    private fun loadCreativeCommonsIcons() {
        val ccIcon = findViewById<ImageView>(R.id.cc_icon)
        val byIcon = findViewById<ImageView>(R.id.by_icon)
        val ncIcon = findViewById<ImageView>(R.id.nc_icon)
        val ndIcon = findViewById<ImageView>(R.id.nd_icon)

        SvgLoader.loadSvgFromUrl(ccIcon, "https://mirrors.creativecommons.org/presskit/icons/cc.svg")
        SvgLoader.loadSvgFromUrl(byIcon, "https://mirrors.creativecommons.org/presskit/icons/by.svg")
        SvgLoader.loadSvgFromUrl(ncIcon, "https://mirrors.creativecommons.org/presskit/icons/nc.svg")
        SvgLoader.loadSvgFromUrl(ndIcon, "https://mirrors.creativecommons.org/presskit/icons/nd.svg")
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(sliderRunnable) // Detener el pase automático al pausar
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(sliderRunnable, delay) // Reanudar pase automático al reanudar
    }
}
