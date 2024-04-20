package com.example.booklibrary;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

public class PdfViewerActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {

    private static final String TAG = PdfViewerActivity.class.getSimpleName();
    private PDFView pdfView;
    private String bookId; // Уникальный идентификатор книги

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfView = findViewById(R.id.pdfView);

        // Получаем URI и bookId из Intent
        String uriString = getIntent().getStringExtra("URI");
        bookId = getIntent().getStringExtra("bookId");
        if (uriString != null && !uriString.isEmpty()) {
            // Открываем PDF с content URI и передаем сохраненную страницу
            openPdf(Uri.parse(uriString), getSavedPage(bookId));
        } else {
            Log.e(TAG, "URI is null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        int currentPage = pdfView.getCurrentPage(); // Получаем номер текущей страницы
        // Сохраняем номер текущей страницы в SharedPreferences с учетом bookId
        getSharedPreferences("PdfViewerPrefs", MODE_PRIVATE)
                .edit()
                .putInt(bookId, currentPage)
                .apply();
    }

    // Метод для открытия PDF-файла с указанием сохраненной страницы
    private void openPdf(Uri uri, int savedPage) {
        pdfView.fromUri(uri)
                .defaultPage(savedPage) // Устанавливаем сохраненный номер страницы
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();
    }

    // Получение сохраненного номера страницы из SharedPreferences с учетом bookId
    private int getSavedPage(String bookId) {
        return getSharedPreferences("PdfViewerPrefs", MODE_PRIVATE)
                .getInt(bookId, 0);
    }

    // Обработчик изменения страницы
    @Override
    public void onPageChanged(int page, int pageCount) {
        Log.d(TAG, "onPageChanged: " + page + "/" + pageCount);
    }

    // Обработчик завершения загрузки
    @Override
    public void loadComplete(int nbPages) {
        Log.d(TAG, "loadComplete: " + nbPages);
    }

    // Обработчик ошибки загрузки страницы
    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page, t);
    }
}
