package id.sentuh.digitalsignage.helper;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import id.sentuh.digitalsignage.models.AppDatabase;
import id.sentuh.digitalsignage.models.FrameResources;
import id.sentuh.digitalsignage.models.FrameResources_Table;
import id.sentuh.digitalsignage.models.Frames;
import id.sentuh.digitalsignage.models.Frames_Table;
import id.sentuh.digitalsignage.models.Prayer;
import id.sentuh.digitalsignage.models.Prayer_Table;
import id.sentuh.digitalsignage.models.Resources;
import id.sentuh.digitalsignage.models.Resources_Table;
import id.sentuh.digitalsignage.models.Views;
import id.sentuh.digitalsignage.models.Views_Table;

/**
 * Created by sony on 2/15/2018.
 */

public abstract class DBHelper {
    public static boolean isViewExist(int id){
        Views views=SQLite.select().from(Views.class).where(Views_Table.id.eq(id)).querySingle();
        return views!=null;
    }
    public static void insertViews(int id,String name,String bg_image,String bg_color,String time,int delay,int order){

        try {
            Views views=new Views();//SQLite.select().from(Views.class).where(Views_Table.id.eq(id)).querySingle();
//            if(views==null){
//                views = new Views();
//
//            }
            views.setId(id);
            views.setPage_name(name);
            views.setBackground_image(bg_image);
            views.setBackground_color(bg_color);
            views.setShowTime(time);
            views.setDelay(delay);
            views.setOrder(order);
            views.save();
        } catch (Exception ex){
            Log.e("DBHelper",ex.getMessage());
        }


    }
    public static Views getViewRow(String name){
        Views views=SQLite.select().from(Views.class).where(Views_Table.page_name.eq(name)).querySingle();
        return views;
    }
    public static Views getViewById(int id){
        Views views=SQLite.select().from(Views.class).where(Views_Table.id.eq(id)).querySingle();
        return views;
    }
    public static boolean isFrameExist(String name,int view_id){
        Frames frame=SQLite.select()
                .from(Frames.class)
                .where(Frames_Table.frame_name.eq(name))
                .and(Frames_Table.view_id.eq(view_id))
                .querySingle();
        return frame!=null;
    }
    public static int insertFrame(int viewid,String name,String description,int x,int y,int width,int height){
        Frames frame=SQLite.select()
                .from(Frames.class)
                .where(Frames_Table.frame_name.eq(name))
                .and(Frames_Table.view_id.eq(viewid))
                .querySingle();
        if(frame==null){
            frame = new Frames();
            frame.setView_id(viewid);
            frame.setFrame_name(name);
        }
        frame.setFrame_desc(description);
        frame.setX(x);
        frame.setY(y);
        frame.setWidth(width);
        frame.setHeight(height);
        frame.save();

        return frame.getId();
    }
    public static List<Views> getViews(){
        return SQLite.select().from(Views.class)
                .orderBy(Views_Table.order,true)
                .queryList();
    }
    public static List<Frames> getFrames(int viewid){
        return SQLite.select()
                .from(Frames.class)
                .where(Frames_Table.view_id.eq(viewid))
                .queryList();
    }
    public static List<Frames> getframeId(int id){
        return SQLite.select()
                .from(Frames.class)
                .where(Frames_Table.id.eq(id))
                .queryList();
    }
    public static List<Frames> getframeName(String name){
        return SQLite.select()
                .from(Frames.class)
                .where(Frames_Table.frame_name.eq(name))
                .queryList();
    }
    public static List<Frames> getAllFrames(){
        return SQLite.select()
                .from(Frames.class)
                .queryList();
    }
    public static List<FrameResources> getFrameResource(int frameid){
        return SQLite.select()
                .from(FrameResources.class)
                .where(FrameResources_Table.frame_id.eq(frameid))
                .queryList();
    }
    public static void insertPrayer(String label,String time){
        Prayer current = SQLite.select().from(Prayer.class)
                .where(Prayer_Table.label_name.eq(label)).querySingle();
        if(current==null){
            current = new Prayer();
        }
        current.setLabel_name(label);
        current.setStr_time(time);
        current.save();
    }
    public static Frames getFrameDetail(String name,int viewid){
        return SQLite.select()
                .from(Frames.class)
                .where(Frames_Table.view_id.eq(viewid)).and(Frames_Table.frame_name.eq(name))
                .querySingle();
    }
    public static boolean isResourceExist(String name){
        Resources resources = SQLite.select().from(Resources.class).where(Resources_Table.file_name.eq(name)).querySingle();
        return resources!=null;
    }
    public static Resources getResourceFromName(String name){
        return SQLite.select()
                .from(Resources.class)
                .where(Resources_Table.file_name.eq(name))
                .querySingle();
    }
    public static void clearDB(Context context){
        FlowManager.getDatabase(AppDatabase.class).reset(context);
    }
}
