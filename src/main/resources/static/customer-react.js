const { useEffect, useMemo, useState } = React;

const API_BASE = "http://localhost:8080/hyperlocaldelivery/api";

function parseApiResponse(json) {
  if (json && typeof json === "object" && "data" in json) {
    return json.data;
  }
  return json;
}

async function apiRequest(path, options = {}) {
  const token = localStorage.getItem("token");
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

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
    throw new Error(message);
  }

  return parseApiResponse(body);
}

function currency(value) {
  const amount = Number(value || 0);
  return `Rs ${amount.toFixed(2)}`;
}

function CustomerApp() {
  const [view, setView] = useState("products");
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [placingOrder, setPlacingOrder] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  const userName = localStorage.getItem("userName") || "Customer";
  const role = localStorage.getItem("role") || "CUSTOMER";

  useEffect(() => {
    if (!localStorage.getItem("token")) {
      window.location.href = "login.html";
      return;
    }

    if (role !== "CUSTOMER") {
      setError("Logged-in user is not a customer account.");
      return;
    }

    initializeData();
  }, []);

  async function initializeData() {
    setLoading(true);
    setError("");
    setNotice("");
    try {
      const productData = await apiRequest("/public/products");
      setProducts(Array.isArray(productData) ? productData : []);

      try {
        const cartData = await apiRequest("/customer/cart");
        setCart(cartData || { items: [], totalAmount: 0 });
      } catch (cartErr) {
        setCart({ items: [], totalAmount: 0 });
        setNotice("Cart will be created automatically when you add your first item.");
      }
    } catch (err) {
      setError(err.message || "Failed to load customer data");
    } finally {
      setLoading(false);
    }
  }

  async function loadOrders() {
    setLoading(true);
    setError("");
    try {
      const orderData = await apiRequest("/customer/orders");
      setOrders(Array.isArray(orderData) ? orderData : []);
      setView("orders");
    } catch (err) {
      setError(err.message || "Failed to load orders");
    } finally {
      setLoading(false);
    }
  }

  async function loadCart() {
    setLoading(true);
    setError("");
    setNotice("");
    try {
      const cartData = await apiRequest("/customer/cart");
      setCart(cartData || { items: [], totalAmount: 0 });
      setView("cart");
    } catch (err) {
      setCart({ items: [], totalAmount: 0 });
      setView("cart");
      setNotice("Your cart is empty. Add products to create a cart.");
    } finally {
      setLoading(false);
    }
  }

  async function addToCart(productId) {
    setError("");
    setNotice("");
    try {
      const updatedCart = await apiRequest("/customer/cart/add", {
        method: "POST",
        body: JSON.stringify({ productId, quantity: 1 }),
      });
      setCart(updatedCart);
      setNotice("Added to cart");
    } catch (err) {
      setError(err.message || "Failed to add product to cart");
    }
  }

  async function updateItemQuantity(cartItemId, quantity) {
    const parsed = Number(quantity);
    if (!Number.isFinite(parsed) || parsed <= 0) {
      setError("Quantity must be at least 1");
      return;
    }

    setError("");
    try {
      const updatedCart = await apiRequest(`/customer/cart/update/${cartItemId}`, {
        method: "PUT",
        body: JSON.stringify({ cartItemId, quantity: parsed }),
      });
      setCart(updatedCart);
    } catch (err) {
      setError(err.message || "Failed to update quantity");
    }
  }

  async function removeCartItem(cartItemId) {
    setError("");
    try {
      const updatedCart = await apiRequest(`/customer/cart/remove/${cartItemId}`, {
        method: "DELETE",
      });
      setCart(updatedCart);
    } catch (err) {
      setError(err.message || "Failed to remove cart item");
    }
  }

  async function clearCart() {
    setError("");
    setNotice("");
    try {
      await apiRequest("/customer/cart/clear", { method: "DELETE" });
      setCart({ items: [], totalAmount: 0 });
      setNotice("Cart cleared");
    } catch (err) {
      setError(err.message || "Failed to clear cart");
    }
  }

  async function placeOrder() {
    const items = cart?.items || [];
    if (!items.length) {
      setError("Cart is empty");
      return;
    }

    const ok = window.confirm(
      `Place order for ${items.length} items worth ${currency(cart.totalAmount)}?`
    );
    if (!ok) return;

    setPlacingOrder(true);
    setError("");
    setNotice("");

    try {
      await apiRequest("/customer/orders/place", { method: "POST" });
      setNotice("Order placed successfully");
      let updatedCart = { items: [], totalAmount: 0 };
      try {
        const cartResponse = await apiRequest("/customer/cart");
        updatedCart = cartResponse || { items: [], totalAmount: 0 };
      } catch (cartErr) {
        updatedCart = { items: [], totalAmount: 0 };
      }

      const updatedOrders = await apiRequest("/customer/orders");

      setCart(updatedCart);
      setOrders(Array.isArray(updatedOrders) ? updatedOrders : []);
      setView("orders");
    } catch (err) {
      setError(err.message || "Failed to place order");
    } finally {
      setPlacingOrder(false);
    }
  }

  function logout() {
    localStorage.clear();
    window.location.href = "login.html";
  }

  const cartCount = useMemo(() => {
    const items = cart?.items || [];
    return items.reduce((sum, item) => sum + Number(item.quantity || 0), 0);
  }, [cart]);

  const activeTitle =
    view === "products" ? "Browse Products" : view === "cart" ? "My Cart" : "My Orders";

  return (
    <div className="min-h-screen flex">
      <aside className="w-64 bg-white border-r border-gray-200 flex flex-col">
        <div className="p-6 border-b">
          <h2 className="text-2xl font-bold text-indigo-600 italic">Hyperlocal.</h2>
        </div>
        <nav className="flex-1 p-4 space-y-2">
          <button
            onClick={() => setView("products")}
            className={`w-full text-left p-3 rounded-xl transition ${
              view === "products" ? "bg-indigo-50 text-indigo-700" : "hover:bg-gray-50 text-gray-600"
            }`}
          >
            <i className="fa-solid fa-store mr-2"></i> Products
          </button>
          <button
            onClick={loadCart}
            className={`w-full text-left p-3 rounded-xl transition ${
              view === "cart" ? "bg-indigo-50 text-indigo-700" : "hover:bg-gray-50 text-gray-600"
            }`}
          >
            <i className="fa-solid fa-cart-shopping mr-2"></i> Cart ({cartCount})
          </button>
          <button
            onClick={loadOrders}
            className={`w-full text-left p-3 rounded-xl transition ${
              view === "orders" ? "bg-indigo-50 text-indigo-700" : "hover:bg-gray-50 text-gray-600"
            }`}
          >
            <i className="fa-solid fa-box mr-2"></i> My Orders
          </button>
        </nav>
        <div className="p-4 border-t">
          <div className="text-xs text-gray-500 mb-3">Signed in as {userName}</div>
          <button
            onClick={logout}
            className="w-full flex items-center justify-center space-x-2 text-red-500 hover:bg-red-50 p-2 rounded-lg transition text-sm"
          >
            <i className="fa-solid fa-right-from-bracket"></i>
            <span>Logout</span>
          </button>
        </div>
      </aside>

      <main className="flex-1 overflow-y-auto">
        <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-8">
          <h1 className="text-lg font-semibold text-gray-800">{activeTitle}</h1>
          <div className="text-sm text-gray-600">Role: {role}</div>
        </header>

        <section className="p-8 space-y-4">
          {loading && <div className="text-gray-500">Loading...</div>}
          {error && <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-lg">{error}</div>}
          {notice && (
            <div className="bg-green-50 border border-green-200 text-green-700 p-3 rounded-lg">{notice}</div>
          )}

          {view === "products" && (
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
              {(products || []).map((p) => (
                <div key={p.productId} className="bg-white p-5 rounded-xl shadow-sm border border-gray-100">
                  <h3 className="font-bold text-gray-800 mb-1">{p.name}</h3>
                  <p className="text-xs text-gray-500 mb-2">{p.category || "General"}</p>
                  <p className="text-sm text-gray-600 mb-3">{p.description || ""}</p>
                  <div className="flex justify-between items-center">
                    <div>
                      <div className="text-indigo-700 font-semibold">{currency(p.price)}</div>
                      <div className="text-xs text-gray-500">Stock: {p.quantity}</div>
                    </div>
                    <button
                      onClick={() => addToCart(p.productId)}
                      className="bg-indigo-600 text-white px-3 py-2 rounded-lg text-sm hover:bg-indigo-700"
                    >
                      Add to Cart
                    </button>
                  </div>
                </div>
              ))}
              {!products?.length && !loading && (
                <div className="text-gray-500 col-span-full">No products available.</div>
              )}
            </div>
          )}

          {view === "cart" && (
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
              <div className="p-5 border-b flex justify-between items-center">
                <h2 className="font-semibold text-gray-800">Cart Items</h2>
                <div className="space-x-2">
                  <button
                    onClick={clearCart}
                    className="px-3 py-2 rounded-lg border border-gray-300 text-sm hover:bg-gray-50"
                  >
                    Clear Cart
                  </button>
                  <button
                    onClick={placeOrder}
                    disabled={placingOrder || !(cart?.items || []).length}
                    className="px-3 py-2 rounded-lg bg-green-600 text-white text-sm hover:bg-green-700 disabled:opacity-60"
                  >
                    {placingOrder ? "Placing..." : "Place Order"}
                  </button>
                </div>
              </div>
              <div className="p-5 space-y-4">
                {(cart?.items || []).map((item) => (
                  <div key={item.cartItemId} className="border rounded-lg p-4 flex flex-col md:flex-row md:items-center md:justify-between gap-3">
                    <div>
                      <div className="font-medium text-gray-800">{item.product?.name}</div>
                      <div className="text-sm text-gray-500">Price: {currency(item.price)}</div>
                    </div>
                    <div className="flex items-center gap-2">
                      <input
                        type="number"
                        min="1"
                        defaultValue={item.quantity}
                        id={`qty-${item.cartItemId}`}
                        className="w-20 border rounded px-2 py-1"
                      />
                      <button
                        onClick={() => {
                          const value = document.getElementById(`qty-${item.cartItemId}`).value;
                          updateItemQuantity(item.cartItemId, value);
                        }}
                        className="px-3 py-1 rounded bg-indigo-600 text-white text-sm"
                      >
                        Update
                      </button>
                      <button
                        onClick={() => removeCartItem(item.cartItemId)}
                        className="px-3 py-1 rounded bg-red-50 text-red-600 text-sm"
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                ))}
                {!(cart?.items || []).length && !loading && (
                  <div className="text-gray-500">Your cart is empty.</div>
                )}
                <div className="pt-2 border-t text-right font-semibold text-gray-800">
                  Total: {currency(cart?.totalAmount || 0)}
                </div>
              </div>
            </div>
          )}

          {view === "orders" && (
            <div className="space-y-4">
              {(orders || []).map((order) => (
                <div key={order.orderId} className="bg-white border border-gray-100 rounded-xl p-5 shadow-sm">

                  <div className="flex flex-wrap justify-between gap-3 mb-3">
                    <div className="font-semibold text-gray-800">Order #{order.orderId}</div>
                    <div className="text-sm px-2 py-1 rounded bg-gray-100 text-gray-700">{order.status}</div>
                  </div>
                  <div className="text-sm text-gray-600 mb-2">Amount: {currency(order.totalAmount)}</div>
                  <div className="text-xs text-gray-500">
                    Placed: {order.createdAt ? new Date(order.createdAt).toLocaleString() : "-"}
                  </div>
                  <div className="mt-3 space-y-1 text-sm text-gray-700">
                    {(order.items || []).map((item, idx) => (
                      <div key={`${order.orderId}-${idx}`}>• {item.productName} x {item.quantity}</div>
                    ))}
                  </div>
                </div>
              ))}
              {!orders?.length && !loading && (
                <div className="text-gray-500">No orders yet.</div>
              )}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<CustomerApp />);
