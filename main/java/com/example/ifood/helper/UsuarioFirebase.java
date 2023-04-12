package com.example.ifood.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class UsuarioFirebase {

    public static String getUID(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getAtuh();
        return Objects.requireNonNull(autenticacao.getCurrentUser()).getUid();
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getAtuh();
        return usuario.getCurrentUser();
    }

    public static void atualizarTipoUsuario(String tipo){
        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo).build();
            user.updateProfile(profile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
