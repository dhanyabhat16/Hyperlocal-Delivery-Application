// 1. SAFE DECLARATIONS (Prevents "Already Declared" Errors)
if (typeof API_BASE === 'undefined') {
    var API_BASE = "http://localhost:8080/hyperlocaldelivery/api";
}

// Use 'var' to allow re-declaration if other files load these too
var role = localStorage.getItem("role");
var userId = localStorage.getItem("userId");
var token = localStorage.getItem("token");

// 2. INITIALIZATION (Runs only if element exists)
window.addEventListener('DOMContentLoaded', () => {
    const roleTitle = document.getElementById("roleTitle");
    if (roleTitle && role) {
        roleTitle.innerText = "Logged in as: " + role.toUpperCase();
    }

    // ROUTING
    if (role === "DELIVERY" || role === "delivery") loadDelivery();
    if (role === "CUSTOMER" || role === "customer") loadCustomer();
    if (role === "WAREHOUSE" || role === "warehouse") loadWarehouse();
    if (role === "ADMIN" || role === "admin") loadAdmin();
});

// 3. UNIFIED REQUEST HANDLER
async function apiRequest(endpoint, method = "GET", body = null) {
    const headers = { "Content-Type": "application/json" };
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(`${API_BASE}${endpoint}`, options);
    
    if (response.status === 401 || response.status === 403) {
        localStorage.clear();
        window.location.href = "login.html";
        return;
    }
    return response.json();
}

// 4. MODULE FUNCTIONS (Using your teammates' paths)

function loadDelivery() {
    // Note: Removed floating fetch(url) from here
    fetch(`${API_BASE}/delivery/my-orders?partnerId=${userId}`, {
        headers: { "Authorization": "Bearer " + token }
    })
    .then(res => res.json())
    .then(data => renderDelivery(data.data || data)); // Handle nested DTOs
}

function loadCustomer() {
    // Matches your controller: /api/customer/orders
    fetch(`${API_BASE}/customer/orders`, {
        headers: { "Authorization": "Bearer " + token }
    })
    .then(res => res.json())
    .then(data => {
        const container = document.getElementById("content");
        if (!container) return;
        const orders = data.data || data;
        
        if (!orders || orders.length === 0) {
            container.innerHTML = "<p class='p-4 text-gray-500'>No orders yet</p>";
            return;
        }

        container.innerHTML = orders.map(o => `
            <div class="bg-white p-4 m-2 rounded-xl shadow-sm border">
                <h3 class="font-bold">Order #${o.orderId}</h3>
                <p class="text-sm text-gray-600">Status: ${o.status}</p>
                <p class="text-indigo-600 font-bold">₹${o.totalAmount}</p>
            </div>
        `).join("");
    });
}

function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

// 5. AUTH FUNCTIONS (Matches AuthController)
function doLogin(e) {
    if(e) e.preventDefault();
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;

    fetch(`${API_BASE}/public/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    })
    .then(res => res.json())
    .then(data => {
        if (data.token) {
            localStorage.setItem("token", data.token);
            localStorage.setItem("role", data.role);
            localStorage.setItem("userId", data.userId);
            localStorage.setItem("userName", data.name);
            
            // Navigate based on role
            if (data.role === "CUSTOMER") window.location.href = "customer.html";
            else if (data.role === "DELIVERY") window.location.href = "delivery.html";
            else window.location.href = "dashboard.html"; 
        } else {
            alert("Login failed: " + data.message);
        }
    })
    .catch(err => alert("Error: " + err.message));
}