package com.developer.sagar.prevoice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class PdfReader extends AppCompatActivity {
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);
        pdfView = findViewById(R.id.pdfView);
        Intent intent = getIntent();
        String bookName = intent.getStringExtra("bookName");
        Uri uri = Uri.parse(bookName);
        pdfView.fromUri(uri);
    }
}
