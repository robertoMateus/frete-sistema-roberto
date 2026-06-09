<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Fretes — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header-lista">
                    <h2>Fretes</h2>
                    <a href="${pageContext.request.contextPath}/fretes/novo" class="btn btn-primary">+ Novo Frete</a>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <c:if test="${not empty param.sucesso}">
                    <div class="mensagem-sucesso">
                        <c:choose>
                            <c:when test="${param.sucesso == 'emitido'}">Frete emitido com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'cancelado'}">Frete cancelado com sucesso.</c:when>
                        </c:choose>
                    </div>
                </c:if>

                <form method="get" action="${pageContext.request.contextPath}/fretes/listar" class="filtro-form">
                    <input type="text" name="filtro" value="${filtro}"
                        placeholder="Buscar por número ou município de destino..." />
                    <button type="submit" class="btn btn-primary">Buscar</button>
                    <c:if test="${not empty filtro}">
                        <a href="${pageContext.request.contextPath}/fretes/listar" class="btn btn-secondary">Limpar</a>
                    </c:if>
                </form>

                <div class="secao">
                    <c:choose>
                        <c:when test="${empty fretes}">
                            <div class="sem-dados">Nenhum frete encontrado.</div>
                        </c:when>
                        <c:otherwise>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Número</th>
                                        <th>Remetente</th>
                                        <th>Destinatário</th>
                                        <th>Origem</th>
                                        <th>Destino</th>
                                        <th>Motorista</th>
                                        <th>Previsão Entrega</th>
                                        <th>Status</th>
                                        <th>Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="frete" items="${fretes}">
                                        <tr>
                                            <td>${frete.numeroFrete}</td>
                                            <td>${frete.remetente.razaoSocial}</td>
                                            <td>${frete.destinatario.razaoSocial}</td>
                                            <td>${frete.municipioOrigem}/${frete.ufOrigem}</td>
                                            <td>${frete.municipioDestino}/${frete.ufDestino}</td>
                                            <td>${frete.motorista.nome}</td>
                                            <td>${frete.dataPrevisaoEntregaFormatada}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${frete.status.name() == 'EMITIDO'}">
                                                        <span
                                                            class="badge badge-emitido">${frete.status.descricao}</span>
                                                    </c:when>
                                                    <c:when test="${frete.status.name() == 'SAIDA_CONFIRMADA'}">
                                                        <span class="badge badge-saida">${frete.status.descricao}</span>
                                                    </c:when>
                                                    <c:when test="${frete.status.name() == 'EM_TRANSITO'}">
                                                        <span
                                                            class="badge badge-transito">${frete.status.descricao}</span>
                                                    </c:when>
                                                    <c:when test="${frete.status.name() == 'ENTREGUE'}">
                                                        <span class="badge badge-ativo">${frete.status.descricao}</span>
                                                    </c:when>
                                                    <c:when test="${frete.status.name() == 'NAO_ENTREGUE'}">
                                                        <span
                                                            class="badge badge-suspenso">${frete.status.descricao}</span>
                                                    </c:when>
                                                    <c:when test="${frete.status.name() == 'CANCELADO'}">
                                                        <span
                                                            class="badge badge-inativo">${frete.status.descricao}</span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="acoes">
                                                    <a href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}"
                                                        class="btn btn-secondary btn-sm">Detalhes</a>
                                                    <c:if test="${frete.status.name() == 'EMITIDO'}">
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/fretes/cancelar"
                                                            style="display:inline"
                                                            onsubmit="return confirm('Deseja cancelar este frete?')">
                                                            <input type="hidden" name="id" value="${frete.id}" />
                                                            <button type="submit"
                                                                class="btn btn-danger btn-sm">Cancelar</button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <c:if test="${totalPaginas > 1}">
                                <div class="paginacao">
                                    <c:if test="${paginaAtual > 1}">
                                        <a
                                            href="${pageContext.request.contextPath}/fretes/listar?pagina=${paginaAtual - 1}&filtro=${filtro}">←
                                            Anterior</a>
                                    </c:if>
                                    <c:forEach begin="1" end="${totalPaginas}" var="p">
                                        <a href="${pageContext.request.contextPath}/fretes/listar?pagina=${p}&filtro=${filtro}"
                                            class="${p == paginaAtual ? 'ativa' : ''}">${p}</a>
                                    </c:forEach>
                                    <c:if test="${paginaAtual < totalPaginas}">
                                        <a
                                            href="${pageContext.request.contextPath}/fretes/listar?pagina=${paginaAtual + 1}&filtro=${filtro}">Próxima
                                            →</a>
                                    </c:if>
                                    <span>Total: ${total} fretes</span>
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </body>

        </html>