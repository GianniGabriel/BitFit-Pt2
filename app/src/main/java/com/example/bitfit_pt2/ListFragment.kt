package com.example.bitfit_pt2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListFragment : Fragment() {

    private lateinit var foodListRecyclerView: RecyclerView
    private lateinit var noFoodItemsTextView: TextView
    private lateinit var addFoodItemButton: Button
    private lateinit var averageCaloriesTextView: TextView
    private lateinit var foodItemList: MutableList<FoodItem>
    private lateinit var foodListAdapter: FoodListAdapter

    private fun updateAverageCalories() {
        val totalCalories = foodItemList.sumOf { it.calories }
        val averageCalories = if (foodItemList.isNotEmpty()) {
            totalCalories / foodItemList.size
        } else {
            0
        }
        averageCaloriesTextView.text = "Average Calories: $averageCalories"
    }

    private fun updateFoodListVisibility() {
        if (foodItemList.isEmpty()) {
            noFoodItemsTextView.visibility = View.VISIBLE
            foodListRecyclerView.visibility = View.GONE
        } else {
            noFoodItemsTextView.visibility = View.GONE
            foodListRecyclerView.visibility = View.VISIBLE
        }
    }

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
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        foodListRecyclerView = view.findViewById(R.id.food_list_recycler_view)
        noFoodItemsTextView = view.findViewById(R.id.no_food_items_text_view)
        addFoodItemButton = view.findViewById(R.id.add_button)
        averageCaloriesTextView = view.findViewById(R.id.average_calories_text_view)

        foodItemList = loadFoodItems()
        foodListAdapter = FoodListAdapter(foodItemList)

        val layoutManager = LinearLayoutManager(activity)
        foodListRecyclerView.layoutManager = layoutManager
        foodListRecyclerView.adapter = foodListAdapter

        addFoodItemButton.setOnClickListener {
            val intent = Intent(activity, AddFoodActivity::class.java)
            startActivityForResult(intent, 200)
        }

        updateFoodListVisibility()
        updateAverageCalories()

        return view
    }

    inner class FoodListAdapter(private val foodItems: MutableList<FoodItem>) :
        RecyclerView.Adapter<FoodListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.food_item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val foodItem = foodItems[position]
            holder.foodNameTextView.text = foodItem.name
            holder.foodCaloriesTextView.text = foodItem.calories.toString()
        }

        override fun getItemCount(): Int {
            return foodItems.size
        }

        fun addFoodItem(foodItem: FoodItem) {
            foodItems.add(foodItem)
            notifyItemInserted(foodItems.size - 1)
        }

        fun getFoodItems(): MutableList<FoodItem> {
            return foodItems
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val foodNameTextView: TextView = view.findViewById(R.id.food_name_text_view)
            val foodCaloriesTextView: TextView = view.findViewById(R.id.food_calories_text_view)
        }
    }
}
