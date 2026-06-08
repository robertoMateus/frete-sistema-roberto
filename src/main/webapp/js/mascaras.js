function aplicarMascaraCpf(id) {
    const el = document.getElementById(id);
    if (!el) return;

    function formatar(v) {
        v = v.replace(/\D/g, '').substring(0, 11);
        if (v.length > 9) v = v.replace(/^(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4');
        else if (v.length > 6) v = v.replace(/^(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3');
        else if (v.length > 3) v = v.replace(/^(\d{3})(\d{0,3})/, '$1.$2');
        return v;
    }

    el.addEventListener('input', function () { this.value = formatar(this.value); });
    el.value = formatar(el.value);
}

function aplicarMascaraTelefone(id) {
    const el = document.getElementById(id);
    if (!el) return;

    function formatar(v) {
        v = v.replace(/\D/g, '').substring(0, 11);
        if (v.length > 10) v = v.replace(/^(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
        else if (v.length > 6) v = v.replace(/^(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
        else if (v.length > 2) v = v.replace(/^(\d{2})(\d{0,5})/, '($1) $2');
        else if (v.length > 0) v = v.replace(/^(\d{0,2})/, '($1');
        return v;
    }

    el.addEventListener('input', function () { this.value = formatar(this.value); });
    el.value = formatar(el.value);
}

function aplicarMascaraCnpj(id) {
    const el = document.getElementById(id);
    if (!el) return;

    function formatar(v) {
        v = v.replace(/\D/g, '').substring(0, 14);
        if (v.length > 12) v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{0,2})/, '$1.$2.$3/$4-$5');
        else if (v.length > 8) v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{0,4})/, '$1.$2.$3/$4');
        else if (v.length > 5) v = v.replace(/^(\d{2})(\d{3})(\d{0,3})/, '$1.$2.$3');
        else if (v.length > 2) v = v.replace(/^(\d{2})(\d{0,3})/, '$1.$2');
        return v;
    }

    el.addEventListener('input', function () { this.value = formatar(this.value); });
    el.value = formatar(el.value);
}

function aplicarMascaraCep(id) {
    const el = document.getElementById(id);
    if (!el) return;

    function formatar(v) {
        v = v.replace(/\D/g, '').substring(0, 8);
        if (v.length > 5) v = v.replace(/^(\d{5})(\d{0,3})/, '$1-$2');
        return v;
    }

    el.addEventListener('input', function () { this.value = formatar(this.value); });
    el.value = formatar(el.value);
}