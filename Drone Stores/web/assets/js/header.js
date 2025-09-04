$(function () {
    $("#middleheader").load("header2.html");
    $("#bottomheader").load("header3.html");
    $("#footer").load("footer2.html");
    $("#trandingproduct").load("tranding_product_load.html");
});
// page load wela iwara unama (DOMContentLoaded) event eka trigger wenawa
window.addEventListener("DOMContentLoaded", async () => {
    // div element eka gannawa
    const container = document.getElementById("mainheader");

    try {
        // ✅ header1 servlet ekata fetch request ekak
        const res = await fetch("Header1");

        // response hari nam (HTTP 200)
        if (res.ok) {
            // header1 servlet eken enna HTML eka gannawa
            const html = await res.text();

            // ✅ div ekata inject karanawa
            container.innerHTML = html;
        } else {
            console.error("Header load failed", res.status);
        }
    } catch (e) {
        console.error("Header fetch error", e);
    }
});


  document.addEventListener("DOMContentLoaded", function () {
      
    fetch("SessionHeader")
      .then(response => response.text())
      .then(data => {
          
        document.getElementById("mainheader").innerHTML = data;

      })
      
      .catch(error => {
          
        console.error("Header load error:", error);

      });
      
  });
  
  document.addEventListener("DOMContentLoaded", function () {
      
    fetch("SessionHeader2")
      .then(response => response.text())
      .then(data => {
          
        document.getElementById("middleheader").innerHTML = data;

      })
      
      .catch(error => {
          
        console.error("Header load error:", error);

      });
      
  });
  
  document.addEventListener("DOMContentLoaded", function () {
      
    fetch("SessionHeader3")
      .then(response => response.text())
      .then(data => {
          
        document.getElementById("bottomheader").innerHTML = data;

      })
      
      .catch(error => {
          
        console.error("Header load error:", error);

      });
      
  });
