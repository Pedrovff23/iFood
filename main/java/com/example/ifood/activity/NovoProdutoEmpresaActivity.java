package com.example.ifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ifood.R;
import com.example.ifood.databinding.ActivityNovoProdutoEmpresaBinding;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.model.Empresa;
import com.example.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;

    private String idUsuarioLogado;
    private ActivityNovoProdutoEmpresaBinding binding;
    private Button validarProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNovoProdutoEmpresaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.includeToolbar.toolbar;
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        iniciarComponentes();

        validarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDadosProdutos();
            }
        });
    }

    private void validarDadosProdutos() {

        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if (!nome.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!preco.isEmpty()) {
                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.salvar();
                    finish();
                    exibirMensagem("Produto salvo com sucesso");

                }else{
                    exibirMensagem("Digite um preço para o produto");
                }
            }else{
                exibirMensagem("Digite uma descrição para o produto");
            }
        }else{
            exibirMensagem("Digite um nome para o produto");
        }
    }


    private void exibirMensagem(String texto) {
        Toast.makeText(NovoProdutoEmpresaActivity.this,
                        texto,
                        Toast.LENGTH_SHORT)
                .show();
    }

    private void iniciarComponentes() {
        idUsuarioLogado = UsuarioFirebase.getUID();
        editProdutoNome = binding.editProdutoNome;
        editProdutoDescricao = binding.editProdutoDescricao;
        editProdutoPreco = binding.editProdutoPreco;
        validarProduto = binding.validarDadosProduto;

    }
}