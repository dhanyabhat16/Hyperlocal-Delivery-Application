const BASE = "http://localhost:8080/api";
const role = localStorage.getItem("role");
const userId = localStorage.getItem("userId");
const token = localStorage.getItem("token");

fetch(url, {
    headers: {
        "Authorization": "Bearer " + token
    }
});

document.getElementById("roleTitle").innerText =
    "Logged in as: " + role.toUpperCase();

if (role === "delivery") loadDelivery();
if (role === "customer") loadCustomer();
if (role === "warehouse") loadWarehouse();
if (role === "admin") loadAdmin();

// DELIVERY
function loadDelivery() {
    fetch(`${BASE}/delivery/my-orders?partnerId=${userId}`)
        .then(res => res.json())
        .then(data => renderDelivery(data));
}

function renderDelivery(data) {
    let html = "";

    data.forEach(d => {
        html += `
        <div class="card">
            Order #${d.orderId}<br>
            Customer: ${d.customerName}<br>
            Status: <span class="status ${d.status}">${d.status}</span><br>

            <button onclick="update(${d.deliveryId}, 'OUT_FOR_DELIVERY')">Out</button>
            <button onclick="update(${d.deliveryId}, 'DELIVERED')">Delivered</button>
        </div>`;
    });

    document.getElementById("content").innerHTML = html;
}

function update(id, status) {
    fetch(`${BASE}/delivery/update-status/${id}?status=${status}`)
        .then(() => loadDelivery());
}

// CUSTOMER
function loadCustomer() {
    const userId = localStorage.getItem("userId");

    fetch(`/hyperlocaldelivery/api/customer/orders?userId=${userId}`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("content");

            if (!data || data.length === 0) {
                container.innerHTML = "<p>No orders yet</p>";
                return;
            }

            container.innerHTML = data.map(o => `
                <div style="background:#fff; padding:15px; margin:10px; border-radius:8px;">
                    <h3>Order #${o.orderId}</h3>
                    <p>Status: ${o.status}</p>
                    <p>Amount: ₹${o.totalAmount}</p>
                </div>
            `).join("");
        });
}

// WAREHOUSE
function loadWarehouse() {
    fetch(`${BASE}/warehouse/orders`)
        .then(res => res.json())
        .then(data => {

            let html = "<h2 class='text-xl font-bold mb-4'>🏭 Orders to Process</h2>";

            data.forEach(o => {
                html += `
                <div class="bg-white p-4 rounded shadow">
                    <p><b>Order #${o.orderId}</b></p>
                    <p>Status: ${o.status}</p>

                    <button onclick="updateWarehouse(${o.orderId}, 'PACKED')"
                        class="bg-blue-500 text-white px-3 py-1 rounded">
                        Mark Packed
                    </button>

                    <button onclick="assignDelivery(${o.orderId})"
                        class="bg-green-500 text-white px-3 py-1 rounded">
                        Assign Delivery
                    </button>
                </div>`;
            });

            document.getElementById("content").innerHTML = html;
        });
}

// ADMIN (placeholder)
function loadAdmin() {
    document.getElementById("content").innerHTML =
        "<div class='card'>Admin panel coming soon</div>";
}

function updateWarehouse(orderId, status) {
    fetch(`${BASE}/warehouse/update-status/${orderId}?status=${status}`)
        .then(() => loadWarehouse());
}

function assignDelivery(orderId) {
    const partnerId = prompt("Enter Delivery Partner ID");

    fetch(`${BASE}/warehouse/assign-delivery?orderId=${orderId}&partnerId=${partnerId}`)
        .then(() => loadWarehouse());
}

// LOGOUT
function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

function register(e) {
    e.preventDefault();

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const role = document.getElementById("role").value;
    const city = document.getElementById("city").value;

    console.log("Sending:", name, email, password, role, city);

    fetch("/hyperlocaldelivery/api/public/auth/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name: name,
            email: email,
            password: password,
            role: role,
            city: city
        })
    })
    .then(async res => {
        const text = await res.text();
        console.log("RAW RESPONSE:", text);

        if (!res.ok) throw new Error(text);
        return JSON.parse(text);
    })
    .then(() => {
        document.getElementById("msg").innerText = "Registered successfully";
    })
    .catch(err => {
        console.error(err);
        document.getElementById("msg").innerText = err.message;
    });
}

function doLogin() {
    console.log("Login clicked");

    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;

    fetch("http://localhost:8080/api/public/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    })
    .then(res => {
        console.log("Login response:", res);
        if (!res.ok) throw new Error("Login failed");
        return res.json();
    })
    .then(data => {
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        localStorage.setItem("userId", data.userId);

        alert("Login successful");
    })
    .catch(err => {
        console.error(err);
        alert("Error: " + err.message);
    });
}