package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName, editTextCategory, editTextDescription;
    private Button buttonAdd, buttonChooseImage;
    private ImageView imageView;
    private DatabaseHelper databaseHelper;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        editTextName = findViewById(R.id.editTextRecipeName);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAdd = findViewById(R.id.buttonAddRecipe);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
        imageView = findViewById(R.id.imageViewRecipe);

        databaseHelper = new DatabaseHelper(this);

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String category = editTextCategory.getText().toString();
                String description = editTextDescription.getText().toString();

                if (name.isEmpty() || category.isEmpty() || description.isEmpty() || selectedImageBitmap == null) {
                    Toast.makeText(AddRecipeActivity.this, "Please fill all fields and choose an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert image to byte array
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] image = byteArrayOutputStream.toByteArray();

                boolean isInserted = databaseHelper.insertRecipe(name, category, description, image);

                if (isInserted) {
                    Toast.makeText(AddRecipeActivity.this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddRecipeActivity.this, RecipeList.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddRecipeActivity.this, "Failed to add recipe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the image URI from data
            selectedImageBitmap = null;
            try {
                // Convert URI to Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
                selectedImageBitmap = bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
