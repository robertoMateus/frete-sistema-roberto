<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${empty cliente.id ? 'Novo Cliente' : 'Editar Cliente'} — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/cliente/listar" class="btn btn-secondary">← Voltar</a>
                    <h2>${empty cliente.id ? 'Novo Cliente' : 'Editar Cliente'}</h2>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <form method="post"
                    action="${pageContext.request.contextPath}/cliente/${empty cliente.id ? 'novo' : 'editar'}">

                    <c:if test="${not empty cliente.id}">
                        <input type="hidden" name="id" value="${cliente.id}" />
                        <input type="hidden" name="status" value="${cliente.status.name()}" />
                    </c:if>

                    <%-- DADOS PRINCIPAIS --%>
                        <div class="card">
                            <div class="card-titulo">Dados Principais</div>

                            <div class="form-row col-2">
                                <div class="form-group">
                                    <label>Razão Social <span class="obrigatorio">*</span></label>
                                    <input type="text" name="razaoSocial" maxlength="150" value="${cliente.razaoSocial}"
                                        required />
                                </div>
                                <div class="form-group">
                                    <label>Nome Fantasia</label>
                                    <input type="text" name="nomeFantasia" maxlength="150"
                                        value="${cliente.nomeFantasia}" />
                                </div>
                            </div>

                            <div class="form-row ${not empty cliente.id ? 'col-3' : 'col-2'}">
                                <div class="form-group">
                                    <label>CNPJ <span class="obrigatorio">*</span></label>
                                    <input type="text" name="cnpj" id="cnpj" maxlength="18"
                                        placeholder="00.000.000/0000-00" value="${cliente.cnpjFormatado}" required />
                                </div>
                                <div class="form-group">
                                    <label>Inscrição Estadual</label>
                                    <input type="text" name="inscricaoEstadual" maxlength="20"
                                        value="${cliente.inscricaoEstadual}" />
                                </div>
                                <c:if test="${not empty cliente.id}">
                                    <div class="form-group">
                                        <label>Status</label>
                                        <input type="text" value="${cliente.status.descricao}" readonly />
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <%-- ENDEREÇO --%>
                            <div class="card">
                                <div class="card-titulo">Endereço</div>

                                <div class="form-row col-4">
                                    <div class="form-group">
                                        <label>Logradouro</label>
                                        <input type="text" name="logradouro" maxlength="150"
                                            value="${cliente.logradouro}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Número</label>
                                        <input type="text" name="numero" maxlength="20" value="${cliente.numero}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Complemento</label>
                                        <input type="text" name="complemento" maxlength="100"
                                            value="${cliente.complemento}" />
                                    </div>
                                    <div class="form-group">
                                        <label>Bairro</label>
                                        <input type="text" name="bairro" maxlength="100" value="${cliente.bairro}" />
                                    </div>
                                </div>

                                <div class="form-row col-endereco">
                                    <div class="form-group">
                                        <label>Município</label>
                                        <input type="text" name="municipio" maxlength="100"
                                            value="${cliente.municipio}" />
                                    </div>
                                    <div class="form-group">
                                        <label>UF</label>
                                        <select name="uf">
                                            <option value="">Selecione</option>
                                            <c:forEach var="uf"
                                                items="${['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO']}">
                                                <option value="${uf}" ${cliente.uf==uf ? 'selected' : '' }>${uf}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>CEP</label>
                                        <input type="text" name="cep" id="cep" maxlength="9" placeholder="00000-000"
                                            value="${cliente.cep}" />
                                    </div>
                                </div>
                            </div>

                            <%-- CONTATO --%>
                                <div class="card">
                                    <div class="card-titulo">Contato</div>

                                    <div class="form-row col-2">
                                        <div class="form-group">
                                            <label>Telefone</label>
                                            <input type="text" name="telefone" id="telefone" maxlength="20"
                                                placeholder="(00) 00000-0000" value="${cliente.telefone}" />
                                        </div>
                                        <div class="form-group">
                                            <label>E-mail</label>
                                            <input type="email" name="email" maxlength="150" value="${cliente.email}" />
                                        </div>
                                    </div>
                                </div>

                                <div class="form-acoes">
                                    <a href="${pageContext.request.contextPath}/cliente/listar"
                                        class="btn btn-secondary">Cancelar</a>
                                    <button type="submit" class="btn btn-primary">
                                        ${empty cliente.id ? 'Cadastrar' : 'Salvar Alterações'}
                                    </button>
                                </div>

                </form>
            </div>

            <script src="${pageContext.request.contextPath}/js/mascaras.js"></script>
            <script>
                aplicarMascaraCnpj('cnpj');
                aplicarMascaraCep('cep');
                aplicarMascaraTelefone('telefone');
            </script>

        </body>

        </html>