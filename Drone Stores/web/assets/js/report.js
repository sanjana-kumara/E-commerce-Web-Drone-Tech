let myPolarChart = null;

const colors = [
    "rgb(255, 99, 132)",
    "rgb(75, 192, 192)",
    "rgb(255, 205, 86)",
    "rgb(201, 203, 207)",
    "rgb(54, 162, 235)"
];

async function loadTopSellingProducts() {
    try {
        // Get web app context path dynamically
        const contextPath = window.location.pathname.split('/')[1] || '';
        const url = `/${contextPath}/TopSellingProductsServlet`.replace(/\/+/g, '/');

        const res = await fetch(url);
        if (!res.ok) throw new Error("HTTP " + res.status);
        const products = await res.json();

        const labels = products.map(p => p.title);
        const data = products.map(p => p.qty);
        const chartColors = colors.slice(0, products.length);

        // Render chart
        const ctx = document.getElementById('myPolarChart').getContext('2d');
        if (myPolarChart) myPolarChart.destroy();
        myPolarChart = new Chart(ctx, {
            type: 'polarArea',
            data: {
                labels,
                datasets: [{
                    label: 'Quantity Sold',
                    data,
                    backgroundColor: chartColors
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { position: 'right' } }
            }
        });

        // Populate product list
        const productList = document.getElementById('productList');
        productList.innerHTML = '';
        products.forEach((p, idx) => {
            const li = document.createElement('li');
            li.className = "border-l-4 p-3 rounded shadow-sm flex justify-between items-center";
            li.style.borderColor = chartColors[idx];
            li.innerHTML = `<span>${p.title}</span><span class="font-semibold">Sold: ${p.qty}</span>`;
            productList.appendChild(li);
        });

    } catch (err) {
        console.error(err);
        document.getElementById('productList').innerHTML = '<li class="p-4 text-red-600">Error loading products</li>';
    }
}

window.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.fade-in').forEach(el => el.classList.add('visible'));
    loadTopSellingProducts();
});
