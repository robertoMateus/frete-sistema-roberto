<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${empty veiculo.id ? 'Novo Veículo' : 'Editar Veículo'} — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/veiculos/listar" class="btn btn-secondary">← Voltar</a>
                    <h2>${empty veiculo.id ? 'Novo Veículo' : 'Editar Veículo'}</h2>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <form method="post"
                    action="${pageContext.request.contextPath}/veiculos/${empty veiculo.id ? 'novo' : 'editar'}">

                    <c:if test="${not empty veiculo.id}">
                        <input type="hidden" name="id" value="${veiculo.id}" />
                        <input type="hidden" name="status" value="${veiculo.status.name()}" />
                    </c:if>

                    <%-- IDENTIFICAÇÃO --%>
                        <div class="card">
                            <div class="card-titulo">Identificação</div>

                            <div class="form-row col-3">
                                <div class="form-group">
                                    <label>Placa <span class="obrigatorio">*</span></label>
                                    <input type="text" name="placa" maxlength="8" placeholder="ABC1D23 ou ABC1234"
                                        value="${veiculo.placa}" required />
                                </div>
                                <div class="form-group">
                                    <label>RNTRC <span class="obrigatorio">*</span></label>
                                    <input type="text" name="rntrc" maxlength="20" value="${veiculo.rntrc}" required />
                                </div>
                                <div class="form-group">
                                    <label>Ano de Fabricação <span class="obrigatorio">*</span></label>
                                    <input type="text" name="anoFabricacao" id="anoFabricacao" maxlength="4"
                                        placeholder="Ex: 2018" value="${veiculo.anoFabricacao}" required/>
                                </div>
                            </div>

                            <div class="form-row ${not empty veiculo.id ? 'col-2' : 'col-1'}">
                                <div class="form-group">
                                    <label>Tipo <span class="obrigatorio">*</span></label>
                                    <select name="tipoVeiculo" required>
                                        <option value="">Selecione</option>
                                        <option value="TRUCK" ${veiculo.tipoVeiculo.name()=='TRUCK' ? 'selected' : '' }>
                                            Truck</option>
                                        <option value="CARRETA" ${veiculo.tipoVeiculo.name()=='CARRETA' ? 'selected'
                                            : '' }>Carreta</option>
                                        <option value="VAN" ${veiculo.tipoVeiculo.name()=='VAN' ? 'selected' : '' }>Van
                                        </option>
                                        <option value="UTILITARIO" ${veiculo.tipoVeiculo.name()=='UTILITARIO'
                                            ? 'selected' : '' }>Utilitário</option>
                                    </select>
                                </div>
                                <c:if test="${not empty veiculo.id}">
                                    <div class="form-group">
                                        <label>Status</label>
                                        <input type="text" value="${veiculo.status.descricao}" readonly />
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <%-- CAPACIDADE --%>
                            <div class="card">
                                <div class="card-titulo">Capacidade</div>

                                <div class="form-row col-3">
                                    <div class="form-group">
                                        <label>Tara (kg) <span class="obrigatorio">*</span></label>
                                        <input id="tara" type="text" inputmode="decimal" name="tara" pattern="[0-9.,]*" maxlength="10"
                                            placeholder="Ex: 8500" value="${veiculo.tara}" required/>
                                    </div>
                                    <div class="form-group">
                                        <label>Capacidade de Carga (kg) <span class="obrigatorio">*</span></label>
                                        <input id="capacidadeCarga" type="text" inputmode="decimal" name="capacidadeCarga"
                                            pattern="[0-9.,]*" maxlength="10" placeholder="Ex: 27000" value="${veiculo.capacidadeCarga}"
                                            required />
                                    </div>
                                    <div class="form-group">
                                        <label>Volume (m³) <span class="obrigatorio">*</span></label>
                                        <input id="volume" type="text" inputmode="decimal" name="volume" pattern="[0-9.,]*"
                                            maxlength="10" placeholder="Ex: 90,00" value="${veiculo.volume}" required/>
                                    </div>
                                </div>
                            </div>

                            <div class="form-acoes">
                                <a href="${pageContext.request.contextPath}/veiculos/listar"
                                    class="btn btn-secondary">Cancelar</a>
                                <button type="submit" class="btn btn-primary">
                                    ${empty veiculo.id ? 'Cadastrar' : 'Salvar Alterações'}
                                </button>
                            </div>

                </form>
            </div>

            <script src="${pageContext.request.contextPath}/js/mascaras.js"></script>
            <script>
                aplicarMascaraAno('anoFabricacao');
                aplicarMascaraDecimal('tara', 10);
                aplicarMascaraDecimal('capacidadeCarga', 10);
                aplicarMascaraDecimal('volume', 10);
            </script>

        </body>


        </html>