<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    header {
        background-color: #1a73e8;
        color: #fff;
        padding: 16px 32px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    header h1 {
        font-size: 1.2rem;
    }

    header nav a {
        color: #fff;
        text-decoration: none;
        margin-left: 20px;
        font-size: 0.9rem;
    }

    header nav a:hover {
        text-decoration: underline;
    }
</style>

<header>
    <h1>GW Gestão de Fretes</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/home">Dashboard</a>
        <a href="${pageContext.request.contextPath}/cliente/listar">Clientes</a>
        <a href="${pageContext.request.contextPath}/motoristas/listar">Motoristas</a>
        <a href="${pageContext.request.contextPath}/veiculos/listar">Veículos</a>
        <a href="${pageContext.request.contextPath}/fretes/listar">Fretes</a>
        <a href="${pageContext.request.contextPath}/manutencoes/listar">Manutenções</a>
        <!-- <a href="${pageContext.request.contextPath}/precosRota/listar">Preços de Rota</a> -->
        <a href="${pageContext.request.contextPath}/auth/logout">Sair</a>
    </nav>
</header>