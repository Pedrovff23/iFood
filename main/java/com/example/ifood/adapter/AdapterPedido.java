package com.example.ifood.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifood.R;
import com.example.ifood.model.ItemPedido;
import com.example.ifood.model.Pedido;

import java.util.ArrayList;
import java.util.List;

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

    private List<Pedido> pedidos;

    public AdapterPedido(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos,
                parent, false);
        return new MyViewHolder(itemLista);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Pedido pedido = pedidos.get(i);
        holder.nome.setText(pedido.getNome());
        holder.endereco.setText("Endereço: " + pedido.getEndereco());
        holder.observacao.setText("Obs: " + pedido.getObsrvacao());

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedido.getItens();
        StringBuilder descricaoItens = new StringBuilder();

        int numeroItem = 1;
        double total = 0.0;
        for (ItemPedido itemPedido : itens) {

            int qtde = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            total += (qtde * preco);

            String nome = itemPedido.getNomeProduto();
            descricaoItens.append(numeroItem).append(") ").append(nome).
                    append(" / (").append(qtde).append(" x R$ ").append(preco).append(") \n");
            numeroItem++;
        }
        descricaoItens.append("Total: R$ ").append(total);
        holder.itens.setText(descricaoItens.toString());

        int metodoPagamento = pedido.getMetodoPagamento();
        String pagamento = metodoPagamento == 0 ? "Dinheiro" : "Máquina cartão";
        holder.pgto.setText("pgto: " + pagamento);

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView endereco;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textPedidoNome);
            endereco = itemView.findViewById(R.id.textPedidoEndereco);
            pgto = itemView.findViewById(R.id.textPedidoPgto);
            observacao = itemView.findViewById(R.id.textPedidoObs);
            itens = itemView.findViewById(R.id.textPedidoItens);
        }
    }

}
