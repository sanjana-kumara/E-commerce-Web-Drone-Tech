async function addToProduct() {

    const product_Name = document.getElementById("p-name").value;
    const product_Category = document.getElementById("p-category").value;
    const product_Brand = document.getElementById("p-brand").value;
    const product_Model = document.getElementById("p-model").value;
    const product_Color = document.getElementById("p-color").value;
    const product_Price = document.getElementById("p-price").value;
    const product_Qty = document.getElementById("p-qty").value;
    const product_Filght_Time = document.getElementById("p-f-time").value;
    const product_Range = document.getElementById("p-range").value;
    const product_Warranty = document.getElementById("p-warranty").value;
    const product_Feature = document.getElementById("p-feature").value;
    const product_Description = document.getElementById("p-description").value;

    const image1 = document.getElementById("image1").files[0];
    const image2 = document.getElementById("image2").files[0];
    const image3 = document.getElementById("image3").files[0];


//    console.log(product_Name);
//    console.log(product_Category);
//    console.log(product_Brand);
//    console.log(product_Model);
//    console.log(product_Color);
//    console.log(product_Price);
//    console.log(product_Qty);
//    console.log(product_Filght_Tim);
//    console.log(product_Range);
//    console.log(product_Warranty);
//    console.log(product_Feature);
//    console.log(product_Description);
//    console.log(image1);
//    console.log(image2);
//    console.log(image3);

    const form = new FormData();
    form.append("product_Name", product_Name);
    form.append("product_Category", product_Category);
    form.append("product_Brand", product_Brand);
    form.append("product_Model", product_Model);
    form.append("product_Color", product_Color);
    form.append("product_Price", product_Price);
    form.append("product_Qty", product_Qty);
    form.append("product_Flight_Time", product_Filght_Time);
    form.append("product_Range", product_Range);
    form.append("product_Warranty", product_Warranty);
    form.append("product_Feature", product_Feature);
    form.append("product_Description", product_Description);
    form.append("image1", image1);
    form.append("image2", image2);
    form.append("image3", image3);

    const response = await fetch(
            "SaveProduct",
            {

                method: "POST",
                body: form

            }
    );

    const data = await response.json(); // This line was missing!

    const popup = new Notification();

    if (data.status) {

        popup.success({

            message: data.message

        });

        // Optional: reset the form
        document.getElementById("product-form").reset();

        // Clear any preview images (if added)
        document.getElementById("preview1").innerHTML = "";
        document.getElementById("preview2").innerHTML = "";
        document.getElementById("preview3").innerHTML = "";


    } else {

        popup.error({

            message: data.message

        });

    }

}




async function loadInitialData() {

    const brandSelect = document.getElementById("p-brand");
    const modelSelect = document.getElementById("p-model");
    const colorSelect = document.getElementById("p-color");
    const categorySelect = document.getElementById("p-category");

    const response = await fetch('SaveProduct');

    if (response.ok) {

        const data = await response.json();

        console.log(data);

        // Populate Brands
        brandSelect.innerHTML = '<option value="0">-- Select Brand --</option>';

        (data.brand || []).forEach(brand => {

            const opt = document.createElement("option");
            opt.value = brand.id;
            opt.text = brand.name;
            brandSelect.appendChild(opt);

        });

        // Populate Model 
        modelSelect.innerHTML = '<option value="0">-- Select Model --</option>';

        (data.model || []).forEach(model => {

            const opt = document.createElement("option");
            opt.value = model.id;
            opt.text = model.name;
            modelSelect.appendChild(opt);

        });

        // Populate Categories
        categorySelect.innerHTML = '<option value="0">-- Select Category --</option>';

        (data.categories || []).forEach(cat => {

            const opt = document.createElement("option");
            opt.value = cat.id;
            opt.text = cat.name;
            categorySelect.appendChild(opt);

        });

        // Populate Colors
        colorSelect.innerHTML = '<option value="0">-- Select Color --</option>';

        (data.colors || []).forEach(color => {

            const opt = document.createElement("option");
            opt.value = color.id;
            opt.text = color.name;
            colorSelect.appendChild(opt);

        });


    } else {

        console.error('Network response was not ok');

    }

}
