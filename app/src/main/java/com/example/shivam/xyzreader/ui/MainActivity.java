package com.example.shivam.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shivam.xyzreader.R;
import com.example.shivam.xyzreader.data.XYZProvider;
import com.example.shivam.xyzreader.extras.ArticleLoader;
import com.example.shivam.xyzreader.extras.DynamicHeightImageView;
import com.example.shivam.xyzreader.extras.UpdaterService;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.ASPECT_RATIO;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.AUTHOR;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.PUBLISHED_DATE;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.THUMB_URL;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query.TITLE;
import static com.example.shivam.xyzreader.extras.ArticleLoader.Query._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    public static final String ITEM_POSITION = "itemPosition";

    Context mContext = this;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        if(savedInstanceState == null)
            refresh();

        getLoaderManager().initLoader(0, null, this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        setupStetho();
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.getAllArticlesInstance(mContext);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        RecylerViewAdapter adapter = new RecylerViewAdapter(cursor);
        adapter.setHasStableIds(true);

        recyclerView.setAdapter(adapter);

        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerView.setAdapter(null);
    }

    private void setupStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateRefreshing(RefreshingState rs) {
        swipeRefreshLayout.setRefreshing(rs.state);
    }

    public static class RefreshingState {
        public final boolean state;

        public RefreshingState(boolean s) {
            state = s;
        }
    }

    private class RecylerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private Cursor mCursor;

        public RecylerViewAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(_ID);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            final MyViewHolder vh = new MyViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, XYZProvider.XYZ.withId(vh
                            .getLayoutPosition()));
                    intent.putExtra(ITEM_POSITION, vh.getLayoutPosition());
                    startActivity(intent);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            mCursor.moveToPosition(position);

            holder.titleView.setText(mCursor.getString(TITLE));

            holder.subtitleView.setText(DateUtils.getRelativeTimeSpanString(
                    mCursor.getLong(PUBLISHED_DATE),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString()
                    + " by "
                    + mCursor.getString(AUTHOR));

            Picasso.with(mContext)
                    .load(mCursor.getString(THUMB_URL))
                    .into(holder.thumbnailView);
            holder.thumbnailView.setAspectRatio(mCursor.getFloat(ASPECT_RATIO));
        }

        @Override
        public int getItemCount() {
            if (mCursor == null)
                return 0;
            else
                return mCursor.getCount();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public MyViewHolder(View view) {
            super(view);
            thumbnailView = (DynamicHeightImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
}
