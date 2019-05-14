package id.sentuh.digitalsignage.models;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

/**
 * Created by user on 7/2/17.
 */
//@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, insertConflict = ConflictAction.IGNORE, updateConflict= ConflictAction.REPLACE)
public class AppDatabase {
    public static final String NAME = "DigitalSignage"; // we will add the .db extension

    public static final int VERSION = 3;

}
