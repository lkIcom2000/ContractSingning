import sys
from reportlab.lib.pagesizes import LETTER
from reportlab.pdfgen import canvas


def create_contract_pdf(company_name, registration_id, address, output_filename="contract.pdf"):
    c = canvas.Canvas(output_filename, pagesize=LETTER)
    width, height = LETTER
    y = height - 50  # Start near the top

    # COMPANY INFORMATION
    c.setFont("Times-Bold", 12)
    c.drawString(50, y, "COMPANY INFORMATION")
    y -= 20
    c.setFont("Times-Roman", 12)
    c.drawString(50, y, f"Company: {company_name}")
    c.drawString(350, y, "MCH Formland")
    y -= 15
    c.drawString(50, y, f"CVR: {registration_id}")
    c.drawString(350, y, "Vardevej 1, DK-7400 Herning")
    y -= 15
    c.drawString(50, y, f"Address: {address}")
    y -= 20
    c.setFont("Times-Bold", 18)
    c.drawString(350, y, "Kontrakt")
    c.setFont("Times-Roman", 12)
    y -= 10
    c.line(50, y, width - 50, y)
    y -= 20

    # FAIR SECTION
    c.setFont("Times-Bold", 12)
    c.drawString(50, y, "Fair:")
    c.setFont("Times-Roman", 12)
    c.drawString(200, y, "Formland Autumn 2025")
    c.drawString(400, y, "Date: 17.08.2025 - 19.08.2025")
    y -= 10
    c.line(50, y, width - 50, y)
    y -= 20

    # Info section
    c.setFont("Times-Roman", 11)
    c.drawString(50, y, "Se øvrige vilkår for deltagelse og betalings-")
    y -= 15
    c.drawString(50, y, "betingelser på efterfølgende sider.")
    y -= 15
    c.drawString(50, y, "MCH Ordensregler kan findes på: wwww.formland.dk")
    y -= 15
    c.drawString(50, y, "OBS: Slutsdelen er IKKE en fakture. Særskilt faktura")
    y -= 15
    c.drawString(50, y, "vil blive sendt senere.")
    y -= 10
    c.line(50, y, width - 50, y)
    y -= 30

    # Stand area section
    c.setFont("Times-Roman", 12)
    c.drawString(50, y, "Stand No:")
    c.drawString(250, y, "3323")
    y -= 15
    c.drawString(50, y, "Stand area:")
    c.drawString(250, y, "35 m2")
    y -= 15
    c.drawString(50, y, "Sektor:")
    c.drawString(250, y, "Finance")
    y -= 15
    c.line(50, y, width - 50, y)   
    y -= 30
    
    
    c.drawString(50, y, "Description")
    c.drawString(250, y, "Quantity")
    c.drawString(350, y, "Unit price")
    c.drawString(450, y, "Total")
    y -= 10
    c.line(50, y, width - 50, y)
    y -= 25
    c.drawString(50, y, "Registration fee")
    c.drawString(450, y, "3999,00 kr")
    y -= 15
    c.drawString(50, y, "Stand rent")
    c.drawString(250, y, "1999,00 kr")
    c.drawString(350, y, "925,00 kr")
    c.drawString(450, y, "1999,00 kr")
    y -= 30
    c.drawString(50, y, "Total")
    c.drawString(450, y, "5998,00 kr")
    y -= 30
    
    c.drawString(50, y, "9.04.25")
    c.drawString(150, y, "Anders Andersen")
    y -= 10
    c.line(50, y, width - 350, y)
    y -= 10
    c.drawString(50, y, "Date")
    c.drawString(150, y, "Signature")
        
    
    
    c.showPage()
    c.save()
    print(f"Contract PDF generated: {output_filename}")


def main():
    print("Please enter the following company information:")
    company_name = input("Company Name: ")
    registration_id = input("Registration ID: ")
    address = input("Address: ")
    create_contract_pdf(company_name, registration_id, address)


if __name__ == "__main__":
    main()
