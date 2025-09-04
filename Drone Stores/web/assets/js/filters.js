let currentPage = 0;
let productsPerPage = 6;
let totalProductCount = 45; // Example total products

async function loadFilters() {

    const response = await fetch("LoadFiltersServlet");

    const json = await response.json();

    if (json.status) {

        // Load Brands (checkboxes)
        const brandFilter = document.getElementById("brandFilter");

        json.brands.forEach((brand, i) => {

            const div = document.createElement("div");
            const input = document.createElement("input");
            const label = document.createElement("label");

            div.className = "form-check";

            input.type = "checkbox";

            input.className = "form-check-input";

            input.value = brand.id;

            input.id = `brand${i}`;

            label.className = "form-check-label";

            label.setAttribute("for", `brand${i}`);

            label.innerText = brand.name;

            div.appendChild(input);

            div.appendChild(label);

            brandFilter.appendChild(div);

        });

        // Load Models (radio buttons)
        const modelFilter = document.getElementById("modelFilter");

        json.models.forEach((model, i) => {

            const label = document.createElement("label");

            const input = document.createElement("input");

            label.className = "form-check-label";

            input.type = "radio";

            input.name = "model";

            input.className = "form-check-input";

            input.value = model.id;

            label.appendChild(input);

            label.append(" " + model.name);

            modelFilter.appendChild(label);

            modelFilter.appendChild(document.createElement("br"));

        });

        // Load Categories (radio buttons)
        const categoryFilter = document.getElementById("categoryFilter");

        json.categories.forEach((cat, i) => {

            const label = document.createElement("label");

            const input = document.createElement("input");

            label.className = "form-check-label";

            input.type = "radio";

            input.name = "category";

            input.className = "form-check-input";

            input.value = cat.id;

            label.appendChild(input);

            label.append(" " + cat.name);

            categoryFilter.appendChild(label);

            categoryFilter.appendChild(document.createElement("br"));

        });

        // Load Colors (radio buttons)
        const colorFilter = document.getElementById("colorFilter");

        json.colors.forEach((color, i) => {

            const label = document.createElement("label");

            const input = document.createElement("input");

            label.className = "form-check-label";

            input.type = "radio";

            input.name = "color";

            input.className = "form-check-input";

            input.value = color.id;

            label.appendChild(input);

            label.append(" " + color.name);

            colorFilter.appendChild(label);

            colorFilter.appendChild(document.createElement("br"));

        });

    } else {

        alert("Failed to load filters: Server returned error status.");

    }

}

async function loadData() {

    const popup = new Notification();

    const response = await fetch("LoadData");

    if (response.ok) {

        const json = await response.json();

        if (json.status) {

            loadOptions("brand", json.brandList, "name");

            loadOptions("condition", json.qualityList, "value");

            loadOptions("color", json.colorList, "value");

            loadOptions("storage", json.storageList, "value");

            updateProductView(json);

        } else {

            popup.error({message: "Something went wrong"});

        }

    } else {

        popup.error({message: "Something went wrong"});

    }

}

function loadOptions(prefix, dataList, property) {

    const options = document.getElementById(prefix + "-options");

    const liTemplate = document.getElementById(prefix + "-li");

    options.innerHTML = "";

    dataList.forEach(item => {

        const liClone = liTemplate.cloneNode(true);

        if (prefix === "color") {

            liClone.style.borderColor = "black";

            liClone.querySelector("#" + prefix + "-a").style.backgroundColor = item[property];

        } else {

            liClone.querySelector("#" + prefix + "-a").textContent = item[property];

        }

        options.appendChild(liClone);

    });

    const allListItems = options.querySelectorAll("li");

    allListItems.forEach(li => {

        li.addEventListener("click", () => {

            allListItems.forEach(item => item.classList.remove("chosen"));

            li.classList.add("chosen");

        });

    });

}

function updateProductView(data) {

    const sortSelect = document.getElementById("sortSelect");

    // Set filteredProducts globally for use in pagination and sorting
    filteredProducts = data.productList;

    totalProductCount = data.allProductCount;

    // Apply initial sorting (low to high by default)
    applySorting();

    renderProducts(filteredProducts);

    // Show result range
    updateResultCountDisplay(filteredProducts.length);

    // Sorting
    sortSelect.onchange = () => {

        applySorting();

        renderProducts(filteredProducts);

        updateResultCountDisplay(filteredProducts.length);

    };

}

function applySorting() {
    
    const sortValue = document.getElementById("sortSelect").value;

    if (sortValue === "low-high") {
        
        filteredProducts.sort((a, b) => a.price - b.price);
        
    } else if (sortValue === "high-low") {
        
        filteredProducts.sort((a, b) => b.price - a.price);
        
    }
    
}


function renderProducts(products) {

    const container = document.getElementById("productContainer");

    container.innerHTML = "";

    products.forEach(product => {

        const imagePath = `product-images/${product.id}/image1.png`;

        const card = document.createElement("div");

        card.className = "bg-white shadow-md rounded-lg overflow-hidden p-4";

        card.innerHTML = `
            <img src="${imagePath}" onerror="this.src='https://via.placeholder.com/300x200'" alt="${product.title}" class="w-full h-40 object-cover mb-3 rounded" />
            <h3 class="text-lg font-semibold">${product.title}</h3>
            <p class="text-green-700 font-bold text-md">Rs. ${Number(product.price).toLocaleString()}</p>
            <a href="product-details.html?id=${product.id}" class="mt-3 inline-block text-center bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded w-full">View Details</a>
        `;

        container.appendChild(card);

    });

}


// Product Result
async function searchProduct(firstResult = 0) {

    currentPage = firstResult / productsPerPage;  // update currentPage properly

    const selectedBrands = Array.from(document.querySelectorAll("#brandFilter input:checked")).map(cb => cb.value);

    const selectedModel = document.querySelector("#modelFilter input:checked")?.value || "";

    const selectedCategory = document.querySelector("#categoryFilter input:checked")?.value || "";

    const selectedColor = document.querySelector("#colorFilter input:checked")?.value || "";

    const minPrice = document.getElementById("minPrice").value;

    const maxPrice = document.getElementById("maxPrice").value;

    const offset = firstResult;

    console.log(`Loading products from offset: ${offset}`);

    const requestData = {

        brands: selectedBrands,
        model: selectedModel,
        category: selectedCategory,
        color: selectedColor,
        minPrice: minPrice,
        maxPrice: maxPrice,
        offset: offset,
        limit: productsPerPage

    };

    const response = await fetch("SearchProductServlet", {

        method: "POST",

        headers: {"Content-Type": "application/json"},

        body: JSON.stringify(requestData)

    });

    if (response.ok) {

        const json = await response.json();

        if (json.status) {

            updateProductView(json);

            totalProductCount = json.allProductCount;

            updatePagination();

        } else {

            alert("No matching products found.");

        }

    } else {

        alert("Search failed. Please try again.");

}

}


function loadProducts() {

    searchProduct(0);

    updatePagination();

}

function resetFilters() {

    // Uncheck all brand checkboxes
    document.querySelectorAll("#brandFilter input[type='checkbox']").forEach(cb => cb.checked = false);

    // Uncheck model, category, color radio buttons
    document.querySelectorAll("#modelFilter input[type='radio']").forEach(rb => rb.checked = false);

    document.querySelectorAll("#categoryFilter input[type='radio']").forEach(rb => rb.checked = false);

    document.querySelectorAll("#colorFilter input[type='radio']").forEach(rb => rb.checked = false);

    // Reset price fields
    document.getElementById("minPrice").value = 0;

    document.getElementById("maxPrice").value = 100000;

    // Reload products with default filters
    searchProduct(0);

}

function updatePagination() {

    let container = document.getElementById("pagination-container");

    container.innerHTML = "";

    let baseButton = document.getElementById("pagination-button-template");

    let totalPages = Math.ceil(totalProductCount / productsPerPage);

    // Prev Button
    let prevBtn = baseButton.cloneNode(true);

    prevBtn.style.display = "inline-block";

    prevBtn.textContent = "Prev";

    prevBtn.className = "btn btn-primary me-2";

    prevBtn.disabled = currentPage < 0;

    prevBtn.addEventListener("click", async () => {

        if (currentPage > 0) {

            currentPage--;
            await searchProduct(currentPage * productsPerPage);

        }

    });

    container.appendChild(prevBtn);

    // Next Button
    let nextBtn = baseButton.cloneNode(true);

    nextBtn.style.display = "inline-block";

    nextBtn.textContent = "Next";

    nextBtn.className = "btn btn-primary";

    nextBtn.disabled = currentPage >= totalPages - 1; // disable if last page

    nextBtn.addEventListener("click", async () => {

        if (currentPage < totalPages - 1) {

            currentPage++;

            await searchProduct(currentPage * productsPerPage);

        }

    });

    container.appendChild(nextBtn);

}

// Initial call
updatePagination();

window.onload = () => {

    loadFilters();

    loadProducts();

};
