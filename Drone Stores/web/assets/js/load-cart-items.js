

async function loadCartItems() {

    const popup = new Notification();


    try {

        const res = await fetch("LoadCartItems");

        const data = await res.json();

        if (!data.status) {

//            alert(data.message || "Cart is empty or failed to load.");

            popup.error({
                message: data.message || "Cart is empty or failed to load."
            });

            return;

        }

        const cartContainer = document.getElementById("cart-item-container");

        cartContainer.innerHTML = "";

        let subtotal = 0;

        data.cartItems.forEach(item => {

            const p = item.product;
            const qty = item.qty;
            const price = p.price;
            const total = price * qty;
            subtotal += total;

            const row = `
                <tr>
                    <td class="product_thumb">
                        <a href="#"><img src="product-images/${p.id}/image1.png" width="70" alt=""></a>
                    </td>
                    <td class="product_name"><a href="#">${p.title}</a></td>
                    <td class="product-price">Rs. ${price.toFixed(2)}</td>
                    <td class="product_quantity">
                        <label>Quantity</label>
                        <input type="number" min="1" value="${qty}" data-id="${p.id}" />
                    </td>
                    <td class="product_total">Rs. ${total.toFixed(2)}</td>
                    <td class="product_remove">
                        <a href="#" onclick="removeFromCart(${p.id})"><i class="ion-android-close"></i></a>
                    </td>
                </tr>
            `;

            cartContainer.innerHTML += row;

        });

        // Update totals
        document.getElementById("order-total").innerText = subtotal.toFixed(2);


    } catch (err) {

        console.error("Cart load failed:", err);

//        alert("Failed to load cart.");

        popup.error({

            message: "Failed to load cart."

        });

    }

}

function removeFromCart(productId) {

    const popup = new Notification();


//    alert(productId);

    fetch(`RemoveCartItem?id=${productId}`, {

        method: 'POST',

        headers: {
            'Content-Type': 'application/json'
        },

        // If needed, you can pass a body:
        body: JSON.stringify({id: productId})

    })

            .then(response => response.json())
            .then(data => {

                console.log(data);

                if (!data.status) {

//                    alert(data.message);

                    popup.success({

                        message: data.message

                    });

                    return;

                }

//                alert("Item removed from cart successfully!");

                popup.info({

                    message: "Item removed from cart successfully!"

                });

                // Reload the cart
                loadCartItems();

            })

            .catch(err => {

                console.error("Failed to remove item:", err);

//                alert("Error removing item from cart.");

                popup.error({

                    message: "Error removing item from cart."

                });

            });

}
