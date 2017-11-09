/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

public class Lote {
    private int id;
    private String nome;
    private double preco;
    private long duracao;
    private long inicio;
    private String comprador;
    
    private boolean encerrado;

    public Lote(String nome, double preco, long duracao, int codigo) {
        this.nome = nome;
        this.preco = preco;
        this.duracao = duracao;
        this.id = codigo;
        this.encerrado = false;
        this.comprador = "";
    }

    public void lance(double valor) {
        this.preco = valor;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public long getDuracao() {
        return duracao;
    }

    public boolean isEncerrado() {
        return encerrado;
    }

    public void encerra() {
        this.encerrado = true;
    }
    
    public void setInicio() {
        this.inicio = System.currentTimeMillis();
    }
    
    public long getInicio() {
        return this.inicio;
    }
    
    public void setComprador(String comprador) {
        this.comprador = comprador;
    }
    
    public String getComprador() {
        return this.comprador;
    }
}