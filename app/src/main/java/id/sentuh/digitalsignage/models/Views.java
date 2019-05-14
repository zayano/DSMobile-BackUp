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
@Parcel(analyze={Views.class})
public class Views extends BaseModel {
    @Column
    @PrimaryKey
    int id;
    @Column
    String page_name;
    @Column
    String background_image;
    @Column
    String background_color;
    @Column
    String background_tile;
    @Column
    int width;
    @Column
    int height;
    @Column
    String show_time;
    @Column
    int delay;
    @Column
    int order;
    public void setId(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }

    public String getBackground_image() {
        return background_image;
    }

    public void setBackground_image(String background_image) {
        this.background_image = background_image;
    }

    public String getBackground_color() {
        return background_color;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }

    public String getBackground_tile() {
        return background_tile;
    }

    public void setBackground_tile(String background_tile) {
        this.background_tile = background_tile;
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

    public String getShowTime() {
        return show_time;
    }

    public void setShowTime(String show_time) {
        this.show_time = show_time;
    }

    public int getDelay() {
        return delay;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
