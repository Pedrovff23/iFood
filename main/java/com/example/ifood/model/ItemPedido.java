package com.example.ifood.model;

public class ItemPedido {
    private String idProduto;
    private String nomeProduto;
    private Integer quantidade;
    private Double preco;

    public String getIdProduto() {
        return idProduto;
    }

    public ItemPedido() {
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
