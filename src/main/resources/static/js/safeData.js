const rawFetch = window.fetch;
function escapeText(value) {
    return value.replace(/[&<>"']/g, ch => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[ch]));
}
function safeData(data) {
    if (Array.isArray(data)) return data.map(safeData);
    if (data && typeof data === 'object') return Object.fromEntries(Object.entries(data).map(([key, value]) => [key, safeData(value)]));
    return typeof data === 'string' ? escapeText(data) : data;
}
window.fetch = async (...args) => {
    const res = await rawFetch(...args);
    const json = res.json.bind(res);
    res.json = async () => safeData(await json());
    return res;
};
