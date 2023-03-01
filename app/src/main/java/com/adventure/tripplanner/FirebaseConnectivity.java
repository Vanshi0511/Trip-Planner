package com.adventure.tripplanner;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class FirebaseConnectivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private final static  String ORGANIZER_DATABASE ="Organizer";
    private final static String TRAVELER_DATABASE ="Traveler";
    private final static String USERS_DATABASE ="Users";
    private final static String DATABASE_TRIP="Trips";
    private final static String DATABASE_BOOKING="Bookings";

    private final static String DATA_NOT_DEFINED="not_defined";

    public static final String STATUS_ORGANIZER="organizer";
    public static final String STATUS_TRAVELER="traveler";

    public FirebaseConnectivity(String path)
    {
        database=FirebaseDatabase.getInstance();
        reference=database.getReference(path);
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference(path);
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public StorageReference getStorageReference()
    {
        return storageReference;
    }

    public static void updateUserStatus(String value,String emailKey)
    {
        String path;
        path="Users/"+emailKey;
        FirebaseConnectivity firebaseConnectivity=new FirebaseConnectivity(path);

        Map<String,Object> map=new HashMap<>();
        map.put("value",value);
        firebaseConnectivity.getReference().updateChildren(map);
    }

    public static String getOrganizerDatabase()
    {
        return ORGANIZER_DATABASE;
    }
    public static  String getTravelerDatabase()
    {
        return TRAVELER_DATABASE;
    }
    public static String getUserDatabase()
    {
        return USERS_DATABASE;
    }
    public static String getDataNotDefined()
    {
        return DATA_NOT_DEFINED;
    }
    public static String getDatabaseTrip(){ return DATABASE_TRIP;}
    public static String getDatabaseBookings(){ return DATABASE_BOOKING;}

}
