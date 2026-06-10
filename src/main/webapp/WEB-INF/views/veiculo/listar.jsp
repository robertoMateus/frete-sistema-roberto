<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Veículos — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header-lista">
                    <h2>Veículos</h2>
                    <a href="${pageContext.request.contextPath}/veiculos/novo" class="btn btn-primary">+ Novo
                        Veículo</a>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <c:if test="${not empty param.sucesso}">
                    <div class="mensagem-sucesso">
                        <c:choose>
                            <c:when test="${param.sucesso == 'cadastrado'}">Veículo cadastrado com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'atualizado'}">Veículo atualizado com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'disponivel'}">Veículo marcado como disponível com sucesso.
                            </c:when>
                            <c:when test="${param.sucesso == 'manutencao'}">Veículo enviado para manutenção com sucesso.
                            </c:when>
                            <c:when test="${param.sucesso == 'excluido'}">Veículo excluído com sucesso.</c:when>
                        </c:choose>
                    </div>
                </c:if>

                <form method="get" action="${pageContext.request.contextPath}/veiculos/listar" class="filtro-form">
                    <input type="text" name="filtro" value="${filtro}" placeholder="Buscar por placa ou RNTRC..." />
                    <button type="submit" class="btn btn-primary">Buscar</button>
                    <c:if test="${not empty filtro}">
                        <a href="${pageContext.request.contextPath}/veiculos/listar"
                            class="btn btn-secondary">Limpar</a>
                    </c:if>
                </form>

                <div class="secao">
                    <c:choose>
                        <c:when test="${empty veiculos}">
                            <div class="sem-dados">Nenhum veículo encontrado.</div>
                        </c:when>
                        <c:otherwise>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Placa</th>
                                        <th>Tipo</th>
                                        <th>Ano</th>
                                        <th>RNTRC</th>
                                        <th>Capacidade (kg)</th>
                                        <th>Volume (m³)</th>
                                        <th>Status</th>
                                        <th>Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="veiculo" items="${veiculos}">
                                        <tr>
                                            <td>${veiculo.placa}</td>
                                            <td>${veiculo.tipoVeiculo.descricao}</td>
                                            <td>${not empty veiculo.anoFabricacao ? veiculo.anoFabricacao : '—'}</td>
                                            <td>${not empty veiculo.rntrc ? veiculo.rntrc : '—'}</td>
                                            <td>${veiculo.capacidadeCarga} kg</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty veiculo.volume}">${veiculo.volume} m³
                                                    </c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${veiculo.status.name() == 'DISPONIVEL'}">
                                                        <span
                                                            class="badge badge-ativo">${veiculo.status.descricao}</span>
                                                    </c:when>
                                                    <c:when test="${veiculo.status.name() == 'EM_VIAGEM'}">
                                                        <span
                                                            class="badge badge-em-transito">${veiculo.status.descricao}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span
                                                            class="badge badge-manutencao">${veiculo.status.descricao}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="acoes">
                                                    <a href="${pageContext.request.contextPath}/veiculos/editar?id=${veiculo.id}"
                                                        class="btn btn-secondary btn-sm">Editar</a>
                                                    <c:if test="${veiculo.status.name() == 'EM_MANUTENCAO'}">
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/veiculos/disponivel"
                                                            style="display:inline"
                                                            onsubmit="return confirm('Deseja marcar este veículo como disponível?')">
                                                            <input type="hidden" name="id" value="${veiculo.id}" />
                                                            <button type="submit"
                                                                class="btn btn-secondary btn-sm">Disponível</button>
                                                        </form>
                                                    </c:if>
                                                    <c:if test="${veiculo.status.name() == 'DISPONIVEL'}">
                                                        <a href="${pageContext.request.contextPath}/manutencoes/novo?idVeiculo=${veiculo.id}"
                                                            class="btn btn-warning btn-sm">Manutenção</a>
                                                    </c:if>
                                                    <form method="post"
                                                        action="${pageContext.request.contextPath}/veiculos/excluir"
                                                        style="display:inline"
                                                        onsubmit="return confirm('Deseja excluir este veículo?')">
                                                        <input type="hidden" name="id" value="${veiculo.id}" />
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
                                            href="${pageContext.request.contextPath}/veiculos/listar?pagina=${paginaAtual - 1}&filtro=${filtro}">←
                                            Anterior</a>
                                    </c:if>
                                    <c:forEach begin="1" end="${totalPaginas}" var="p">
                                        <a href="${pageContext.request.contextPath}/veiculos/listar?pagina=${p}&filtro=${filtro}"
                                            class="${p == paginaAtual ? 'ativa' : ''}">${p}</a>
                                    </c:forEach>
                                    <c:if test="${paginaAtual < totalPaginas}">
                                        <a
                                            href="${pageContext.request.contextPath}/veiculos/listar?pagina=${paginaAtual + 1}&filtro=${filtro}">Próxima
                                            →</a>
                                    </c:if>
                                    <span>Total: ${total} veículos</span>
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </body>

        </html>