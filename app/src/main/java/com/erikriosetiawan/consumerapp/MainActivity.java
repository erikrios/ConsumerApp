package com.erikriosetiawan.consumerapp;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.erikriosetiawan.consumerapp.adapter.ConsumerAdapter;
import com.erikriosetiawan.consumerapp.entity.NoteItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.erikriosetiawan.consumerapp.db.DatabaseContract.NoteColumns.CONTENT_URI;
import static com.erikriosetiawan.consumerapp.helper.MappingHelper.mapCursorToArrayList;

public class MainActivity extends AppCompatActivity implements LoadNotesCallback {

    private ConsumerAdapter consumerAdapter;
    private MainActivity.DataObserver myObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Consumer App");
        }

        RecyclerView rvNotes = findViewById(R.id.lv_notes);
        consumerAdapter = new ConsumerAdapter(this);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);
        rvNotes.setAdapter(consumerAdapter);
        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        myObserver = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);
        new getData(this, this).execute();
    }

    @Override
    public void postExecute(Cursor notes) {
        ArrayList<NoteItem> listNotes = mapCursorToArrayList(notes);
        if (listNotes.size() > 0) {
            consumerAdapter.setListNotes(listNotes);
        } else {
            Toast.makeText(this, "Tidak ada data saat ini", Toast.LENGTH_SHORT).show();
            consumerAdapter.setListNotes(new ArrayList<NoteItem>());
        }
    }

    private static class getData extends AsyncTask<Void, Void, Cursor> {

        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadNotesCallback> weakCallback;

        private getData(Context context, LoadNotesCallback callback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return weakContext.get().getContentResolver().query(CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            weakCallback.get().postExecute(cursor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    static class DataObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */

        final Context context;

        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new getData(context, (MainActivity) context).execute();
        }
    }

}
