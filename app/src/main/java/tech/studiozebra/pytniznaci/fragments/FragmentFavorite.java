package tech.studiozebra.pytniznaci.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tech.studiozebra.pytniznaci.Config;
import tech.studiozebra.pytniznaci.R;
import tech.studiozebra.pytniznaci.activities.ActivityDetailStory;
import tech.studiozebra.pytniznaci.activities.ActivityDetailViewPager;
import tech.studiozebra.pytniznaci.adapters.RecyclerAdapterFavorite;
import tech.studiozebra.pytniznaci.utilities.ClickListener;
import tech.studiozebra.pytniznaci.utilities.DatabaseHandler;
import tech.studiozebra.pytniznaci.utilities.JsonConstant;
import tech.studiozebra.pytniznaci.utilities.JsonUtils;
import tech.studiozebra.pytniznaci.models.ItemFavorite;

import java.util.ArrayList;
import java.util.List;

import tech.studiozebra.pytniznaci.activities.ActivityDetailStory;
import tech.studiozebra.pytniznaci.activities.ActivityDetailViewPager;
import tech.studiozebra.pytniznaci.utilities.ClickListener;
import tech.studiozebra.pytniznaci.utilities.DatabaseHandler;
import tech.studiozebra.pytniznaci.utilities.JsonConstant;
import tech.studiozebra.pytniznaci.utilities.JsonUtils;

public class FragmentFavorite extends Fragment {

    RecyclerView recyclerView;
    DatabaseHandler databaseHandler;
    private DatabaseHandler.DatabaseManager databaseManager;
    RecyclerAdapterFavorite adapter;
    TextView textView;
    JsonUtils jsonUtils;
    List<ItemFavorite> listViewAdapter;
    ArrayList<String> book_list, book_list_cat_name;
    ArrayList<String> book_cid, book_cat_id, book_cat_name, book_title, book_image, book_desc, book_subtitle;
    String[] str_book_list, str_book_list_cat_name;
    String[] str_book_cid, str_book_cat_id, str_book_cat_name, str_book_title, str_book_image, str_book_desc, str_book_subtitle;
    int text_length = 0;
    ItemFavorite itemFavorite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(tech.studiozebra.pytniznaci.R.layout.fragment_favorite, container, false);

        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) v.findViewById(tech.studiozebra.pytniznaci.R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        textView = (TextView) v.findViewById(tech.studiozebra.pytniznaci.R.id.textView1);
        databaseHandler = new DatabaseHandler(getActivity());
        databaseManager = DatabaseHandler.DatabaseManager.INSTANCE;
        databaseManager.init(getActivity());
        jsonUtils = new JsonUtils(getActivity());

        listViewAdapter = databaseHandler.getAllData();
        adapter = new RecyclerAdapterFavorite(getActivity(), listViewAdapter);
        recyclerView.setAdapter(adapter);

        if (listViewAdapter.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Config.ENABLE_VIEW_PAGER) {

                            itemFavorite = listViewAdapter.get(position);
                            int pos = Integer.parseInt(itemFavorite.getCatId());

                            Intent intplay = new Intent(getActivity(), ActivityDetailViewPager.class);
                            intplay.putExtra("POSITION", pos);
                            intplay.putExtra("CATEGORY_ITEM_CID", str_book_cid);
                            intplay.putExtra("CATEGORY_ITEM_NAME", str_book_cat_name);
                            intplay.putExtra("CATEGORY_ITEM_CAT_ID", str_book_cat_id);
                            intplay.putExtra("CATEGORY_ITEM_NEWSIMAGE", str_book_image);
                            intplay.putExtra("CATEGORY_ITEM_NEWSHEADING", str_book_title);
                            intplay.putExtra("CATEGORY_ITEM_NEWSDESCRI", str_book_desc);
                            intplay.putExtra("CATEGORY_ITEM_NEWSDATE", str_book_subtitle);

                            startActivity(intplay);

                        } else {

                            itemFavorite = listViewAdapter.get(position);
                            int pos = Integer.parseInt(itemFavorite.getCatId());

                            Intent intplay = new Intent(getActivity(), ActivityDetailStory.class);
                            intplay.putExtra("POSITION", pos);
                            JsonConstant.NEWS_ITEMID = itemFavorite.getCatId();

                            startActivity(intplay);

                        }
                    }
                }, 400);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return v;
    }

    public void onDestroy() {
        // Log.e("OnDestroy", "called");
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Log.e("OnPaused", "called");
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log.e("OnResume", "called");
        // when back key pressed or go one tab to another we update the favorite
        // item so put in resume
        listViewAdapter = databaseHandler.getAllData();
        adapter = new RecyclerAdapterFavorite(getActivity(), listViewAdapter);
        recyclerView.setAdapter(adapter);
        if (listViewAdapter.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }

        book_list = new ArrayList<String>();
        book_list_cat_name = new ArrayList<String>();
        book_cid = new ArrayList<String>();
        book_cat_id = new ArrayList<String>();
        // allListNewsCatImage=new ArrayList<String>();
        book_cat_name = new ArrayList<String>();
        book_title = new ArrayList<String>();
        book_image = new ArrayList<String>();
        book_desc = new ArrayList<String>();
        book_subtitle = new ArrayList<String>();

        str_book_list = new String[book_list.size()];
        str_book_list_cat_name = new String[book_list_cat_name.size()];
        str_book_cid = new String[book_cid.size()];
        str_book_cat_id = new String[book_cat_id.size()];
        // allArrayNewsCatImage=new String[allListNewsCatImage.size()];
        str_book_cat_name = new String[book_cat_name.size()];
        str_book_title = new String[book_title.size()];
        str_book_image = new String[book_image.size()];
        str_book_desc = new String[book_desc.size()];
        str_book_subtitle = new String[book_subtitle.size()];

        for (int j = 0; j < listViewAdapter.size(); j++) {
            ItemFavorite objAllBean = listViewAdapter.get(j);

            book_cat_id.add(objAllBean.getCatId());
            str_book_cat_id = book_cat_id.toArray(str_book_cat_id);

            book_cid.add(String.valueOf(objAllBean.getCId()));
            str_book_cid = book_cid.toArray(str_book_cid);

            book_cat_name.add(objAllBean.getBookName());
            str_book_cat_name = book_cat_name
                    .toArray(str_book_cat_name);

            book_title.add(String.valueOf(objAllBean.getStoryTitle()));
            str_book_title = book_title
                    .toArray(str_book_title);

            book_image.add(String.valueOf(objAllBean.getStoryImage()));
            str_book_image = book_image.toArray(str_book_image);

            book_desc.add(String.valueOf(objAllBean.getStoryDescription()));
            str_book_desc = book_desc.toArray(str_book_desc);

            book_subtitle.add(String.valueOf(objAllBean.getStorySubTitle()));
            str_book_subtitle = book_subtitle.toArray(str_book_subtitle);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(tech.studiozebra.pytniznaci.R.menu.menu_search, menu);

        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(tech.studiozebra.pytniznaci.R.id.search));

        final MenuItem searchMenuItem = menu.findItem(tech.studiozebra.pytniznaci.R.id.search);

        searchView
                .setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        // TODO Auto-generated method stub
                        if (!hasFocus) {
                            searchMenuItem.collapseActionView();
                            searchView.setQuery("", false);
                        }
                    }
                });

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {

                text_length = newText.length();
                listViewAdapter.clear();

                for (int i = 0; i < str_book_title.length; i++) {
                    if (text_length <= str_book_title[i].length()) {
                        if (str_book_title[i].toLowerCase().contains(newText.toLowerCase())) {

                            ItemFavorite objItem = new ItemFavorite();

                            objItem.setCatId(str_book_cat_id[i]);
                            objItem.setCId(str_book_cid[i]);
                            objItem.setBookName(str_book_cat_name[i]);
                            // objItem.setCategoryImage(allArrayNewsCatImage[i]);
                            objItem.setStoryTitle(str_book_title[i]);
                            objItem.setStoryImage(str_book_image[i]);
                            objItem.setStoryDescription(str_book_desc[i]);
                            objItem.setStorySubTitle(str_book_subtitle[i]);

                            listViewAdapter.add(objItem);

                        }
                    }
                }

                adapter = new RecyclerAdapterFavorite(getActivity(), listViewAdapter);
                recyclerView.setAdapter(adapter);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}