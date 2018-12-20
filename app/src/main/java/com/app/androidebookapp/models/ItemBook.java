package com.app.androidebookapp.models;

import java.io.Serializable;

public class ItemBook implements Serializable {

    private int id;
    public String book_id;
    public String book_name;
    public String book_image;
    public String author;
    public String type;
    public String pdf_name;
    public String count;

    public ItemBook() {
    }

    public ItemBook(String book_id) {
        this.book_id = book_id;
    }

    public ItemBook(String book_id, String book_name, String book_image, String author, String type, String pdf_name, String count) {
        this.book_id = book_id;
        this.book_name = book_name;
        this.book_image = book_image;
        this.author = author;
        this.type = type;
        this.pdf_name = pdf_name;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_image() {
        return book_image;
    }

    public void setBook_image(String book_image) {
        this.book_image = book_image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPdf_name() {
        return pdf_name;
    }

    public void setPdf_name(String pdf_name) {
        this.pdf_name = pdf_name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
