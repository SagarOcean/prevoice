package com.developer.sagar.prevoice;

import android.widget.ImageView;

public class Book {
    String pdfImage,pdfName,pdfSize;

    public Book(String pdfImage, String pdfName,String pdfSize) {
        this.pdfImage = pdfImage;
        this.pdfName = pdfName;
        this.pdfSize = pdfSize;
    }

    public String getPdfSize() {
        return pdfSize;
    }

    public void setPdfSize(String pdfSize) {
        this.pdfSize = pdfSize;
    }

    public String getPdfImage() {
        return pdfImage;
    }

    public void setPdfImage(String pdfImage) {
        this.pdfImage = pdfImage;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }
}
