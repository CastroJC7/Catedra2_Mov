package sv.edu.udb.comida_mex

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sv.edu.udb.comida_mex.databinding.ItemFoodBinding

class FoodAdapter(
    private var foodList: List<Food>,
    private val onBuyClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.bind(food)
    }

    override fun getItemCount(): Int = foodList.size

    fun updateData(newList: List<Food>) {
        foodList = newList
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.nameTextView.text = food.Nombre
            binding.priceTextView.text = "Precio: $${String.format("%.2f", food.Precio)}"
            binding.descriptionTextView.text = food.Descripcion

            // Cargar la imagen con Glide
            Glide.with(binding.root.context)
                .load(food.url)  // Cargar la URL de la imagen
                .into(binding.foodImageView)  // El ImageView en el layout

            binding.buyButton.setOnClickListener { onBuyClick(food) }
        }
    }
}
