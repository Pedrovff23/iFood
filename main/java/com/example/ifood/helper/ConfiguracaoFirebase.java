package com.example.ifood.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ConfiguracaoFirebase {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;
    private static StorageReference storageReference;

    public static String getUID(){
        FirebaseAuth autenticacao = getAtuh();
        return Objects.requireNonNull(autenticacao.getCurrentUser()).getUid();
    }

    public static DatabaseReference getFirebase(){
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    public static FirebaseAuth getAtuh(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    public static StorageReference getStorage(){
        if(storageReference == null){
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference;
    }
}
