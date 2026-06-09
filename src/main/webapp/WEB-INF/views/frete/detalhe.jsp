<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Frete ${frete.numeroFrete} — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/fretes/listar" class="btn btn-secondary">← Voltar</a>
                    <h2>Frete ${frete.numeroFrete}</h2>
                    <c:choose>
                        <c:when test="${frete.status.name() == 'EMITIDO'}">
                            <span class="badge badge-emitido">${frete.status.descricao}</span>
                        </c:when>
                        <c:when test="${frete.status.name() == 'SAIDA_CONFIRMADA'}">
                            <span class="badge badge-saida-confirmada">${frete.status.descricao}</span>
                        </c:when>
                        <c:when test="${frete.status.name() == 'EM_TRANSITO'}">
                            <span class="badge badge-em-transito">${frete.status.descricao}</span>
                        </c:when>
                        <c:when test="${frete.status.name() == 'ENTREGUE'}">
                            <span class="badge badge-entregue">${frete.status.descricao}</span>
                        </c:when>
                        <c:when test="${frete.status.name() == 'NAO_ENTREGUE'}">
                            <span class="badge badge-nao-entregue">${frete.status.descricao}</span>
                        </c:when>
                        <c:when test="${frete.status.name() == 'CANCELADO'}">
                            <span class="badge badge-cancelado">${frete.status.descricao}</span>
                        </c:when>
                    </c:choose>
                </div>

                <%-- mensagens --%>
                    <c:if test="${not empty erro}">
                        <div class="erro">${erro}</div>
                    </c:if>

                    <c:if test="${not empty param.sucesso}">
                        <div class="mensagem-sucesso">
                            <c:choose>
                                <c:when test="${param.sucesso == 'saida'}">Saída confirmada com sucesso.</c:when>
                                <c:when test="${param.sucesso == 'transito'}">Frete registrado em trânsito.</c:when>
                                <c:when test="${param.sucesso == 'entregue'}">Entrega registrada com sucesso.</c:when>
                                <c:when test="${param.sucesso == 'naoEntregue'}">Não entrega registrada com sucesso.
                                </c:when>
                                <c:when test="${param.sucesso == 'cancelado'}">Frete cancelado com sucesso.</c:when>
                                <c:when test="${param.sucesso == 'ocorrencia'}">Ocorrência registrada com sucesso.
                                </c:when>
                            </c:choose>
                        </div>
                    </c:if>

                    <%-- Ações do fluxo --%>
                        <c:if
                            test="${frete.status.name() == 'EMITIDO' or frete.status.name() == 'SAIDA_CONFIRMADA' or frete.status.name() == 'EM_TRANSITO'}">
                            <div class="card">
                                <div class="card-titulo">Ações</div>
                                <div class="acoes">

                                    <c:if test="${frete.status.name() == 'EMITIDO'}">
                                        <a href="${pageContext.request.contextPath}/fretes/confirmarSaida?id=${frete.id}"
                                            class="btn btn-primary">Confirmar Saída</a>
                                        <form method="post" action="${pageContext.request.contextPath}/fretes/cancelar"
                                            style="display:inline"
                                            onsubmit="return confirm('Deseja cancelar este frete?')">
                                            <input type="hidden" name="id" value="${frete.id}" />
                                            <button type="submit" class="btn btn-danger">Cancelar Frete</button>
                                        </form>
                                    </c:if>

                                    <c:if test="${frete.status.name() == 'SAIDA_CONFIRMADA'}">
                                        <form method="post"
                                            action="${pageContext.request.contextPath}/fretes/emTransito"
                                            style="display:inline"
                                            onsubmit="return confirm('Registrar frete como Em Trânsito?')">
                                            <input type="hidden" name="id" value="${frete.id}" />
                                            <button type="submit" class="btn btn-primary">Registrar Em Trânsito</button>
                                        </form>
                                    </c:if>

                                    <c:if test="${frete.status.name() == 'EM_TRANSITO'}">
                                        <button type="button" class="btn btn-primary"
                                            onclick="abrirModalEntrega()">Registrar Entrega</button>
                                        <a href="${pageContext.request.contextPath}/fretes/naoEntregue?id=${frete.id}"
                                            class="btn btn-warning">Registrar Não Entrega</a>
                                    </c:if>

                                    <button type="button" class="btn btn-secondary" onclick="abrirModalOcorrencia()">+
                                        Registrar Ocorrência</button>

                                </div>
                            </div>
                        </c:if>

                        <%-- Partes --%>
                            <div class="card">
                                <div class="card-titulo">Partes Envolvidas</div>
                                <div class="form-row col-2">
                                    <div class="form-group">
                                        <label>Remetente</label>
                                        <input type="text" readonly value="${frete.remetente.razaoSocial}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Destinatário</label>
                                        <input type="text" readonly value="${frete.destinatario.razaoSocial}" />
                                    </div>
                                </div>
                                <div class="form-row col-2">
                                    <div class="form-group">
                                        <label>Motorista</label>
                                        <input type="text" readonly value="${frete.motorista.nome}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Veículo</label>
                                        <input type="text" readonly value="${frete.veiculo.placa}" />
                                    </div>
                                </div>
                            </div>

                            <%-- Origem e destino --%>
                                <div class="card">
                                    <div class="card-titulo">Origem e Destino</div>
                                    <div class="form-row col-2">
                                        <div class="form-group">
                                            <label>Origem</label>
                                            <input type="text" readonly
                                                value="${frete.municipioOrigem} / ${frete.ufOrigem}" />
                                        </div>
                                        <div class="form-group">
                                            <label>Destino</label>
                                            <input type="text" readonly
                                                value="${frete.municipioDestino} / ${frete.ufDestino}" />
                                        </div>
                                    </div>
                                </div>

                                <%-- Carga --%>
                                    <div class="card">
                                        <div class="card-titulo">Carga</div>
                                        <div class="form-row col-2">
                                            <div class="form-group">
                                                <label>Descrição da Carga</label>
                                                <input type="text" readonly value="${frete.descricaoCarga}" />
                                            </div>
                                            <div class="form-group">
                                                <label>Volumes</label>
                                                <input type="text" readonly value="${frete.volumeCarga}" />
                                            </div>
                                        </div>
                                        <div class="form-row col-2">
                                            <div class="form-group">
                                                <label>Peso Bruto (kg)</label>
                                                <input type="text" readonly value="${frete.pesoCarga}" />
                                            </div>
                                            <div class="form-group">
                                                <label>Data Prevista de Entrega</label>
                                                <input type="text" readonly
                                                    value="${frete.dataPrevisaoEntregaFormatada}" />
                                            </div>
                                        </div>
                                    </div>

                                    <%-- Valores --%>
                                        <div class="card">
                                            <div class="card-titulo">Valores</div>
                                            <div class="form-row col-3">
                                                <div class="form-group">
                                                    <label>Valor do Frete</label>
                                                    <input type="text" readonly value="R$ ${frete.valorFrete}" />
                                                </div>
                                                <div class="form-group">
                                                    <label>Alíquota ICMS (%)</label>
                                                    <input type="text" readonly value="${frete.aliquotaIcms}" />
                                                </div>
                                                <div class="form-group">
                                                    <label>Valor ICMS</label>
                                                    <input type="text" readonly value="R$ ${frete.valorIcms}" />
                                                </div>
                                            </div>
                                            <div class="form-row col-2">
                                                <div class="form-group">
                                                    <label>Valor Total</label>
                                                    <input type="text" readonly value="R$ ${frete.valorTotal}" />
                                                </div>
                                            </div>
                                        </div>

                                        <%-- Datas do ciclo--%>
                                            <div class="card">
                                                <div class="card-titulo">Histórico de Datas</div>
                                                <div class="form-row col-2">
                                                    <div class="form-group">
                                                        <label>Data de Emissão</label>
                                                        <input type="text" readonly
                                                            value="${frete.dataEmissaoFormatada}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label>Data de Saída</label>
                                                        <input type="text" readonly
                                                            value="${not empty frete.dataSaidaFormatada ? frete.dataSaidaFormatada : '—'}" />
                                                    </div>
                                                </div>
                                                <div class="form-row col-2">
                                                    <div class="form-group">
                                                        <label>Data de Entrega</label>
                                                        <input type="text" readonly
                                                            value="${not empty frete.dataEntregaFormatada ? frete.dataEntregaFormatada : '—'}" />
                                                    </div>
                                                    <div class="form-group">
                                                        <label>Previsão de Entrega</label>
                                                        <input type="text" readonly
                                                            value="${frete.dataPrevisaoEntregaFormatada}" />
                                                    </div>
                                                </div>
                                            </div>

                                            <%-- OCORRÊNCIAS --%>
                                                <div class="card">
                                                    <div class="card-titulo">Ocorrências</div>
                                                    <c:choose>
                                                        <c:when test="${empty ocorrencias}">
                                                            <div class="sem-dados">Nenhuma ocorrência registrada.</div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="secao"
                                                                style="box-shadow:none; margin-bottom:0;">
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
                                                                                        <c:when
                                                                                            test="${not empty oc.municipio}">
                                                                                            ${oc.municipio} / ${oc.uf}
                                                                                        </c:when>
                                                                                        <c:otherwise>—</c:otherwise>
                                                                                    </c:choose>
                                                                                </td>
                                                                                <td>
                                                                                    <c:choose>
                                                                                        <c:when
                                                                                            test="${not empty oc.descricao}">
                                                                                            ${oc.descricao}</c:when>
                                                                                        <c:otherwise>—</c:otherwise>
                                                                                    </c:choose>
                                                                                </td>
                                                                                <td>
                                                                                    <c:choose>
                                                                                        <c:when
                                                                                            test="${not empty oc.nomeRecebedor}">
                                                                                            ${oc.nomeRecebedor}</c:when>
                                                                                        <c:otherwise>—</c:otherwise>
                                                                                    </c:choose>
                                                                                </td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

            </div>

            <%-- Registrar entrega --%>
                <div id="modalEntrega" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%;
         background:rgba(0,0,0,0.45); z-index:1000; align-items:center; justify-content:center;">
                    <div
                        style="background:#fff; border-radius:8px; padding:32px; width:100%; max-width:420px; box-shadow:0 8px 32px rgba(0,0,0,0.2);">
                        <h3 style="margin-bottom:20px; font-size:1.1rem;">Registrar Entrega</h3>
                        <form method="post" action="${pageContext.request.contextPath}/fretes/entregue">
                            <input type="hidden" name="id" value="${frete.id}" />
                            <div class="form-group" style="margin-bottom:20px;">
                                <label>Data/Hora da Entrega <span class="obrigatorio">*</span></label>
                                <input type="datetime-local" name="dataEntrega" required />
                            </div>
                            <div class="form-acoes">
                                <button type="button" class="btn btn-secondary"
                                    onclick="fecharModalEntrega()">Cancelar</button>
                                <button type="submit" class="btn btn-primary">Confirmar Entrega</button>
                            </div>
                        </form>
                    </div>
                </div>

                <%--  Registrar Ocorrencia--%>
                    <div id="modalOcorrencia" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%;
         background:rgba(0,0,0,0.45); z-index:1000; align-items:center; justify-content:center;">
                        <div
                            style="background:#fff; border-radius:8px; padding:32px; width:100%; max-width:480px; box-shadow:0 8px 32px rgba(0,0,0,0.2);">
                            <h3 style="margin-bottom:20px; font-size:1.1rem;">Registrar Ocorrência</h3>
                            <form method="post" action="${pageContext.request.contextPath}/ocorrencias/novo">
                                <input type="hidden" name="idFrete" value="${frete.id}" />
                                <div class="form-row col-2" style="margin-bottom:0;">
                                    <div class="form-group" style="margin-bottom:16px;">
                                        <label>Tipo <span class="obrigatorio">*</span></label>
                                        <select name="tipo" id="tipoOcorrencia" required onchange="verificarTipo()">
                                            <option value="">Selecione</option>
                                            <option value="SAIDA_PATIO">Saída do Pátio</option>
                                            <option value="EM_ROTA">Em Rota</option>
                                            <option value="TENTATIVA_ENTREGA">Tentativa de Entrega</option>
                                            <option value="ENTREGA_REALIZADA">Entrega Realizada</option>
                                            <option value="AVARIA">Avaria</option>
                                            <option value="EXTRAVIO">Extravio</option>
                                            <option value="OUTROS">Outros</option>
                                        </select>
                                    </div>
                                    <div class="form-group" style="margin-bottom:16px;">
                                        <label>Data/Hora <span class="obrigatorio">*</span></label>
                                        <input type="datetime-local" name="dataHoraOcorrencia" required />
                                    </div>
                                </div>
                                <div class="form-row col-2" style="margin-bottom:0;">
                                    <div class="form-group" style="margin-bottom:16px;">
                                        <label>Município <span class="obrigatorio">*</span></label>
                                        <input type="text" name="municipio" maxlength="100" required />
                                    </div>
                                    <div class="form-group" style="margin-bottom:16px;">
                                        <label>UF <span class="obrigatorio">*</span></label>
                                        <select name="uf" required>
                                            <option value="">UF</option>
                                            <c:forEach var="uf"
                                                items="${['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO']}">
                                                <option value="${uf}">${uf}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group" style="margin-bottom:16px;" id="grupoDescricao">
                                    <label id="labelDescricao">Descrição</label>
                                    <textarea name="descricao" rows="3" maxlength="500"
                                        style="resize:vertical;"></textarea>
                                </div>
                                <div id="grupoRecebedor" style="display:none;">
                                    <div class="form-row col-2" style="margin-bottom:0;">
                                        <div class="form-group" style="margin-bottom:16px;">
                                            <label>Nome do Recebedor <span class="obrigatorio">*</span></label>
                                            <input type="text" name="nomeRecebedor" maxlength="150"
                                                id="nomeRecebedor" />
                                        </div>
                                        <div class="form-group" style="margin-bottom:16px;">
                                            <label>Documento do Recebedor <span class="obrigatorio">*</span></label>
                                            <input type="text" name="documentoRecebedor" maxlength="20"
                                                id="documentoRecebedor" />
                                        </div>
                                    </div>
                                </div>
                                <div class="form-acoes">
                                    <button type="button" class="btn btn-secondary"
                                        onclick="fecharModalOcorrencia()">Cancelar</button>
                                    <button type="submit" class="btn btn-primary">Salvar Ocorrência</button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <script>
                        function abrirModalEntrega() {
                            document.getElementById('modalEntrega').style.display = 'flex';
                        }
                        function fecharModalEntrega() {
                            document.getElementById('modalEntrega').style.display = 'none';
                        }
                        function abrirModalOcorrencia() {
                            document.getElementById('modalOcorrencia').style.display = 'flex';
                        }
                        function fecharModalOcorrencia() {
                            document.getElementById('modalOcorrencia').style.display = 'none';
                        }

                        ['modalEntrega', 'modalOcorrencia'].forEach(function (id) {
                            document.getElementById(id).addEventListener('click', function (e) {
                                if (e.target === this) this.style.display = 'none';
                            });
                        });

                        function verificarTipo() {
                            const tipo = document.getElementById('tipoOcorrencia').value;
                            const grupoRecebedor = document.getElementById('grupoRecebedor');
                            const labelDescricao = document.getElementById('labelDescricao');
                            const nomeRecebedor = document.getElementById('nomeRecebedor');
                            const documentoRecebedor = document.getElementById('documentoRecebedor');
                            const descricao = document.querySelector('[name="descricao"]');

                            const tiposObrigatorios = ['AVARIA', 'EXTRAVIO', 'OUTROS'];

                            if (tiposObrigatorios.includes(tipo)) {
                                labelDescricao.innerHTML = 'Descrição <span class="obrigatorio">*</span>';
                                descricao.required = true;
                            } else {
                                labelDescricao.innerHTML = 'Descrição';
                                descricao.required = false;
                            }

                            if (tipo === 'ENTREGA_REALIZADA') {
                                grupoRecebedor.style.display = 'block';
                                nomeRecebedor.required = true;
                                documentoRecebedor.required = true;
                            } else {
                                grupoRecebedor.style.display = 'none';
                                nomeRecebedor.required = false;
                                documentoRecebedor.required = false;
                            }
                        }
                    </script>

        </body>

        </html>