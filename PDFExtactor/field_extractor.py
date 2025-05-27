import PyPDF2
import re

def fields_of(header, var_next_header, fix_next_header, pageFileObj):
    fields = set()
    pdfReader = PyPDF2.PdfFileReader(pdfFileObj)
    npages = pdfReader.numPages
    for page in range(npages):
        pageObj = pdfReader.getPage(page)
        text = pageObj.extractText()
        header_index = text.find(header)
        var_next_header_index = text.find(var_next_header)
        fix_next_header_index = text.find(fix_next_header)
        next_header_index = fix_next_header_index if (var_next_header_index == -1)  else var_next_header_index
        if header_index != -1 and next_header_index != -1:
            focus = text[len(header) + header_index : next_header_index]
            fs = re.split("\d+\.\d+", focus)
            for field in fs:
                if field not in fields:
                    fields.add(field)
    return fields

pdfFileObj = open('invoices.pdf', 'rb')
talk_summary_fields = fields_of("Talk Summary", "SMS and MMS Summary", "SMS and MMS Summary", pdfFileObj)
sms_mms_summary_fields = fields_of("SMS and MMS Summary", "Data Summary", "Data Summary", pdfFileObj)
data_summary_fields = fields_of("Data Summary", "Recurring Charges", "Recurring Charges", pdfFileObj)
recurring_charges_fields = fields_of("Recurring Charges", "Other Charges", "Account Summary", pdfFileObj)
other_charges_fields = fields_of("Other Charges", "Account Summary", "Account Summary", pdfFileObj)
account_summary_fields = fields_of("Account Summary", "Service Plan details for the mobile no.", "Service", pdfFileObj)

print("\nTalk Summary")
for field in talk_summary_fields:
    print(field)
print("\nSMS and MMS Summary")
for field in sms_mms_summary_fields:
    print(field)
print("\nData Summary")
for field in data_summary_fields:
    print(field)
print("\nRecurring Charges")
for field in recurring_charges_fields:
    print(field)
print("\nOther Charges")
for field in other_charges_fields:
    print(field)
print("\nAccount Summary")
for field in account_summary_fields:
    print(field)
