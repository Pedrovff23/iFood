package com.example.ifood.model;

import com.example.ifood.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String idUsuario;
    private String urlImage;
    private String nome;
    private String tempo;
    private String categoria;
    private Double precoEntraga;

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(getIdUsuario());
        empresaRef.setValue(this);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecoEntraga() {
        return precoEntraga;
    }

    public void setPrecoEntraga(Double precoEntraga) {
        this.precoEntraga = precoEntraga;
    }
}
