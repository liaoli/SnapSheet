package com.unimelb.feelinglucky.snapsheet.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.transition.ChangeTransform;

import com.unimelb.feelinglucky.snapsheet.Bean.Message;
import com.unimelb.feelinglucky.snapsheet.Bean.User;
import com.unimelb.feelinglucky.snapsheet.Database.FriendChatDbSchema;
import com.unimelb.feelinglucky.snapsheet.Database.FriendDbSchema;
import com.unimelb.feelinglucky.snapsheet.Database.FriendDbSchema.FriendTable;
import com.unimelb.feelinglucky.snapsheet.Database.ImgDbSchema;
import com.unimelb.feelinglucky.snapsheet.Database.SnapSeetDataStore;
import com.unimelb.feelinglucky.snapsheet.Database.SnapSeetDataStore.ChatMessage;
import com.unimelb.feelinglucky.snapsheet.Database.UserDbSchema.UserTable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.unimelb.feelinglucky.snapsheet.SingleInstance.DatabaseInstance.database;

/**
 * Created by leveyleonhardt on 9/9/16.
 */
public class DatabaseUtils {
    public static ContentValues getUserContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.Cols.USERNAME, user.getUsername());
        values.put(UserTable.Cols.BIRTHDAY, user.getBirthday().getTime());
        values.put(UserTable.Cols.MOBILE, user.getMobile());
        values.put(UserTable.Cols.EMAIL, user.getEmail());
        values.put(UserTable.Cols.PASSWORD, user.getPassword());
        values.put(UserTable.Cols.AVATAR, user.getAvatar());
        values.put(UserTable.Cols.DEVICE, user.getDevice_id());
        return values;
    }

    public static ContentValues getFriendContentValues(User friend) {
        ContentValues values = new ContentValues();
        values.put(FriendTable.Cols.USERNAME, friend.getUsername());
        values.put(FriendTable.Cols.MOBILE, friend.getMobile());
        values.put(FriendTable.Cols.EMAIL, friend.getEmail());
        return values;
    }

    public static ContentValues getFriendChatContentValues(String username) {
        ContentValues values = new ContentValues();
        values.put(FriendChatDbSchema.FriendChatTable.Cols.USERNAME, username);
        return values;
    }

    public static ContentValues getFriendChatContentValuesMax(String username) {
        String search = "MAX(" + FriendChatDbSchema.FriendChatTable.Cols.CHAT_PRIORITY + ")";
        Cursor cursor = database.query(FriendChatDbSchema.FriendChatTable.NAME, new String[]{search}, null, null, null, null, null);
        Integer max = 0;
        if (cursor.moveToNext()) {
            // Zero means the index of the column.
            max = cursor.getInt(0);
        }


        ContentValues values = new ContentValues();
        values.put(FriendChatDbSchema.FriendChatTable.Cols.USERNAME, username);
        values.put(FriendChatDbSchema.FriendChatTable.Cols.CHAT_PRIORITY, max + 1);
        if (cursor != null) {
            cursor.close();
        }
        return values;
    }

    public static void refreshUserDb(SQLiteDatabase database, User user) {
        ContentValues values = getUserContentValues(user);
        if (database != null) {
            database.delete(UserTable.NAME, null, null);
            database.insert(UserTable.NAME, null, values);
        }
    }

    public static void refreshFriendDb(SQLiteDatabase database, User[] friends) {
        database.delete(FriendTable.NAME, null, null);
        for (int i = 0; i < friends.length; ++i) {
            ContentValues values = getFriendContentValues(friends[i]);
            database.insert(FriendTable.NAME, null, values);

//            updateFriendChatDb(database, friends[i].getUsername());
        }
    }


    public static void updateFriendChatDb(SQLiteDatabase database, String username) {
        Cursor cursor = database.query(FriendChatDbSchema.FriendChatTable.NAME,
                new String[]{FriendChatDbSchema.FriendChatTable.Cols.USERNAME},
                FriendChatDbSchema.FriendChatTable.Cols.USERNAME + "=?", new String[]{username}, null, null, null);
        if (!cursor.moveToNext()) {
            ContentValues values = getFriendChatContentValues(username);
            database.insert(FriendChatDbSchema.FriendChatTable.NAME, null, values);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void updateFriendChatDb(Context context, SQLiteDatabase database, String username) {
        Cursor cursor = database.query(FriendChatDbSchema.FriendChatTable.NAME,
                new String[]{FriendChatDbSchema.FriendChatTable.Cols.USERNAME},
                FriendChatDbSchema.FriendChatTable.Cols.USERNAME + "=?", new String[]{username}, null, null, null);
        if (!cursor.moveToNext()) {
            ContentValues values = getFriendChatContentValuesMax(username);
            Uri chatFriendListUri = SnapSeetDataStore.ChatFriendList.CONTENT_URI.buildUpon().build();
            context.getContentResolver().insert(
                    chatFriendListUri, values);


            //database.insert(FriendChatDbSchema.FriendChatTable.NAME, null, values);
//            updateChatPriority(database, username);


            //context.getContentResolver().notifyChange(chatFriendListUri, null);

        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void updateChatPriority(SQLiteDatabase database, String userName) {

        String search = "MAX(" + FriendChatDbSchema.FriendChatTable.Cols.CHAT_PRIORITY + ")";
        Cursor cursor = database.query(FriendChatDbSchema.FriendChatTable.NAME, new String[]{search}, null, null, null, null, null);
        Integer max = 0;
        if (cursor.moveToNext()) {
            // Zero means the index of the column.
            max = cursor.getInt(0);
        }

        ContentValues values = new ContentValues();
        values.put(FriendChatDbSchema.FriendChatTable.Cols.CHAT_PRIORITY, max + 1);
        database.update(FriendChatDbSchema.FriendChatTable.NAME, values, FriendChatDbSchema.FriendChatTable.Cols.USERNAME + "=?", new String[]{userName});
        if (cursor != null) {
            cursor.close();
        }
    }

    public static List<String> fetchFriends(SQLiteDatabase database) {
        Cursor cursor = database.query(FriendTable.NAME, new String[]{FriendTable.Cols.USERNAME}, null, null, null, null, null);
        int columnIndex = cursor.getColumnIndex(FriendTable.Cols.USERNAME);
//        String[] friendArray = new String[cursor.getCount()];
        List<String> friendList = new ArrayList<>();
        while (cursor.moveToNext()) {
            friendList.add(cursor.getString(columnIndex));
        }
        if (cursor != null) {
            cursor.close();
        }
        return friendList;
    }

    public static void insertFriendDb(SQLiteDatabase database, User friend) {
        ContentValues value = getFriendContentValues(friend);
        database.insert(FriendTable.NAME, null, value);
    }

    public static String[] loadFriendsWithPriority(SQLiteDatabase database) {
        String orderBy = FriendChatDbSchema.FriendChatTable.Cols.CHAT_PRIORITY + " DESC";
        List<String> userList = new ArrayList<>();

        Cursor cursor = database.query(FriendChatDbSchema.FriendChatTable.NAME, new String[]{FriendChatDbSchema.FriendChatTable.Cols.USERNAME,
                FriendChatDbSchema.FriendChatTable.Cols.CHAT_PRIORITY}, null, null, null, null, orderBy);
        int usernameIndex = cursor.getColumnIndex(FriendChatDbSchema.FriendChatTable.Cols.USERNAME);
//        int priorityIndex = cursor.getColumnIndex(FriendChatDbSchema.FriendChatTable.Cols.CHAT_PRIORITY);
        while (cursor.moveToNext()) {
            userList.add(cursor.getString(usernameIndex));
        }

        String[] result = new String[userList.size()];
        result = userList.toArray(result);
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }



    public static Bitmap getImg(SQLiteDatabase database) {
        Cursor cursor = database.query(ImgDbSchema.ImgTable.NAME,
                new String[]{ImgDbSchema.ImgTable.Cols.IMG}, null, null, null, null, null);
        int columnIndex = cursor.getColumnIndex(ImgDbSchema.ImgTable.Cols.IMG);
        Bitmap mBitmap = null;
        while (cursor.moveToNext()) {
            byte[] image = cursor.getBlob(columnIndex);
            mBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
        if (cursor != null) {
            cursor.close();
        }
        return mBitmap;
    }

    public static byte[] getImgSteam(SQLiteDatabase database) {
        Cursor cursor = database.query(ImgDbSchema.ImgTable.NAME,
                new String[]{ImgDbSchema.ImgTable.Cols.IMG}, null, null, null, null, null);
        int columnIndex = cursor.getColumnIndex(ImgDbSchema.ImgTable.Cols.IMG);
        byte[] image = new byte[0];
        while (cursor.moveToNext()) {
            image = cursor.getBlob(columnIndex);
        }
        if (cursor != null) {
            cursor.close();
        }
        return image;
    }

    public static void storeImg(SQLiteDatabase database, Bitmap mBitmap) {
        if (mBitmap == null) {
            database.delete(ImgDbSchema.ImgTable.NAME, null, null);
        } else {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            ContentValues cv = new ContentValues();
            cv.put(ImgDbSchema.ImgTable.Cols.IMGRTEXT, "temp");
            cv.put(ImgDbSchema.ImgTable.Cols.IMG, os.toByteArray());
            database.insert(ImgDbSchema.ImgTable.NAME, null, cv);
        }

    }

    public static boolean isFriend(SQLiteDatabase database, String username) {
        Cursor cursor = database.query(FriendDbSchema.FriendTable.NAME,
                new String[]{FriendDbSchema.FriendTable.Cols.USERNAME},
                FriendDbSchema.FriendTable.Cols.USERNAME + "=?", new String[]{username}, null, null, null);

        return cursor.moveToNext();
    }

    public static boolean isFriendByMobile(SQLiteDatabase database, String mobile) {

        Cursor cursor = database.query(FriendDbSchema.FriendTable.NAME,
                new String[]{FriendTable.Cols.MOBILE},
                FriendDbSchema.FriendTable.Cols.MOBILE + "=?", new String[]{mobile}, null, null, null);

        boolean hasNext = cursor.moveToNext();
        if (cursor != null) {
            cursor.close();
        }
        return hasNext;
    }


    public static boolean isImgLocked(SQLiteDatabase database) {
        Cursor cursor = database.query(ImgDbSchema.ImgTable.NAME,
                new String[]{ImgDbSchema.ImgTable.Cols.ISLOCKED},
                ImgDbSchema.ImgTable.Cols.ISLOCKED + "=?", new String[]{}, null, null, null);
        int columnIndex = cursor.getColumnIndex(ImgDbSchema.ImgTable.Cols.ISLOCKED);
        boolean result;
        if (cursor.moveToNext()) {
            Integer lock = cursor.getInt(columnIndex);
            if (lock == 1) {
                result = true;
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }


    public static ContentValues buildChatMessage(Map<String, String> data) {
        Message message = new Message();
        message.setFrom(data.get("fromUsername"));
        message.setTo(data.get("toUser"));
        message.setType(data.get("type"));
        message.setContent(data.get("message"));
        message.setLive_time(data.get("live_time"));
        message.setStatus(data.get("status"));
        if (data.containsKey("remoteId")) {
            message.setRemoteId(data.get("remoteId"));
        }

        return buildChatMessage(message);
    }
    public static ContentValues buildChatMessage(Message message) {
        ContentValues values = new ContentValues();
        values.put(ChatMessage.FROM, message.getFrom());
        values.put(ChatMessage.TO, message.getTo());
        values.put(ChatMessage.MESSAGE, message.getContent());
        values.put(ChatMessage.TYPE, message.getType());
        values.put(ChatMessage.STATUS, Integer.parseInt(message.getStatus()));
        values.put(ChatMessage.EXPIRE_TIME, Integer.parseInt(message.getLive_time()));
        values.put(ChatMessage.REMOTE_ID, message.getRemoteId());  // maybe null

        return values;
    }

    public static Message buildMessageFromCursor(Cursor cursor) {
        Message message = new Message();
        message.setLocalId(cursor.getInt(cursor.getColumnIndex(ChatMessage._ID)));
        message.setFrom(cursor.getString(cursor.getColumnIndex(ChatMessage.FROM)));
        message.setType(cursor.getString(cursor.getColumnIndex(ChatMessage.TYPE)));
        message.setContent(cursor.getString(cursor.getColumnIndex(ChatMessage.MESSAGE)));
        message.setTo(cursor.getString(cursor.getColumnIndex(ChatMessage.TO)));
        // status
        message.setStatus(Integer.toString(cursor.getInt(cursor.getColumnIndex(ChatMessage.STATUS))));
        // expiration time
        message.setLive_time(Integer.toString(cursor.getInt(cursor.getColumnIndex(ChatMessage.EXPIRE_TIME))));

        message.setRemoteId(cursor.getString(cursor.getColumnIndex(ChatMessage.REMOTE_ID)));
        return message;
    }
}
