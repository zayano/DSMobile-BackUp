package id.sentuh.digitalsignage.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by sony on 2/18/2018.
 */
@Table(database = AppDatabase.class)
@Parcel(analyze={Popups.class})
public class Popups extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    String file_name;
    @Column
    String time;

    public int getId() {
        return id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Popups{" +
                "id=" + id +
                ", file_name='" + file_name + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
