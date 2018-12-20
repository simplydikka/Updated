package com.solodroid.androidebookappdemo.models;

public class ItemStoryList {

    private String CId;
    private String CategoryName;
    private String CategoryImage;
    private String CatId;
    private String StoryTitle;
    private String StoryDescription;
    private String StoryImage;
    private String StorySubTitle;

    public String getCId() {
        return CId;
    }

    public void setCId(String cid) {
        this.CId = cid;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryname) {
        this.CategoryName = categoryname;
    }

    public String getCategoryImage() {
        return CategoryImage;
    }

    public void setCategoryImage(String categoryimage) {
        this.CategoryImage = categoryimage;
    }


    public String getCatId() {
        return CatId;
    }

    public void setCatId(String catid) {
        this.CatId = catid;
    }

    public String getStoryTitle() {
        return StoryTitle;
    }

    public void setStoryTitle(String newsheading) {
        this.StoryTitle = newsheading;
    }

    public String getStoryDescription() {
        return StoryDescription;
    }

    public void setStoryDescription(String newsdescription) {
        this.StoryDescription = newsdescription;
    }

    public String getStoryImage() {
        return StoryImage;
    }

    public void setStoryImage(String newsimage) {
        this.StoryImage = newsimage;
    }

    public String getStorySubTitle() {
        return StorySubTitle;
    }

    public void setStorySubTitle(String newsdate) {
        this.StorySubTitle = newsdate;
    }

}
