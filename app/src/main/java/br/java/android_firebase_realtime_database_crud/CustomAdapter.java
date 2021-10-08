package br.java.android_firebase_realtime_database_crud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    TextView nome;
    TextView idade;
    Context context;

    ArrayList<Dados> dado;

    LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList<Dados> dado) {
        this.context = context;
        this.dado = dado;
    }
    @Override
    public int getCount() {
        return dado.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.from(context).inflate(R.layout.custom_list, viewGroup, false);

        nome = (TextView) view.findViewById(R.id.lerNome);
        idade = (TextView) view.findViewById(R.id.lerIdade);

        nome.setText(nome.getText() + " " + dado.get(i).getNome());
        idade.setText(idade.getText()+ " " + dado.get(i).getIdade());

        return view;
    }
}
