package com.example.ifood.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ifood.databinding.ActivityConfiguracoesUsuarioBinding;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.model.Produto;
import com.example.ifood.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {
    private ActivityConfiguracoesUsuarioBinding binding;
    private DatabaseReference reference;
    private EditText editUsuarioNome;
    private String idUsuario;
    private EditText editUsuarioEndereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfiguracoesUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarCompontnetes();

        Toolbar toolbar = binding.includeToolbar.toolbar;
        toolbar.setTitle("Configurações Usuario");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recuperarDadosUsuario();

        binding.Salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = editUsuarioNome.getText().toString();
                String endereco = editUsuarioEndereco.getText().toString();

                if (!nome.isEmpty()) {
                    if (!endereco.isEmpty()) {
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(idUsuario);
                        usuario.setNome(nome);
                        usuario.setEndereco(endereco);
                        usuario.salvar();
                        exibirMensagem("Dados salvo com sucesso");
                        finish();

                    } else {
                        exibirMensagem("Digite seu endereço");
                    }
                } else {
                    exibirMensagem("Digite seu nome");
                }
            }
        });
    }

    private void recuperarDadosUsuario(){
        DatabaseReference reference1 = reference.child("usuarios").child(idUsuario);
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() !=null){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    assert usuario != null;
                    editUsuarioEndereco.setText(usuario.getEndereco());
                    editUsuarioNome.setText(usuario.getNome());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

            private void exibirMensagem(String texto) {
                Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                texto,
                                Toast.LENGTH_SHORT)
                        .show();
            }

            private void inicializarCompontnetes() {
                editUsuarioEndereco = binding.editUsuarioEndereco;
                editUsuarioNome = binding.editUsuarioNome;
                reference = ConfiguracaoFirebase.getFirebase();
                idUsuario = UsuarioFirebase.getUID();
            }
        }