
// Set product ID from URL to hidden input when page loads
window.addEventListener("DOMContentLoaded", () => {

    const urlParams = new URLSearchParams(window.location.search);

    const productId = urlParams.get("id");

    if (productId) {

        document.getElementById("productId").value = productId;

    }

});

const popup = new Notification();

function addToCart(event) {

    const productId = document.getElementById("productId").value;

    const qty = document.getElementById("qtyInput").value;

    if (!productId || isNaN(productId)) {

        popup.error({

            message: "Invalid Product ID!"

        });

        return;

    }

    if (!qty || isNaN(qty) || parseInt(qty) < 1) {

//        alert("Please enter a valid quantity.");

        popup.error({

            message: "Please enter a valid quantity."

        });

        return;

    }

    fetch(`AddToCart?prId=${productId}&qty=${qty}`)
            .then(response => response.json())
            .then(data => {

                if (data.status) {

                    popup.success({

                        message: data.message
                    });


                } else {

                    popup.error({

                        message: data.message

                    });

                }

            })

            .catch(error => {

                console.error("Error adding to cart:", error);

                popup.error({

                    message: "Something went wrong. Please try again."

                });

            });

}