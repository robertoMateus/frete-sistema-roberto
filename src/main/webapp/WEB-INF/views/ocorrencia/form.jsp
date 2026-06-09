<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar Ocorrência — GW Gestão de Fretes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" />
</head>

<body>
    <jsp:include page="/WEB-INF/views/components/header.jsp" />

    <div class="container">
        <div class="page-header">
            <a href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}" class="btn btn-secondary">← Voltar</a>
            <h2>Registrar Ocorrência - Frete ${frete.numeroFrete}</h2>
        </div>

        <c:if test="${not empty erro}">
            <div class="erro">${erro}</div>
        </c:if>

        <div class="card">
            <div class="card-titulo">Ocorrência</div>
            <form method="post" action="${pageContext.request.contextPath}/ocorrencias/novo">
                <input type="hidden" name="idFrete" value="${frete.id}" />

                <div class="form-group">
                    <label>Tipo <span class="obrigatorio">*</span></label>
                    <select name="tipo" id="tipoOcorrencia" required onchange="verificarTipo()">
                        <option value="">Selecione</option>
                        <c:forEach var="t" items="${tiposOcorrencia}">
                            <option value="${t.name()}" ${ocorrencia != null && ocorrencia.tipo != null && ocorrencia.tipo.name() == t.name() ? 'selected' : ''}>${t.descricao}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Data/Hora <span class="obrigatorio">*</span></label>
                    <input type="datetime-local" name="dataHoraOcorrencia" value="${ocorrencia != null ? ocorrencia.dataHoraOcorrencia : ''}" required />
                </div>

                <div class="form-row col-2">
                    <div class="form-group">
                        <label>Município <span class="obrigatorio">*</span></label>
                        <input type="text" name="municipio" maxlength="100" value="${ocorrencia != null ? ocorrencia.municipio : ''}" />
                    </div>
                    <div class="form-group">
                        <label>UF <span class="obrigatorio">*</span></label>
                        <select name="uf">
                            <option value="">UF</option>
                            <c:forEach var="uf" items="${['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO']}">
                                <option value="${uf}" ${ocorrencia != null && ocorrencia.uf == uf ? 'selected' : ''}>${uf}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label>Descrição</label>
                    <textarea name="descricao" rows="3">${ocorrencia != null ? ocorrencia.descricao : ''}</textarea>
                </div>

                <div id="grupoRecebedor" style="display:block;">
                    <div class="form-row col-2">
                        <div class="form-group">
                            <label>Nome do Recebedor</label>
                            <input type="text" name="nomeRecebedor" maxlength="150" value="${ocorrencia != null ? ocorrencia.nomeRecebedor : ''}" />
                        </div>
                        <div class="form-group">
                            <label>Documento do Recebedor</label>
                            <input type="text" name="documentoRecebedor" maxlength="20" value="${ocorrencia != null ? ocorrencia.documentoRecebedor : ''}" />
                        </div>
                    </div>
                </div>

                <div class="form-acoes">
                    <a href="${pageContext.request.contextPath}/fretes/detalhe?id=${frete.id}" class="btn btn-secondary">Cancelar</a>
                    <button type="submit" class="btn btn-primary">Salvar Ocorrência</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function verificarTipo() {
            const tipo = document.getElementById('tipoOcorrencia').value;
            const grupoRecebedor = document.getElementById('grupoRecebedor');
            if (tipo === 'ENTREGA_REALIZADA') {
                grupoRecebedor.style.display = 'block';
            } else {
                grupoRecebedor.style.display = 'none';
            }
        }
    </script>

</body>

</html>
