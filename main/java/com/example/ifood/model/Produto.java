package com.example.ifood.model;

import com.example.ifood.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Produto {

    private String idUsuario;
    private String nome;
    private String idProduto;
    private String descricao;
    private Double preco;

    public Produto() {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produto = reference.child("produtos");
        setIdProduto(produto.push().getKey());
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public void salvar(){
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produto = reference.child("produtos")
                .child(getIdUsuario()).child(getIdProduto());
        produto.setValue(this);
    }

    public void remove(){
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produto = reference
                .child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        produto.removeValue();
    }
}
