<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Motoristas — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header-lista">
                    <h2>Motoristas</h2>
                    <a href="${pageContext.request.contextPath}/motoristas/novo" class="btn btn-primary">+ Novo
                        Motorista</a>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <c:if test="${not empty param.sucesso}">
                    <div class="mensagem-sucesso">
                        <c:choose>
                            <c:when test="${param.sucesso == 'cadastrado'}">Motorista cadastrado com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'atualizado'}">Motorista atualizado com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'inativado'}">Motorista inativado com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'ativado'}">Motorista ativado com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'suspenso'}">Motorista suspenso com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'excluido'}">Motorista excluído com sucesso.</c:when>
                        </c:choose>
                    </div>
                </c:if>

                <form method="get" action="${pageContext.request.contextPath}/motoristas/listar" class="filtro-form">
                    <input type="text" name="filtro" value="${filtro}" placeholder="Buscar por nome ou CPF..." />
                    <button type="submit" class="btn btn-primary">Buscar</button>
                    <c:if test="${not empty filtro}">
                        <a href="${pageContext.request.contextPath}/motoristas/listar"
                            class="btn btn-secondary">Limpar</a>
                    </c:if>
                </form>

                <div class="secao">
                    <c:choose>
                        <c:when test="${empty motoristas}">
                            <div class="sem-dados">Nenhum motorista encontrado.</div>
                        </c:when>
                        <c:otherwise>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Nome</th>
                                        <th>CPF</th>
                                        <th>Telefone</th>
                                        <th>CNH</th>
                                        <th>Validade CNH</th>
                                        <th>Vínculo</th>
                                        <th>Status</th>
                                        <th>Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="motorista" items="${motoristas}">
                                        <tr>
                                            <td>${motorista.nome}</td>
                                            <td>${motorista.cpfFormatado}</td>
                                            <td>${not empty motorista.telefone ? motorista.telefone : '—'}</td>
                                            <td>${motorista.numeroCnh} — ${motorista.categoriaCnh.name()}</td>
                                            <td>
                                                ${motorista.dataValidadeCnhFormatada}
                                                <c:if
                                                    test="${motorista.dataValidadeCnh != null and now != null and motorista.dataValidadeCnh.isBefore(now)}">
                                                    <span class="badge badge-cnh-vencida">Vencida</span>
                                                </c:if>
                                            </td>
                                            <td>${motorista.tipoVinculo.descricao}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${motorista.status.name() == 'ATIVO'}">
                                                        <span
                                                            class="badge badge-ativo">${motorista.status.descricao}</span>
                                                    </c:when>
                                                    <c:when test="${motorista.status.name() == 'SUSPENSO'}">
                                                        <span
                                                            class="badge badge-suspenso">${motorista.status.descricao}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span
                                                            class="badge badge-inativo">${motorista.status.descricao}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="acoes">
                                                    <a href="${pageContext.request.contextPath}/motoristas/editar?id=${motorista.id}"
                                                        class="btn btn-secondary btn-sm">Editar</a>
                                                    <c:choose>
                                                        <c:when test="${motorista.status.name() == 'ATIVO'}">
                                                            <form method="post"
                                                                action="${pageContext.request.contextPath}/motoristas/inativar"
                                                                style="display:inline"
                                                                onsubmit="return confirm('Deseja inativar este motorista?')">
                                                                <input type="hidden" name="id"
                                                                    value="${motorista.id}" />
                                                                <button type="submit"
                                                                    class="btn btn-secondary btn-sm">Inativar</button>
                                                            </form>
                                                            <form method="post"
                                                                action="${pageContext.request.contextPath}/motoristas/suspender"
                                                                style="display:inline"
                                                                onsubmit="return confirm('Deseja suspender este motorista?')">
                                                                <input type="hidden" name="id"
                                                                    value="${motorista.id}" />
                                                                <button type="submit"
                                                                    class="btn btn-warning btn-sm">Suspender</button>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form method="post"
                                                                action="${pageContext.request.contextPath}/motoristas/ativar"
                                                                style="display:inline"
                                                                onsubmit="return confirm('Deseja ativar este motorista?')">
                                                                <input type="hidden" name="id"
                                                                    value="${motorista.id}" />
                                                                <button type="submit"
                                                                    class="btn btn-primary btn-sm">Ativar</button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <form method="post"
                                                        action="${pageContext.request.contextPath}/motoristas/excluir"
                                                        style="display:inline"
                                                        onsubmit="return confirm('Deseja excluir este motorista?')">
                                                        <input type="hidden" name="id" value="${motorista.id}" />
                                                        <button type="submit"
                                                            class="btn btn-danger btn-sm">Excluir</button>
                                                    </form>
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
                                            href="${pageContext.request.contextPath}/motoristas/listar?pagina=${paginaAtual - 1}&filtro=${filtro}">←
                                            Anterior</a>
                                    </c:if>
                                    <c:forEach begin="1" end="${totalPaginas}" var="p">
                                        <a href="${pageContext.request.contextPath}/motoristas/listar?pagina=${p}&filtro=${filtro}"
                                            class="${p == paginaAtual ? 'ativa' : ''}">${p}</a>
                                    </c:forEach>
                                    <c:if test="${paginaAtual < totalPaginas}">
                                        <a
                                            href="${pageContext.request.contextPath}/motoristas/listar?pagina=${paginaAtual + 1}&filtro=${filtro}">Próxima
                                            →</a>
                                    </c:if>
                                    <span>Total: ${total} motoristas</span>
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </body>

        </html>