// 1. Variable setup - using 'var' to avoid "already declared" errors
var API_DELIVERY = "http://localhost:8080/hyperlocaldelivery/api/delivery";
var dToken = localStorage.getItem("token");
var dPartnerId = localStorage.getItem("userId");
var currentTab = 'available';

// 2. Tab Switching Logic
function switchTab(tab) {
    currentTab = tab; // Update global state
    const assignedBtn = document.getElementById("tabAssigned");
    const availableBtn = document.getElementById("tabAvailable");
    
    if (tab === 'assigned') {
        if(assignedBtn) assignedBtn.className = "px-4 py-2 rounded-lg text-sm font-bold bg-white shadow-sm";
        if(availableBtn) availableBtn.className = "px-4 py-2 rounded-lg text-sm font-bold text-gray-500";
        loadMyTasks();
    } else {
        if(availableBtn) availableBtn.className = "px-4 py-2 rounded-lg text-sm font-bold bg-white shadow-sm";
        if(assignedBtn) assignedBtn.className = "px-4 py-2 rounded-lg text-sm font-bold text-gray-500";
        loadAvailableTasks();
    }
}

// 3. Load Assigned Tasks (My Tasks)
async function loadMyTasks() {
    try {
        const response = await fetch(`${API_DELIVERY}/my-orders?partnerId=${dPartnerId}`, {
            headers: { "Authorization": `Bearer ${dToken}` }
        });
        const result = await response.json();
        
        // Handle both Wrapped DTOs {data: [...]} and Raw Arrays [...]
        const tasks = result.data ? result.data : result; 
        console.log("My Tasks received:", tasks); // Check this in F12 console!
        renderTasks(tasks, false);
    } catch (err) { console.error(err); }
}

// 4. Load Available Tasks
async function loadAvailableTasks() {
    try {
        const response = await fetch(`${API_DELIVERY}/available`, {
            headers: { "Authorization": `Bearer ${dToken}` }
        });
        const result = await response.json();
        
        const tasks = result.data ? result.data : result;
        console.log("Available Tasks received:", tasks);
        renderTasks(tasks, true);
    } catch (err) { console.error(err); }
}

// 5. Unified Rendering Function (Fixes 'renderDelivery' error)
function renderTasks(tasks, isAvailableTab) {
    const list = document.getElementById("taskList");
    if (!list) return;

    // 1. Handle Empty State
    if (!tasks || tasks.length === 0) {
        list.innerHTML = `<div class="p-10 text-center text-gray-400 bg-white rounded-2xl border border-dashed">No orders found.</div>`;
        return;
    }

    // 2. Map and Render
    list.innerHTML = tasks.map(t => {
        // --- DATA MAPPING SECTION ---
        // Handle field differences between OrderDTO and DeliveryDTO
        const displayOrderId = t.orderId || t.order_id || "N/A";
        const displayDeliveryId = t.deliveryId || t.delivery_id;
        
        // Use customerName from DTO or drill into user.name from Order entity
        const customerName = t.customerName || (t.user ? t.user.name : "Customer");
        
        // Format Items List
        let itemsHTML = "";
        if (t.items && t.items.length > 0) {
            itemsHTML = t.items.map(item => {
                // Handle nested product object from OrderDTO
                const pName = item.productName || (item.product ? item.product.name : "Unknown Item");
                return `<li>• ${item.quantity}x ${pName}</li>`;
            }).join('');
        } else if (t.itemNames) {
            // Handle simple string list if using the updated DTO
            itemsHTML = t.itemNames.map(name => `<li>• ${name}</li>`).join('');
        } else {
            itemsHTML = `<li>Check order manifest</li>`;
        }

        const totalPrice = t.totalAmount || t.total_amount || "0.00";
        const displayAddress =
            t.address ||
            t.user?.defaultAddress?.street ||
            t.user?.city ||
            "Standard Delivery";

        // --- HTML TEMPLATE SECTION ---
        return `
        <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 mb-4 transition hover:shadow-md">
            <div class="flex justify-between items-start mb-4">
                <div>
                    <p class="text-[10px] font-bold text-indigo-500 uppercase tracking-widest">Order #${displayOrderId}</p>
                    <h3 class="font-bold text-gray-800 text-lg">${customerName}</h3>
                    <p class="text-xs text-gray-500">
                        <i class="fa-solid fa-location-dot mr-1"></i> ${displayAddress}
                    </p>
                </div>
                ${getActionButtons(t)}
            </div>
            
            <div class="bg-gray-50 p-4 rounded-xl mt-4">
                <div class="flex justify-between items-center mb-2">
                    <span class="text-[10px] font-bold text-gray-400 uppercase">Items to Deliver</span>
                    <span class="font-bold text-indigo-600 text-sm">₹${totalPrice}</span>
                </div>
                <ul class="text-xs text-gray-600 space-y-1">
                    ${itemsHTML}
                </ul>
            </div>
        </div>
        `;
    }).join('');
}

// Ensure you have the companion helper for buttons
function getActionButtons(t) {
    if (currentTab === 'available') {
        // Available tab uses the ID from the available pool
        const id = t.orderId;
        return `<button onclick="acceptOrder(${id})" class="bg-indigo-600 text-white px-4 py-2 rounded-lg text-xs font-bold hover:bg-indigo-700">Accept Order</button>`;
    }
    
    // Status Flow for "My Tasks"
    switch(t.status) {
        case 'ASSIGNED':
            return `<button onclick="updateStatus(${t.deliveryId}, 'PICKED_UP')" class="bg-orange-500 text-white px-4 py-2 rounded-lg text-xs font-bold hover:bg-orange-600">Mark Picked Up</button>`;
        case 'PICKED_UP':
            return `<button onclick="updateStatus(${t.deliveryId}, 'OUT_FOR_DELIVERY')" class="bg-blue-500 text-white px-4 py-2 rounded-lg text-xs font-bold hover:bg-blue-600">Start Delivery</button>`;
        case 'OUT_FOR_DELIVERY':
            return `<button onclick="updateStatus(${t.deliveryId}, 'DELIVERED')" class="bg-green-600 text-white px-4 py-2 rounded-lg text-xs font-bold hover:bg-green-600">Mark Delivered</button>`;
        default:
            return `<span class="text-gray-400 italic text-xs">Completed</span>`;
    }
}
// Alias for script.js compatibility
var renderDelivery = renderTasks;

// 6. Action Functions
async function acceptOrder(id) {
    try {
        await fetch(`${API_DELIVERY}/accept/${id}`, {
            headers: { "Authorization": `Bearer ${dToken}` }
        });
        switchTab('assigned');
    } catch (err) { console.error("Accept failed", err); }
}

async function completeOrder(id) {
    try {
        // Matches @GetMapping("/update-status/{deliveryId}")?status=DELIVERED
        await fetch(`${API_DELIVERY}/update-status/${id}?status=DELIVERED`, {
            headers: { "Authorization": `Bearer ${dToken}` }
        });
        loadMyTasks();
    } catch (err) { console.error("Status update failed", err); }
}

// 7. Startup
window.onload = () => {
    if(document.getElementById("partnerName")) {
        const name = localStorage.getItem("userName") || "Partner";
        document.getElementById("partnerName").innerText = "Welcome, " + name;
    }
    switchTab('available');
};



async function updateStatus(deliveryId, nextStatus) {
    console.log(`Updating Delivery ${deliveryId} to ${nextStatus}`);
    try {
        const response = await fetch(`${API_DELIVERY}/update-status/${deliveryId}?status=${nextStatus}`, {
            method: 'GET', // Your controller uses @GetMapping for this
            headers: { 
                "Authorization": "Bearer " + dToken 
            }
        });

        if (response.ok) {
            console.log("Status updated successfully");
            loadMyTasks(); // Refresh the list to show the next button
        } else {
            const errorData = await response.json();
            alert("Error: " + (errorData.message || "Failed to update status"));
        }
    } catch (err) {
        console.error("Network error during status update:", err);
    }
}