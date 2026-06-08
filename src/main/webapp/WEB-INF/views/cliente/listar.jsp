<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clientes — GW Gestão de Fretes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
</head>
<body>

<jsp:include page="/WEB-INF/views/components/header.jsp" />

<div class="container">

    <div class="page-header-lista">
        <h2>Clientes</h2>
        <a href="${pageContext.request.contextPath}/cliente/novo" class="btn btn-primary">+ Novo Cliente</a>
    </div>

    <c:if test="${not empty erro}">
        <div class="erro">${erro}</div>
    </c:if>

    <c:if test="${not empty param.sucesso}">
        <div class="mensagem-sucesso">
            <c:choose>
                <c:when test="${param.sucesso == 'cadastrado'}">Cliente cadastrado com sucesso.</c:when>
                <c:when test="${param.sucesso == 'atualizado'}">Cliente atualizado com sucesso.</c:when>
                <c:when test="${param.sucesso == 'inativado'}">Cliente inativado com sucesso.</c:when>
                <c:when test="${param.sucesso == 'excluido'}">Cliente excluído com sucesso.</c:when>
            </c:choose>
        </div>
    </c:if>

    <form method="get" action="${pageContext.request.contextPath}/cliente/listar" class="filtro-form">
        <input type="text" name="filtro" value="${filtro}" placeholder="Buscar por razão social, nome fantasia ou CNPJ..." />
        <button type="submit" class="btn btn-primary">Buscar</button>
        <c:if test="${not empty filtro}">
            <a href="${pageContext.request.contextPath}/cliente/listar" class="btn btn-secondary">Limpar</a>
        </c:if>
    </form>

    <div class="secao">
        <c:choose>
            <c:when test="${empty clientes}">
                <div class="sem-dados">Nenhum cliente encontrado.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>Razão Social</th>
                            <th>Nome Fantasia</th>
                            <th>CNPJ</th>
                            <th>Município/UF</th>
                            <th>Telefone</th>
                            <th>Status</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cliente" items="${clientes}">
                            <tr>
                                <td>${cliente.razaoSocial}</td>
                                <td>${not empty cliente.nomeFantasia ? cliente.nomeFantasia : '—'}</td>
                                <td>${cliente.cnpjFormatado}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty cliente.municipio and not empty cliente.uf}">
                                            ${cliente.municipio}/${cliente.uf}
                                        </c:when>
                                        <c:when test="${not empty cliente.municipio}">
                                            ${cliente.municipio}
                                        </c:when>
                                        <c:when test="${not empty cliente.uf}">
                                            ${cliente.uf}
                                        </c:when>
                                        <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${not empty cliente.telefone ? cliente.telefone : '—'}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cliente.status.name() == 'ATIVO'}">
                                            <span class="badge badge-ativo">${cliente.status.descricao}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-inativo">${cliente.status.descricao}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="acoes">
                                        <a href="${pageContext.request.contextPath}/cliente/editar?id=${cliente.id}"
                                           class="btn btn-secondary btn-sm">Editar</a>
                                        <c:if test="${cliente.status.name() == 'ATIVO'}">
                                            <form method="post" action="${pageContext.request.contextPath}/cliente/inativar"
                                                  style="display:inline"
                                                  onsubmit="return confirm('Deseja inativar este cliente?')">
                                                <input type="hidden" name="id" value="${cliente.id}" />
                                                <button type="submit" class="btn btn-secondary btn-sm">Inativar</button>
                                            </form>
                                        </c:if>
                                        <form method="post" action="${pageContext.request.contextPath}/cliente/excluir"
                                              style="display:inline"
                                              onsubmit="return confirm('Deseja excluir este cliente?')">
                                            <input type="hidden" name="id" value="${cliente.id}" />
                                            <button type="submit" class="btn btn-danger btn-sm">Excluir</button>
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
                            <a href="${pageContext.request.contextPath}/cliente/listar?pagina=${paginaAtual - 1}&filtro=${filtro}">← Anterior</a>
                        </c:if>
                        <c:forEach begin="1" end="${totalPaginas}" var="p">
                            <a href="${pageContext.request.contextPath}/cliente/listar?pagina=${p}&filtro=${filtro}"
                               class="${p == paginaAtual ? 'ativa' : ''}">${p}</a>
                        </c:forEach>
                        <c:if test="${paginaAtual < totalPaginas}">
                            <a href="${pageContext.request.contextPath}/cliente/listar?pagina=${paginaAtual + 1}&filtro=${filtro}">Próxima →</a>
                        </c:if>
                        <span>Total: ${total} clientes</span>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>

</div>
</body>
</html>