package com.solodroid.androidebookappdemo.models;

public class ItemBooks {

    private int BookId;
    private String BookName;
    private String BookAuthorName;
    private String BookImage;
    private String PdfName;

    public int getBookId() {
        return BookId;
    }

    public void setBookId(int categoryid) {
        this.BookId = categoryid;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String categoryname) {
        this.BookName = categoryname;
    }

    public String getBookAuthorName() {
        return BookAuthorName;
    }

    public void setBookAuthorName(String categoryauthorname) {
        this.BookAuthorName = categoryauthorname;
    }

    public String getCategoryImageurl() {
        return BookImage;

    }

    public void setCategoryImageurl(String catimageurl) {
        this.BookImage = catimageurl;
    }

    public String getPdfName() {
        return PdfName;
    }

    public void setPdfName(String pdfName) {
        PdfName = pdfName;
    }
}
