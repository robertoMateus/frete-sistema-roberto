<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="pt-BR">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Dashboard — Sistema de Gestão de Fretes</title>
                <style>
                    * {
                        box-sizing: border-box;
                        margin: 0;
                        padding: 0;
                    }

                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f6f8;
                        color: #333;
                    }

                    .container {
                        padding: 32px;
                    }

                    .boas-vindas {
                        margin-bottom: 28px;
                    }

                    .boas-vindas h2 {
                        font-size: 1.4rem;
                        color: #333;
                    }

                    .boas-vindas p {
                        font-size: 0.9rem;
                        color: #666;
                        margin-top: 4px;
                    }

                    .cards {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                        gap: 20px;
                        margin-bottom: 36px;
                    }

                    .card {
                        background-color: #fff;
                        border-radius: 8px;
                        padding: 24px;
                        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
                        border-left: 5px solid #ccc;
                    }

                    .card.emitido {
                        border-left-color: #f0a500;
                    }

                    .card.saida {
                        border-left-color: #1a73e8;
                    }

                    .card.transito {
                        border-left-color: #0d9e6e;
                    }

                    .card.atrasado {
                        border-left-color: #e53935;
                    }

                    .card .numero {
                        font-size: 2.2rem;
                        font-weight: bold;
                        color: #333;
                    }

                    .card.atrasado .numero {
                        color: #e53935;
                    }

                    .card .label {
                        font-size: 0.85rem;
                        color: #666;
                        margin-top: 6px;
                    }

                    .secao {
                        background-color: #fff;
                        border-radius: 8px;
                        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
                        margin-bottom: 28px;
                        overflow: hidden;
                    }

                    .secao-header {
                        padding: 16px 24px;
                        border-bottom: 1px solid #eee;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }

                    .secao-header h3 {
                        font-size: 1rem;
                        color: #333;
                    }

                    .secao-header a {
                        font-size: 0.85rem;
                        color: #1a73e8;
                        text-decoration: none;
                    }

                    .secao-header a:hover {
                        text-decoration: underline;
                    }

                    table {
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 0.9rem;
                    }

                    thead th {
                        background-color: #f8f9fa;
                        padding: 12px 16px;
                        text-align: left;
                        font-weight: bold;
                        color: #555;
                        border-bottom: 1px solid #eee;
                    }

                    tbody td {
                        padding: 12px 16px;
                        border-bottom: 1px solid #f0f0f0;
                        color: #444;
                    }

                    tbody tr:last-child td {
                        border-bottom: none;
                    }

                    tbody tr:hover {
                        background-color: #fafafa;
                    }

                    .badge {
                        display: inline-block;
                        padding: 3px 10px;
                        border-radius: 12px;
                        font-size: 0.78rem;
                        font-weight: bold;
                    }

                    .badge-emitido {
                        background-color: #fff3cd;
                        color: #856404;
                    }

                    .badge-saida-confirmada {
                        background-color: #cce5ff;
                        color: #004085;
                    }

                    .badge-em-transito {
                        background-color: #d4edda;
                        color: #155724;
                    }

                    .badge-manutencao {
                        background-color: #fff3cd;
                        color: #856404;
                    }

                    .sem-dados {
                        padding: 24px;
                        text-align: center;
                        color: #999;
                        font-size: 0.9rem;
                    }

                    .erro {
                        background-color: #fdecea;
                        color: #c0392b;
                        border: 1px solid #e74c3c;
                        border-radius: 4px;
                        padding: 10px 14px;
                        margin-bottom: 20px;
                        font-size: 0.875rem;
                    }
                </style>
            </head>

            <body>

                <jsp:include page="/WEB-INF/views/components/header.jsp" />

                <div class="container">

                    <div class="boas-vindas">
                        <h2>Bem-vindo, ${sessionScope.usuarioLogado.nome}!</h2>
                        <p>Resumo operacional do dia.</p>
                    </div>

                    <c:if test="${not empty erro}">
                        <div class="erro">${erro}</div>
                    </c:if>

                    <div class="cards">
                        <div class="card emitido">
                            <div class="numero">${totalEmitidos}</div>
                            <div class="label">Fretes Emitidos</div>
                        </div>
                        <div class="card saida">
                            <div class="numero">${totalSaidaConfirmada}</div>
                            <div class="label">Saída Confirmada</div>
                        </div>
                        <div class="card transito">
                            <div class="numero">${totalEmTransito}</div>
                            <div class="label">Em Trânsito</div>
                        </div>
                        <div class="card atrasado">
                            <div class="numero">${totalAtrasados}</div>
                            <div class="label">Fretes Atrasados</div>
                        </div>
                    </div>

                    <div class="secao">
                        <div class="secao-header">
                            <h3>Fretes em Aberto</h3>
                            <a href="${pageContext.request.contextPath}/fretes/listar">Ver todos →</a>
                        </div>
                        <c:choose>
                            <c:when test="${empty ultimosFretes}">
                                <div class="sem-dados">Nenhum frete em aberto no momento.</div>
                            </c:when>
                            <c:otherwise>
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Número</th>
                                            <th>Destino</th>
                                            <th>Motorista</th>
                                            <th>Previsão Entrega</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="frete" items="${ultimosFretes}">
                                            <tr>
                                                <td>
                                                    <a
                                                        href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}">
                                                        ${frete.numeroFrete}
                                                    </a>
                                                </td>
                                                <td>${frete.municipioDestino}/${frete.ufDestino}</td>
                                                <td>${frete.motorista.nome}</td>
                                                <td>${frete.dataPrevisaoEntregaFormatada}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${frete.status.name() == 'EMITIDO'}">
                                                            <span class="badge badge-emitido">Emitido</span>
                                                        </c:when>
                                                        <c:when test="${frete.status.name() == 'SAIDA_CONFIRMADA'}">
                                                            <span class="badge badge-saida-confirmada">Saída
                                                                Confirmada</span>
                                                        </c:when>
                                                        <c:when test="${frete.status.name() == 'EM_TRANSITO'}">
                                                            <span class="badge badge-em-transito">Em Trânsito</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge">${frete.status.name()}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="secao">
                        <div class="secao-header">
                            <h3>Veículos em Manutenção</h3>
                            <a href="${pageContext.request.contextPath}/manutencoes/listar">Ver todos →</a>
                        </div>
                        <c:choose>
                            <c:when test="${empty manutencoesEmAberto}">
                                <div class="sem-dados">Nenhum veículo em manutenção no momento.</div>
                            </c:when>
                            <c:otherwise>
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Placa</th>
                                            <th>Tipo</th>
                                            <th>Descrição</th>
                                            <th>Início</th>
                                            <th>Situação</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="manutencao" items="${manutencoesEmAberto}">
                                            <tr>
                                                <td>${manutencao.veiculo.placa}</td>
                                                <td>${manutencao.tipo.descricao}</td>
                                                <td>${manutencao.descricao}</td>
                                                <td>${manutencao.dataInicioFormatada}</td>
                                                <td>
                                                    <span class="badge badge-manutencao">Em Aberto</span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </div>
            </body>

            </html>