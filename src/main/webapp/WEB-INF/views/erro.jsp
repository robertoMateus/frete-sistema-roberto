<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Erro — Sistema de Gestão de Fretes</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f6f8; display: flex; justify-content: center; align-items: center; min-height: 100vh; }
        .box { background: #fff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; max-width: 400px; }
        h1 { color: #e53935; margin-bottom: 12px; }
        p { color: #666; margin-bottom: 24px; }
        a { color: #1a73e8; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="box">
        <h1>Ops, algo deu errado.</h1>
        <p>Ocorreu um erro inesperado. Por favor, tente novamente ou entre em contato com o suporte.</p>
        <a href="${pageContext.request.contextPath}/home">Voltar ao início</a>
    </div>
</body>
</html>