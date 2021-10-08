package br.java.android_firebase_realtime_database_crud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LayoutInflater inflater1;

    int count = 0;
    String nome;
    int idade;

    EditText txtnome;
    EditText txtidade;
    EditText txtbuscar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Dados dado;

    ListView listView;

    ArrayList<Dados> dadosLista;

    CustomAdapter customAdapter;

    String chave;
    int temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Estudantes");
        chave = databaseReference.push().getKey();

        txtnome = (EditText) findViewById(R.id.escreverNome);
        txtidade = (EditText) findViewById(R.id.escreverIdade);
        listView = (ListView) findViewById(R.id.lerLista);
        
        findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                try {
                    nome = txtnome.getText().toString().trim();
                    
                    if (TextUtils.isEmpty(nome)) {
                        Toast.makeText(getApplicationContext(), "Por favor digite o nome", Toast.LENGTH_SHORT).show();
                    } else {
                        idade = Integer.parseInt(txtidade.getText().toString().trim());

                        dado = new Dados(databaseReference.push().getKey(), nome, idade);
                        databaseReference.child(dado.getChave()).setValue(dado);

                        Toast.makeText(getApplicationContext(), "Enviar", Toast.LENGTH_SHORT).show();

                        txtnome.setText("");
                        txtidade.setText("");
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dadosLista = new ArrayList<>();
        customAdapter = new CustomAdapter(MainActivity.this, dadosLista);
        listView.setAdapter(customAdapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Dados datam = snapshot.getValue(Dados.class);
                dadosLista.add(datam);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final View v = inflater1.from(getApplicationContext()).inflate(R.layout.custom_alert, null);
                temp = i;
                final EditText atualizarNome;
                final EditText atualizarIdade;

                atualizarNome = (EditText) v.findViewById(R.id.atualizaNome);
                atualizarIdade = (EditText) v.findViewById(R.id.escreverIdade);

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setView(v);
                final AlertDialog alert = builder.create();

                v.findViewById(R.id.atualizar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dados tempDado = new Dados(dadosLista.get(temp).getChave(), atualizarNome.getText().toString().trim(),
                                Integer.parseInt(atualizarIdade.getText().toString().trim()));

                        databaseReference.child(dadosLista.get(temp).getChave()).setValue(tempDado);
                        
                        dadosLista.remove(temp);
                        dadosLista.add(temp, tempDado);
                        customAdapter.notifyDataSetChanged();
                    }
                });
                
                v.findViewById(R.id.deletar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (temp == -1) {
                            Toast.makeText(getApplicationContext(), "Não há dados para excluir", Toast.LENGTH_SHORT).show();
                        } else {
                            databaseReference.child(dadosLista.get(temp).getChave()).removeValue();
                            dadosLista.remove(temp);
                            customAdapter.notifyDataSetChanged();
                            alert.cancel();

                            temp = -1;
                        }
                    }
                });

                atualizarNome.setText(dadosLista.get(temp).getNome());
                atualizarIdade.setText("" + dadosLista.get(temp).getIdade());

                try {
                    alert.show();
                } catch (Exception e) {
                    Log.d("show", "onItemClick: " + e);
                }

                return;
            }
        });

        txtbuscar = (EditText) findViewById(R.id.buscar);
        findViewById(R.id.btn_buscar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nome = txtbuscar.getText().toString().trim();
                databaseReference.orderByChild("nome").equalTo(nome).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ++count;

                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            dado = snapshot1.getValue(Dados.class);
                            dadosLista.clear();
                            dadosLista.add(dado);
                            Log.d("log", "OnDataChange: "+snapshot1.child("nome").getValue());
                        }
                        funcao();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        atualizacaoEmTempoReal();
    }
    public void atualizacaoEmTempoReal() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dadosLista.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    dado = snapshot1.getValue(Dados.class);
                    dadosLista.add(dado);
                }

                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
    public void funcao() {
        if (count != 0) {
            customAdapter = new CustomAdapter(getApplicationContext(), dadosLista);
            listView.setAdapter(customAdapter);
        } else {
            Toast.makeText(getApplicationContext(), "Não há dados para mostrar", Toast.LENGTH_SHORT).show();
            listView.setVisibility(View.GONE);
        }
    }
}