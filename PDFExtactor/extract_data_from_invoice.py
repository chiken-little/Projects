import PyPDF2
import re, json

fields = {"Talk Summary" : ["Local Calls (MTN to Fixed Line)", "CUG Calls", "International Video Calls",
                            "MTN Content", "International Voice Calls", "Roaming Calls",
                            "Local Calls (MTN to MTN)", "Local Calls (MTN to Other mobile networks)",
                            "Local Video Calls (MTN to Other Mobile Network)",
                            "Local Video Calls (MTN to MTN)"],
          "SMS and MMS Summary" : ["CUG SMS", "Total SMS", "Total MMS"],
          "Data Summary": ["Data Usage", "Roaming Data", "MTN WECA DATA"],
          "Recurring Charges" : ["RENTALS SAMSUNG J5 PRIME 50GHC", "DATA SVC:4GB Capping",
                                 "DATA SVC:1GB Capping", "Vas Rentals",
                                 "DATA SVC:2.5GB Capping", "Vas Rentals", "DATA_PKG:6GB Capping",
                                 "RESIDENTIAL FTTX UHS MRC 240ghc", "CRBT_POST", "DATA PKG:2.5GB Capping",
                                 "DATA_SVC:10GB Capping", "DATA PKG:4GB Capping", "DATA_SVC:6GB Capping",
                                 "DATA SVC:400MB Capping", "DATA_PKG:10GB Capping", "CUG Open LCA"],
          "Other Charges": ["EDR-PDM", "VAS One Time Charge", "400MB TOPUP Charge"],
          "Account Summary": ["GETFL 2.5%", "Grand Total", "CST 6%", "Sub Total", "Value of current invoice",
                              "NHIL 2.5%", "VAT 12.5%"]
         }

mobile_pretext = "Service Plan details for the mobile no. : "
name_pretext = "Name: "

pdfFileObj = open("invoices.pdf", "rb")
pdfReader = PyPDF2.PdfFileReader(pdfFileObj)
npages = pdfReader.numPages


def get_value(field, text, all_fields):
    field_index = text.find(field)
    if field_index != -1:
        value = re.search('\d+\.\d+', text[field_index + len(field) : ]).group()
        if value != '':
            return float(value)
    return 0.0 if all_fields else None


def invoice_data(page, all_fields=False):
    invoice = {}
    pageObject = pdfReader.getPage(page)
    text = pageObject.extractText()
    mobile_pretext_index = text.find(mobile_pretext)
    name_pretext_index = text.rfind(name_pretext)

    if mobile_pretext_index != -1 and name_pretext_index != -1:
        mobile = text[mobile_pretext_index + len(mobile_pretext) : name_pretext_index]
        name = text[name_pretext_index + len(name_pretext) : text.rfind("Page")]
        invoice["mobile"] = mobile
        invoice["name"] = name

    for field in fields:
        invoice[field] = {}
        for subfield in fields[field]:
            subfield_value = get_value(subfield, text, all_fields)
            if subfield_value or all_fields:
                invoice[field][subfield] = subfield_value

    return invoice

if __name__ == "__main__":
    print(json.dumps(invoice_data(9, True), indent=4))
    print(json.dumps(invoice_data(9), indent=4))
