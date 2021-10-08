package br.java.android_firebase_realtime_database_crud;

public class Dados {

    String chave;
    String nome;
    int idade;

    public Dados() {
    }

    public Dados(String chave, String nome, int idade) {
        this.chave = chave;
        this.nome = nome;
        this.idade = idade;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }
}
