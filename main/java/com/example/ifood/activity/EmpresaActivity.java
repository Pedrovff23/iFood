package com.example.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.ifood.R;
import com.example.ifood.adapter.AdapterProduto;
import com.example.ifood.databinding.ActivityEmpresaBinding;
import com.example.ifood.databinding.ToolbarBinding;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.listener.RecyclerItemClickListener;
import com.example.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

    private ActivityEmpresaBinding binding;
    private FirebaseAuth auth;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private final List<Produto> produtos = new ArrayList<>();
    private DatabaseReference reference;
    private String idUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmpresaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.includeToolbar.toolbar;
        toolbar.setTitle("Ifood - empresa");
        setSupportActionBar(toolbar);
        inicializarComponentes();
        auth = ConfiguracaoFirebase.getAtuh();

        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos,this);
        recyclerProdutos.setAdapter(adapterProduto);

        recuperarProdutos();

        recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerProdutos,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remove();
                Toast.makeText(EmpresaActivity.this,
                        "Produto excluido com sucesso",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        }));

    }

    private void recuperarProdutos(){
        DatabaseReference prodRef = reference.child("produtos")
                .child(idUsuarioLogado);

        prodRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear();
                for( DataSnapshot ds: snapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void inicializarComponentes() {
        recyclerProdutos = binding.recyclerProdutos;
        idUsuarioLogado = ConfiguracaoFirebase.getUID();
        reference = ConfiguracaoFirebase.getFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menuSair){
            desolagarUsuario();
        }else if (item.getItemId() == R.id.menuConfiguracoes){
            abrirConfiguracoes();
        }else if (item.getItemId() == R.id.menuNovoProduto){
            abrirNovoProduto();
        }else if (item.getItemId() == R.id.menuPedidos){
            abrirPedidos();
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void abrirPedidos() {
        Intent intent = new Intent(EmpresaActivity.this,
                PedidosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void abrirNovoProduto() {
        Intent intent = new Intent(EmpresaActivity.this,
                NovoProdutoEmpresaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void abrirConfiguracoes() {
        Intent intent = new Intent(EmpresaActivity.this,
                ConfiguracoesEmpresaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void desolagarUsuario() {
        try {
            auth.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}