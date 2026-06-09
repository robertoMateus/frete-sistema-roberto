<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocorrências — GW Gestão de Fretes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
</head>

<body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />

    <div class="container">
        <div class="page-header">
            <a href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}" class="btn btn-secondary">← Voltar</a>
            <h2>Ocorrências - Frete ${frete.numeroFrete}</h2>
        </div>

        <c:if test="${not empty param.sucesso}">
            <div class="mensagem-sucesso">Ocorrência registrada com sucesso.</div>
        </c:if>

        <div class="card">
            <div class="card-titulo">Lista de Ocorrências</div>
            <c:choose>
                <c:when test="${empty ocorrencias}">
                    <div class="sem-dados">Nenhuma ocorrência registrada.</div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>Data/Hora</th>
                                <th>Tipo</th>
                                <th>Local</th>
                                <th>Descrição</th>
                                <th>Recebedor</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="oc" items="${ocorrencias}">
                                <tr>
                                    <td>${oc.dataHoraFormatada}</td>
                                    <td>${oc.tipo.descricao}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty oc.municipio}">${oc.municipio} / ${oc.uf}</c:when>
                                            <c:otherwise>—</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${not empty oc.descricao ? oc.descricao : '—'}</td>
                                    <td>${not empty oc.nomeRecebedor ? oc.nomeRecebedor : '—'}</td>
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
