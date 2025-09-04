
async function saveProfile() {

    const popup = new Notification();

    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const mobileNo = document.getElementById("mobileNo").value.trim();
    const postalCode = document.getElementById("postalcode").value.trim();
    const address1 = document.getElementById("address1").value.trim();
    const address2 = document.getElementById("address2").value.trim();
    const provinceId = document.getElementById("provinceSelect").value;
    const districtId = document.getElementById("districtSelect").value;
    const cityId = document.getElementById("city").value;

    const data = {
        firstName,
        lastName,
        mobileNo,
        address1,
        address2,
        provinceId,
        districtId,
        cityId,
        postalCode
    };

    try {

        const response = await fetch("SaveProfileServlet", {

            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)

        });

        const result = await response.json();

        if (result.status) {

//            alert(result.message); // success message

            popup.success({

                message: result.message

            });

            location.reload();

        } else {

//            alert(result.message); // validation error message from servlet

            popup.error({

                message: result.message

            });

        }

    } catch (error) {

        console.error("Error:", error);
//        alert("Something went wrong while saving your profile.");

        popup.error({

            message: "Something went wrong while saving your profile."

        });

    }

}

async function loadData() {

    const popup = new Notification();

    const response = await fetch("SaveProfileServlet");

    if (response.ok) {

        const json = await response.json();

        console.log(json);

        if (Array.isArray(json.addressList) && json.addressList.length > 0) {

            const addressUL = document.getElementById("addressUL");

            addressUL.innerHTML = "";

            const fullName = `${json.firstName} ${json.lastName}`;

            const email = json.email;

            json.addressList.forEach(address => {
                const line1 = address.line1;
                const line2 = address.line2;
                const mobileno = address.mobile_no;
                const provinceSelect = address.province_id?.name || "N/A";
                const districtSelect = address.distric_id?.name || "N/A";
                const citySelect = address.city_id?.name || "N/A";
                const postalcode = address.postal_code;

                const line = document.createElement("li");

                line.className = "mt--30";

                line.innerHTML = `
                    <input type="radio" name="address"
                        data-name="${fullName}"
                        data-email="${email}"
                        data-city="${citySelect}"
                        data-province="${provinceSelect}"
                        data-district="${districtSelect}"
                        data-postalcode="${postalcode}"
                        data-mobileno="${mobileno}"
                        data-line1 ="${line1}"
                        data-line2 ="${line2}"
                    />
                    Address:- ${line1}, ${line2} <br/>
                    Province:- ${provinceSelect} Province <br/>
                    District:- ${districtSelect} District <br/>
                    City:- ${citySelect} <br/>
                    Postal Code:- ${postalcode} <br/>
                    Mobile Number:- ${mobileno} <br/><br/>
                `;

                addressUL.appendChild(line);

            });

            document.getElementById("addName").innerHTML = `Name: ${fullName}`;

            document.getElementById("addEmail").innerHTML = `Email: ${email}`;

            // Optional: bind change event
            document.querySelectorAll('input[name="address"]').forEach(radio => {

                radio.addEventListener("change", () => {

                    if (typeof window.renderOrderSummary === "function") {

                        window.renderOrderSummary();

                    }

                });

            });

        } else {

//            console.log("No addresses found.");
            popup.error({

                message: "No addresses found."

            });

        }

    } else {

//        console.error("Failed to load data from SaveProfileServlet");
        popup.error({

            message: "Failed to load data from SaveProfileServlet"

        });

    }

}

document.addEventListener("DOMContentLoaded", loadData);

async function loadProfileToForm() {

    await loadLocationData();

    const popup = new Notification();

    const response = await fetch("SaveProfileServlet");

    if (response.ok) {

        const json = await response.json();

        document.getElementById("firstName").value = json.firstName || "";
        document.getElementById("lastName").value = json.lastName || "";
        document.getElementById("mobileNo").value = json.addressList && json.addressList.length > 0 ? json.addressList[0].mobile_no : "";
        document.getElementById("postalcode").value = json.addressList && json.addressList.length > 0 ? json.addressList[0].postal_code : "";

        if (json.addressList && json.addressList.length > 0) {

            const addr = json.addressList[0];

            document.getElementById("address1").value = addr.line1 || "";
            document.getElementById("address2").value = addr.line2 || "";

            document.getElementById("provinceSelect").value = addr.province.id;

            loadDistricts();

            setTimeout(() => {

                document.getElementById("districtSelect").value = addr.district.id;
                loadCities();

                setTimeout(() => {

                    document.getElementById("city").value = addr.city.id;

                }, 300);

            }, 300);

        }

    } else {

//        alert("Failed to load profile data.");
        popup.error({

            message: "Failed to load profile data."

        });

    }

}



let districtList = [];
let cityList = [];

// Utility for loading options into a <select>
function loadSelect(selectId, list, property) {

    const select = document.getElementById(selectId);

    select.length = 1;

    list.forEach(item => {

        const option = document.createElement("option");
        option.value = item.id;
        option.innerText = item[property];
        select.appendChild(option);

    });

}

// Load all location data from servlet, cache to arrays, populate provinces
function loadLocationData() {

    const popup = new Notification();

    fetch("LoadLocationData")

            .then(response => response.json())

            .then(json => {

                if (json.status) {

                    // Load provinces
                    loadSelect("provinceSelect", json.provinceList, "name");

                    // Cache district and city lists
                    districtList = json.districList;
                    cityList = json.cityList;

                } else {

//                    alert("Unable to load location data.");

                    popup.error({

                        message: "Unable to load location data."

                    });

                }
            })

            .catch(() =>
    
                alert("Failed to load location data.")

            );

}

// Populate districts for selected province
function loadDistricts() {

    const provinceId = document.getElementById("provinceSelect").value;

    const districtSelect = document.getElementById("districtSelect");

    districtSelect.length = 1;

    // Also reset cities, since districts are changing
    document.getElementById("city").length = 1;

    districtList.forEach(d => {

        // Check property safely
        if (d.province_id && String(d.province_id.id) === String(provinceId)) {

            const option = document.createElement("option");
            option.value = d.id;
            option.innerText = d.name;
            districtSelect.appendChild(option);

        }

    });

}

// Populate cities for selected district
function loadCities() {

    const districtId = document.getElementById("districtSelect").value;

    const citySelect = document.getElementById("city");

    citySelect.length = 1;

    cityList.forEach(c => {

        if (c.distric_id && String(c.distric_id.id) === String(districtId)) {

            const option = document.createElement("option");
            option.value = c.id;
            option.innerText = c.name;
            citySelect.appendChild(option);

        }

    });

}
