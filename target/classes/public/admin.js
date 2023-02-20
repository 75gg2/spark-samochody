function yearInvoiceFilter() {
    const filter = document.getElementById("yearInvoiceFilter")
    fetch("/invoiceByYear?year=" + filter.value, {method: "post"}).then(e => e.json())
        .then(e => {
            if (e) {
                alert("wygenerowano pomyślnie")
                refreshInvoicesLinks()
            }
        })

}

function allInvoiceFilter() {
    fetch("/invoiceOfAll", {method: "post"}).then(e => e.json())
        .then(e => {
            if (e) {
                alert("wygenerowano pomyślnie")
                refreshInvoicesLinks()
            }
        })

}

function priceInvoiceFilter() {
    const filter1 = document.getElementById("priceInvoiceFilter1")
    const filter2 = document.getElementById("priceInvoiceFilter2")
    fetch("/invoiceByPrice?price1=" + filter1.value + "&price2=" + filter2.value, {method: "post"})
        .then(e => e.json())
        .then(e => {
            if (e) {
                alert("wygenerowano pomyślnie")
                refreshInvoicesLinks()
            }
        })
}

function refreshInvoicesLinks() {
    fetch("/invoicesFilter").then(e => e.json())
        .then(e => {
            let str = ""
            if (Array.isArray(e)) {
                for (a of e[0]) {
                    str += `<a href="${a.fileName}"><abbr title="${a.description}">pobierz</abbr></a>`
                }
                document.getElementById("yearInvoiceFiltered").innerHTML = str

                for (a of e[1]) {
                    str += `<a href="${a.fileName}"><abbr title="${a.description}">pobierz</abbr></a>`
                }
                document.getElementById("priceInvoiceFiltered").innerHTML = str
                for (a of e[2]) {
                    str += `<a href="${a.fileName}"><abbr title="${a.description}">pobierz</abbr></a>`
                }
                document.getElementById("allInvoiceFiltered").innerHTML = str
            }
        })
}