<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Detalhe da Manutenção — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header-lista">
                    <div style="display:flex; align-items:center; gap:16px;">
                        <a href="${pageContext.request.contextPath}/manutencoes/listar?idVeiculo=${manutencao.veiculo.id}"
                            class="btn btn-secondary">← Voltar</a>
                        <h2>Detalhe da Manutenção</h2>
                    </div>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <div class="secao">
                    <table class="tabela-detalhe">
                        <tbody>
                            <tr>
                                <th>Veículo</th>
                                <td>${manutencao.veiculo.placa} — ${manutencao.veiculo.tipoVeiculo.descricao}</td>
                            </tr>
                            <tr>
                                <th>Tipo</th>
                                <td>
                                    <span
                                        class="badge ${manutencao.tipo.name() == 'PREVENTIVA' ? 'badge-preventiva' : 'badge-corretiva'}">
                                        ${manutencao.tipo.descricao}
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <th>Descrição</th>
                                <td>${not empty manutencao.descricao ? manutencao.descricao : '—'}</td>
                            </tr>
                            <tr>
                                <th>Data Início</th>
                                <td>${manutencao.dataInicioFormatada}</td>
                            </tr>
                            <tr>
                                <th>Data Fim</th>
                                <td>${manutencao.dataFimFormatada}</td>
                            </tr>
                            <tr>
                                <th>Custo (R$)</th>
                                <td>${not empty manutencao.custo ? manutencao.custo : '—'}</td>
                            </tr>
                            <tr>
                                <th>Status</th>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty manutencao.dataFim}">
                                            <span class="badge badge-manutencao">Em andamento</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-ativo">Concluída</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

            </div>
        </body>

        </html>