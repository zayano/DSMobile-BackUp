package id.sentuh.digitalsignage.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

@Table(database = AppDatabase.class)
@Parcel(analyze={Prayer.class})
public class Prayer extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    String label_name;
    @Index
    @Column
    String str_time;

    public int getId() {
        return id;
    }


    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public String getStr_time() {
        return str_time;
    }

    public void setStr_time(String str_time) {
        this.str_time = str_time;
    }

    @Override
    public String toString() {
        return "Prayer{" +
                "id=" + id +
                ", label_name='" + label_name + '\'' +
                ", str_time='" + str_time + '\'' +
                '}';
    }
}
