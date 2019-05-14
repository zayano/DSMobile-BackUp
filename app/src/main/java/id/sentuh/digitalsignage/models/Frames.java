package id.sentuh.digitalsignage.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by sony on 2/15/2018.
 */
@Table(database = AppDatabase.class)
@Parcel(analyze={Frames.class})
public class Frames extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    String frame_name;
    @Column
    String frame_desc;
    @Column
    int view_id;
    @Column
    int x;
    @Column
    int y;
    @Column
    int width;
    @Column
    int height;

    public int getId() {
        return id;
    }

    public String getFrame_name() {
        return frame_name;
    }

    public void setFrame_name(String frame_name) {
        this.frame_name = frame_name;
    }

    public String getFrame_desc() {
        return frame_desc;
    }

    public void setFrame_desc(String frame_desc) {
        this.frame_desc = frame_desc;
    }

    public int getView_id() {
        return view_id;
    }

    public void setView_id(int view_id) {
        this.view_id = view_id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Frames{" +
                "id=" + id +
                ", frame_name='" + frame_name + '\'' +
                ", frame_desc='" + frame_desc + '\'' +
                ", view_id=" + view_id +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
