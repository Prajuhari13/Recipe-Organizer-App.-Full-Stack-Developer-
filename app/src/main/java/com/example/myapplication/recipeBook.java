package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class recipeBook extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        ImageButton indianButton = findViewById(R.id.Indian);
        ImageButton italianButton = findViewById(R.id.Italian);
        ImageButton chineseButton = findViewById(R.id.Chinese);
        ImageButton allButton = findViewById(R.id.All);
        Button addButton = findViewById(R.id.button12);

        indianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecipesByCategory("Indian");
            }
        });

        italianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecipesByCategory("Italian");
            }
        });

        chineseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecipesByCategory("Chinese");
            }
        });

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecipesByCategory("All");
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(recipeBook.this, AddRecipeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showRecipesByCategory(String category) {
        Cursor cursor;
        if (category.equals("All")) {
            cursor = databaseHelper.getRecipesByCategory("");
        } else {
            cursor = databaseHelper.getRecipesByCategory(category);
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No recipes found for " + category, Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            stringBuilder.append("Name: ").append(cursor.getString(1)).append("\n");
            stringBuilder.append("Description: ").append(cursor.getString(3)).append("\n\n");
        }

        Intent intent = new Intent(recipeBook.this, RecipeList.class);
        intent.putExtra("recipes", stringBuilder.toString());
        startActivity(intent);
    }
}
