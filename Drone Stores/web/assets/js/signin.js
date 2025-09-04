async function SignIn() {
    
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    
    const signIn = {
        
        email: email,
        password: password
                
    };
    
    const signinJSON = JSON.stringify(signIn);
    
    const response = await fetch(
            "SignIn",
            {
                
                method: "POST",
                body: signinJSON,
                header: {
                    
                    "Content-Type": "application/json"
                            
                }
                
            }
    );
    
    if (response.ok) {
        
        const json = await response.json();
        
        if (json.status) {
            
            console.log(json);
            
            if (json.message === "Verified") {
                
                window.location = "index.html";
                
            } else if (json.message === "Admin") {
                
                window.location = "admin-user-management.html";
                
            } else {
                
                window.location = "account-verify.html";
                
            }
            
        } else {
            
            const messageBox = document.getElementById("message");
            messageBox.textContent = json.message;
            messageBox.className = "text-red-700 bg-red-100 border border-red-400 text-center text-sm mt-4 p-2 rounded-lg";
            messageBox.classList.remove("hidden");
            
        }
        
    } else {
        
        const messageBox = document.getElementById("message");
        messageBox.textContent = "Sign In failed. Please try again!";
        messageBox.className = "text-red-700 bg-red-100 border border-red-400 text-center text-sm mt-4 p-2 rounded-lg";
        messageBox.classList.remove("hidden");
        
    }
    
}

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