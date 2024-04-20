package com.example.booklibrary;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 123;
    private final static int PERMISSION_CODE = 42042;

    // Поля для ввода названия книги, автора и кнопок
    EditText title_input, author_input;
    Button add_button, select_file_button;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Находим элементы пользовательского интерфейса
        title_input = findViewById(R.id.title_input);
        author_input = findViewById(R.id.author_input);
        select_file_button = findViewById(R.id.choose_file_button);

        // Обработчик нажатия на кнопку выбора файла
        select_file_button.setOnClickListener(view -> {
            // Проверяем разрешение на чтение внешнего хранилища
            if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Запрашиваем разрешение у пользователя, если оно не предоставлено
                ActivityCompat.requestPermissions(AddActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_CODE);
            } else {
                // Разрешение уже предоставлено, открываем выбор файла
                openFilePicker();
            }
        });

        // Проверяем необходимость запроса разрешения для доступа к внешнему хранилищу (только для Android 11 и выше)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
            }
        }
    }

    // Метод для открытия диалога выбора файла
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        try {
            openDocumentLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            // Обработка исключения, если не найдено приложение для открытия документов
            Toast.makeText(this, "Ошибка выбора файла", Toast.LENGTH_SHORT).show();
        }
    }

    // Обработка результата запроса разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, открываем выбор файла
                openFilePicker();
            } else {
                // Разрешение отклонено, информируем пользователя
                Toast.makeText(this, "Отказано в доступе к чтению внешнего хранилища.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Обработка результата выбора файла
    @SuppressLint("WrongConstant")
    ActivityResultLauncher<Intent> openDocumentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            final int takeFlags = data.getFlags()
                                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            getContentResolver().takePersistableUriPermission(uri, takeFlags);

                            String title = title_input.getText().toString().trim();
                            String author = author_input.getText().toString().trim();
                            if (!title.isEmpty() && !author.isEmpty()) {
                                MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                                myDB.addBook(title, author, uri.toString());
                            } else {
                                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

    // Обработка результата запроса управления внешним хранилищем (только для Android 11 и выше)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MANAGE_EXTERNAL_STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Разрешение предоставлено, пробуем снова открыть файл
                openFilePicker();
            } else {
                // Разрешение отклонено, информируем пользователя
                Toast.makeText(this, "Отказано в доступе к управлению внешним хранилищем.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}