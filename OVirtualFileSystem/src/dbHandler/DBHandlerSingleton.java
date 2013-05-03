package dbHandler;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DBHandlerSingleton {

    private OGraphDatabase currentDB;
    private DBHandlerSingleton dbHandlerSingleton;

    private DBHandlerSingleton(String dbURL) {
        currentDB = new OGraphDatabase(dbURL);
    }

    public DBHandlerSingleton getDBHandler(String dbUrl) {
        if (dbHandlerSingleton == null)
            dbHandlerSingleton = new DBHandlerSingleton(dbUrl);
        return dbHandlerSingleton;
    }

    public ODocument getRecordByOID(OIdentifiable oid) {
        return (ODocument) oid.getRecord();
    }

}
