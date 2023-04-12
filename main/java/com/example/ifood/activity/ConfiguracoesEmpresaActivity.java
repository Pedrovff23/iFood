package com.example.ifood.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ifood.R;
import com.example.ifood.databinding.ActivityConfiguracoesEmpresaBinding;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.model.Empresa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo, editEmpresaTaxa;
    private ImageView imagePerfilEmpresa;
    private Button salvar;
    private static final int SELECAO_GALERIA = 2200;
    private StorageReference storageReference;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    private DatabaseReference firebaseRef;

    private ActivityConfiguracoesEmpresaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfiguracoesEmpresaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.includeToolbar.toolbar;
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Configurações inicias
        iniciarComponentes();
        storageReference = ConfiguracaoFirebase.getStorage();
        idUsuarioLogado = UsuarioFirebase.getUID();
        firebaseRef = ConfiguracaoFirebase.getFirebase();


        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = editEmpresaNome.getText().toString();
                String taxa = editEmpresaTaxa.getText().toString();
                String categoria = editEmpresaCategoria.getText().toString();
                String tempo = editEmpresaTempo.getText().toString();

                if (!nome.isEmpty()) {

                    if (!taxa.isEmpty()) {

                        if (!categoria.isEmpty()) {

                            if (!tempo.isEmpty()) {
                                Empresa empresa = new Empresa();
                                empresa.setIdUsuario(idUsuarioLogado);
                                empresa.setNome(nome);
                                empresa.setPrecoEntraga(Double.parseDouble(taxa));
                                empresa.setCategoria(categoria);
                                empresa.setTempo(tempo);
                                empresa.setUrlImage(urlImagemSelecionada);
                                empresa.salvar();
                                finish();

                            } else {
                                exibirMensagem("Preencha o campo tempo");
                            }

                        } else {
                            exibirMensagem("Preencha o campo categoria");
                        }

                    } else {
                        exibirMensagem("Preencha o campo taxa");
                    }

                } else {
                    exibirMensagem("Preencha o campo nome");
                }
            }

            private void exibirMensagem(String texto) {
                Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                texto,
                                Toast.LENGTH_SHORT)
                        .show();
            }

        });

        recuperarDadosEmpresa();
    }

    private void recuperarDadosEmpresa(){
        DatabaseReference empresaRef = firebaseRef.child("empresas")
                .child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() !=null){
                    Empresa empresa = snapshot.getValue(Empresa.class);
                    assert empresa != null;
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaCategoria.setText(empresa.getCategoria());
                    editEmpresaTaxa.setText(empresa.getPrecoEntraga().toString());
                    editEmpresaTempo.setText(empresa.getTempo());

                    urlImagemSelecionada = empresa.getUrlImage();
                    if(!Objects.equals(urlImagemSelecionada, "")){
                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilEmpresa);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {
                if (requestCode == SELECAO_GALERIA) {
                    assert data != null;
                    Uri localImagem = data.getData();
                    imagem = MediaStore
                            .Images
                            .Media
                            .getBitmap(getContentResolver(), localImagem);
                }
                if (imagem != null) {
                    imagePerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference.child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            urlImagemSelecionada = task.getResult().toString();
                                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                                    "Sucesso ao fazer upload da imagem",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void iniciarComponentes() {
        editEmpresaNome = binding.editEmpresaNome;
        editEmpresaCategoria = binding.editEmpresaCategoria;
        editEmpresaTempo = binding.editEmpresaTempoEntrega;
        editEmpresaTaxa = binding.editEmpresaTaxa;
        salvar = binding.Salvar;
        imagePerfilEmpresa = binding.imgPerfilEmpresa;
    }
}