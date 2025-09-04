
// Extract product ID from the URL
function getProductIdFromUrl() {

    const params = new URLSearchParams(window.location.search);
    return params.get("id");

}

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


async function loadProductDetails() {

    const productId = getProductIdFromUrl();

    if (!productId) {

        alert("No product ID found in URL.");
        return;

    }

    try {

        const response = await fetch(`GetProductDetails?id=${productId}`);

        const json = await response.json();

        if (json.status) {

            const product = json.product;

            console.log(product);

            // Title and price
            document.querySelector("#breadscrumb").innerText =
                    ` <div class="container">
                <div class="row">
                    <div class="col-12">
                        <div class="breadcrumb_content">
                            <ul>
                                <li><a href="index-2.html">home</a></li>
                                <li><a href="productview.html">Product</a></li>
                                <li>Product details</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>`;

            document.querySelector(".product_d_right h1").innerText = product.title;

            document.querySelector(".current_price").innerText = `Rs. ${Number(product.price).toLocaleString()}.00`;

            document.querySelector(".product_desc").innerHTML = `
            <ul>
                <li>Title :- ${product.title}</li>
                <li>Brand :- ${product.brand.name}</li>
                <li>Model :- ${product.model.name}</li>
                <li>${product.qty} In Stock</li>
            </ul>
`;

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
                    <tr><td class="first_child">Warranty</td><td>${product.warranty || 'N/A'}</td></tr>
            `;

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
                        </li>
                    `;

            }

            // Re-initialize elevateZoom
            setTimeout(() => {

                $("#zoom1").elevateZoom({

                    zoomType: "lens",
                    lensShape: "round",
                    lensSize: 200

                });

            }, 300);

            // Optionally reinitialize Owl Carousel if used
            if ($(".single-product-active").hasClass("owl-carousel")) {

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

            // Set quantity input's max to product.qty
            const qtyInput = document.getElementById("qtyInput");

            if (qtyInput) {

                qtyInput.setAttribute("max", product.qty);

                qtyInput.setAttribute("min", 1);

                // Prevent typing invalid values
                qtyInput.addEventListener("input", () => {

                    let val = parseInt(qtyInput.value);

                    if (isNaN(val) || val < 1) {

                        qtyInput.value = 1;

                    } else if (val > product.qty) {

                        qtyInput.value = product.qty;

                    }

                });

            }

        } else {

            alert("Product not found.");

        }

    } catch (error) {

        console.error("Error loading product:", error);
        alert("Something went wrong while loading product details.");

    }

}

window.addToCart = async function (event) {
    event.preventDefault();

    const productId = document.getElementById("productId")?.value;
    const qty = document.getElementById("qtyInput")?.value;

    console.log("Product ID:", productId);
    console.log("Quantity:", qty);


    if (!productId || !qty) {
        alert("Missing product ID or quantity.");
        return;
    }

    try {
        const formData = new FormData();
        formData.append("productId", productId);
        formData.append("qty", qty);

        const response = await fetch("AddToCart", {
            method: "POST",
            body: formData
        });

        if (response.ok) {
            const result = await response.json();
            if (result.status) {
                alert(result.message || "Product added to cart!");
            } else {
                alert(result.message || "Failed to add product.");
            }
        } else {
            alert("Error: Failed to connect to server.");
        }
    } catch (error) {
        alert("Exception: " + error.message);
    }
};

