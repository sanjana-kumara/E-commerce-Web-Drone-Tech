window.onload = function () {

    fetch("LoadTrendingProducts")

            .then(res => res.json())
            .then(products => {

                const container = document.querySelector("#Tranding");
                container.innerHTML = "";

                console.log(products);

                products.forEach(p => {

                    const html = `
                <div class="col-xl-4 col-lg-4 col-md-6 col-sm-8 col-10 d-flex justify-content-center">
                    <div class="single-tranding">
                        <a href="product-details.html?id=${p.id}">
                            <div class="tranding-pro-img text-center">
                                <img 
                                    src="${p.image}" 
                                    alt="${p.name}" 
                                    onerror="this.src='assets/img/default.jpg';"
                                    style="width: 150px; height: 150px; object-fit: cover; border-radius: 10px;"
                                >
                            </div>
                            <div class="tranding-pro-title text-center">
                                <h3>${p.name}</h3>
                                <h4>Product Details</h4>
                            </div>
                            <div class="tranding-pro-price text-center">
                                <div class="price_box">
                                    <span class="current_price">Rs.${p.price.toFixed(2)}</span>
                                    <span class="old_price">Rs.${(p.price + 200).toFixed(2)}</span>
                                </div>
                            </div>
                        </a>
                    </div>
                </div>
            `;
                    container.insertAdjacentHTML("beforeend", html);

                });

            });

};


window.addEventListener("DOMContentLoaded", () => {

    const container = document.querySelector("#Collection");

    container.innerHTML = "<p>Loading...</p>";

    fetch("LoadOtherCollections")
            .then(res => {

                if (!res.ok)
                    throw new Error("Network response was not ok");

                return res.json();

            })

            .then(products => {

                container.innerHTML = "";

                console.log(products);

                products.forEach(p => {

                    const html = `
                <div class="col-xl-4 col-lg-4 col-md-6 col-sm-8 col-10 d-flex justify-content-center">
                    <div class="single-tranding mb-30">
                        <a href="product-details.html?id=${p.id}">
                            <div class="tranding-pro-img text-center">
                                <img 
                                    src="${p.image}" 
                                    alt="${p.name}" 
                                    onerror="this.src='assets/img/default.jpg';"
                                    style="width: 200px; height: 200px; object-fit: cover; border-radius: 10px;"
                                >
                            </div>
                            <div class="tranding-pro-title text-center">
                                <h3>${p.name}</h3>
                                <h4>Product Details</h4>
                            </div>
                            <div class="tranding-pro-price text-center">
                                <div class="price_box">
                                    <span class="current_price">Rs.${p.price.toFixed(2)}</span>
                                    <span class="old_price">Rs.${(p.price + 200).toFixed(2)}</span>
                                </div>
                            </div>
                        </a>
                    </div>
                </div>`;

                    container.insertAdjacentHTML("beforeend", html);

                });

            })

            .catch(err => {

                console.error("Failed to load other products:", err);

                container.innerHTML = "<p class='text-danger'>Failed to load products.</p>";

            });

});
