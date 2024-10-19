package sv.edu.udb.comida_mex

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import java.net.HttpURLConnection
import java.net.URL

object SvgLoader {
    fun loadSvgFromUrl(imageView: ImageView, url: String) {
        Thread {
            try {
                val svgUrl = URL(url)
                val urlConnection: HttpURLConnection = svgUrl.openConnection() as HttpURLConnection
                urlConnection.doInput = true
                urlConnection.connect()
                val inputStream = urlConnection.inputStream
                val svg = SVG.getFromInputStream(inputStream)
                val drawable = PictureDrawable(svg.renderToPicture())

                // Cambiar el drawable en el hilo principal
                imageView.post {
                    imageView.setImageDrawable(drawable)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
