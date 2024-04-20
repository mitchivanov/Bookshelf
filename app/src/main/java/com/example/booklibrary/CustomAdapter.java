package com.example.booklibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> book_id, book_title, book_author, book_path;
    Animation translate_anim;

    CustomAdapter(Context context, ArrayList<String> book_id, ArrayList<String> book_title, ArrayList<String> book_author,
                  ArrayList<String> book_path){
        this.context = context;
        this.book_id = book_id;
        this.book_title = book_title;
        this.book_author = book_author;
        this.book_path = book_path;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание нового ViewHolder при необходимости
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // Привязка данных к ViewHolder в данной позиции
        holder.book_id_txt.setText(book_id.get(position));
        holder.book_title_txt.setText(book_title.get(position));
        holder.book_author_txt.setText(book_author.get(position));
        holder.book_path_txt.setText(book_path.get(position));

        // Обработчик нажатия на элемент RecyclerView
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Получение URI для открытия PDF-файла
                String uriString = book_path.get(position);
                String bookId = book_id.get(position); // Получаем уникальный идентификатор книги
                Uri uri = Uri.parse(uriString);
                if (uri != null) {
                    // Открытие PDF-файла с помощью PdfViewerActivity с передачей URI и bookId
                    Intent intent = new Intent(context, PdfViewerActivity.class);
                    intent.putExtra("URI", uriString);
                    intent.putExtra("bookId", bookId); // передача bookId
                    context.startActivity(intent);
                } else {
                    // Вывод сообщения об ошибке, если URI пуст
                    Toast.makeText(context, "URI is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // Возвращает общее количество элементов в данных
        return book_id.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView book_id_txt, book_title_txt, book_author_txt, book_path_txt;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Инициализация элементов внутри ViewHolder
            book_id_txt = itemView.findViewById(R.id.book_id_txt);
            book_title_txt = itemView.findViewById(R.id.book_title_txt);
            book_author_txt = itemView.findViewById(R.id.book_author_txt);
            book_path_txt = itemView.findViewById(R.id.book_path_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            // Анимация для элемента RecyclerView
            Animation translate_anim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }
    }
}
