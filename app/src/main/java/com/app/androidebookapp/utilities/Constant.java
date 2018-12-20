package com.app.androidebookapp.utilities;

import com.app.androidebookapp.models.ItemStory;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    public static String CATEGORY_TITLE;
    public static String CATEGORY_ID;

    public static final String JSON_ARRAY_NAME = "AndroidEbookApp";

    public static final String BOOK_ID = "book_id";
    public static final String BOOK_NAME = "book_name";
    public static final String BOOK_IMAGE = "book_image";
    public static final String AUTHOR = "author";
    public static final String TYPE = "type";
    public static final String PDF_NAME = "pdf_name";
    public static final String COUNT = "count";

    public static final String STORY_ID = "story_id";
    public static final String STORY_TITLE = "story_title";
    public static final String STORY_SUBTITLE = "story_subtitle";
    public static final String STORY_DESCRIPTION = "story_description";

    public static final int DELAY_LOAD_MORE = 1500;
    public static final int DELAY_PROGRESS = 4000;
    public static final int DELAY_REFRESH_MEDIUM = 1000;
    public static final int DELAY_REFRESH_SHORT = 500;

    public static ArrayList<ItemStory> arrayList = new ArrayList<ItemStory>();
    public static String search_item = "";

}
