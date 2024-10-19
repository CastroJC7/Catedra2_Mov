package sv.edu.udb.comida_mex

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Food(
    val id: String = "",
    val Nombre: String = "",
    val Precio: Double = 0.0,
    val Descripcion: String = "",
    val url: String = ""
) : Parcelable
