package com.example.ifood.model;

import com.example.ifood.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String endereco;

    public Usuario() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void salvar(){
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuario = reference.child("usuarios").child(getIdUsuario());
        usuario.setValue(this);
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
