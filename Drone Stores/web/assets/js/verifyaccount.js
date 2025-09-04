async function verifyAccount() {

    const popup = new Notification();

    const verifycode = document.getElementById("verificationcode").value;

//    console.log(verifycode);

    const verification = {

        verificationCode: verifycode

    };

    const verificationJSON = JSON.stringify(verification);

    const response = await fetch(
            "VerifyAccount",
            {

                method: "POST",
                body: verificationJSON,
                header: {

                    "Content-Type": "application/json"

                }

            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.status) {// if true

            const json = await response.json();

            if (json.status) {

                popup.success({

                    message: json.message

                });

                location.reload();

                window.location.href = "index.html"; // redirect to index

            } else {// When status fales

                if (json.message === "1") { //Email not found!!

                    window.location = "signin.html";

                } else {

                    const messageBox = document.getElementById("message");
                    messageBox.textContent = json.message;
                    messageBox.className = "text-red-700 bg-red-100 border border-red-400 text-center text-sm mt-4 p-2 rounded-lg";
                    messageBox.classList.remove("hidden");

                }

            }

        } else {

            const messageBox = document.getElementById("message");
            messageBox.textContent = "Verification faild. Please try again!";
            messageBox.className = "text-red-700 bg-red-100 border border-red-400 text-center text-sm mt-4 p-2 rounded-lg";
            messageBox.classList.remove("hidden");

        }

        setTimeout(() => {

            document.getElementById("message").classList.add("hidden");

        }, 3000);

    }

}