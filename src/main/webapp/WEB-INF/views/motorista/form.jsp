<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${empty motorista.id ? 'Novo Motorista' : 'Editar Motorista'} — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/motoristas/listar" class="btn btn-secondary">←
                        Voltar</a>
                    <h2>${empty motorista.id ? 'Novo Motorista' : 'Editar Motorista'}</h2>
                </div>

                <c:if test="${cnhVencida}">
                    <div class="alerta">A CNH deste motorista está vencida. Ele não poderá ser atribuído a novos fretes.
                    </div>
                </c:if>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <form method="post"
                    action="${pageContext.request.contextPath}/motoristas/${empty motorista.id ? 'novo' : 'editar'}">

                    <c:if test="${not empty motorista.id}">
                        <input type="hidden" name="id" value="${motorista.id}" />
                        <input type="hidden" name="status" value="${motorista.status.name()}" />
                    </c:if>

                    <%-- DADOS PESSOAIS --%>
                        <div class="card">
                            <div class="card-titulo">Dados Pessoais</div>

                            <div class="form-row col-2">
                                <div class="form-group">
                                    <label>Nome <span class="obrigatorio">*</span></label>
                                    <input type="text" name="nome" maxlength="150" value="${motorista.nome}" required />
                                </div>
                                <div class="form-group">
                                    <label>CPF <span class="obrigatorio">*</span></label>
                                    <input type="text" name="cpf" id="cpf" maxlength="14" placeholder="000.000.000-00"
                                        value="${motorista.cpfFormatado}" required />
                                </div>
                            </div>

                            <div class="form-row col-3">
                                <div class="form-group">
                                    <label>Data de Nascimento <span class="obrigatorio">*</span></label>
                                    <input type="date" name="dataNascimento" value="${motorista.dataNascimento}"
                                        required />
                                </div>
                                <div class="form-group">
                                    <label>Telefone <span class="obrigatorio">*</span></label>
                                    <input type="text" name="telefone" id="telefone" maxlength="20" required
                                        placeholder="(00) 00000-0000" value="${motorista.telefone}" />
                                </div>
                                <c:if test="${not empty motorista.id}">
                                    <div class="form-group">
                                        <label>Status</label>
                                        <input type="text" value="${motorista.status.descricao}" readonly />
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <%-- DADOS DA CNH --%>
                            <div class="card">
                                <div class="card-titulo">CNH</div>

                                <div class="form-row col-3">
                                    <div class="form-group">
                                        <label>Número da CNH <span class="obrigatorio">*</span></label>
                                        <input type="text" name="numeroCnh" maxlength="20"
                                            value="${motorista.numeroCnh}" required />
                                    </div>
                                    <div class="form-group">
                                        <label>Categoria <span class="obrigatorio">*</span></label>
                                        <select name="categoriaCnh" required>
                                            <option value="">Selecione</option>
                                            <option value="A" ${motorista.categoriaCnh.name()=='A' ? 'selected' : '' }>A
                                            </option>
                                            <option value="B" ${motorista.categoriaCnh.name()=='B' ? 'selected' : '' }>B
                                            </option>
                                            <option value="C" ${motorista.categoriaCnh.name()=='C' ? 'selected' : '' }>C
                                            </option>
                                            <option value="D" ${motorista.categoriaCnh.name()=='D' ? 'selected' : '' }>D
                                            </option>
                                            <option value="E" ${motorista.categoriaCnh.name()=='E' ? 'selected' : '' }>E
                                            </option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>Validade da CNH <span class="obrigatorio">*</span></label>
                                        <input type="date" name="dataValidadeCnh" value="${motorista.dataValidadeCnh}"
                                            required />
                                    </div>
                                </div>
                            </div>

                            <%-- VÍNCULO --%>
                                <div class="card">
                                    <div class="card-titulo">Vínculo</div>

                                    <div class="form-row col-2">
                                        <div class="form-group">
                                            <label>Tipo de Vínculo <span class="obrigatorio">*</span></label>
                                            <select name="tipoVinculo" required>
                                                <option value="">Selecione</option>
                                                <option value="FUNCIONARIO"
                                                    ${motorista.tipoVinculo.name()=='FUNCIONARIO' ? 'selected' : '' }>
                                                    Funcionário</option>
                                                <option value="AGREGADO" ${motorista.tipoVinculo.name()=='AGREGADO'
                                                    ? 'selected' : '' }>Agregado</option>
                                                <option value="TERCEIRO" ${motorista.tipoVinculo.name()=='TERCEIRO'
                                                    ? 'selected' : '' }>Terceiro</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-acoes">
                                    <a href="${pageContext.request.contextPath}/motoristas/listar"
                                        class="btn btn-secondary">Cancelar</a>
                                    <button type="submit" class="btn btn-primary">
                                        ${empty motorista.id ? 'Cadastrar' : 'Salvar Alterações'}
                                    </button>
                                </div>

                </form>
            </div>

            <script src="${pageContext.request.contextPath}/js/mascaras.js"></script>
            <script>
                aplicarMascaraCpf('cpf');
                aplicarMascaraTelefone('telefone');
            </script>

        </body>

        </html>