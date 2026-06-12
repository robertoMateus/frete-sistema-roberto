<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${not empty manutencao.id ? 'Editar' : 'Nova'} Manutenção — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header-lista">
                    <div style="display:flex; align-items:center; gap:16px;">
                        <a href="${pageContext.request.contextPath}/manutencoes/listar<c:if test=" ${not empty
                            veiculo}">?idVeiculo=${veiculo.id}</c:if>"
                            class="btn btn-secondary">← Voltar</a>
                        <h2>${not empty manutencao.id ? 'Editar Manutenção' : 'Nova Manutenção'}</h2>
                    </div>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <div class="secao">
                    <div style="padding: 28px 32px;">
                        <form method="post"
                            action="${pageContext.request.contextPath}/manutencoes/${not empty manutencao.id ? 'editar' : 'novo'}">

                            <c:if test="${not empty manutencao.id}">
                                <input type="hidden" name="id" value="${manutencao.id}" />
                            </c:if>

                            <div class="form-row col-2">
                                <div class="form-group">
                                    <label for="idVeiculo">Veículo <span class="obrigatorio">*</span></label>
                                    <c:choose>
                                        <c:when test="${not empty veiculo}">
                                            <input type="text" value="${veiculo.placa}" disabled />
                                            <input type="hidden" name="idVeiculo" value="${veiculo.id}" />
                                        </c:when>
                                        <c:otherwise>
                                            <select id="idVeiculo" name="idVeiculo" required>
                                                <option value="">Selecione o veículo...</option>
                                                <c:forEach var="v" items="${veiculos}">
                                                    <option value="${v.id}" ${manutencao.veiculo.id==v.id ? 'selected'
                                                        : '' }>
                                                        ${v.placa} — ${v.tipoVeiculo.descricao}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="form-group">
                                    <label for="tipo">Tipo <span class="obrigatorio">*</span></label>
                                    <select id="tipo" name="tipo" required>
                                        <option value="">Selecione...</option>
                                        <option value="PREVENTIVA" ${manutencao.tipo.name()=='PREVENTIVA' ? 'selected'
                                            : '' }>Preventiva</option>
                                        <option value="CORRETIVA" ${manutencao.tipo.name()=='CORRETIVA' ? 'selected'
                                            : '' }>Corretiva</option>
                                    </select>
                                </div>
                            </div>

                            <div class="form-row col-2">
                                <div class="form-group">
                                    <label for="dataInicio">Data de Início <span class="obrigatorio">*</span></label>
                                    <input type="date" id="dataInicio" name="dataInicio"
                                        value="${manutencao.dataInicioISO}" required />
                                </div>

                                <div class="form-group">
                                    <label for="dataFim">Data de Conclusão</label>
                                    <input type="date" id="dataFim" name="dataFim" value="${manutencao.dataFimISO}" />
                                    <small class="campo-dica">Deixe em branco para manutenção em andamento.</small>
                                </div>
                            </div>

                            <div class="form-row col-2">
                                <div class="form-group">
                                    <label for="custo">Custo (R$)</label>
                                    <input type="text" id="custo" name="custo" placeholder="0,00"
                                        value="${manutencao.custo}" autocomplete="off" />
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="descricao">Descrição</label>
                                    <textarea id="descricao" name="descricao" rows="4" maxlength="500"
                                        placeholder="Descreva o serviço realizado ou a ser realizado...">${manutencao.descricao}</textarea>
                                    <small class="campo-dica">Máximo 500 caracteres.</small>
                                </div>
                            </div>

                            <div class="form-acoes">
                                <a href="${pageContext.request.contextPath}/manutencoes/listar<c:if test=" ${not empty
                                    veiculo}">?idVeiculo=${veiculo.id}</c:if>"
                                    class="btn btn-secondary">Cancelar</a>
                                <button type="submit" class="btn btn-primary">
                                    ${not empty manutencao.id ? 'Salvar Alterações' : 'Registrar Manutenção'}
                                </button>
                            </div>

                        </form>
                    </div>
                </div>

            </div>

            <script src="${pageContext.request.contextPath}/js/mascaras.js"></script>
            <script>
                aplicarMascaraDecimal('custo', 9);
            </script>
        </body>

        </html>