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
@Parcel(analyze={Resources.class})
public class Resources extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    String file_name;
    @Column
    String mime;
    @Column
    String content;

    public int getId() {
        return id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Resources{" +
                "id=" + id +
                ", file_name='" + file_name + '\'' +
                ", mime='" + mime + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
