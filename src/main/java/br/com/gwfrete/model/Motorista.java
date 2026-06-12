package br.com.gwfrete.model;

import java.time.LocalDate;

public class Motorista {
    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;

    // Dados para cnh
    private String numeroCnh;
    private LocalDate dataValidadeCnh;
    private CategoriaCnh categoriaCnh;

    private TipoVinculoMotorista tipoVinculo;
    private StatusMotorista status;
    private Veiculo veiculo;

    public Motorista() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(String numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public LocalDate getDataValidadeCnh() {
        return dataValidadeCnh;
    }

    public void setDataValidadeCnh(LocalDate dataValidadeCnh) {
        this.dataValidadeCnh = dataValidadeCnh;
    }

    public CategoriaCnh getCategoriaCnh() {
        return categoriaCnh;
    }

    public void setCategoriaCnh(CategoriaCnh categoriaCnh) {
        this.categoriaCnh = categoriaCnh;
    }

    public TipoVinculoMotorista getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(TipoVinculoMotorista tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    public StatusMotorista getStatus() {
        return status;
    }

    public void setStatus(StatusMotorista status) {
        this.status = status;
    }

    

    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11)
            return cpf;
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9, 11);
    }

    public String getDataNascimentoFormatada() {
        if (dataNascimento == null)
            return "";
        return dataNascimento.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataValidadeCnhFormatada() {
        if (dataValidadeCnh == null)
            return "";
        return dataValidadeCnh.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

}
