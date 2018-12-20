package com.solodroid.androidebookappdemo.models;

public class ItemFavorite {

    private int id;
    private String CatId;
    private String CId;
    private String BookName;
    private String StoryTitle;
    private String StoryImage;
    private String StoryDescription;
    private String StorySubTitle;

    public ItemFavorite() {

    }

    public ItemFavorite(String CatId) {
        this.CatId = CatId;
    }

    public ItemFavorite(String catid, String cid, String categoryname, String newsheading, String newsimage, String newsdesc, String newsdate) {
        this.CatId = catid;
        this.CId = cid;
        this.BookName = categoryname;
        this.StoryTitle = newsheading;
        this.StoryImage = newsimage;
        this.StoryDescription = newsdesc;
        this.StorySubTitle = newsdate;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCatId() {
        return CatId;
    }

    public void setCatId(String catid) {
        this.CatId = catid;
    }

    public String getCId() {
        return CId;
    }

    public void setCId(String cid) {
        this.CId = cid;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String categoryname) {
        this.BookName = categoryname;
    }

    public String getStoryTitle() {
        return StoryTitle;
    }

    public void setStoryTitle(String newsheading) {
        this.StoryTitle = newsheading;
    }

    public String getStoryImage() {
        return StoryImage;
    }

    public void setStoryImage(String newsimage) {
        this.StoryImage = newsimage;
    }

    public String getStoryDescription() {
        return StoryDescription;
    }

    public void setStoryDescription(String newsdesc) {
        this.StoryDescription = newsdesc;
    }

    public String getStorySubTitle() {
        return StorySubTitle;
    }

    public void setStorySubTitle(String newsdate) {
        this.StorySubTitle = newsdate;
    }

}
