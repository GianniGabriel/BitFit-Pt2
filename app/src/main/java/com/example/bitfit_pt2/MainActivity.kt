package com.example.bitfit_pt2

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitfit_pt2.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController


class MainActivity : AppCompatActivity() {
    private lateinit var foodListAdapter: FoodListAdapter
    private lateinit var noItemsTextView: TextView

    private lateinit var foodListRecyclerView: RecyclerView
    private lateinit var addFoodButton: Button
    private lateinit var noFoodItemsTextView: TextView
    private lateinit var averageCaloriesTextView: TextView

    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView

    private var foodItemList: MutableList<FoodItem> = mutableListOf()


    companion object {
        private const val ADD_FOOD_ITEM_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        bottomNav = findViewById(R.id.bottom_nav)

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.listFragment, R.id.dashboardFragment
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        foodListRecyclerView = findViewById(R.id.food_list_recycler_view)
        noFoodItemsTextView = findViewById(R.id.no_food_items_text_view)
        addFoodButton = findViewById(R.id.add_button)
        averageCaloriesTextView = findViewById(R.id.average_calories_text_view)

        foodItemList = loadFoodItems()
        foodListAdapter = FoodListAdapter(foodItemList)

        val layoutManager = LinearLayoutManager(this)
        foodListRecyclerView.layoutManager = layoutManager
        foodListRecyclerView.adapter = foodListAdapter

        addFoodButton.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivityForResult(intent, ADD_FOOD_ITEM_REQUEST_CODE)
        }

        updateFoodListVisibility()
        updateAverageCalories()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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

    private fun updateAverageCalories() {
        val totalCalories = foodItemList.sumOf { it.calories }
        val averageCalories = if (foodItemList.isNotEmpty()) {
            totalCalories / foodItemList.size
        } else {
            0
        }
        averageCaloriesTextView.text = "Average Calories: $averageCalories"
    }

    private fun onFoodItemAdded(foodItem: FoodItem) {
        foodItemList.add(foodItem)
        foodListRecyclerView.adapter?.notifyItemInserted(foodItemList.size - 1)
        updateFoodListVisibility()
        updateAverageCalories()

        saveFoodItems()
    }

    private fun startAddFoodActivity() {
        val intent = Intent(this, AddFoodActivity::class.java)
        startActivityForResult(intent, ADD_FOOD_ITEM_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_FOOD_ITEM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val foodItem = data?.getParcelableExtra<FoodItem>(AddFoodActivity.EXTRA_FOOD_ITEM)
            if (foodItem != null) {
                onFoodItemAdded(foodItem)
            }
        }
    }

    private fun loadFoodItems(): MutableList<FoodItem> {
        val sharedPreferences = getSharedPreferences("food_items", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("food_items", null)
        val type = object : TypeToken<MutableList<FoodItem>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun saveFoodItems() {
        val sharedPreferences = getSharedPreferences("food_items", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(foodListAdapter.getFoodItems())
        editor.putString("food_items", json)
        editor.apply()
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
