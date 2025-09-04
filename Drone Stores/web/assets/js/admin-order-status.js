// Load orders dynamically
function loadOrders() {

    fetch("AdminOrderStatus")

            .then(res => res.json())
            .then(data => {

                const container = document.querySelector("#orderList");
                container.innerHTML = "";

                data.forEach((order, index) => {

                    console.log(order);

                    const html = `
<div class="grid grid-cols-1 md:grid-cols-6 gap-2 items-center border-b py-4 fade-in">
  <div>
    <p class="text-sm font-medium">Order ID</p>
    <p>#ORD000${order.orderId}</p>
  </div>
  <div>
    <p class="text-sm font-medium">Customer</p>
    <p>${order.customerName}</p>
  </div>
  <div>
    <p class="text-sm font-medium">Product</p>
    <p>${order.productName}</p>
  </div>
  <div>
    <p class="text-sm font-medium">Price</p>
    <p>Rs. ${order.price}</p>
  </div>
  <div>
    <p class="text-sm font-medium">Qty</p>
    <p>${order.qty}</p>
  </div>
  <div>
    <p class="text-sm font-medium">Status</p>
    <select class="w-full p-2 border rounded-lg" id="status${index}">
      <option ${order.status === 'Processing' ? 'selected' : ''}>Processing</option>
      <option ${order.status === 'Shipped' ? 'selected' : ''}>Shipped</option>
      <option ${order.status === 'Delivered' ? 'selected' : ''}>Delivered</option>
      <option ${order.status === 'Cancelled' ? 'selected' : ''}>Cancelled</option>
    </select>
    <button onclick="updateStatus(${order.orderItemId}, 'status${index}')" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg w-full mt-2">
      Update
    </button>
  </div>
</div>
`;


                    container.innerHTML += html;

                });

                // Animate
                document.querySelectorAll('.fade-in').forEach(el => el.classList.add('visible'));

            })

            .catch(e => {

                document.querySelector("#orderList").innerHTML = "<p class='text-red-600'>Failed to load orders.</p>";

                console.error(e);

            });

}

// Update status
function updateStatus(orderItemId, selectId) {

    const popup = new Notification();

    const newStatus = document.getElementById(selectId).value;

    fetch("AdminOrderStatus", {

        method: "POST",

        headers: {"Content-Type": "application/x-www-form-urlencoded"},

        body: `orderItemId=${orderItemId}&status=${encodeURIComponent(newStatus)}`

    })

            .then(res => res.text())

            .then(result => {

                if (result === "success") {

//                    alert("Status updated successfully");

                    popup.success({

                        message: "Status updated successfully"

                    });

                } else {

//                    alert("Failed to update status");

                    popup.error({

                        message: "Failed to update status"

                    });

                }

            })

            .catch(e => {

                alert("Error updating status");

                popup.error({

                    message: "Error updating status"

                });

                console.error(e);

            });

}

// Load orders on page load
document.addEventListener("DOMContentLoaded", loadOrders);
