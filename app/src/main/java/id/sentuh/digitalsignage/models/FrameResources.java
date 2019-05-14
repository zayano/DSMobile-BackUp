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
@Parcel(analyze={FrameResources.class})
public class FrameResources extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    int frame_id;
    @Column
    String resource_name;
    @Column
    int waktu;

    public int getId() {
        return id;
    }

    public int getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(int frame_id) {
        this.frame_id = frame_id;
    }

    public String getResource_name() {
        return resource_name;
    }

    public void setResource_name(String resource_name) {
        this.resource_name = resource_name;
    }

    public int getWaktu() {
        return waktu;
    }

    public void setWaktu(int waktu) {
        this.waktu = waktu;
    }

    @Override
    public String toString() {
        return "FrameResources{" +
                "id=" + id +
                ", frame_id=" + frame_id +
                ", resource_name='" + resource_name + '\'' +
                ", waktu=" + waktu +
                '}';
    }
}
