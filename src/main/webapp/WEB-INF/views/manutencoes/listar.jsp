<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Manutenções — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header-lista">
                    <c:choose>
                        <c:when test="${not empty veiculo}">
                            <div style="display:flex; align-items:center; gap:16px;">
                                <a href="${pageContext.request.contextPath}/veiculos/listar" class="btn btn-secondary">←
                                    Voltar</a>
                                <h2>Manutenções — ${veiculo.placa}</h2>
                            </div>
                            <a href="${pageContext.request.contextPath}/manutencoes/novo?idVeiculo=${veiculo.id}"
                                class="btn btn-primary">+ Nova Manutenção</a>
                        </c:when>
                        <c:otherwise>
                            <h2>Manutenções em Aberto</h2>
                            <a href="${pageContext.request.contextPath}/manutencoes/novo" class="btn btn-primary">+ Nova
                                Manutenção</a>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <c:if test="${not empty param.sucesso}">
                    <div class="mensagem-sucesso">
                        <c:choose>
                            <c:when test="${param.sucesso == 'registrado'}">Manutenção registrada com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'atualizado'}">Manutenção atualizada com sucesso.</c:when>
                            <c:when test="${param.sucesso == 'concluido'}">Manutenção concluída com sucesso. Veículo
                                retornou para Disponível.</c:when>
                        </c:choose>
                    </div>
                </c:if>

                <div class="secao">
                    <c:choose>
                        <c:when test="${empty manutencoes}">
                            <div class="sem-dados">Nenhuma manutenção encontrada.</div>
                        </c:when>
                        <c:otherwise>
                            <table>
                                <thead>
                                    <tr>
                                        <c:if test="${empty veiculo}">
                                            <th>Veículo</th>
                                        </c:if>
                                        <th>Tipo</th>
                                        <th>Descrição</th>
                                        <th>Data Início</th>
                                        <th>Data Fim</th>
                                        <th>Custo (R$)</th>
                                        <th>Status</th>
                                        <th>Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="m" items="${manutencoes}">
                                        <tr>
                                            <c:if test="${empty veiculo}">
                                                <td>${m.veiculo.placa}</td>
                                            </c:if>
                                            <td>
                                                <span
                                                    class="badge ${m.tipo.name() == 'PREVENTIVA' ? 'badge-preventiva' : 'badge-corretiva'}">
                                                    ${m.tipo.descricao}
                                                </span>
                                            </td>
                                            <td>${not empty m.descricao ? m.descricao : '—'}</td>
                                            <td>${m.dataInicioFormatada}</td>
                                            <td>${m.dataFimFormatada}</td>
                                            <td>${not empty m.custo ? m.custo : '—'}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty m.dataFim}">
                                                        <span class="badge badge-manutencao">Em andamento</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-ativo">Concluída</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="acoes">
                                                    <c:if test="${empty m.dataFim}">
                                                        <a href="${pageContext.request.contextPath}/manutencoes/editar?id=${m.id}"
                                                            class="btn btn-secondary btn-sm">Editar</a>
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/manutencoes/concluir"
                                                            style="display:inline"
                                                            onsubmit="return confirm('Deseja concluir esta manutenção? O veículo retornará para Disponível.')">
                                                            <input type="hidden" name="id" value="${m.id}" />
                                                            <button type="submit"
                                                                class="btn btn-primary btn-sm">Concluir</button>
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
                                            href="${pageContext.request.contextPath}/manutencoes/listar?pagina=${paginaAtual - 1}<c:if test="${not empty veiculo}">&idVeiculo=${veiculo.id}</c:if>">←
                                            Anterior</a>
                                    </c:if>
                                    <c:forEach begin="1" end="${totalPaginas}" var="p">
                                        <a href="${pageContext.request.contextPath}/manutencoes/listar?pagina=${p}<c:if test="${not empty veiculo}">&idVeiculo=${veiculo.id}</c:if>"
                                            class="${p == paginaAtual ? 'ativa' : ''}">${p}</a>
                                    </c:forEach>
                                    <c:if test="${paginaAtual < totalPaginas}">
                                        <a
                                            href="${pageContext.request.contextPath}/manutencoes/listar?pagina=${paginaAtual + 1}<c:if test="${not empty veiculo}">&idVeiculo=${veiculo.id}</c:if>">Próxima
                                            →</a>
                                    </c:if>
                                    <span>Total: ${total} manutenção<c:if test="${total != 1}">ções</c:if></span>
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </body>

        </html>