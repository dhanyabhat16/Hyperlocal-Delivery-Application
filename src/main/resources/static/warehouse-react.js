const { useEffect, useMemo, useState } = React;

const API_BASE = "http://localhost:8080/hyperlocaldelivery/api";
const ORDER_STATUSES = [
  "PENDING",
  "CONFIRMED",
  "PLACED",
  "PACKED",
  "ASSIGNED",
  "PICKED_UP",
  "IN_TRANSIT",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
  "CANCELLED",
];

function parseResponseBody(body) {
  if (body && typeof body === "object" && Object.prototype.hasOwnProperty.call(body, "data")) {
    return body.data;
  }
  return body;
}

async function apiRequest(path, options = {}) {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("Authentication required");
  }

  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
    Authorization: `Bearer ${token}`,
  };

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  const contentType = response.headers.get("content-type") || "";
  const body = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message =
      (body && body.message) ||
      (typeof body === "string" ? body : "Request failed");

    if (response.status === 401 || response.status === 403) {
      localStorage.clear();
      window.location.href = "login.html";
    }
    throw new Error(message);
  }

  return parseResponseBody(body);
}

function formatCurrency(amount) {
  return `Rs ${Number(amount || 0).toFixed(2)}`;
}

function WarehouseApp() {
  const [activeTab, setActiveTab] = useState("products");
  const [products, setProducts] = useState([]);
  const [lowStock, setLowStock] = useState([]);
  const [orders, setOrders] = useState([]);
  const [analytics, setAnalytics] = useState(null);
  const [days, setDays] = useState(7);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  const role = localStorage.getItem("role") || "";
  const name = localStorage.getItem("userName") || "Warehouse Manager";

  useEffect(() => {
    if (!localStorage.getItem("token")) {
      window.location.href = "login.html";
      return;
    }

    if (role !== "WAREHOUSE") {
      setError("This page is only for warehouse manager accounts.");
      setTimeout(() => {
        window.location.href = "login.html";
      }, 800);
      return;
    }

    loadInitialData();
  }, []);

  async function loadInitialData() {
    setLoading(true);
    setError("");
    try {
      const [productData, lowStockData, orderData, analyticsData] = await Promise.all([
        apiRequest("/public/products"),
        apiRequest("/warehouse/products/low-stock"),
        apiRequest("/warehouse/orders"),
        apiRequest("/warehouse/analytics?days=7"),
      ]);

      setProducts(Array.isArray(productData) ? productData : []);
      setLowStock(Array.isArray(lowStockData) ? lowStockData : []);
      setOrders(Array.isArray(orderData) ? orderData : []);
      setAnalytics(analyticsData || null);
    } catch (err) {
      setError(err.message || "Failed to load warehouse dashboard");
    } finally {
      setLoading(false);
    }
  }

  async function refreshProducts() {
    const [productData, lowStockData] = await Promise.all([
      apiRequest("/public/products"),
      apiRequest("/warehouse/products/low-stock"),
    ]);
    setProducts(Array.isArray(productData) ? productData : []);
    setLowStock(Array.isArray(lowStockData) ? lowStockData : []);
  }

  async function refreshOrders() {
    const orderData = await apiRequest("/warehouse/orders");
    setOrders(Array.isArray(orderData) ? orderData : []);
  }

  async function updateStock(productId) {
    const input = document.getElementById(`stock-${productId}`);
    if (!input) return;

    const quantity = Number(input.value);
    if (!Number.isFinite(quantity) || quantity < 0) {
      setError("Stock must be a non-negative number.");
      return;
    }

    setError("");
    setNotice("");
    try {
      await apiRequest(`/warehouse/products/${productId}/stock?quantity=${quantity}`, {
        method: "PUT",
      });
      await refreshProducts();
      setNotice("Stock updated successfully.");
    } catch (err) {
      setError(err.message || "Failed to update stock");
    }
  }

  async function toggleAvailability(productId, current) {
    setError("");
    setNotice("");
    try {
      await apiRequest(
        `/warehouse/products/${productId}/availability?available=${!Boolean(current)}`,
        { method: "PUT" }
      );
      await refreshProducts();
      setNotice("Product availability updated.");
    } catch (err) {
      setError(err.message || "Failed to update availability");
    }
  }

  async function updateOrderStatus(orderId, status) {
    setError("");
    setNotice("");
    try {
      await apiRequest(`/warehouse/orders/${orderId}/status?status=${encodeURIComponent(status)}`, {
        method: "PUT",
      });
      await refreshOrders();
      setNotice(`Order #${orderId} status updated to ${status}.`);
    } catch (err) {
      setError(err.message || "Failed to update order status");
    }
  }

  async function loadAnalytics() {
    setError("");
    try {
      const data = await apiRequest(`/warehouse/analytics?days=${days}`);
      setAnalytics(data || null);
      setNotice("Analytics refreshed.");
    } catch (err) {
      setError(err.message || "Failed to load analytics");
    }
  }

  function logout() {
    localStorage.clear();
    window.location.href = "login.html";
  }

  const pendingCount = useMemo(
    () => (orders || []).filter((order) => ["PENDING", "PLACED", "CONFIRMED", "PACKED"].includes(order.status)).length,
    [orders]
  );

  return (
    <div className="min-h-screen flex">
      <aside className="w-72 bg-white border-r border-slate-200 flex flex-col">
        <div className="px-6 py-6 border-b">
          <h1 className="text-2xl font-bold text-indigo-600">Hyperlocal</h1>
          <p className="text-xs text-slate-500 mt-1">Warehouse Manager Console</p>
        </div>

        <nav className="flex-1 p-4 space-y-2">
          <button
            onClick={() => setActiveTab("products")}
            className={`w-full text-left p-3 rounded-lg ${activeTab === "products" ? "bg-indigo-50 text-indigo-700" : "hover:bg-slate-50 text-slate-700"}`}
          >
            <i className="fa-solid fa-boxes-stacked mr-2"></i> My Products
          </button>
          <button
            onClick={() => setActiveTab("orders")}
            className={`w-full text-left p-3 rounded-lg ${activeTab === "orders" ? "bg-indigo-50 text-indigo-700" : "hover:bg-slate-50 text-slate-700"}`}
          >
            <i className="fa-solid fa-truck mr-2"></i> Orders ({pendingCount})
          </button>
          <button
            onClick={() => setActiveTab("analytics")}
            className={`w-full text-left p-3 rounded-lg ${activeTab === "analytics" ? "bg-indigo-50 text-indigo-700" : "hover:bg-slate-50 text-slate-700"}`}
          >
            <i className="fa-solid fa-chart-line mr-2"></i> Analytics
          </button>
          <button
            onClick={() => setActiveTab("lowStock")}
            className={`w-full text-left p-3 rounded-lg ${activeTab === "lowStock" ? "bg-indigo-50 text-indigo-700" : "hover:bg-slate-50 text-slate-700"}`}
          >
            <i className="fa-solid fa-triangle-exclamation mr-2"></i> Low Stock
          </button>
        </nav>

        <div className="p-4 border-t">
          <p className="text-sm text-slate-700 font-semibold">{name}</p>
          <p className="text-xs text-slate-500 mb-3">Role: {role}</p>
          <button
            onClick={logout}
            className="w-full bg-red-50 text-red-600 text-sm py-2 rounded-lg hover:bg-red-100"
          >
            <i className="fa-solid fa-right-from-bracket mr-2"></i>
            Logout
          </button>
        </div>
      </aside>

      <main className="flex-1">
        <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-8">
          <h2 className="font-semibold text-slate-800">
            {activeTab === "products" && "Warehouse Products"}
            {activeTab === "orders" && "Warehouse Orders"}
            {activeTab === "analytics" && "Sales Analytics"}
            {activeTab === "lowStock" && "Low Stock Monitoring"}
          </h2>
          <button
            onClick={loadInitialData}
            className="text-sm border border-slate-300 px-3 py-1.5 rounded-lg hover:bg-slate-50"
          >
            Refresh
          </button>
        </header>

        <section className="p-8 space-y-4">
          {loading && <div className="text-slate-500">Loading data...</div>}
          {error && <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-lg">{error}</div>}
          {notice && <div className="bg-green-50 border border-green-200 text-green-700 p-3 rounded-lg">{notice}</div>}

          {activeTab === "products" && (
            <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
              {products.map((product) => (
                <div key={product.productId} className="bg-white border border-slate-200 rounded-xl p-4">
                  <div className="flex justify-between items-start gap-4">
                    <div>
                      <h3 className="font-semibold text-slate-800">{product.name}</h3>
                      <p className="text-sm text-slate-500 mt-1">{product.description || "No description"}</p>
                      <p className="text-sm text-indigo-700 mt-2">{formatCurrency(product.price)}</p>
                    </div>
                    <span className={`text-xs px-2 py-1 rounded ${product.available ? "bg-green-100 text-green-700" : "bg-slate-200 text-slate-700"}`}>
                      {product.available ? "Available" : "Unavailable"}
                    </span>
                  </div>
                  <div className="mt-4 flex flex-wrap items-center gap-2">
                    <input
                      id={`stock-${product.productId}`}
                      type="number"
                      min="0"
                      defaultValue={product.quantity}
                      className="w-24 border border-slate-300 rounded px-2 py-1 text-sm"
                    />
                    <button
                      onClick={() => updateStock(product.productId)}
                      className="text-sm bg-indigo-600 text-white px-3 py-1.5 rounded hover:bg-indigo-700"
                    >
                      Update Stock
                    </button>
                    <button
                      onClick={() => toggleAvailability(product.productId, product.available)}
                      className="text-sm border border-slate-300 px-3 py-1.5 rounded hover:bg-slate-50"
                    >
                      {product.available ? "Mark Unavailable" : "Mark Available"}
                    </button>
                  </div>
                </div>
              ))}
              {!products.length && !loading && <p className="text-slate-500">No products found for this warehouse.</p>}
            </div>
          )}

          {activeTab === "orders" && (
            <div className="space-y-3">
              {orders.map((order) => (
                <div key={order.orderId} className="bg-white border border-slate-200 rounded-xl p-4">
                  <div className="flex flex-wrap items-center justify-between gap-3">
                    <div>
                      <h3 className="font-semibold text-slate-800">Order #{order.orderId}</h3>
                      <p className="text-sm text-slate-500">{formatCurrency(order.totalAmount)} | {order.items?.length || 0} items</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs bg-slate-100 text-slate-700 px-2 py-1 rounded">{order.status}</span>
                      <select
                        defaultValue={order.status}
                        onChange={(e) => updateOrderStatus(order.orderId, e.target.value)}
                        className="border border-slate-300 rounded px-2 py-1 text-sm"
                      >
                        {ORDER_STATUSES.map((status) => (
                          <option key={status} value={status}>{status}</option>
                        ))}
                      </select>
                    </div>
                  </div>
                </div>
              ))}
              {!orders.length && !loading && <p className="text-slate-500">No orders assigned to this warehouse yet.</p>}
            </div>
          )}

          {activeTab === "analytics" && (
            <div className="bg-white border border-slate-200 rounded-xl p-6">
              <div className="flex items-end gap-3 mb-5">
                <div>
                  <label className="block text-xs text-slate-500 mb-1">Days</label>
                  <input
                    type="number"
                    min="1"
                    value={days}
                    onChange={(e) => setDays(Number(e.target.value) || 7)}
                    className="w-24 border border-slate-300 rounded px-2 py-1"
                  />
                </div>
                <button
                  onClick={loadAnalytics}
                  className="bg-indigo-600 text-white px-3 py-2 rounded hover:bg-indigo-700 text-sm"
                >
                  Load Analytics
                </button>
              </div>

              {analytics ? (
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="bg-slate-50 p-4 rounded-lg">
                    <p className="text-xs text-slate-500">Warehouse</p>
                    <p className="font-semibold text-slate-800 mt-1">{analytics.warehouseName}</p>
                    <p className="text-sm text-slate-500">{analytics.city}</p>
                  </div>
                  <div className="bg-slate-50 p-4 rounded-lg">
                    <p className="text-xs text-slate-500">Total Sales</p>
                    <p className="font-semibold text-slate-800 mt-1">{formatCurrency(analytics.totalSales)}</p>
                  </div>
                  <div className="bg-slate-50 p-4 rounded-lg">
                    <p className="text-xs text-slate-500">Orders</p>
                    <p className="font-semibold text-slate-800 mt-1">{analytics.orderCount}</p>
                    <p className="text-sm text-slate-500">Avg/day: {formatCurrency(analytics.averageSalesPerDay)}</p>
                  </div>
                </div>
              ) : (
                <p className="text-slate-500">Analytics not available.</p>
              )}
            </div>
          )}

          {activeTab === "lowStock" && (
            <div className="bg-white border border-slate-200 rounded-xl p-4">
              <h3 className="font-semibold text-slate-800 mb-3">Products with stock below threshold</h3>
              <div className="space-y-2">
                {lowStock.map((product) => (
                  <div key={product.productId} className="flex items-center justify-between border border-slate-100 rounded p-3">
                    <div>
                      <p className="font-medium text-slate-800">{product.name}</p>
                      <p className="text-xs text-slate-500">{formatCurrency(product.price)}</p>
                    </div>
                    <span className="text-sm bg-red-100 text-red-700 px-2 py-1 rounded">Stock: {product.quantity}</span>
                  </div>
                ))}
                {!lowStock.length && !loading && (
                  <p className="text-slate-500">No low-stock products.</p>
                )}
              </div>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<WarehouseApp />);
