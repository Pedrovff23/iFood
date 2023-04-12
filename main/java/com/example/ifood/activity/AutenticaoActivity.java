package com.example.ifood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.ifood.databinding.ActivityAutenticaoBinding;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AutenticaoActivity extends AppCompatActivity {
    private ActivityAutenticaoBinding binding;
    private EditText textoEmail, textoSenha;
    private SwitchCompat tipoAcesso, tipoUsuario;
    private Button botaoAcessar;
    private FirebaseAuth autentificao;
    private LinearLayout linearLayoutTipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAutenticaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarComponentes();
        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    linearLayoutTipoUsuario.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = textoEmail.getText().toString();
                String senha = textoSenha.getText().toString();

                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {
                        //Verifica o status do switch
                        if (tipoAcesso.isChecked()) { //Cadstro
                            validarCadastro(email, senha);

                        } else { //Login
                            validarLogin(email, senha);
                        }
                    } else {
                        Toast.makeText(AutenticaoActivity.this,
                                "Preencha a Senha",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AutenticaoActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        tipoAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //Login
    private void validarLogin(String email, String senha) {

        autentificao.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AutenticaoActivity.this,
                                    "Logado com sucesso",
                                    Toast.LENGTH_SHORT).show();

                            String tipoUsuario = Objects.requireNonNull(task.getResult().getUser())
                                    .getDisplayName();
                            assert tipoUsuario != null;
                            abrirTelaHome(tipoUsuario);

                        } else {
                            Toast.makeText(AutenticaoActivity.this,
                                    "Erro ao fazer login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Cadastro
    private void validarCadastro(String email, String senha) {

        autentificao.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AutenticaoActivity.this,
                                    "Usuário Cadastrado com sucesso",
                                    Toast.LENGTH_SHORT).show();

                            String tipoUsuario = getTipoUsuario();
                            UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                            abrirTelaHome(tipoUsuario);

                        } else {
                            String erroExecao = "";

                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthWeakPasswordException e) {
                                erroExecao = "Digite uma senha mais Forte!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExecao = "Digite um email válido!";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erroExecao = "Esta conta já foi cadastrada!";
                            } catch (Exception e) {
                                erroExecao = "Erro ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(AutenticaoActivity.this, erroExecao,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void abrirTelaHome(String tipoUsuario) {
        if(tipoUsuario.equals("E")){

            startActivity(new Intent(AutenticaoActivity.this,
                    EmpresaActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();

        }else {
            startActivity(new Intent(AutenticaoActivity.this,
                    HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }

    }

    private void verificarUsuarioLogado(){
        FirebaseUser user = autentificao.getCurrentUser();
        if(user!=null){
            String tipoUsuario = UsuarioFirebase.getUsuarioAtual().getDisplayName();
            assert tipoUsuario != null;
            abrirTelaHome(tipoUsuario);
        }
    }

    private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "E" : "U";
    }

    private void inicializarComponentes() {
        textoEmail = binding.inputEmail;
        textoSenha = binding.inputSenha;
        tipoAcesso = binding.tipoAcesso;
        tipoUsuario = binding.tipoUsuario;
        botaoAcessar = binding.botaoAcessar;
        linearLayoutTipoUsuario = binding.linearTipoUsuario;
        autentificao = ConfiguracaoFirebase.getAtuh();
    }
}