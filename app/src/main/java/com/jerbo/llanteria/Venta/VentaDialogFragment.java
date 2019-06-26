package com.jerbo.llanteria.Venta;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.*;
import android.widget.TextView;
import com.jerbo.llanteria.Models.Producto;
import com.jerbo.llanteria.R;

public class VentaDialogFragment extends DialogFragment {
    TextView modificar, eliminar;
    static Producto item;

    public static Metodos metodos;

    public VentaDialogFragment() {
    }

    public static VentaDialogFragment newInstance(Producto p, Metodos m) {
        VentaDialogFragment frag = new VentaDialogFragment();
        item = p;
        metodos = m;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.venta_fragment_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        modificar = view.findViewById(R.id.dialog_modificar);
        eliminar = view.findViewById(R.id.dialog_eliminar);
        eliminar.setOnClickListener(v -> {
            metodos.eliminar(item);
            dismiss();
        });
        modificar.setOnClickListener(v -> {
            metodos.modificar(item);
            dismiss();
        });

    }

    public interface Metodos {
        void eliminar(Producto p);

        void modificar(Producto p);
    }


}
