package com.pulse.app;
import android.content.*; import android.database.Cursor; import android.database.sqlite.*; import org.json.*;
public class PulseDb extends SQLiteOpenHelper {
 public PulseDb(Context c){super(c,"pulse.db",null,1);}
 public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE snapshots(id INTEGER PRIMARY KEY AUTOINCREMENT, ts INTEGER NOT NULL, payload TEXT NOT NULL)");db.execSQL("CREATE INDEX idx_snapshots_ts ON snapshots(ts)");}
 public void onUpgrade(SQLiteDatabase db,int o,int n){}
 public void save(JSONObject payload){ContentValues v=new ContentValues();v.put("ts",System.currentTimeMillis());v.put("payload",payload.toString());getWritableDatabase().insert("snapshots",null,v);getWritableDatabase().delete("snapshots","ts < ?",new String[]{String.valueOf(System.currentTimeMillis()-48L*3600_000L)});}
 public JSONArray history(int minutes){JSONArray a=new JSONArray();long since=System.currentTimeMillis()-minutes*60_000L;try(Cursor c=getReadableDatabase().rawQuery("SELECT ts,payload FROM snapshots WHERE ts>=? ORDER BY ts ASC",new String[]{String.valueOf(since)})){while(c.moveToNext()){JSONObject o=new JSONObject(c.getString(1));o.put("snapshotTs",c.getLong(0));a.put(o);}}catch(Exception ignored){}return a;}
 public JSONObject previous(){try(Cursor c=getReadableDatabase().rawQuery("SELECT payload FROM snapshots ORDER BY ts DESC LIMIT 1",null)){if(c.moveToFirst())return new JSONObject(c.getString(0));}catch(Exception ignored){}return new JSONObject();}
}