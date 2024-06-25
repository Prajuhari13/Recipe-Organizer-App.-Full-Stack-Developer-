package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeList extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView listViewRecipes;
    private SearchView searchView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> recipeList;
    private ArrayList<Integer> recipeIds;
    private String currentCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        databaseHelper = new DatabaseHelper(this);
        listViewRecipes = findViewById(R.id.listViewRecipes);
        searchView = findViewById(R.id.searchView);
        recipeList = new ArrayList<>();
        recipeIds = new ArrayList<>();

        if (getIntent().hasExtra("category")) {
            currentCategory = getIntent().getStringExtra("category");
        }

        loadRecipes(currentCategory, "");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadRecipes(currentCategory, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadRecipes(currentCategory, newText);
                return false;
            }
        });

        listViewRecipes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int recipeId = recipeIds.get(position);
                new AlertDialog.Builder(RecipeList.this)
                        .setTitle("Delete Recipe")
                        .setMessage("Are you sure you want to delete this recipe?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                databaseHelper.deleteRecipe(recipeId);
                                loadRecipes(currentCategory, "");
                                Toast.makeText(RecipeList.this, "Recipe deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }

    private void loadRecipes(String category, String query) {
        recipeList.clear();
        recipeIds.clear();

        Cursor cursor;
        if (TextUtils.isEmpty(query)) {
            cursor = databaseHelper.getRecipesByCategory(category);
        } else {
            cursor = databaseHelper.getRecipesByCategoryAndQuery(category, query);
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No recipes found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(3);
            recipeIds.add(id);
            recipeList.add("Name: " + name + "\nDescription: " + description);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        listViewRecipes.setAdapter(adapter);
    }
}
