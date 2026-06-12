<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${not empty frete.id ? 'Editar Frete' : 'Novo Frete'} — GW Gestão de Fretes</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
        </head>

        <body>

            <jsp:include page="/WEB-INF/views/components/header.jsp" />

            <div class="container">

                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/fretes/listar" class="btn btn-secondary">← Voltar</a>
                    <h2>${not empty frete.id ? 'Editar Frete' : 'Novo Frete'}</h2>
                </div>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <form method="post"
                    action="${pageContext.request.contextPath}/fretes/${not empty frete.id ? 'editar' : 'novo'}">

                    <c:if test="${not empty frete.id}">
                        <input type="hidden" name="id" value="${frete.id}" />
                    </c:if>

                    <%-- PARTES ENVOLVIDAS --%>
                        <div class="card">
                            <div class="card-titulo">Partes Envolvidas</div>

                            <div class="form-row col-2">
                                <div class="form-group">
                                    <label>Remetente <span class="obrigatorio">*</span></label>
                                    <select name="idRemetente" required>
                                        <option value="">Selecione o remetente</option>
                                        <c:forEach var="c" items="${clientes}">
                                            <option value="${c.id}" ${frete.remetente.id==c.id ? 'selected' : '' }>
                                                ${c.razaoSocial}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>Destinatário <span class="obrigatorio">*</span></label>
                                    <select name="idDestinatario" required>
                                        <option value="">Selecione o destinatário</option>
                                        <c:forEach var="c" items="${clientes}">
                                            <option value="${c.id}" ${frete.destinatario.id==c.id ? 'selected' : '' }>
                                                ${c.razaoSocial}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="form-row col-2">
                                <div class="form-group">
                                    <label>Motorista <span class="obrigatorio">*</span></label>
                                    <select name="idMotorista" required>
                                        <option value="">Selecione o motorista</option>
                                        <c:forEach var="m" items="${motoristas}">
                                            <option value="${m.id}" ${frete.motorista.id==m.id ? 'selected' : '' }>
                                                ${m.nome}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>Veículo <span class="obrigatorio">*</span></label>
                                    <select name="idVeiculo" required>
                                        <option value="">Selecione o veículo</option>
                                        <c:forEach var="v" items="${veiculos}">
                                            <option value="${v.id}" ${frete.veiculo.id==v.id ? 'selected' : '' }>
                                                ${v.placa}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <%-- ORIGEM E DESTINO --%>
                            <div class="card">
                                <div class="card-titulo">Origem e Destino</div>

                                <div class="form-row col-2">
                                    <div class="form-group">
                                        <label>Município de Origem <span class="obrigatorio">*</span></label>
                                        <input type="text" name="municipioOrigem" maxlength="100"
                                            value="${frete.municipioOrigem}" required />
                                    </div>
                                    <div class="form-group">
                                        <label>UF de Origem <span class="obrigatorio">*</span></label>
                                        <select name="ufOrigem" required>
                                            <option value="">UF</option>
                                            <c:forEach var="uf"
                                                items="${['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO']}">
                                                <option value="${uf}" ${frete.ufOrigem==uf ? 'selected' : '' }>${uf}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-row col-2">
                                    <div class="form-group">
                                        <label>Município de Destino <span class="obrigatorio">*</span></label>
                                        <input type="text" name="municipioDestino" maxlength="100"
                                            value="${frete.municipioDestino}" required />
                                    </div>
                                    <div class="form-group">
                                        <label>UF de Destino <span class="obrigatorio">*</span></label>
                                        <select name="ufDestino" required>
                                            <option value="">UF</option>
                                            <c:forEach var="uf"
                                                items="${['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO']}">
                                                <option value="${uf}" ${frete.ufDestino==uf ? 'selected' : '' }>${uf}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <%-- CARGA --%>
                                <div class="card">
                                    <div class="card-titulo">Carga</div>

                                    <div class="form-row col-2">
                                        <div class="form-group">
                                            <label>Descrição da Carga <span class="obrigatorio">*</span></label>
                                            <input type="text" name="descricaoCarga" maxlength="255"
                                                value="${frete.descricaoCarga}" required />
                                        </div>
                                        <div class="form-group">
                                            <label>Volumes <span class="obrigatorio">*</span></label>
                                            <input type="text" id="volumeCarga" name="volumeCarga" maxlength="6"
                                                placeholder="Ex: 10" value="${frete.volumeCarga}" required />
                                        </div>
                                    </div>

                                    <div class="form-row col-2">
                                        <div class="form-group">
                                            <label>Peso Bruto (kg) <span class="obrigatorio">*</span></label>
                                            <input type="text" id="pesoCarga" name="pesoCarga" inputmode="decimal"
                                                pattern="[0-9.,]*" maxlength="12" placeholder="Ex: 15000,00"
                                                value="${frete.pesoCarga}" required />
                                        </div>
                                        <div class="form-group">
                                            <label>Data Prevista de Entrega <span class="obrigatorio">*</span></label>
                                            <input type="datetime-local" name="dataPrevisaoEntrega"
                                                value="${frete.dataPrevisaoEntrega}" required />
                                        </div>
                                    </div>
                                </div>

                                <%-- VALORES --%>
                                    <div class="card">
                                        <div class="card-titulo">Valores</div>

                                        <div class="form-row col-3">
                                            <div class="form-group">
                                                <label>Valor do Frete (R$) <span class="obrigatorio">*</span></label>
                                                <input type="text" id="valorFrete" name="valorFrete" inputmode="decimal"
                                                    pattern="[0-9.,]*" maxlength="12" placeholder="Ex: 3500,00"
                                                    value="${frete.valorFrete}" required />
                                            </div>
                                            <div class="form-group">
                                                <label>Alíquota ICMS (%) <span class="obrigatorio">*</span></label>
                                                <input type="text" id="aliquotaIcms" name="aliquotaIcms"
                                                    inputmode="decimal" pattern="[0-9.,]*" maxlength="5"
                                                    placeholder="Ex: 12,00" value="${frete.aliquotaIcms}" required />
                                            </div>
                                            <div class="form-group">
                                                <label>Valor ICMS (R$)</label>
                                                <input type="text" id="valorIcmsExibido" readonly
                                                    placeholder="Calculado automaticamente" />
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-acoes">
                                        <a href="${pageContext.request.contextPath}/fretes/listar"
                                            class="btn btn-secondary">Cancelar</a>
                                        <button type="submit" class="btn btn-primary">
                                            ${not empty frete.id ? 'Salvar Alterações' : 'Emitir Frete'}
                                        </button>
                                    </div>

                </form>
            </div>

            <script src="${pageContext.request.contextPath}/js/mascaras.js"></script>
            <script>
                aplicarMascaraDecimal('pesoCarga', 12);
                aplicarMascaraDecimal('valorFrete', 12);
                aplicarMascaraDecimal('aliquotaIcms', 5);

                function calcularIcms() {
                    const valorStr = document.getElementById('valorFrete').value.replace(',', '.');
                    const aliqStr = document.getElementById('aliquotaIcms').value.replace(',', '.');
                    const valor = parseFloat(valorStr);
                    const aliq = parseFloat(aliqStr);
                    const exibido = document.getElementById('valorIcmsExibido');
                    if (!isNaN(valor) && !isNaN(aliq) && aliq > 0) {
                        const icms = (valor * aliq / 100).toFixed(2).replace('.', ',');
                        exibido.value = 'R$ ' + icms;
                    } else {
                        exibido.value = '';
                    }
                }
                document.getElementById('valorFrete').addEventListener('blur', calcularIcms);
                document.getElementById('aliquotaIcms').addEventListener('blur', calcularIcms);
                calcularIcms();
            </script>

        </body>

        </html>