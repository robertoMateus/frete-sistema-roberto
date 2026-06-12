<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Motoristas — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
            <style>
                .modal-overlay {
                    display: none;
                    position: fixed;
                    inset: 0;
                    background: rgba(0, 0, 0, 0.45);
                    z-index: 1000;
                    align-items: center;
                    justify-content: center;
                }

                .modal-overlay.aberto {
                    display: flex;
                }

                .modal-box {
                    background: #fff;
                    border-radius: 8px;
                    padding: 28px 32px;
                    min-width: 320px;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
                }

                .modal-box h3 {
                    margin: 0 0 6px 0;
                    font-size: 1.1rem;
                    color: #1A3A5C;
                }

                .modal-box p {
                    margin: 0 0 18px 0;
                    color: #555;
                    font-size: 0.9rem;
                }

                .modal-box label {
                    display: block;
                    font-size: 0.85rem;
                    font-weight: bold;
                    margin-bottom: 6px;
                    color: #333;
                }

                .modal-box input[type="date"] {
                    width: 100%;
                    padding: 8px 10px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                    font-size: 0.95rem;
                    margin-bottom: 12px;
                    box-sizing: border-box;
                }

                .modal-acoes {
                    display: flex;
                    gap: 10px;
                    justify-content: flex-end;
                    margin-top: 8px;
                }

                .erro-modal {
                    display: none;
                    background: #fff3f3;
                    border: 1px solid #f5c6cb;
                    color: #721c24;
                    border-radius: 4px;
                    padding: 8px 12px;
                    font-size: 0.85rem;
                    margin-bottom: 12px;
                }
            </style>
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

                                                    <button type="button" class="btn btn-secondary btn-sm"
                                                        onclick="abrirModalRomaneio('${motorista.id}', '${motorista.nome}')">
                                                        Romaneio
                                                    </button>

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

            <!-- ── Modal Romaneio ── -->
            <div class="modal-overlay" id="modalRomaneio">
                <div class="modal-box">
                    <h3>Gerar Romaneio</h3>
                    <p id="modalNomeMotorista"></p>
                    <label for="dataRomaneio">Data do romaneio:</label>
                    <input type="date" id="dataRomaneio" />
                    <div class="erro-modal" id="erroRomaneio"></div>
                    <div class="modal-acoes">
                        <button type="button" class="btn btn-secondary"
                            onclick="fecharModalRomaneio()">Cancelar</button>
                        <button type="button" class="btn btn-primary" id="btnGerarRomaneio"
                            onclick="gerarRomaneio()">Gerar PDF</button>
                    </div>
                </div>
            </div>

            <script>
                var idMotoristaAtual = null;
                var contextPath = '${pageContext.request.contextPath}';

                function abrirModalRomaneio(id, nome) {
                    idMotoristaAtual = id;
                    document.getElementById('modalNomeMotorista').textContent = 'Motorista: ' + nome;
                    document.getElementById('dataRomaneio').value = new Date().toISOString().substring(0, 10);
                    ocultarErroModal();
                    document.getElementById('modalRomaneio').classList.add('aberto');
                }

                function fecharModalRomaneio() {
                    idMotoristaAtual = null;
                    document.getElementById('modalRomaneio').classList.remove('aberto');
                }

                function gerarRomaneio() {
                    var data = document.getElementById('dataRomaneio').value;
                    if (!data) {
                        mostrarErroModal('Informe a data do romaneio.');
                        return;
                    }

                    var url = contextPath + '/relatorios/romaneio?idMotorista=' + idMotoristaAtual + '&data=' + data;
                    var btn = document.getElementById('btnGerarRomaneio');

                    ocultarErroModal();
                    btn.disabled = true;
                    btn.textContent = 'Verificando...';

                    fetch(url)
                        .then(function (resp) {
                            var contentType = resp.headers.get('Content-Type') || '';
                            if (resp.ok && contentType.includes('application/pdf')) {
                                window.open(url, '_blank');
                                fecharModalRomaneio();
                            } else {
                                return resp.text().then(function (html) {
                                    var match = html.match(/id="mensagem-erro-romaneio"[^>]*>([^<]+)</);
                                    var msg = match ? match[1].trim()
                                        : 'Nenhum frete encontrado para a data informada.';
                                    mostrarErroModal(msg);
                                });
                            }
                        })
                        .catch(function () {
                            mostrarErroModal('Erro de comunicação. Tente novamente.');
                        })
                        .finally(function () {
                            btn.disabled = false;
                            btn.textContent = 'Gerar PDF';
                        });
                }

                function mostrarErroModal(msg) {
                    var el = document.getElementById('erroRomaneio');
                    el.textContent = msg;
                    el.style.display = 'block';
                }

                function ocultarErroModal() {
                    var el = document.getElementById('erroRomaneio');
                    el.textContent = '';
                    el.style.display = 'none';
                }

                document.getElementById('modalRomaneio').addEventListener('click', function (e) {
                    if (e.target === this) fecharModalRomaneio();
                });
            </script>

        </body>

        </html>