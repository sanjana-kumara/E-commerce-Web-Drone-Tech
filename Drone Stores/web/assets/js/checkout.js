
payhere.onCompleted = function onCompleted(orderId) {

    const popup = new Notification();
    popup.success({
        message: "Payment completed. OrderID:" + orderId
    });

};

// Payment window closed
payhere.onDismissed = function onDismissed() {

    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");

};

// Error occurred
payhere.onError = function onError(error) {

    // Note: show an error page
    console.log("Error:" + error);

};


window.renderOrderSummary = async function () {

    const popup = new Notification();

    const selectedRadio = document.querySelector('input[name="address"]:checked');

    if (!selectedRadio) {

//        alert("Please select a delivery address.");

        popup.info({

            message: "Please select a delivery address."

        });

        return;

    }


    const addressId = selectedRadio.value;
    const city = selectedRadio.dataset.city || "";

    // Calculate shipping locally
    const shipping = city.toLowerCase() === "colombo" ? 100.0 : 200.0;

    const shippingType = city.toLowerCase() === "colombo" ? "Within Colombo" : "Outside Colombo";

    const tbody = document.querySelector(".order_table tbody");

    const tfoot = document.querySelector(".order_table tfoot");

    if (!tbody || !tfoot) {

//        alert("Order summary table structure is missing.");

        popup.error({

            message: "Order summary table structure is missing."

        });

        return;

    }

    const response = await fetch(`GetCheckoutServlet?addressId=${encodeURIComponent(addressId)}`);

    let data;

    try {

        // Try parsing as JSON even if Content-Type is wrong
        data = await response.json();

    } catch (e) {

        const rawText = await response.text();

        console.error("Expected JSON, got:", rawText);

        popup.error({

            message: "Server returned invalid data. Please try again."

        });

        return;

    }

    if (!data.status) {

        popup.error({

            message: data.message

        });

        return;

    }

    // Build cart table
    tbody.innerHTML = "";
    let subtotal = 0;

    data.cart.forEach(item => {

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${item.product || "Unnamed Product"} <strong>× ${item.qty || 0}</strong></td>
            <td>Rs.${(item.total || 0).toFixed(2)}</td>
        `;

        tbody.appendChild(row);

        subtotal += item.total || 0;

    });

    const grandtotal = subtotal + shipping;

    // Update totals
    tfoot.innerHTML = `
        <tr>
            <th>Cart Subtotal</th>
            <td>Rs.${subtotal.toFixed(2)}</td>
        </tr>
        <tr>
            <th>Shipping (${shippingType})</th>
            <td><strong>Rs.${shipping.toFixed(2)}</strong></td>
        </tr>
        <tr class="order_total">
            <th>Order Total</th>
            <td><strong>Rs.${grandtotal.toFixed(2)}</strong></td>
        </tr>
    `;

};

async function checkout() {
    
    const popup = new Notification();

    // get all radio buttons with name address
    const radios = document.querySelectorAll('input[name="address"]');

    // find checked one
    let selectedRadio = null;
    
    for (const radio of radios) {
        
        if (radio.checked) {
            
            selectedRadio = radio;
            break;
            
        }
        
    }

    if (!selectedRadio) {
        
        popup.info({
            
            message: "Please select an address before proceeding."
            
        });
        
        return;
        
    }

    // get data attributes
    const name = selectedRadio.getAttribute('data-name');
    const email = selectedRadio.getAttribute('data-email');
    const city = selectedRadio.getAttribute('data-city');
    const province = selectedRadio.getAttribute('data-province');
    const district = selectedRadio.getAttribute('data-district');
    const postalcode = selectedRadio.getAttribute('data-postalcode');
    const mobileno = selectedRadio.getAttribute('data-mobileno');
    const adline1 = selectedRadio.getAttribute('data-line1');
    const adline2 = selectedRadio.getAttribute('data-line2');

    // update the displayed details on the page
    document.getElementById('addName').innerText = `Name :- ${name}`;
    document.getElementById('addEmail').innerText = `Email :- ${email}`;

    // show confirmation dialog using Notification
    popup.dialog({
        
        title: "Confirm Order",
        
        message: "Is your order complete?",
        
        callback: async function(result) {
            
            if (result === 'ok') {

                let data = {
                    Name: name,
                    Email: email,
                    city: city,
                    Province: province,
                    Distric: district,
                    PostalCode: postalcode,
                    MobileNo: mobileno,
                    AdLine1: adline1,
                    AdLine2: adline2
                };

                let dataJson = JSON.stringify(data);

                const response = await fetch("Checkout", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: dataJson
                });

                if (response.ok) {
                    
                    const json = await response.json();
                    
                    if (json.status) {
                        
                        console.log(json);
                        
                        // PayHere Process
                        payhere.startPayment(json.payhereJson);
                        
                    } else {
                        
                        popup.error({
                            
                            message: json.message
                            
                        });
                        
                    }
                    
                } else {
                    
                    popup.error({
                        
                        message: "Something went wrong. Please try again!"
                        
                    });
                    
                }

            } else {
                
                popup.info({
                    
                    message: "Checkout cancelled. Please review your order."
                    
                });
                
            }
            
        }
        
    });
    
}

