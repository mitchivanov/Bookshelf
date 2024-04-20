package com.example.booklibrary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    EditText title_input, author_input, path_input;
    Button update_button, delete_button;

    String id, title, author, path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Инициализация элементов пользовательского интерфейса
        title_input = findViewById(R.id.title_input2);
        author_input = findViewById(R.id.author_input2);
        delete_button = findViewById(R.id.delete_button);

        // Получение данных из Intent и установка их в поля ввода
        getAndSetIntentData();

        // Установка заголовка ActionBar с названием книги
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }

        // Обработчик нажатия кнопки обновления
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Обновление данных в базе данных
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                title = title_input.getText().toString().trim();
                author = author_input.getText().toString().trim();
                path = path_input.getText().toString().trim();
                myDB.updateData(id, title, author, path);
            }
        });

        // Обработчик нажатия кнопки удаления
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Подтверждение удаления записи
                confirmDialog();
            }
        });
    }

    // Получение данных из Intent и установка их в соответствующие поля
    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("title") &&
                getIntent().hasExtra("author") && getIntent().hasExtra("path")){
            // Получение данных из Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            path = getIntent().getStringExtra("path");

            // Установка данных в поля ввода
            title_input.setText(title);
            author_input.setText(author);
            path_input.setText(path);
            Log.d("stev", title+" "+author+" "+path);
        }else{
            Toast.makeText(this, "Нет данных.", Toast.LENGTH_SHORT).show();
        }
    }

    // Диалоговое окно подтверждения удаления записи
    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить " + title + " ?");
        builder.setMessage("Вы уверены, что хотите удалить " + title + " ?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Удаление записи из базы данных
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Ничего не делать при отмене удаления
            }
        });
        builder.create().show();
    }
}
