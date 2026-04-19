// SAFE DECLARATION
if (typeof API_BASE === 'undefined') {
    var API_BASE = "http://localhost:8080/hyperlocaldelivery/api";
}

async function loadProducts() {
    const grid = document.getElementById("productGrid");
    try {
        const response = await fetch(`${API_BASE}/public/products`, {
            headers: { 
                "Authorization": "Bearer " + localStorage.getItem("token"), // Note the space after Bearer
                "Content-Type": "application/json"
            } });
        const result = await response.json();
        if (result.success && result.data) {
            renderProducts(result.data);
        }
    } catch (err) { console.error("Product load failed", err); }
}

async function updateCartUI() {
    const token = localStorage.getItem("token");
    // Hardcoded URL for one test to eliminate variable errors
    const testUrl = "http://localhost:8080/hyperlocaldelivery/api/customer/cart";
    
    try {
        const response = await fetch(testUrl, {
            headers: { "Authorization": "Bearer " + token }
        });
        
        if (response.ok) {
            const result = await response.json();
            console.log("SUCCESS! Cart data:", result);
        } else {
            console.log("Failed with status:", response.status);
        }
    } catch (err) {
        console.error("Fetch failed completely", err);
    }
}

function renderProducts(products) {
    const grid = document.getElementById("productGrid");
    if (!grid) return;
    grid.innerHTML = products.map(p => `
        <div class="bg-white p-5 rounded-2xl shadow-sm border border-gray-100">
            <h3 class="font-bold text-gray-800">${p.name}</h3>
            <p class="text-xl font-extrabold text-indigo-600">₹${p.price}</p>
            <button onclick="addToCart(${p.productId})" class="bg-indigo-600 text-white px-4 py-2 rounded-lg mt-2">
                Add to Cart
            </button>
        </div>
    `).join('');
}

window.onload = () => {
    loadProducts();
    updateCartUI();
};