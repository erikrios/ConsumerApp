package com.erikriosetiawan.consumerapp.helper;

import android.database.Cursor;

import com.erikriosetiawan.consumerapp.entity.NoteItem;

import static com.erikriosetiawan.consumerapp.db.DatabaseContract.NoteColumns.*;

import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<NoteItem> mapCursorToArrayList(Cursor notesCursor) {
        ArrayList<NoteItem> noteList = new ArrayList<>();

        while (notesCursor.moveToNext()) {
            int id = notesCursor.getInt(notesCursor.getColumnIndexOrThrow(_ID));
            String title = notesCursor.getString(notesCursor.getColumnIndexOrThrow(TITLE));
            String description = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DESCRIPTION));
            String date = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DATE));
            noteList.add(new NoteItem(id, title, description, date));
        }
        return noteList;
    }
}
