package com.example.bitfit_pt2
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.min

class DashboardFragment : Fragment() {

    private lateinit var averageCaloriesTextView: TextView
    private lateinit var minimumCaloriesTextView: TextView
    private lateinit var maximumCaloriesTextView: TextView

    private fun loadFoodItems(): MutableList<FoodItem> {
        val sharedPreferences = requireContext().getSharedPreferences("food_items", AppCompatActivity.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("food_items", null)
        val type = object : TypeToken<MutableList<FoodItem>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        averageCaloriesTextView = view.findViewById(R.id.average_calories_text_view)
        minimumCaloriesTextView = view.findViewById(R.id.minimum_calories_text_view)
        maximumCaloriesTextView = view.findViewById(R.id.maximum_calories_text_view)

        val foodItemList = loadFoodItems()

        if (foodItemList.isEmpty()) {
            averageCaloriesTextView.text = getString(R.string.no_food_items_added)
        } else {
            val totalCalories = foodItemList.sumBy { it.calories }
            val averageCalories = totalCalories / foodItemList.size
            averageCaloriesTextView.text = getString(R.string.average_calories, averageCalories)

            val minimumCaloriesFood = foodItemList.minByOrNull { it.calories }
            minimumCaloriesTextView.text = getString(R.string.minimum_calories, minimumCaloriesFood?.name, minimumCaloriesFood?.calories)
            val maximumCaloriesFood = foodItemList.maxByOrNull { it.calories }

            maximumCaloriesTextView.text = getString(R.string.maximum_calories, maximumCaloriesFood?.name, maximumCaloriesFood?.calories)
        }

        return view
    }
}
