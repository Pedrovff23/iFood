package com.example.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.ifood.R;
import com.example.ifood.adapter.AdapterPedido;
import com.example.ifood.adapter.AdapterProduto;
import com.example.ifood.databinding.ActivityCardapioBinding;
import com.example.ifood.databinding.ActivityPedidosBinding;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.listener.RecyclerItemClickListener;
import com.example.ifood.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PedidosActivity extends AppCompatActivity {
    private ActivityPedidosBinding binding;
    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPedidosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getUID();

        Toolbar toolbar = binding.includeToolbar.toolbar;
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter(adapterPedido);

        recuperarPedidos();

        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerPedidos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onLongItemClick(View view, int position) {
                Pedido pedido = pedidos.get(position);
                pedido.setStatus("finalizado");
                pedido.atualizarStatus();
                adapterPedido.notifyDataSetChanged();
                adapterPedido.notifyItemRemoved(position);
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

    }



    private void recuperarPedidos() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Carregando dados");
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(idEmpresa);
        Query pedidoPesquisa = pedidoRef.orderByChild("status").equalTo("Confirmado");
        pedidoPesquisa.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pedidos.clear();
                if(snapshot.getValue()!=null){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                }
                adapterPedido.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes() {
        recyclerPedidos = binding.recyclerPedidos;
    }
}