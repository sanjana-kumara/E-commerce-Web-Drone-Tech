async function signOut() {

    const response = await fetch("LogOut");

    if (response.ok) {

        const json = await response.json();

        if (json.status) {

            window.location = "signin.html";

        } else {

            window.location.reload();

        }

    } else {

        console.log("Logout Failed!");

    }

}
