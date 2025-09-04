function loadUsers() {

    fetch("LoadUsersServlet")
            .then(response => response.json())
            .then(users => {

                const tbody = document.getElementById("user-table-body");
                tbody.innerHTML = "";

                users.forEach(user => {

                    const tr = document.createElement("tr");

                    const fullName = `${user.first_name} ${user.last_name}`;
                    const isActive = user.verification === "Verified";

                    tr.innerHTML = `
                    <td class="product_name"><a href="#">${user.email}</a></td>
                    <td class="product-price">${fullName}</td>
                    <td class="product_quantity px-4 py-2 border ${isActive ? 'text-green-500' : 'text-red-500'}">
                        ${isActive ? "Active" : "Deactivated"}
                    </td>
                    <td class="product_total">
                        <button 
                            data-user-id="${user.id}" 
                            onclick="toggleStatus(${user.id}, '${isActive ? 'Block' : 'Active'}')"
                            class="${isActive ? 'bg-yellow-500 hover:bg-yellow-600' : 'bg-green-500 hover:bg-green-600'} text-white px-3 py-1 rounded">
                            ${isActive ? "Block" : "Active"}
                        </button>
                    </td>
                `;

                    tbody.appendChild(tr);

                });

            })

            .catch(error => console.error("Error loading users:", error));

}

function toggleStatus(userId, action) {

    fetch('ToggleUserStatus', {

        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },

        body: `userId=${userId}&action=${action}`

    })
            .then(response => response.json())
            .then(data => {

                if (data.success) {

                    const button = document.querySelector(`button[data-user-id="${userId}"]`);
                    const row = button.closest("tr");

                    const statusCell = row.children[2];
                    statusCell.textContent = data.newStatus;
                    statusCell.className = `product_quantity px-4 py-2 border ${data.newStatus === "Active" ? "text-green-500" : "text-red-500"}`;

                    button.textContent = data.newStatus === "Active" ? "Block" : "Active";
                    button.className = `${data.newStatus === "Active"
                            ? "bg-yellow-500 hover:bg-yellow-600"
                            : "bg-green-500 hover:bg-green-600"} text-white px-3 py-1 rounded`;

                    location.reload();


                } else {

                    alert("Failed to update status: " + (data.message || "Unknown error"));

                }

            })

            .catch(error => {

                console.error("Error toggling user status:", error);

            });

}
