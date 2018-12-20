package com.app.androidebookapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.androidebookapp.Config;
import com.app.androidebookapp.R;
import com.app.androidebookapp.adapters.AdapterSearch;
import com.app.androidebookapp.models.ItemBook;
import com.app.androidebookapp.utilities.Constant;
import com.app.androidebookapp.utilities.DbHandler;
import com.app.androidebookapp.utilities.GDPR;
import com.app.androidebookapp.utilities.ItemOffsetDecoration;
import com.app.androidebookapp.utilities.Utils;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivitySearchBook extends AppCompatActivity {

    private EditText et_search;
    private ImageButton bt_clear;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    AdapterSearch adapterBook;
    ArrayList<ItemBook> itemBooks;
    ItemBook itemBook;
    View lyt_no_item;
    ProgressBar progressBar;
    private DbHandler dbHandler;
    private CharSequence charSequence = null;
    private View view;
    Snackbar snackbar;
    private AdView adView;
    private InterstitialAd interstitialAd;
    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        view = findViewById(android.R.id.content);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        setupToolbar();

        initMobileAds();
        loadBannerAd();
        loadInterstitialAd();

        et_search = findViewById(R.id.et_search);
        bt_clear = findViewById(R.id.bt_clear);
        bt_clear.setVisibility(View.GONE);

        lyt_no_item = findViewById(R.id.lyt_no_item);
        progressBar = findViewById(R.id.progressBar);

        itemBooks = new ArrayList<ItemBook>();

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), Config.SPAN_COUNT);

        et_search.addTextChangedListener(textWatcher);

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.book_space);
        recyclerView.addItemDecoration(itemDecoration);

    }

    public void searchAction() {
        final String query = et_search.getText().toString().trim();
        if (Utils.isNetworkAvailable(this)) {
            if (!query.equals("")) {
                resetAdapter();
                lyt_no_item.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new MyTask().execute(Config.ADMIN_PANEL_URL + "/api/api.php?search_book=" + query);
                    }
                }, 2000);
            } else {
                snackbar = Snackbar.make(view, getResources().getString(R.string.msg_search_input), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "failed to connect network!", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (itemBooks.size() == 0) {
                progressBar.setVisibility(View.VISIBLE);
            }
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return Utils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (itemBooks.size() == 0) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            recyclerView.setVisibility(View.VISIBLE);


            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext(), "Something Went Wrong! Please check your internet and reload", Toast.LENGTH_SHORT).show();
            }

            try {
                JSONArray jsonArray = new JSONObject(result).getJSONArray(Constant.JSON_ARRAY_NAME);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objJson = jsonArray.getJSONObject(i);
                    String book_id = objJson.getString(Constant.BOOK_ID);
                    String book_name = objJson.getString(Constant.BOOK_NAME);
                    String book_image = objJson.getString(Constant.BOOK_IMAGE);
                    String author = objJson.getString(Constant.AUTHOR);
                    String type = objJson.getString(Constant.TYPE);
                    String pdf_name = objJson.getString(Constant.PDF_NAME);
                    String count = objJson.getString(Constant.COUNT);
                    ItemBook itemCategory = new ItemBook(book_id, book_name, book_image, author, type, pdf_name, count);
                    itemBooks.add(itemCategory);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //isOver = true;
            }
            setAdapterToRecyclerView();
            onItemClickListener();
            onItemOverflowClickListener();

        }
    }

    public void setAdapterToRecyclerView() {
        if (itemBooks.size() < Config.LOAD_MORE + 1) {
            adapterBook = new AdapterSearch(this, itemBooks);
            recyclerView.setAdapter(adapterBook);
        }
        adapterBook.notifyDataSetChanged();
        setEmptyText();
    }

    public void resetAdapter() {
        recyclerView.getRecycledViewPool().clear();
        itemBooks.clear();
    }

    private void setEmptyText() {
        if (adapterBook.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    public void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            if (c.toString().trim().length() == 0) {
                bt_clear.setVisibility(View.GONE);
            } else {
                bt_clear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onItemClickListener() {
        adapterBook.setOnItemClickListener(new AdapterSearch.OnItemClickListener() {
            @Override
            public void onItemClick(View v, ItemBook obj, int position) {
                itemBook = itemBooks.get(position);

                if (itemBook.getType().equals("pdf_upload")) {
                    Intent intent = new Intent(getApplicationContext(), ActivityPdfView.class);
                    intent.putExtra("POSITION", position);
                    intent.putExtra("TITLE", itemBook.getBook_name());
                    intent.putExtra("URL", Config.ADMIN_PANEL_URL + "/upload/pdf/" + itemBook.getPdf_name());
                    startActivity(intent);
                } else if (itemBook.getType().equals("pdf_url")) {
                    Intent intent = new Intent(getApplicationContext(), ActivityPdfView.class);
                    intent.putExtra("POSITION", position);
                    intent.putExtra("TITLE", itemBook.getBook_name());
                    intent.putExtra("URL", itemBook.getPdf_name());
                    startActivity(intent);
                } else {
                    Constant.CATEGORY_ID = itemBook.getBook_id();
                    Constant.CATEGORY_TITLE = itemBook.getBook_name();
                    Intent intent = new Intent(getApplicationContext(), ActivityStory.class);
                    startActivity(intent);
                }
                showInterstitialAd();
            }
        });
    }

    public void onItemOverflowClickListener() {
        adapterBook.OnItemOverflowClickListener(new AdapterSearch.OnItemOverflowClickListener() {
            @Override
            public void onItemClick(View v, ItemBook obj, int position) {
                itemBook = itemBooks.get(position);

                PopupMenu popup = new PopupMenu(ActivitySearchBook.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_context_favorite:
                                if (charSequence.equals(getString(R.string.option_set_favorite))) {
                                    dbHandler.AddtoFavorite(new ItemBook(
                                            itemBook.getBook_id(),
                                            itemBook.getBook_name(),
                                            itemBook.getBook_image(),
                                            itemBook.getAuthor(),
                                            itemBook.getType(),
                                            itemBook.getPdf_name(),
                                            itemBook.getCount()
                                    ));
                                    //Toast.makeText(getActivity(), getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content), itemBook.getBook_name() + " " + getString(R.string.favorite_added), Snackbar.LENGTH_SHORT).show();

                                } else if (charSequence.equals(getString(R.string.option_unset_favorite))) {
                                    dbHandler.RemoveFav(new ItemBook(itemBook.getBook_id()));
                                    //Toast.makeText(getActivity(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content), itemBook.getBook_name() + " " + getString(R.string.favorite_removed), Snackbar.LENGTH_SHORT).show();

                                }
                                return true;

                            case R.id.menu_context_share:

                                String share_title = android.text.Html.fromHtml(itemBook.getBook_name()).toString();
                                String share_content = android.text.Html.fromHtml(getResources().getString(R.string.share_content)).toString();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" + share_content + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                return true;

                            default:
                        }
                        return false;
                    }
                });
                popup.show();

                dbHandler = new DbHandler(ActivitySearchBook.this);
                List<ItemBook> data = dbHandler.getFavRow(itemBook.getBook_id());
                if (data.size() == 0) {
                    popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.option_set_favorite);
                    charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
                } else {
                    if (data.get(0).getBook_id().equals(itemBook.getBook_id())) {
                        popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.option_unset_favorite);
                        charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (et_search.length() > 0) {
            et_search.setText("");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                if (et_search.length() > 0) {
                    et_search.setText("");
                } else {
                    super.onBackPressed();
                }
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public void initMobileAds() {
        if (Config.ENABLE_ADMOB_BANNER_ADS || Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            MobileAds.initialize(ActivitySearchBook.this, getResources().getString(R.string.admob_app_id));
        }
    }

    public void loadBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivitySearchBook.this)).build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            interstitialAd = new InterstitialAd(ActivitySearchBook.this);
            interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_unit_id));
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivitySearchBook.this)).build();
            interstitialAd.loadAd(adRequest);

            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        }
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                if (counter == Config.ADMOB_INTERSTITIAL_ADS_INTERVAL) {
                    interstitialAd.show();
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }
    }

}
