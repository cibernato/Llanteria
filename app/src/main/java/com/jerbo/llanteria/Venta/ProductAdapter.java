package com.jerbo.llanteria.Venta;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jerbo.llanteria.Models.Producto;
import com.jerbo.llanteria.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductoViewHolder> {

    Context context;
    List<Producto> productos;
    onClickListeners mListener;

    public interface onClickListeners {
        void onItemClick(Producto item);
        boolean onLongClick(Producto item);
    }

    public ProductAdapter(Context context, List<Producto> productos, onClickListeners onClickListeners) {
        this.context = context;
        this.productos = productos;
        this.mListener = onClickListeners;
    }


    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_item_list, viewGroup, false);
        return new ProductoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder productoViewHolder, int i) {
        productoViewHolder.bind(productos.get(i),mListener);
//        productoViewHolder.id.setText(String.valueOf(productos.get(i).getId()));
//        productoViewHolder.nom.setText(String.valueOf(productos.get(i).getProducto()));
//        productoViewHolder.precio.setText(String.valueOf(productos.get(i).getPrecio()));
//        productoViewHolder.cantidad.setText(String.valueOf(productos.get(i).getCantidad()));


    }


    @Override
    public int getItemCount() {
        return productos.size();
    }


    public class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView id, nom, precio, cantidad;
        onClickListeners onClickListeners;
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.product_list_id);
            nom = itemView.findViewById(R.id.product_list_nombre);
            precio = itemView.findViewById(R.id.product_list_precio);
            cantidad = itemView.findViewById(R.id.product_list_cantidad);

        }
        public void bind(final Producto item, final onClickListeners listener) {
            id.setText(String.valueOf(item.getId()));
            nom.setText(String.valueOf(item.getProducto()));
            precio.setText(String.valueOf(item.getPrecio()));
            cantidad.setText(String.valueOf(item.getCantidad()));
            onClickListeners = listener;
            itemView.setOnClickListener(v -> listener.onItemClick(item));
            itemView.setOnLongClickListener(v -> listener.onLongClick(item));
        }

    }
}
