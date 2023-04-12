package com.example.ifood.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifood.R;
import com.example.ifood.adapter.AdapterEmpresa;
import com.example.ifood.adapter.AdapterProduto;
import com.example.ifood.databinding.ActivityHomeBinding;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.listener.RecyclerItemClickListener;
import com.example.ifood.model.Empresa;
import com.example.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FirebaseAuth auth;
    private RecyclerView recyclerViewEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = ConfiguracaoFirebase.getAtuh();
        Toolbar toolbar = binding.includeToolbar.toolbar;
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerViewEmpresa = binding.recyclerEmpresas;

        recyclerViewEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmpresa.setHasFixedSize(true);
        adapterEmpresa= new AdapterEmpresa(empresas);
        recyclerViewEmpresa.setAdapter(adapterEmpresa);

        recuperarEmpresas();
    }

    private void recuperarEmpresas(){
        DatabaseReference empresaRef = firebaseRef.child("empresas");

        empresaRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();
                for( DataSnapshot ds: snapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        //Configurar botao de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView = (SearchView) item.getActionView();

        searchView.setQueryHint("Pesquisar restaurante");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });

        recyclerViewEmpresa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerViewEmpresa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Empresa empresaselecionada = empresas.get(position);
                                Intent i = new Intent(HomeActivity.this,
                                        CardapioActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("empresa",empresaselecionada);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view,
                                                    int i, long l) {

                            }
                        }
                )
        );

        return super.onCreateOptionsMenu(menu);
    }

    private void pesquisarEmpresas(String pesquisa){
        DatabaseReference ref = firebaseRef.child("empresas");
        Query query = ref.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();
                for( DataSnapshot ds: snapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menuSair) {
            desolagarUsuario();
        } else if (item.getItemId() == R.id.menuConfiguracoes) {
            abrirConfiguracoes();
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirConfiguracoes() {
        Intent intent = new Intent(HomeActivity.this, ConfiguracoesUsuarioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void desolagarUsuario() {
        try {
            auth.signOut();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}