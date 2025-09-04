
// Extract product ID from the URL
function getProductIdFromUrl() {

    const params = new URLSearchParams(window.location.search);
    return params.get("id");

}

// Map color names to bootstrap/tailwind-like classes
function getColorClass(colorName) {

    switch (colorName.toLowerCase()) {

        case "black":
            return "bg-dark";
        case "white":
            return "bg-white border";
        case "red":
            return "bg-danger";
        case "orange":
            return "bg-warning";
        case "gray":
        case "grey":
            return "bg-secondary";
        default:
            return "bg-light";

    }

}

// Load detailed product info by ID from URL
async function loadProductDetails() {

    const popup = new Notification();

    const productId = getProductIdFromUrl();

    if (!productId) {

//        alert("No product ID found in URL.");

        popup.error({

            message: "No product ID found in URL."

        });

        return;

    }

    try {

        const response = await fetch(`GetProductDetails?id=${productId}`);

        const json = await response.json();

        if (json.status) {

            const product = json.product;

            // Title and price
            document.querySelector(".product_d_right h1").innerText = product.title;

            document.querySelector(".current_price").innerText = `Rs. ${Number(product.price).toLocaleString()}.00`;

            document.querySelector(".product_desc").innerHTML = `
                <ul>
            
                    <li>Title :- ${product.title}</li>
                    <li>Brand :- ${product.brand.name}</li>
                    <li>Model :- ${product.model.name}</li>
                    <li>${product.qty} In Stock</li>
            
                </ul>`;

            // Description
            document.querySelector(".product_info_content p").innerText = product.description || "No description";

            // Specifications table
            const specTable = document.querySelector(".product_d_table tbody");

            specTable.innerHTML = `
                <tr><td class="first_child">Title</td><td>${product.title}</td></tr>
                <tr><td class="first_child">Brand</td><td>${product.brand.name}</td></tr>
                <tr><td class="first_child">Model</td><td>${product.model.name}</td></tr>
                <tr><td class="first_child">Flight Time</td><td>${product.flightTime || 'N/A'}</td></tr>
                <tr><td class="first_child">Range</td><td>${product.range || 'N/A'}</td></tr>
                <tr><td class="first_child">Features</td><td>${product.features || 'N/A'}</td></tr>
                <tr><td class="first_child">Warranty</td><td>${product.warranty || 'N/A'}</td></tr>`;

            // Main Image
            const mainImg = document.getElementById("zoom1");

            const mainImgPath = `product-images/${product.id}/image1.png`;

            mainImg.src = mainImgPath;

            mainImg.setAttribute("data-zoom-image", mainImgPath);

            // Thumbnails
            const gallery = document.getElementById("gallery_01");

            gallery.innerHTML = "";
            for (let i = 1; i <= 3; i++) {
                const imgPath = `product-images/${product.id}/image${i}.png`;
                gallery.innerHTML += `
                    <li>
                        <a href="#" class="elevatezoom-gallery" data-image="${imgPath}" data-zoom-image="${imgPath}">
                            <img src="${imgPath}" alt="thumb-${i}" />
                        </a>
                    </li>`;

            }

            // Re-initialize elevateZoom if jQuery is loaded and elevateZoom plugin used
            setTimeout(() => {

                if (typeof $ !== "undefined" && $("#zoom1").elevateZoom) {

                    $("#zoom1").elevateZoom({
                        zoomType: "lens",
                        lensShape: "round",
                        lensSize: 200
                    });

                }

            }, 300);

            // Optionally reinitialize Owl Carousel if used
            if (typeof $ !== "undefined" && $(".single-product-active").hasClass("owl-carousel")) {

                $(".single-product-active").owlCarousel('destroy');

                $(".single-product-active").owlCarousel({

                    items: 4,
                    margin: 10,
                    nav: true,
                    dots: false

                });

            }

            // Render Single Color Option
            const colorList = document.getElementById("colorOptions");

            if (colorList && product.color && product.color.name) {

                colorList.innerHTML = "";

                const li = document.createElement("li");

                li.className = `${getColorClass(product.color.name)} rounded-circle mx-1`;

                li.style.width = "25px";

                li.style.height = "25px";

                li.style.display = "inline-block";

                li.innerHTML = `<a href="#" title="${product.color.name}" style="display:block;width:100%;height:100%;border-radius:50%;"></a>`;

                colorList.appendChild(li);

            }


        } else {

//            alert("Product not found.");

            popup.error({

                message: "Product not found."

            });

        }

    } catch (error) {

        console.error("Error loading product:", error);

        popup.error({

            message: "Something went wrong while loading product details."

        });

    }

}

// Load all approved products and show in a table with action buttons
function loadsProduct() {

    const popup = new Notification();

    $.get("ApprovedProducts", function (products) {

        const tbody = document.querySelector("tbody");

        tbody.innerHTML = "";

        products.forEach(p => {

            const seller = p.user_id ? `${p.user_id.first_name || ''} ${p.user_id.last_name || ''}` : 'Unknown';

            const imagePath = `product-images/${p.id}/image1.png`;

            const row = `
                <tr>
                    <td class="px-4 py-2">
                        <img src="${imagePath}" class="w-16 h-16 rounded" alt="Product Image" />
                    </td>
                    <td class="px-4 py-2">${p.title}</td>
                    <td class="px-4 py-2">${seller}</td>
                    <td class="px-4 py-2">
                        <input type="number" value="${p.qty}" min="1" max="100"
                               class="border px-2 py-1 rounded w-20" readonly />
                    </td>
                    <td class="px-4 py-2">Rs.${p.price.toFixed(2)}</td>
                    <td class="px-4 py-2">
                        <div class="flex flex-col md:flex-row md:space-x-2 space-y-2 md:space-y-0">
                            <button class="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600 approve-btn" data-id="${p.id}">Approve</button>
                        </div>
                    </td>
                </tr>
            `;

            tbody.insertAdjacentHTML("beforeend", row);

        });

        document.querySelectorAll(".approve-btn").forEach(btn => {

            btn.addEventListener("click", async e => {

                const id = e.target.getAttribute("data-id");

                const params = new URLSearchParams();
                params.append("id", id);

                try {
                    const res = await fetch("ProductApprove", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        body: params.toString()
                    });

                    const result = await res.json();
                    console.log(result);

                    if (result.status) {

                        window.location = "admin-product-management.html";

                    }

                } catch (error) {

                    console.error("Fetch error:", error);

                    popup.error({

                        message: "Network or server error."

                    });

                }

            });

        });

    });

}

document.addEventListener("DOMContentLoaded", function () {

    loadActiveProducts();

});

function loadActiveProducts() {

    fetch("LoadActiveProducts")

            .then(response => {

                if (!response.ok) {

                    throw new Error("Failed to fetch products");

                }

                return response.json();

            })

            .then(products => {

                const tbody = document.getElementById("product-table-body");
                tbody.innerHTML = ""; // clear old rows

                products.forEach(p => {

                    console.log(p);

                    const row = `
                    <tr>
                        <td class="product_thumb">
                            <img src="product-images/${p.id}/image1.png" alt="${p.title}" style="width: 50px; height: 50px;">
                        </td>
                        <td class="product_name">${p.title}</td>
                        <td class="product-price">Rs. ${p.price}</td>
                        <td class="product_quantity">${p.qty}</td>
                        <td class="product_quantity text-green-500 font-semibold">Active</td>
                        <td class="product_total">
                            <button class="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600" onclick="deactivateProduct(${p.id})">Deactivate</button>
                        </td>
                    </tr>
                `;

                    tbody.insertAdjacentHTML("beforeend", row);

                });

            })

            .catch(error => console.error(error));

}

function deactivateProduct(productId) {

    const popup = new Notification({position: 'center', isHideTitle: false, duration: 0});

    popup.dialog({

        title: "Confirm Deactivation",

        message: "Are you sure you want to deactivate this product?",

        callback: function (result) {

            if (result === 'ok') {

                // Proceed with fetch request
                fetch("LoadActiveProducts", {

                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: "productId=" + encodeURIComponent(productId)

                })

                        .then(response => {

                            if (!response.ok) {

                                throw new Error("Failed to deactivate product");

                            }

                            return response.json();

                        })

                        .then(data => {

                            if (data.status === true) {

                                popup.success({

                                    title: "Success",
                                    message: "Product deactivated successfully!"

                                });

                                loadsProduct();

                                location.reload();

                            } else {

                                popup.error({

                                    title: "Error",
                                    message: "Failed to deactivate product: " + (data.message || "Unknown error")

                                });

                            }

                        })

                        .catch(error => {

                            console.error("Error:", error);

                            popup.error({

                                title: "Error",

                                message: "An error occurred while deactivating the product."

                            });

                        });

            }

        }

    });


}
