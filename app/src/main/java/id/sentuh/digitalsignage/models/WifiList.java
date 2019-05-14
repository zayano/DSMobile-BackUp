package id.sentuh.digitalsignage.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by sony on 3/23/2018.
 */
@Table(database = AppDatabase.class)
@Parcel(analyze={WifiList.class})
public class WifiList extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    @Unique(unique = false, uniqueGroups = 1)
    public String ssid_name;

    public int getId() {
        return id;
    }
}
