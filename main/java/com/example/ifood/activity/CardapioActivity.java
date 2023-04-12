package com.example.ifood.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifood.R;
import com.example.ifood.adapter.AdapterProduto;
import com.example.ifood.databinding.ActivityCardapioBinding;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.listener.RecyclerItemClickListener;
import com.example.ifood.model.Empresa;
import com.example.ifood.model.ItemPedido;
import com.example.ifood.model.Pedido;
import com.example.ifood.model.Produto;
import com.example.ifood.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardapioActivity extends AppCompatActivity {

    private ActivityCardapioBinding binding;
    private RecyclerView recyclerViewProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresa;
    private Empresa empresaSelecionada;
    private AdapterProduto adapterProduto;
    private final List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference reference;
    private AlertDialog dialog2;
    private String idUsuarioLogado;
    private String idEmpresa;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private TextView textCarrinhoQtd, textCarrinhoTotal;
    private int metodoDePagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardapioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.includeToolbar.toolbar;
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        recyclerViewProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerViewProdutosCardapio.setAdapter(adapterProduto);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");
            textNomeEmpresa.setText(empresaSelecionada.getNome());
            idEmpresa = empresaSelecionada.getIdUsuario();

            String url = empresaSelecionada.getUrlImage();
            Picasso.get().load(url).into(imageEmpresaCardapio);
        }
        recuperarProdutos();
        recuperarDadosUsuarios();

        recyclerViewProdutosCardapio.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewProdutosCardapio,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        confirmarQuantidade(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }

    private void confirmarQuantidade(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");
        EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");
        builder.setView(editQuantidade);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String quantidade = editQuantidade.getText().toString();

                Produto produtoSelecionado = produtos.get(position);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getPreco());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));
                itensCarrinho.add(itemPedido);
                if(pedidoRecuperado == null ){
                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                }
                pedidoRecuperado.setNome(usuario.getNome());
                pedidoRecuperado.setEndereco(usuario.getEndereco());
                pedidoRecuperado.setItens(itensCarrinho);
                pedidoRecuperado.salvar();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarDadosUsuarios() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Carregando dados");
        dialog.setCancelable(false);
        dialog2 = dialog.create();
        dialog2.show();

        DatabaseReference usuarioRef = reference
                .child("usuarios")
                .child(idUsuarioLogado);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    usuario = snapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                recuperarPedido();
            }
        });

    }

    private void recuperarPedido() {
        DatabaseReference pedidoRef = reference.child("pedidos_usuario").child(idEmpresa)
                .child(idUsuarioLogado);

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if(snapshot.getValue() != null){
                    pedidoRecuperado = snapshot.getValue(Pedido.class);
                    assert pedidoRecuperado != null;
                    itensCarrinho = pedidoRecuperado.getItens();

                    for(ItemPedido pedido: itensCarrinho){
                        int qtde = pedido.getQuantidade();
                        Double preco = pedido.getPreco();

                        totalCarrinho += (qtde + preco);
                        qtdItensCarrinho += qtde;
                    }
                }
                DecimalFormat df = new DecimalFormat("0.00");
                textCarrinhoQtd.setText("qtd: " + qtdItensCarrinho);
                textCarrinhoTotal.setText("total: " + df.format(totalCarrinho));
                dialog2.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarProdutos() {
        DatabaseReference prodRef = reference.child("produtos")
                .child(empresaSelecionada.getIdUsuario());

        prodRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menuPedido) {
            System.out.println();
            confirmarPedido();
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmarPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento");
        CharSequence[] itens = new CharSequence[]{
                "Dinheiro", "Maquina cartão"
        };
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                metodoDePagamento = i;
            }
        });
        EditText editObsercao = new EditText(this);
        editObsercao.setHint("Digite uma observação");
        builder.setView(editObsercao);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String observacao = editObsercao.getText().toString();
                pedidoRecuperado.setMetodoPagamento(metodoDePagamento);
                pedidoRecuperado.setObsrvacao(observacao);
                pedidoRecuperado.setStatus("Confirmado");
                pedidoRecuperado.confirmar();
                pedidoRecuperado.remover();
                pedidoRecuperado = null;
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void inicializarComponentes() {
        recyclerViewProdutosCardapio = binding.recyclerProdutosCardapio;
        imageEmpresaCardapio = binding.imageEmpresaCardapio;
        textNomeEmpresa = binding.textNomeEmpresaCardapio;
        reference = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getUID();
        textCarrinhoTotal = binding.textCarrinhoTotal;
        textCarrinhoQtd = binding.textCarrinhoQtd;

    }
}