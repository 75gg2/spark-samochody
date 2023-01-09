function yearInvoiceFilter() {
    const filter = document.getElementById("yearInvoiceFilter")
    fetch("/invoiceByYear?year=" + filter.value, {method: "post"}).then(e=>e.json())
        .then(e=> {
            if (e) {
                alert("wygenerowano pomyślnie")
                refreshInvoicesLinks()
            }
        })

}

function priceInvoiceFilter() {
    const filter1 = document.getElementById("priceInvoiceFilter1")
    const filter2 = document.getElementById("priceInvoiceFilter2")
}
function refreshInvoicesLinks() {
    fetch("/invoicesFilter").then(e=>e.json())
        .then(e=>{
            let str = ""
            if(Array.isArray(e)){
                for(a of e[0]) {
                    str += `<a href="${a}">pobierz</a>`
                }
                document.getElementById("yearInvoiceFiltered").innerHTML = str
            }
        })
}