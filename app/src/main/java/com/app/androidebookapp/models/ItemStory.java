package com.app.androidebookapp.models;

public class ItemStory {

    private String story_id;
    private String story_title;
    private String story_subtitle;
    private String story_description;
    private String book_id;

    public ItemStory(String story_id, String story_title, String story_subtitle, String story_description, String book_id) {
        this.story_id = story_id;
        this.story_title = story_title;
        this.story_subtitle = story_subtitle;
        this.story_description = story_description;
        this.book_id = book_id;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String getStory_title() {
        return story_title;
    }

    public void setStory_title(String story_title) {
        this.story_title = story_title;
    }

    public String getStory_subtitle() {
        return story_subtitle;
    }

    public void setStory_subtitle(String story_subtitle) {
        this.story_subtitle = story_subtitle;
    }

    public String getStory_description() {
        return story_description;
    }

    public void setStory_description(String story_description) {
        this.story_description = story_description;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

}
