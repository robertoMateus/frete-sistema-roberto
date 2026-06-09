<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Confirmar Saída — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>
            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">
                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}"
                        class="btn btn-secondary">← Voltar</a>
                    <h2>Confirmar Saída — ${frete.numeroFrete}</h2>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <div class="card">
                    <div class="card-titulo">Confirmação de Saída</div>
                    <form method="post" action="${pageContext.request.contextPath}/fretes/confirmarSaida">
                        <input type="hidden" name="id" value="${frete.id}" />

                        <div class="form-row col-2" style="margin-bottom:20px;">
                            <div class="form-group">
                                <label>Destino</label>
                                <input type="text" readonly value="${frete.municipioDestino} / ${frete.ufDestino}" />
                            </div>
                            <div class="form-group">
                                <label>Motorista</label>
                                <input type="text" readonly value="${frete.motorista.nome}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Data/Hora da Saída <span class="obrigatorio">*</span></label>
                            <input type="datetime-local" name="dataSaida" required />
                        </div>

                        <div class="form-acoes">
                            <a href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}"
                                class="btn btn-secondary">Cancelar</a>
                            <button type="submit" class="btn btn-primary">Confirmar Saída</button>
                        </div>
                    </form>
                </div>
            </div>

        </body>

        </html>