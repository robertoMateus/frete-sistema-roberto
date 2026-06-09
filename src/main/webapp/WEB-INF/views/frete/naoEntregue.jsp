<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Registrar Não Entrega — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>
            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">
                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}"
                        class="btn btn-secondary">← Voltar</a>
                    <h2>Registrar Não Entrega — ${frete.numeroFrete}</h2>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <div class="card">
                    <div class="card-titulo">Não Entrega</div>
                    <form method="post" action="${pageContext.request.contextPath}/fretes/naoEntregue">
                        <input type="hidden" name="id" value="${frete.id}" />

                        <div class="form-group">
                            <label>Data/Hora <span class="obrigatorio">*</span></label>
                            <input type="datetime-local" name="dataOcorrencia" required />
                        </div>

                        <div class="form-group">
                            <label>Motivo <span class="obrigatorio">*</span></label>
                            <textarea name="motivo" rows="3" required></textarea>
                        </div>

                        <div class="form-acoes">
                            <a href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}"
                                class="btn btn-secondary">Cancelar</a>
                            <button type="submit" class="btn btn-primary">Registrar Não Entrega</button>
                        </div>
                    </form>
                </div>
            </div>

        </body>

        </html>