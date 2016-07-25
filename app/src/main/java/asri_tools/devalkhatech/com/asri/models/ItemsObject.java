package asri_tools.devalkhatech.com.asri.models;

/**
 * Created by Array-PC on 27-Apr-16.
 */
public class ItemsObject {

    private  String databaseId;
    private String databaseValue;

    public ItemsObject ( String databaseId , String databaseValue ) {
        this.databaseId = databaseId;
        this.databaseValue = databaseValue;
    }

    public String getId () {
        return databaseId;
    }

    public String getValue () {
        return databaseValue;
    }

    @Override
    public String toString () {
        return databaseValue;
    }

}
