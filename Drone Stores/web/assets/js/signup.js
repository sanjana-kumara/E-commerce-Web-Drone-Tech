function togglePasswordVisibility() {
    const passwordField = document.getElementById("password");
    const eyeIcon = document.getElementById("eye-icon");

    if (passwordField.type === "password") {
        passwordField.type = "text";
        eyeIcon.classList.add("fa-eye");
        eyeIcon.classList.remove("fa-eye-slash");
    } else {
        passwordField.type = "password";
        eyeIcon.classList.add("fa-eye-slash");
        eyeIcon.classList.remove("fa-eye");
    }
}

async  function SignUp() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

//    console.log(firstName);
//    console.log(lastName);
//    console.log(email);
//    console.log(password);

    User = {

        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password

    };

    const userJSON = JSON.stringify(User);

    const response = await fetch(
            "SignUp",
            {

                method: "POST",
                body: userJSON,
                header: {
                    "Content-Type": "application/json"
                }

            }

    );

    if (response.ok) { // success

        const json = await response.json();

        if (json.status) {

            // Redirect to verification page
            window.location = "account-verify.html";

        } else {

            // Show custom message with red styling
            const messageBox = document.getElementById("message");
            messageBox.textContent = json.message;
            messageBox.className = "text-red-700 bg-red-100 border border-red-400 text-center text-sm mt-4 p-2 rounded-lg";
            messageBox.classList.remove("hidden");

        }

    } else {

        // Show generic failure message
        const messageBox = document.getElementById("message");
        messageBox.textContent = "Registration failed. Please try again.";
        messageBox.className = "text-red-700 bg-red-100 border border-red-400 text-center text-sm mt-4 p-2 rounded-lg";
        messageBox.classList.remove("hidden");

    }

    setTimeout(() => {
        document.getElementById("message").classList.add("hidden");
    }, 3000);

}