package com.example.architecturecomponents;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = Note.class,version = 1,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;
    public abstract NoteDao noteDao();
    static int coreCount = Runtime.getRuntime().availableProcessors();
    static final ExecutorService executorService = Executors.newFixedThreadPool(coreCount);
    static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.
                    databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            NoteDao noteDao = instance.noteDao();
            executorService.execute(()->{
                noteDao.insert(new Note("Title 1", "Description 1", 1));
                noteDao.insert(new Note("Title 2", "Description 2", 2));
                noteDao.insert(new Note("Title 3", "Description 3", 3));
            });
//            new PopulateDbAsyncTask(instance).execute();
        }
    };

//    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
//        private NoteDao noteDao;
//        private PopulateDbAsyncTask(NoteDatabase db) {
//            noteDao = db.noteDao();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            noteDao.insert(new Note("Title 1", "Description 1", 1));
//            noteDao.insert(new Note("Title 2", "Description 2", 2));
//            noteDao.insert(new Note("Title 3", "Description 3", 3));
//            return null;
//        }
//    }
}
