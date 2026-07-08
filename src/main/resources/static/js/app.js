const role = document.body.dataset.role;
const token = sessionStorage.getItem('token');
const userId = sessionStorage.getItem('userId');
const id = name => document.getElementById(name);
const esc = value => String(value ?? '').replace(/[&<>"']/g, char => ({'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[char]));
const num = value => Number(value).toLocaleString('en', {maximumFractionDigits: 1});
const today = () => new Date().toLocaleDateString('en-CA');
const date = value => new Date(value + 'T12:00:00').toLocaleDateString('en', {day: 'numeric', month: 'short', year: 'numeric'});
const badge = value => `<span class="badge ${esc(value)}">${esc(value)}</span>`;
const empty = (title, note) => `<div class="empty"><strong>${esc(title)}</strong>${esc(note)}</div>`;
let camps = [], regs = [], requests = [], stocks = [], profile = {}, totals = {};
let loading = false;
let busy = false;
let refreshFailed = false;
let campView = '';
let regView = '';
let requestView = '';
let ledgerView = '';
function signOut(notice) {
    sessionStorage.clear(); if (notice) sessionStorage.setItem('notice', notice);
    location.replace('/');
}
async function api(path, method = 'GET', body) {
    const res = await fetch(path, {method, headers: {Authorization: 'Bearer ' + token, 'Content-Type': 'application/json'},
        ...(body ? {body: JSON.stringify(body)} : {}), cache: 'no-store'});
    const text = await res.text(); let data;
    try {data = JSON.parse(text);} catch {data = {message: text};}
    if (res.status === 401) {signOut('Your session ended. Please sign in again.'); throw new Error('Session ended');}
    if (!res.ok) throw new Error(data.message || 'Could not save the change');
    return data;
}
function showMsg(text, success = false) {
    id('pageMsg').hidden = false; id('pageMsg').textContent = text;
    id('pageMsg').classList.toggle('success', success);
}
function dateBlock(value) {
    const dt = new Date(value + 'T12:00:00');
    return `<div class="dateBlock"><strong>${dt.getDate()}</strong><span>${dt.toLocaleDateString('en', {month: 'short'})}</span></div>`;
}
function action(name, key, text, cls = '') {
    return `<button type="button" class="${cls}" data-action="${name}" data-id="${esc(key)}">${esc(text)}</button>`;
}
function renderCamps() {
    const query = id('campSearch').value.toLowerCase().trim();
    const state = id('campState').value; const day = id('campDate').value;
    const rows = camps.filter(c => (c.name + ' ' + c.location).toLowerCase().includes(query) && (!state || c.status === state) && (!day || c.date === day));
    const view = JSON.stringify([rows, regs, profile.eligible]);
    id('campCount').textContent = rows.length + ' shown';
    if (view === campView) return; campView = view;
    id('campList').innerHTML = rows.map(c => {
        let actions = '';
        if (role === 'donor') {
            const reg = regs.find(r => r.campId === c.campId && r.status !== 'Cancelled');
            if (reg) actions = badge(reg.status);
            else if (['Active', 'Scheduled'].includes(c.status) && c.date >= today() && c.remaining > 0 && profile.eligible) actions = action('register', c.campId, 'Register ↗');
            else actions = `<span class="caption">${!profile.eligible ? 'Not eligible yet' : c.remaining === 0 ? 'Camp full' : 'Closed'}</span>`;
        } else {
            if (!['Completed', 'Cancelled'].includes(c.status)) actions += action('editCamp', c.campId, 'Edit', 'textBtn');
            actions += action('registrations', c.campId, 'Donors', 'textBtn');
            if (c.status === 'Draft') actions += action('publish', c.campId, 'Publish ↗');
            if (c.status === 'Scheduled' && c.date === today()) actions += action('activate', c.campId, 'Start camp ↗');
            if (c.status === 'Active') actions += action('complete', c.campId, 'Complete');
            if (!['Completed', 'Cancelled'].includes(c.status)) actions += action('cancelCamp', c.campId, 'Cancel', 'textBtn danger');
        }
        return `<article class="campRow">${dateBlock(c.date)}<div><div class="rowTop"><h3>${esc(c.name)}</h3>${badge(c.status)}</div><p class="rowMeta">${esc(c.location)} · ${esc(c.time.slice(0, 5))}</p><div class="rowBottom"><div class="capacity"><span class="caption">${['Completed', 'Cancelled'].includes(c.status) ? num(c.registered) + ' registered' : num(c.remaining) + ' of ' + num(c.capacity) + ' places left'}</span><div class="meter"><span style="transform:scaleX(${Math.min(1, c.registered / c.capacity)})"></span></div></div><div class="rowActions">${actions}</div></div></div></article>`;
    }).join('') || empty('No camps here yet.', query || state || day ? 'Try another search or clear the filters.' : role === 'organiser' ? 'Create your first camp using the form.' : 'Check back when an organiser publishes a camp.');
}
function renderDonor(history) {
    id('donorType').textContent = profile.bloodType;
    id('eligibility').textContent = profile.eligible ? 'Ready to donate' : 'Waiting period';
    id('eligibilityRing').style.strokeDashoffset = profile.eligible ? '0' : String(119.4 * Math.min(1, Math.max(0, (new Date(profile.nextDonationDate) - new Date(today())) / 86400000 / 90)));
    id('nextDate').textContent = profile.eligible ? 'You can register for an open camp.' : 'Next date: ' + date(profile.nextDonationDate);
    id('lastDate').textContent = profile.lastDonationDate ? date(profile.lastDonationDate) : 'No donations yet';
    id('regCount').textContent = regs.filter(r => r.status === 'Registered').length + ' reserved';
    const view = JSON.stringify(regs);
    if (view !== regView) {
        regView = view;
        id('registrationList').innerHTML = regs.map(r => `<article class="registrationRow"><div class="rowTop"><h3>${esc(r.name)}</h3>${badge(r.status)}</div><p class="rowMeta">${date(r.date)} · ${esc(r.location)}</p>${r.status === 'Registered' && r.campStatus !== 'Completed' && r.date >= today() ? action('cancelReg', r.campId, 'Cancel place', 'textBtn danger') : ''}</article>`).join('') || empty('Your next visit starts here.', 'Register for a camp from the list. Your reserved place will appear here.');
    }
    const select = id('donationCamp'); const selected = select.value;
    const options = regs.filter(r => r.status === 'Registered' && r.campStatus === 'Active' && r.date === today());
    const html = '<option value="">Choose a camp</option>' + options.map(r => `<option value="${esc(r.campId)}">${esc(r.name)}</option>`).join('');
    if (select.innerHTML !== html) {select.innerHTML = html; select.value = selected;}
    id('donationForm').querySelector('button').disabled = busy || !profile.eligible || !options.length;
    id('historyCount').textContent = history.length + ' records';
    id('historyList').innerHTML = history.length ? `<div class="tableWrap"><table><thead><tr><th>Date</th><th>Blood group</th><th>Units</th><th>Status</th></tr></thead><tbody>${history.map(d => `<tr><td>${date(d.donationDate)}</td><td>${esc(d.bloodType)}</td><td>${num(d.quantity)}</td><td>${badge(d.status)}</td></tr>`).join('')}</tbody></table></div>` : empty('No donations recorded.', 'After your visit, use the donation form to save your record.');
}
function renderStock() {
    if (!id('stockGrid').querySelector('.stockItem')) id('stockGrid').innerHTML = Object.keys(totals).map(type => `<article class="stockItem" data-type="${esc(type)}"><div class="stockType"><span>${esc(type)}</span><i hidden></i></div><div class="stockQty">0</div><div class="meter"><span></span></div></article>`).join('');
    const max = Math.max(60, ...Object.values(totals));
    id('stockGrid').querySelectorAll('.stockItem').forEach(el => {
        const qty = totals[el.dataset.type]; el.classList.toggle('low', qty < 10);
        el.querySelector('i').hidden = qty >= 10; el.querySelector('.stockQty').textContent = num(qty);
        el.querySelector('.meter span').style.transform = `scaleX(${qty / max})`;
    });
    id('stockTotal').textContent = num(Object.values(totals).reduce((sum, qty) => sum + qty, 0)) + ' usable units';
    if (role !== 'admin') return;
    const expired = stocks.filter(s => s.expired);
    const soon = stocks.filter(s => !s.expired && s.expiryDate <= new Date(Date.now() + 7 * 86400000).toLocaleDateString('en-CA'));
    id('expiredCount').textContent = expired.length + ' expired batches';
    id('expiryInfo').textContent = num(expired.reduce((sum, s) => sum + s.quantity, 0)) + ' excluded units · ' + soon.length + ' batches expire within 7 days';
    id('removeExpired').disabled = busy || !expired.length;
    const view = JSON.stringify(stocks);
    if (view === ledgerView || id('stockLedger').contains(document.activeElement)) return;
    ledgerView = view;
    id('stockLedger').innerHTML = stocks.length ? `<table><thead><tr><th>Blood group</th><th>Added</th><th>Expires</th><th>Status</th><th>Counted units</th></tr></thead><tbody>${stocks.map(s => `<tr><td>${esc(s.bloodType)}</td><td>${date(s.dateAdded)}</td><td>${date(s.expiryDate)}</td><td>${badge(s.expired ? 'Expired' : 'Usable')}</td><td><form class="stockEdit" data-id="${esc(s.stockId)}"><label class="srOnly" for="qty-${esc(s.stockId)}">Units of ${esc(s.bloodType)}, batch added ${date(s.dateAdded)}</label><input id="qty-${esc(s.stockId)}" name="quantity" type="number" min="0" max="1000" step="0.5" required value="${s.quantity}"><button>Save</button><span class="formMsg" role="status"></span></form></td></tr>`).join('')}</tbody></table>` : empty('No stock entries.', 'Add the first batch with the stock intake form.');
}
function renderRequests() {
    id('requestCount').textContent = requests.length + (role === 'admin' ? ' pending' : ' records');
    const view = JSON.stringify([requests, totals]); if (view === requestView) return; requestView = view;
    id('requestList').innerHTML = requests.map(r => `<article class="requestRow"><strong class="requestType">${esc(r.bloodType)}</strong><div><h3>${num(r.quantity)} units${r.hospitalName ? ' · ' + esc(r.hospitalName) : ''}</h3><p class="rowMeta">${r.requestDate ? date(r.requestDate) : ''}</p><div class="badges">${badge(r.severity)}${badge(r.status)}</div></div>${role === 'admin' ? Number(totals[r.bloodType] || 0) >= r.quantity ? action('fulfill', r.requestId, 'Fulfill ↗') : '<span class="caption">Needs stock</span>' : ''}</article>`).join('') || empty(role === 'admin' ? 'The queue is clear.' : 'No requests yet.', role === 'admin' ? 'New hospital requests appear here in severity order.' : 'Use the form to send a request to the blood bank.');
}
async function refresh() {
    if (loading || busy || document.hidden) return;
    loading = true; id('refresh').disabled = true;
    try {
        if (role === 'donor') {
            const data = await Promise.all([api('/api/camps?size=100&sort=date,asc'), api('/api/donors/' + userId), api('/api/donors/' + userId + '/registrations'), api('/api/donors/' + userId + '/donations')]);
            camps = data[0].content; profile = data[1]; regs = data[2]; renderCamps(); renderDonor(data[3]);
        } else if (role === 'organiser') {
            camps = (await api('/api/camps?size=100&mine=true&sort=date,asc')).content; renderCamps();
        } else {
            const data = await Promise.all([api('/api/inventory/stock'), api(role === 'admin' ? '/api/inventory/requests/pending' : '/api/hospitals/' + userId + '/requests'), ...(role === 'admin' ? [api('/api/inventory/stock/detailed')] : [])]);
            totals = data[0]; requests = data[1]; if (role === 'admin') stocks = data[2]; renderStock(); renderRequests();
        }
        id('liveStatus').textContent = 'Updated ' + new Date().toLocaleTimeString('en', {hour: '2-digit', minute: '2-digit'});
        id('liveStatus').classList.remove('offline');
        if (refreshFailed) id('pageMsg').hidden = true;
        refreshFailed = false;
    } catch (err) {refreshFailed = true; id('liveStatus').textContent = 'Could not refresh'; id('liveStatus').classList.add('offline'); showMsg(err instanceof TypeError ? 'Connection lost. Use Refresh to try again.' : err.message);}
    finally {loading = false; id('refresh').disabled = false;}
}
function confirmAction(title, note) {
    const dialog = id('confirmDialog'); id('confirmTitle').textContent = title; id('confirmText').textContent = note;
    dialog.returnValue = ''; dialog.showModal();
    return new Promise(resolve => dialog.addEventListener('close', () => resolve(dialog.returnValue === 'confirm'), {once: true}));
}
function clearCamp() {
    id('campForm').reset(); id('campForm').elements.campId.value = '';
    id('campFormTitle').textContent = 'Create a camp'; id('saveCamp').textContent = 'Save draft ↗'; id('clearCamp').hidden = true;
}
async function runAction(btn) {
    if (busy) return;
    const key = btn.dataset.id; const name = btn.dataset.action;
    const camp = camps.find(c => c.campId === key);
    if (name === 'editCamp') {
        const form = id('campForm'); for (const field of ['campId', 'name', 'location', 'date', 'time', 'capacity']) form.elements[field].value = camp[field];
        id('campFormTitle').textContent = 'Edit camp'; id('saveCamp').textContent = 'Save changes ↗'; id('clearCamp').hidden = false;
        form.scrollIntoView({block: 'center'}); form.elements.name.focus(); return;
    }
    if (['cancelReg', 'cancelCamp', 'complete', 'fulfill'].includes(name) && !await confirmAction(
        {cancelReg: 'Cancel your place?', cancelCamp: 'Cancel this camp?', complete: 'Complete this camp?', fulfill: 'Fulfill this request?'}[name],
        {cancelReg: 'Your place will be available to another donor.', cancelCamp: 'Active registrations will be cancelled too.', complete: 'Donation recording closes after completion.', fulfill: 'The requested units will be deducted from usable stock.'}[name])) return;
    busy = true; btn.disabled = true;
    try {
        if (name === 'registrations') {
            const data = await api('/api/camps/' + key + '/registrations');
            id('dialogTitle').textContent = camp.name;
            id('dialogBody').innerHTML = data.length ? `<table><thead><tr><th>Donor</th><th>Group</th><th>Status</th></tr></thead><tbody>${data.map(r => `<tr><td>${esc(r.donorName)}</td><td>${esc(r.bloodType)}</td><td>${badge(r.status)}</td></tr>`).join('')}</tbody></table>` : empty('No registrations yet.', 'Publish the camp to make it available to donors.');
            id('detailDialog').showModal();
        } else {
            if (name === 'register') await api('/api/donors/register-camp', 'POST', {donorId: userId, campId: key});
            if (name === 'cancelReg') await api('/api/donors/' + userId + '/registrations/' + key, 'DELETE');
            if (name === 'cancelCamp') await api('/api/camps/' + key, 'DELETE');
            if (name === 'publish') await api('/api/camps/' + key + '/publish', 'POST');
            if (['activate', 'complete'].includes(name)) await api('/api/camps/' + key + '/status', 'PATCH', {status: name === 'activate' ? 'Active' : 'Completed'});
            if (name === 'fulfill') await api('/api/inventory/fulfill/' + key + '?strategyType=' + id('strategy').value, 'POST');
            showMsg({register: 'Place reserved.', cancelReg: 'Registration cancelled.', cancelCamp: 'Camp cancelled.', publish: 'Camp published.', activate: 'Camp is active.', complete: 'Camp completed.', fulfill: 'Request fulfilled.'}[name], true);
        }
    } catch (err) {showMsg(err instanceof TypeError ? 'Could not connect. Try again.' : err.message);}
    finally {busy = false; btn.disabled = false; await refresh();}
}
async function submitForm(event) {
    event.preventDefault(); if (busy) return;
    const form = event.target; const body = Object.fromEntries(new FormData(form));
    const btn = form.querySelector('button'); const msg = form.querySelector('.formMsg'); const text = btn.textContent;
    busy = true; btn.disabled = true; btn.textContent = 'Saving...'; msg.textContent = ''; msg.classList.remove('success');
    try {
        if (form.id === 'campForm') {body.capacity = Number(body.capacity); await api('/api/camps' + (body.campId ? '/' + body.campId : ''), body.campId ? 'PUT' : 'POST', body); clearCamp();}
        if (form.id === 'donationForm') await api('/api/donors/donate', 'POST', {...body, donorId: userId});
        if (form.id === 'requestForm') await api('/api/hospitals/' + userId + '/requests', 'POST', body);
        if (form.id === 'stockForm') await api('/api/inventory/stock/add', 'POST', body);
        if (form.classList.contains('stockEdit')) await api('/api/inventory/stock/' + form.dataset.id, 'PATCH', body);
        msg.textContent = 'Saved.'; msg.classList.add('success');
    } catch (err) {msg.textContent = err instanceof TypeError ? 'Could not connect. Try again.' : err.message;}
    finally {busy = false; btn.disabled = false; btn.textContent = form.id === 'campForm' ? (form.elements.campId.value ? 'Save changes ↗' : 'Save draft ↗') : text; await refresh();}
}
if (!token || !userId || sessionStorage.getItem('role') !== role) signOut('Sign in with the account for this page.');
else {
    id('userName').textContent = sessionStorage.getItem('userName');
    id('logout').addEventListener('click', () => signOut());
    id('refresh').addEventListener('click', refresh);
    document.addEventListener('click', event => {const btn = event.target.closest('[data-action]'); if (btn) runAction(btn);});
    document.addEventListener('submit', event => {if (!event.target.closest('dialog')) submitForm(event);});
    for (const filter of ['campSearch', 'campState', 'campDate']) if (id(filter)) id(filter).addEventListener(filter === 'campSearch' ? 'input' : 'change', renderCamps);
    if (id('clearCamp')) {id('clearCamp').addEventListener('click', clearCamp); id('newCampDate').min = today();}
    id('closeDetail').addEventListener('click', () => id('detailDialog').close());
    if (id('removeExpired')) id('removeExpired').addEventListener('click', async () => {
        if (busy || !await confirmAction('Remove expired stock?', 'Only expired batches will be deleted.')) return;
        busy = true; id('removeExpired').disabled = true;
        try {const data = await api('/api/inventory/stock/expired', 'DELETE'); showMsg(data.message, true);} catch (err) {showMsg(err.message);}
        finally {busy = false; await refresh();}
    });
    document.addEventListener('visibilitychange', () => {if (!document.hidden) refresh();});
    refresh(); setInterval(refresh, 20000);
}
