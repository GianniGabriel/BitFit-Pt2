package com.example.bitfit_pt2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.bitfit_pt2.FoodItem
import com.example.bitfit_pt2.R

class AddFoodActivity : AppCompatActivity() {
    private lateinit var foodNameEditText: EditText
    private lateinit var foodCaloriesEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        foodNameEditText = findViewById(R.id.food_name_edit_text)
        foodCaloriesEditText = findViewById(R.id.food_calories_edit_text)

        val submitButton: Button = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            val name = foodNameEditText.text.toString().trim()
            val calories = foodCaloriesEditText.text.toString().trim().toIntOrNull()

            if (name.isNotEmpty() && calories != null && calories > 0) {
                val foodItem = FoodItem(name, calories)
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_FOOD_ITEM, foodItem)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                foodNameEditText.error = "Please enter a valid food name"
                foodCaloriesEditText.error = "Please enter a valid number of calories"
            }
        }
    }

    companion object {
        const val EXTRA_FOOD_ITEM = "extra_food_item"
    }
}
