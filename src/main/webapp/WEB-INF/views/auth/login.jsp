<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Login — Sistema de Gestão de Fretes</title>
            <style>
                * {
                    box-sizing: border-box;
                    margin: 0;
                    padding: 0;
                }

                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f6f8;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 100vh;
                }

                .login-container {
                    background-color: #fff;
                    padding: 40px;
                    border-radius: 8px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                    width: 100%;
                    max-width: 380px;
                }

                .login-container h1 {
                    font-size: 1.4rem;
                    color: #333;
                    margin-bottom: 8px;
                    text-align: center;
                }

                .login-container p.subtitulo {
                    font-size: 0.875rem;
                    color: #666;
                    text-align: center;
                    margin-bottom: 28px;
                }

                .form-group {
                    margin-bottom: 18px;
                }

                .form-group label {
                    display: block;
                    font-size: 0.875rem;
                    color: #444;
                    margin-bottom: 6px;
                }

                .form-group input {
                    width: 100%;
                    padding: 10px 12px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                    font-size: 0.95rem;
                    transition: border-color 0.2s;
                }

                .form-group input:focus {
                    outline: none;
                    border-color: #1a73e8;
                }

                .erro {
                    background-color: #fdecea;
                    color: #c0392b;
                    border: 1px solid #e74c3c;
                    border-radius: 4px;
                    padding: 10px 14px;
                    font-size: 0.875rem;
                    margin-bottom: 18px;
                }

                .btn-entrar {
                    width: 100%;
                    padding: 11px;
                    background-color: #1a73e8;
                    color: #fff;
                    border: none;
                    border-radius: 4px;
                    font-size: 1rem;
                    cursor: pointer;
                    transition: background-color 0.2s;
                }

                .btn-entrar:hover {
                    background-color: #1558b0;
                }
            </style>
        </head>

        <body>
            <div class="login-container">
                <h1>Sistema de Gestão de Fretes</h1>
                <p class="subtitulo">Acesse sua conta para continuar</p>

                <c:if test="${not empty erro}">
                    <div class="erro">${erro}</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/auth/login">
                    <div class="form-group">
                        <label for="login">Login</label>
                        <input type="text" id="login" name="login" value="${not empty param.login ? param.login : ''}"
                            placeholder="Digite seu login" required autofocus />
                    </div>
                    <div class="form-group">
                        <label for="senha">Senha</label>
                        <input type="password" id="senha" name="senha" placeholder="Digite sua senha" required />
                    </div>
                    <button type="submit" class="btn-entrar">Entrar</button>
                </form>
            </div>
        </body>

        </html>