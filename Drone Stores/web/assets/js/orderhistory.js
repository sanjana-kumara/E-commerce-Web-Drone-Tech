window.onload = function () {

    fetch("OrderHistory")

            .then((response) => response.json())
            .then((orders) => {

                const ordersDiv = document.getElementById("orders");

                ordersDiv.innerHTML = "";

                if (orders.length === 0) {

                    ordersDiv.innerHTML = `<div class="text-center text-gray-600 text-lg">No orders found.</div>`;
                    return;

                }

                orders.forEach((order, index) => {

                    const orderCard = document.createElement("div");

                    orderCard.className = "bg-white p-6 rounded-xl shadow-lg fade-in";

                    const createdDate = new Date(order.created_at);

                    const formattedDate = createdDate.toISOString().split("T")[0];

                    orderCard.innerHTML = `
            <div class="flex flex-col md:flex-row justify-between items-start md:items-center">
              <div>
                <p class="text-gray-700 font-semibold">Order ID: <span class="text-black">#ORD000${order.id}</span></p>
                <p class="text-sm text-gray-500">Placed on: ${formattedDate}</p>
                <p class="mt-2 text-sm text-gray-600">Items: ${order.items}</p>
              </div>
              <div class="mt-4 md:mt-0 text-right">
                <p class="font-bold text-lg text-gray-900">Rs. ${order.total.toLocaleString()}</p>
                <span class="inline-block px-3 py-1 rounded-full bg-green-100 text-green-700 text-sm mt-2">${order.status}</span>
                </div>
            </div>
          `;

                    ordersDiv.appendChild(orderCard);

                });

                // Animate fade-in
                document.querySelectorAll(".fade-in").forEach((el, i) => {

                    setTimeout(() => el.classList.add("visible"), i * 100);

                });

            })
            .catch((error) => {

                console.error("Failed to load order history:", error);

                document.getElementById("orders").innerHTML = "<div class='text-red-500 text-center'>Failed to load orders.</div>";

            });

};