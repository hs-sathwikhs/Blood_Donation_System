const form = document.getElementById('authForm');
const msg = document.getElementById('authMsg');
let role = 'donor';
let register = false;
let busy = false;
const paths = {donor: '/donor', organiser: '/organiser', hospital: '/hospital', admin: '/inventory'};
function setMode() {
    document.querySelectorAll('[data-role]').forEach(btn => btn.setAttribute('aria-pressed', btn.dataset.role === role));
    document.getElementById('registerFields').hidden = !register;
    document.querySelectorAll('.donorField').forEach(el => el.hidden = role !== 'donor');
    document.querySelector('.hospitalField').hidden = role !== 'hospital';
    for (const id of ['name', 'age', 'location', 'registrationNo']) {
        document.getElementById(id).required = register && (id === 'name' || (id === 'age' ? role === 'donor' : role === 'hospital'));
    }
    document.querySelectorAll('#registerFields input, #registerFields select').forEach(input => {
        input.disabled = !register || Boolean(input.closest('[hidden]'));
    });
    document.getElementById('password').minLength = register ? 8 : 1;
    document.getElementById('password').autocomplete = register ? 'new-password' : 'current-password';
    document.getElementById('formTitle').textContent = register ? 'Join your next camp.' : 'Welcome back.';
    document.getElementById('formNote').textContent = register ? 'A few details to get started.' : 'Choose your role to sign in.';
    document.getElementById('authBtn').textContent = register ? 'Create account ↗' : 'Sign in ↗';
    document.getElementById('switchNote').textContent = register ? 'Have an account?' : 'First time here?';
    document.getElementById('switchMode').textContent = register ? 'Sign in' : 'Create an account';
    document.querySelector('.switchText').hidden = role === 'admin';
    msg.textContent = ''; msg.classList.remove('success');
}
document.querySelectorAll('[data-role]').forEach(btn => btn.addEventListener('click', () => {
    if (busy) return;
    role = btn.dataset.role; if (role === 'admin') register = false;
    setMode();
}));
document.getElementById('switchMode').addEventListener('click', () => {if (!busy) {register = !register; setMode();}});
document.getElementById('showPassword').addEventListener('click', event => {
    const input = document.getElementById('password');
    input.type = input.type === 'password' ? 'text' : 'password';
    event.currentTarget.textContent = input.type === 'password' ? 'Show' : 'Hide';
    event.currentTarget.setAttribute('aria-label', input.type === 'password' ? 'Show password' : 'Hide password');
});
form.addEventListener('submit', async event => {
    event.preventDefault(); if (busy) return;
    busy = true;
    const btn = document.getElementById('authBtn');
    btn.disabled = true; msg.textContent = ''; msg.classList.remove('success');
    const isRegister = register;
    btn.textContent = isRegister ? 'Creating account...' : 'Signing in...';
    try {
        const body = Object.fromEntries(new FormData(form));
        const res = await fetch('/api/auth/' + (isRegister ? 'register/' : 'login/') + role, {
            method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(body)
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Could not sign in');
        if (isRegister) {
            register = false; setMode(); msg.textContent = data.message; msg.classList.add('success');
            document.getElementById('password').value = '';
        } else {
            sessionStorage.setItem('token', data.token); sessionStorage.setItem('userId', data.id);
            sessionStorage.setItem('userName', data.name); sessionStorage.setItem('role', data.role);
            location.assign(paths[data.role]);
        }
    } catch (err) {msg.textContent = err instanceof TypeError ? 'Could not connect. Try again.' : err.message;}
    finally {busy = false; btn.disabled = false; btn.textContent = register ? 'Create account ↗' : 'Sign in ↗';}
});
async function loadPreview() {
    const list = document.getElementById('previewCamps');
    try {
        const res = await fetch('/api/camps?size=100&sort=date,asc');
        if (!res.ok) throw new Error('Camps are unavailable');
        const data = await res.json();
        const today = new Date().toLocaleDateString('en-CA');
        const camps = data.content.filter(c => ['Active', 'Scheduled'].includes(c.status) && c.date >= today).slice(0, 3);
        list.replaceChildren();
        for (const camp of camps) {
            const row = document.createElement('article'); row.className = 'previewRow';
            const date = new Date(camp.date + 'T12:00:00');
            const block = document.createElement('div'); block.className = 'dateBlock';
            const day = document.createElement('strong'); day.textContent = date.getDate();
            const month = document.createElement('span'); month.textContent = date.toLocaleDateString('en', {month: 'short'});
            block.append(day, month);
            const details = document.createElement('div'); const title = document.createElement('h3'); title.textContent = camp.name;
            const meta = document.createElement('p'); meta.className = 'rowMeta'; meta.textContent = camp.location + ' · ' + camp.remaining + ' places left';
            details.append(title, meta); row.append(block, details); list.append(row);
        }
        if (!camps.length) {const p = document.createElement('p'); p.className = 'empty'; p.textContent = 'No open camps yet. Check back after an organiser publishes one.'; list.append(p);}
        document.getElementById('previewStatus').textContent = 'From the camp list';
    } catch (err) {
        list.textContent = 'Could not load camps. You can still sign in.';
        document.getElementById('previewStatus').textContent = 'Unavailable';
    }
}
setMode();
const notice = sessionStorage.getItem('notice');
if (notice) {msg.textContent = notice; sessionStorage.removeItem('notice');}
loadPreview();
